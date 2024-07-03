package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;

/**
 * An implementation of {@link MonteCarloTreeSearch}, using a light playout and no multithreadding.
 * It uses a custom grid.
 * 
 * @author sistumpf
 */
public class MCTS implements MonteCarloTreeSearch {
  private AIConfig config;
  private int teams;
  private int maxDistance;
  private TreeNode root;
  public AtomicInteger simulationCounter;
  public AtomicInteger heuristicCounter;
  private AtomicInteger expansionCounter;
  private Move influencer;

  public MCTS(TreeNode root, AIConfig config) {
    this.config = config;
    this.setRoot(root);
//    this.rand = new Random();
    simulationCounter = new AtomicInteger();
    heuristicCounter = new AtomicInteger();
    this.expansionCounter = new AtomicInteger();
    this.teams = root.getReferenceGameState().getTeams().length;
    this.maxDistance =
        (int)
            Math.round(
                Math.sqrt(
                    Math.pow(root.getReferenceGameState().getGrid().getGrid().length, 2)
                        + Math.pow(root.getReferenceGameState().getGrid().getGrid()[0].length, 2)));
  }

  @Override
  public Move getMove(int milis) {
    long time = System.currentTimeMillis();

    while (System.currentTimeMillis() - time < milis) {
      // Schritte des UCT abarbeiten
      TreeNode selected = selectAndExpand(getRoot(), config.C);
      backpropagate(selected, simulate(selected));
    }

    TreeNode bestChild = getRootBest(getRoot());

    // Hier werden wichtige Daten zur Auswahl ausgegeben
    //      printResults(bestChild);

    influencer = null;
    return bestChild.getReferenceGameState().getLastMove().toMove();
  }
  
  @Override
  public Move getMove(Move influencer, int milis) {
    this.influencer = influencer;
    return getMove(milis);
  }

  /**
   * Selects a node to simulate on using the UCT formula. expands a children if a node in the chain
   * has unexpanded ones.
   *
   * @param parent node, from it on the nodes will be checked for one to simulate on
   * @param constant C used in UCT formula
   * @return the node to simulate on
   */
  TreeNode selectAndExpand(TreeNode node, double C) {
    while (isTerminal(node.getReferenceGameState(), node.getOperateOn()) == -1) {
      if (!isFullyExpanded(node)) {
        this.expansionCounter.incrementAndGet();
        return expand(node);
      } else {
        node = bestChild(node, C);
      }
    }
    return node;
  }

  /**
   * Adds one child to the parent node, the child is identical to the parent but simulated one move
   * further.
   *
   * @param the selected node which gets expanded
   * @return the new child
   * @return null if anything unforeseen happens
   */
  TreeNode expand(TreeNode parent) {
    for (int i = 0; i < parent.getChildren().length; i++) {
      if (parent.getChildren()[i] == null) {
        TreeNode child = parent.clone(parent.getReferenceGameState().clone());
        oneMove(child, parent, false, child.getOperateOn());
        parent.getChildren()[i] = child;
        return child;
      }
    }
    return null;
  }

  /**
   * !! This method is removed for now to make this MCTS more resource efficient and because of the long Thread starting times. 
   * It was not viable to start the Threads, as it took (almost) longer than calling simulate() !!
   * Simulates a certain amount of simulations simultaneously.
   *
   * @param simulateOn
   * @return an array containing a number of wins for the team at position teamId
   */
  /*int[] multiSimulate(TreeNode simulateOn) {
    int[] winners = new int[simulateOn.gameState.getTeams().length];

    try {
      // Create a list of Callable tasks for parallel execution
      List<Callable<int[]>> tasks = new LinkedList<>();
      for (int i = 0; i < config.numThreads; i++) {
        tasks.add(
            () -> {
              return simulate(simulateOn);
            });
      }

      // Submit tasks for parallel execution
      List<Future<int[]>> futures = executorService.invokeAll(tasks);
      for (int i = 0; i < futures.size(); i++) {
        try {
          int[] wins = futures.get(i).get();
          for (int j = 0; j < wins.length; j++) {
            winners[j] += wins[j];
          }
        } catch (Exception e) {
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return winners;
  }*/

