package org.ctf.shared.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

/**
 * @author sistumpf
 */
public class MCTS {
  AIConfig config;
  static int count;
  int teams;
  int maxDistance;
  public TreeNode root;
  public AtomicInteger simulationCounter;
  public AtomicInteger heuristicCounter;
  public AtomicInteger expansionCounter;

  public MCTS(TreeNode root, AIConfig config) {
    this.config = config;
    this.root = root;
//    this.rand = new Random();
    simulationCounter = new AtomicInteger();
    heuristicCounter = new AtomicInteger();
    expansionCounter = new AtomicInteger();
    this.teams = root.gameState.getTeams().length;
    this.maxDistance =
        (int)
            Math.round(
                Math.sqrt(
                    Math.pow(root.gameState.getGrid().length, 2)
                        + Math.pow(root.gameState.getGrid()[0].length, 2)));
  }

  /**
   * Starts a Monte Carlo Tree Search from a given state of the game, if the given time runs out the
   * best calculated move is returned.
   *
   * @param time in milliseconds the algorithm is allowed to take
   * @param Constant C used in the UCT formula
   * @return the algorithms choice for the best move
   */
  public Move getMove(int milis, double C) {
    long time = System.currentTimeMillis();

    while (System.currentTimeMillis() - time < milis) {
      // Schritte des UCT abarbeiten
      TreeNode selected = selectAndExpand(root, C);
      backpropagate(selected, simulate(selected));
    }

    TreeNode bestChild = getRootBest(root);

    // Hier werden wichtige Daten zur Auswahl ausgegeben
    //      printResults(bestChild);

    return (bestChild.gameState.getLastMove());
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
    while (isTerminal(node.gameState, node.operateOn) == -1) {
      if (!isFullyExpanded(node)) {
        expansionCounter.incrementAndGet();
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
    for (int i = 0; i < parent.children.length; i++) {
      if (parent.children[i] == null) {
        TreeNode child = parent.clone(parent.copyGameState());
        oneMove(child, parent, false, child.operateOn);
        parent.children[i] = child;
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
    int isTerminal = isTerminal(simulateOn.gameState, change);
    int[] winners = new int[this.teams];
    int count = config.MAX_STEPS;
    if (isTerminal >= 0) {
      winners[isTerminal] += count;
      return winners;
    }

    simulateOn = simulateOn.clone(simulateOn.copyGameState());
    simulateOn.operateOn = change;

    for (; count > 0 && isTerminal == -1; count--, isTerminal = isTerminal(simulateOn.gameState, change)) {
      oneMove(simulateOn, simulateOn, true, change);
      removeTeamCheck(simulateOn.gameState);
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
    Team[] teams = node.gameState.getTeams();
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
                GameUtilities.getReach(p.getDescription().getMovement().getDirections(), dir)
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
        child.wins[i] += wins[i];
      }
      child = child.parent;
    }
  }

  /**
   * Checks if a game is in a terminal state.
   *
   * @param a node to check if it is terminal
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return -1: the game is not in a terminal state 0 - Integer.MAX_VALUE: winner team id -2: error
   */
  public int isTerminal(GameState gameState, ReferenceMove change) {
    int teamsLeft = 0;
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if (gameState.getTeams()[i] != null) {
        teamsLeft++;
      }
    }

    for (int i = gameState.getCurrentTeam();
        teamsLeft > 1;
        i = GameUtilities.toNextTeam(gameState).getCurrentTeam()) {
      boolean canMove = false;
      for (int j = 0; !canMove && j < gameState.getTeams()[i].getPieces().length; j++) {
        // only if a move can be made no exception is thrown
        try {
          RandomAI.pickMoveComplex(gameState, change);
          canMove = true;
        } catch (Exception e) {
        }
      }
      if (canMove) {
        return -1;
      } else if (!canMove) {
        GameUtilities.removeTeam(gameState, i);
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
    return parent.possibleMoves.keySet().size() == 0;
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

    for (int i = 0; i < parent.children.length; i++) {
      if (parent.children[i] == null) continue;

      uctCurrent = parent.children[i].getUCT(c);

      if (uctCurrent >= uctMax) {
        uctMax = uctCurrent;
        bestChild = parent.children[i];
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
    TreeNode bestChild = null;

    for (int i = 0; i < root.children.length; i++) {
      if (root.children[i] != null && root.children[i] == bestChild(root, 0))
        bestChild = root.children[i];
    }

    return bestChild;
  }

  /**
   * Simulates one move and returns a new node containing the new state.
   *
   * @param alter node, this nodes GameState is altered
   * @param original node, the move made gets removed from it
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return a child node containing the simulation result
   */
  void oneMove(TreeNode alter, TreeNode original, boolean simulate, ReferenceMove change) {
    if(!simulate) {
    alterGameState(alter.gameState, new ReferenceMove(alter.gameState, getAndRemoveMoveHeuristic(original)));
    alter.initPossibleMovesAndChildren();
    }
    else {
      try {
        alterGameState(alter.gameState, RandomAI.pickMoveComplex(alter.gameState, change));
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
    for (Piece piece : parent.possibleMoves.keySet()) {
      for (int i = 0; i < parent.possibleMoves.get(piece).size(); i++) {
        int[] pos = parent.possibleMoves.get(piece).get(i);
        if (GameUtilities.emptyField(parent.gameState.getGrid(), pos)) continue;
        if (GameUtilities.otherTeamsBase(parent.gameState.getGrid(), pos, piece)) {
          return createMoveDeleteIndex(parent, piece, i);
        }
        if (GameUtilities.occupiedByWeakerOpponent(parent.gameState, pos, piece)) {
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
            parent.possibleMoves.keySet()
                .toArray()[ThreadLocalRandom.current().nextInt(parent.possibleMoves.keySet().size())];
    int randomMove = ThreadLocalRandom.current().nextInt(parent.possibleMoves.get(key).size());

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
    move.setNewPosition(parent.possibleMoves.get(key).get(index));

    parent.possibleMoves.get(key).remove(index);
    if (parent.possibleMoves.get(key).size() <= 0) {
      parent.possibleMoves.remove(key);
    }

    return move;
  }

  /**
   * This method checks if a team got no more flags or no more pieces.
   *
   * @param gameState
   */
  public void removeTeamCheck(GameState gameState) {
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if (gameState.getTeams()[i] == null) continue;
      if (gameState.getTeams()[i].getFlags() == 0
          || gameState.getTeams()[i].getPieces().length == 0) {
        GameUtilities.removeTeam(gameState, i--);
      }
    }
  }

  /**
   * A given GameState is updated with a given move. This method is pretty much a replica of the
   * makeMove method from the server.
   *
   * @param gameState
   * @param move
   */
  public void alterGameState(GameState gameState, ReferenceMove move) {
    String occupant = gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]];
    Piece picked = move.getPiece();
    int[] oldPos = picked.getPosition();

    gameState.getGrid()[oldPos[0]][oldPos[1]] = "";

    if (occupant.contains("p:")) {
      int occupantTeam = GameUtilities.getOccupantTeam(gameState.getGrid(), move.getNewPosition());
      gameState.getTeams()[occupantTeam].setPieces(
          Arrays.asList(gameState.getTeams()[occupantTeam].getPieces()).stream()
              .filter(p -> !p.getId().equals(occupant))
              .toArray(Piece[]::new));
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPiece().getId();
      picked.setPosition(move.getNewPosition());
    } else if (occupant.contains("b:")) {
      int occupantTeam = GameUtilities.getOccupantTeam(gameState.getGrid(), move.getNewPosition());
      gameState.getTeams()[occupantTeam].setFlags(
          gameState.getTeams()[occupantTeam].getFlags() - 1);
      picked.setPosition(
          GameUtilities.respawnPiecePosition(
              gameState, gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid()[picked.getPosition()[0]][picked.getPosition()[1]] = picked.getId();
    } else {
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPiece().getId();
      picked.setPosition(move.getNewPosition());
    }
    gameState.setLastMove(move.toMove());
    GameUtilities.toNextTeam(gameState);
  }

  /**
   * prints some important values to the console
   *
   * @param best move chosen by getRootBest() method
   */
  public String printResults(Move move) {
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
            + expansionCounter
            + ", simulations till the end: "
            + simulationCounter
            + ", heuristic used: "
            + heuristicCounter);
    sb.append("\nBest children:");
    // if not all children are expanded they cannot be sorted.
    try {
      Arrays.sort(root.children);
    } catch (NullPointerException npe) {
    }
    ;
    int n = 5;
    for (int i = 0; i < (root.children.length > n ? n : root.children.length); i++) {
      if (root.children[i] == null) {
        n += 1;
        continue;
      }
      Move rootMove = root.children[i].gameState.getLastMove();
      sb.append(
          "\n   "
              + rootMove.getPieceId()
              + " to ["
              + rootMove.getNewPosition()[0]
              + ","
              + rootMove.getNewPosition()[1]
              + "]"
              + " winning chance: "
              + (root.children[i].getV() * 100)
              + "% with "
              + root.children[i].getNK()
              + " nodes"
              + ", uct: "
              + root.children[i].getUCT(config.C)
              + " wins 0 "
              + root.children[i].wins[0]
              + ", wins 1 "
              + root.children[i].wins[1]);
    }
    return sb.toString();
  }
}