  /**
   * Simulates a game from a specific node to finish (or a maximum step value of Constants.MAX_STEPS
   * simulation), First checks if a node is in a terminal state, if thats the case the simulation
   * ends and the result is returned.
   *
   * @param the node from which a game is going to be simulated
   * @return an array containing a number of wins for the team at position teamId
   */
  int[] simulate(TreeNode simulateOn) {
    simulationCounter.incrementAndGet();
    ReferenceMove change = new ReferenceMove(null, new int[] {0,0});
    int isTerminal = isTerminal(simulateOn.getReferenceGameState(), change);
    int[] winners = new int[this.teams];
    int count = config.MAX_STEPS;
    if (isTerminal >= 0) {
      winners[isTerminal] += count;
      return winners;
    }

    simulateOn = simulateOn.clone(simulateOn.getReferenceGameState().clone());
    simulateOn.setOperateOn(change);

    for (; count > 0 && isTerminal == -1; count--, isTerminal = isTerminal(simulateOn.getReferenceGameState(), change)) {
      oneMove(simulateOn, simulateOn, true, change);
      removeTeamCheck(simulateOn.getReferenceGameState());
    }
    if (isTerminal < 0) {
      simulationCounter.decrementAndGet();
      heuristicCounter.incrementAndGet();
      winners[terminalHeuristic(simulateOn)] += 1;
    } else {
      winners[isTerminal] += count;
    }

    return winners;
  }

  /**
   * A heuristic to evaluate the winner of a given nodes gameState.
   *
   * @param a node which will be analyzed
   * @return the best teams teamId (as an int)
   */
  int terminalHeuristic(TreeNode node) {
    Team[] teams = node.getReferenceGameState().getTeams();
    int[] points = new int[teams.length];

    for (int i = 0; i < teams.length; i++) {
      if (teams[i] == null) continue;

      for (Piece p : teams[i].getPieces()) {
        points[i] += p.getDescription().getAttackPower() * config.attackPowerMultiplier;
        points[i] += 1 * config.pieceMultiplier;
        for (int j = 0; j < teams.length; j++) {
          if (j == i || teams[j] == null) {
            continue;
          }
          // reward being close to enemy base
          points[i] +=
              (this.maxDistance - distanceToBase(teams[j].getBase(), p.getPosition()))
                  * config.distanceBaseMultiplier;
        }

        if (p.getDescription().getMovement().getDirections() != null) {
          for (int dir = 0; dir < 8; dir++)
            points[i] +=
                MCTSUtilities.getReach(p.getDescription().getMovement().getDirections(), dir)
                    * config.directionMultiplier;
        } else {
          points[i] += 8 * config.shapeReachMultiplier;
        }
      }

      /*for(int j=0; j<teams.length; j++) {
        if(j == i)
          continue;
        for(Piece ep : teams[j].getPieces()) {
          //punish enemy being close to ones own base
          points[i] -= (this.maxDistance - Math.sqrt(Math.pow(teams[i].getBase()[1]-ep.getPosition()[1], 2)
              + Math.pow(teams[i].getBase()[0]-ep.getPosition()[0], 2))) * Constants.distanceBaseMultiplier * 10;
        }
      }*/

      points[i] += teams[i].getFlags() * config.flagMultiplier;
    }

    int max = 0;
    for (int i = 0; i < points.length; i++) if (points[i] > points[max]) max = i;

    return max;
  }

  /**
   * Calculates the euclidean distance between two 2D positions
   *
   * @param base
   * @param piece
   * @return distance between base and piece
   */
  float distanceToBase(int[] base, int[] piece) {
    return (float) Math.sqrt(Math.pow(base[1] - piece[1], 2) + Math.pow(base[0] - piece[0], 2));
  }

  /**
   * picks a (random) move to make a move on
   *
   * @return a random chosen move out of an ArrayList containing possible moves.
   */
  int[] pickRandomMove(ArrayList<int[]> moveList) {
    return moveList.get(ThreadLocalRandom.current().nextInt(moveList.size()));
  }

  /**
   * Propagates the simulation result up the tree until the root element is reached.
   *
   * @param node on which the simulation was executed
   * @param an int Array containing as many spaces as teams are left, a place in the Array
   *     corresponds to the teamId and contains the number of wins of that team.
   */
  void backpropagate(TreeNode child, int[] wins) {
    while (child != null) {
      for (int i = 0; i < wins.length; i++) {
        child.getWins()[i] += wins[i];
      }
      child = child.getParent();
    }
  }

  /**
   * Checks if a game is in a terminal state.
   *
   * @param a node to check if it is terminal
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return -1: the game is not in a terminal state 0 - Integer.MAX_VALUE: winner team id -2: error
   */
  public int isTerminal(ReferenceGameState gameState, ReferenceMove change) {
    int teamsLeft = 0;
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if (gameState.getTeams()[i] != null) {
        teamsLeft++;
      }
    }

    for(int i= gameState.getCurrentTeam();
        teamsLeft > 1; 
        i = MCTSUtilities.toNextTeam(gameState).getCurrentTeam()) {
      
      boolean canMove = false;
      for (int j = 0; !canMove && j < gameState.getTeams()[i].getPieces().size(); j++) {
        // only if a move can be made no exception is thrown
        try {
          MCTSUtilities.pickMoveComplex(gameState, change);
          canMove = true;
        } catch (Exception e) {
        }
      }
      if (canMove) {
        return -1;
      } else if (!canMove) {
        MCTSUtilities.removeTeam(gameState, i);
        teamsLeft--;
      }
    }

    if (teamsLeft <= 1) {
      for (Team team : gameState.getTeams())
        if (team != null) return Integer.parseInt(team.getId());
      return -2;
    }
    return -1;
  }

  /**
   * Checks if all possible children from a specific node are expanded
   *
   * @param parent node
   * @return true if all children are expanded
   */
  boolean isFullyExpanded(TreeNode parent) {
    return parent.getPossibleMoves().keySet().size() == 0;
  }

  /**
   * checks all the parents children for their UCT value, returns the node with the highest value.
   *
   * @param parent node
   * @param C value for UCT
   * @return the child node with the highest UCT value
   */
  TreeNode bestChild(TreeNode parent, double c) {
    double uctCurrent;
    double uctMax = 0;
    TreeNode bestChild = null;

    if(influencer != null) {
      if(Integer.parseInt(influencer.getTeamId()) == (parent.getReferenceGameState().getCurrentTeam()))
        if(Math.random() * 100 < chooseMovePercentage) // dont pick influencer EVERY time
          for(TreeNode node : parent.getChildren()) 
            if(GameUtilities.moveEquals(influencer, node.getReferenceGameState().getLastMove().toMove())) {
              return node;
            }
    }

    for (int i = 0; i < parent.getChildren().length; i++) {
      if (parent.getChildren()[i] == null) continue;

      uctCurrent = parent.getChildren()[i].getUCT(c);

      if (uctCurrent >= uctMax) {
        uctMax = uctCurrent;
        bestChild = parent.getChildren()[i];
      }
    }
    return bestChild;
  }

  /**
   * Checks all children from a given node for the best move, assuming all nodes are expanded and
   * already simulated.
   *
   * @param a node where all children will be checked for the best move
   * @return the TreeNode containing the best Move
   */
  TreeNode getRootBest(TreeNode root) {
    TreeNode bestChild = root.getChildren()[0];

    for (int i = 0; i < root.getChildren().length; i++) {
      if (root.getChildren()[i] != null && root.getChildren()[i] == bestChild(root, 0))
        bestChild = root.getChildren()[i];
    }

    return bestChild;
  }

  /**
   * Simulates one move and returns a new node containing the new state.
   *
   * @param alter node, this nodes ReferenceGameState is altered
   * @param original node, the move made gets removed from it
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return a child node containing the simulation result
   */
  void oneMove(TreeNode alter, TreeNode original, boolean simulate, ReferenceMove change) {
    if(!simulate) {
    alterGameState(alter.getReferenceGameState(), new ReferenceMove(alter.getReferenceGameState(), getAndRemoveMoveHeuristic(original)));
    alter.initPossibleMovesAndChildren();
    }
    else {
      try {
        alterGameState(alter.getReferenceGameState(), MCTSUtilities.pickMoveComplex(alter.getReferenceGameState(), change));
//        alterGameState(alter.gameState, MCTSUtilities.pickMoveSimple(alter.gameState, change));
      } catch (NoMovesLeftException | InvalidShapeException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * A nodes possible moves get checked, the first one to fit the heuristic gets returned. The
   * heuristic does not check all moves if a move fits the heuristic. If no move fits the heuristic,
   * a random one gets returned.
   *
   * @param parent
   * @return possible move
   */
  Move getAndRemoveMoveHeuristic(TreeNode parent) {
    for (Piece piece : parent.getPossibleMoves().keySet()) {
      for (int i = 0; i < parent.getPossibleMoves().get(piece).size(); i++) {
        int[] pos = parent.getPossibleMoves().get(piece).get(i);
        if (MCTSUtilities.emptyField(parent.getReferenceGameState().getGrid(), pos)) continue;
        if (MCTSUtilities.otherTeamsBase(parent.getReferenceGameState().getGrid(), pos, piece.getPosition())) {
          return createMoveDeleteIndex(parent, piece, i);
        }
        if (MCTSUtilities.occupiedByWeakerOpponent(parent.getReferenceGameState().getGrid().getPosition(pos[1], pos[0]).getPiece(), piece)) {
          return createMoveDeleteIndex(parent, piece, i);
        }
      }
    }

    return getAndRemoveMoveRandom(parent);
  }

  /**
   * Returns a valid random move from a TreeNode. Removes the move from the nodes possibleMoves.
   *
   * @param parent
   * @return random move
   */
  Move getAndRemoveMoveRandom(TreeNode parent) {
    Piece key =
        (Piece)
            parent.getPossibleMoves().keySet()
                .toArray()[ThreadLocalRandom.current().nextInt(parent.getPossibleMoves().keySet().size())];
    int randomMove = ThreadLocalRandom.current().nextInt(parent.getPossibleMoves().get(key).size());

    return createMoveDeleteIndex(parent, key, randomMove);
  }

  /**
   * Removes a move from the parents possibleMoves, returns that move.
   *
   * @param parent
   * @param key
   * @param index in parent.possibleMoves
   * @return the move made
   */
  Move createMoveDeleteIndex(TreeNode parent, Piece key, int index) {
    Move move = new Move();
    move.setPieceId(key.getId());
    move.setNewPosition(parent.getPossibleMoves().get(key).get(index));

    parent.getPossibleMoves().get(key).remove(index);
    if (parent.getPossibleMoves().get(key).size() <= 0) {
      parent.getPossibleMoves().remove(key);
    }

    return move;
  }

  /**
   * This method checks if a team got no more flags or no more pieces.
   *
   * @param gameState
   */
  public void removeTeamCheck(ReferenceGameState gameState) {
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if (gameState.getTeams()[i] == null) continue;
      if (gameState.getTeams()[i].getFlags() == 0
          || gameState.getTeams()[i].getPieces().size() == 0) {
        MCTSUtilities.removeTeam(gameState, i--);
      }
    }
    if(gameState.getTeams()[gameState.getCurrentTeam()] == null)
      MCTSUtilities.toNextTeam(gameState);
  }


  /**
   * A given GameState is updated with a given move.
   * This method updates a gameState and the grid with a given move.
   * A given Piece moves to a new position, capturing a flag, piece or an empty field.
   * @param gameState
   * @param move
   */
  public void alterGameState(ReferenceGameState gameState, ReferenceMove move) {
    GridObjectContainer occupant = gameState.getGrid().getPosition(move.getNewPosition()[1], move.getNewPosition()[0]);
    Piece picked = move.getPiece();
    gameState.getGrid().setPosition(null, picked.getPosition()[1], picked.getPosition()[0]);
    if(occupant == null) {
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), move.getNewPosition()[1], move.getNewPosition()[0]);
      picked.setPosition(move.getNewPosition());
    } else if(occupant.getObject() == GridObjects.piece) {
      gameState.getTeams()[occupant.getTeamId()].getPieces().remove(occupant.getPiece());
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), move.getNewPosition()[1], move.getNewPosition()[0]);
      picked.setPosition(move.getNewPosition());
    } else {
      gameState.getTeams()[occupant.getTeamId()].setFlags(gameState.getTeams()[occupant.getTeamId()].getFlags() -1);
      picked.setPosition(MCTSUtilities.respawnPiecePosition(gameState.getGrid(), gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), picked.getPosition()[1], picked.getPosition()[0]);
    }
    gameState.setLastMove(move);
    MCTSUtilities.toNextTeam(gameState);
  }

  /**
   * prints some important values to the console
   *
   * @param best move chosen by getRootBest() method
   */
  public String printResults(Move move) {
//    this.root.printGrid();
    StringBuilder sb = new StringBuilder();
    sb.append(
        "Piece "
            + move.getPieceId()
            + " moves to "
            + move.getNewPosition()[0]
            + ","
            + move.getNewPosition()[1]);
    sb.append(
        "\nNodes expanded: "
            + getExpansionCounter()
            + ", simulations till the end: "
            + simulationCounter
            + ", heuristic used: "
            + heuristicCounter);
    sb.append("\nBest children:");
    // if not all children are expanded they cannot be sorted.
    try {
      Arrays.sort(getRoot().getChildren());
    } catch (NullPointerException npe) {
    }
    ;
    int n = 5;
    for (int i = 0; i < (getRoot().getChildren().length > n ? n : getRoot().getChildren().length); i++) {
      if (getRoot().getChildren()[i] == null) {
        n += 1;
        continue;
      }
      Move rootMove = getRoot().getChildren()[i].getReferenceGameState().getLastMove().toMove();
      sb.append(
          "\n   "
              + rootMove.getPieceId()
              + " to ["
              + rootMove.getNewPosition()[0]
              + ","
              + rootMove.getNewPosition()[1]
              + "]"
              + " winning chance: "
              + (getRoot().getChildren()[i].getV() * 100)
              + "% with "
              + getRoot().getChildren()[i].getNK()
              + " nodes"
              + ", uct: "
              + getRoot().getChildren()[i].getUCT(config.C)
              + " wins 0 "
              + getRoot().getChildren()[i].getWins()[0]
              + ", wins 1 "
              + getRoot().getChildren()[i].getWins()[1]);
    }
    return sb.toString();
  }

  @Override
  public TreeNode getRoot() {
    return root;
  }

  @Override
  public void setRoot(MonteCarloTreeNode root) {
    this.root = (TreeNode)root;
  }

  @Override
  public AtomicInteger getExpansionCounter() {
    return expansionCounter;
  }

  @Override
  public void setExpansionCounter(int expansionCounter) {
    this.expansionCounter.set(expansionCounter);
  }

  @Override
  public AtomicInteger getHeuristicCounter() {
    return heuristicCounter;
  }

  @Override
  public void setHeuristicCounter(int heuristicCounter) {
    this.heuristicCounter.set(heuristicCounter);
  }

  @Override
  public AtomicInteger getSimulationCounter() {
    return simulationCounter;
  }

  @Override
  public void setSimulationCounter(int simulationCounter) {
    this.simulationCounter.set(simulationCounter);
  }
}
