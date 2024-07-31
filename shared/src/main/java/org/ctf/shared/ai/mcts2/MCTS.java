package org.ctf.shared.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;

/**
 * @author sistumpf
 */
public class MCTS implements MonteCarloTreeSearch {
  private AIConfig config;
  private Random rand;
  private int teams;
  private int maxDistance;
  private TreeNode root;
  private ExecutorService executorService;
  private AtomicInteger simulationCounter;
  private AtomicInteger heuristicCounter;
  private AtomicInteger expansionCounter;
  private Move influencer;
  private boolean allowedToRun;
  
  public MCTS(TreeNode root, AIConfig config) {
    this.allowedToRun = true;
    this.config = config;
    this.setRoot(root);
    this.rand = new Random();
    simulationCounter = new AtomicInteger();
    heuristicCounter = new AtomicInteger();
    this.expansionCounter = new AtomicInteger();
    this.teams = root.getReferenceGameState().getTeams().length;
    this.maxDistance = (int)Math.round(Math.sqrt(Math.pow(root.getReferenceGameState().getGrid().getGrid().length, 2) + Math.pow(root.getReferenceGameState().getGrid().getGrid()[0].length, 2)));
    this.executorService  = Executors.newFixedThreadPool(config.numThreads);
  }

  @Override
  public Move getMove(int milis){
    long time = System.currentTimeMillis();

    while(System.currentTimeMillis() - time < milis && allowedToRun){
      //Schritte des UCT abarbeiten
      if(getRoot().getReferenceGameState().getLastMove() == null)
        getRoot().getReferenceGameState().setLastMove(new ReferenceMove(null, new int[2]));
      TreeNode selected = selectAndExpand(getRoot(), config.C);
      backpropagate(selected, simulate(selected));
    }

    TreeNode bestChild = getRootBest(getRoot());

    // Hier werden wichtige Daten zur Auswahl ausgegeben 
    //      printResults(bestChild);

    this.executorService.shutdown();
    
    if(isTerminal(getRoot().getReferenceGameState()) >= 0 || 
        isTerminal(getRoot().getReferenceGameState()) == -2)
      return null;
    
    influencer = null;
    return bestChild.getReferenceGameState().getLastMove().toMove();
  }

  @Override
  public Move getMove(Move influencer, int milis) {
    this.influencer = influencer;
    return getMove(milis);
  }
  
  @Override
  public void shutdown() {
    this.allowedToRun = false;
  }
  
  /**
   * Selects a node to simulate on using the UCT formula.
   * expands a children if a node in the chain has unexpanded ones.
   * @param parent node, from it on the nodes will be checked for one to simulate on
   * @param constant C used in UCT formula
   * @return the node to simulate on
   */
  TreeNode selectAndExpand(TreeNode node, double C){
    while(isTerminal(node.getReferenceGameState()) == -1) {      
      if(!isFullyExpanded(node)){
        this.expansionCounter.incrementAndGet();
        return expand(node);
      } else {
        node = bestChild(node, C);
      }
    }
    return node;
  }


  /**
   * Adds one child to the parent node, the child is identical to the parent but simulated one move further.
   * @param the selected node which gets expanded
   * @return the new child
   * @return null if anything unforeseen happens
   */
  TreeNode expand(TreeNode parent){
    for(int i=0; i<parent.getChildren().length; i++) {
      if(parent.getChildren()[i] == null) {
        TreeNode child = parent.clone(parent.getReferenceGameState().clone());
//        child.initPossibleMovesAndChildren();
        oneMove(child, parent, false);
        parent.getChildren()[i] = child;
        return child;
      }
    }
    return null;
  }

  /**
   * Simulates a certain amount of simulations simultaneously.
   * @param simulateOn
   * @return an array containing a number of wins for the team at position teamId
   */
  int[] multiSimulate(TreeNode simulateOn) { 
    int[] winners = new int[simulateOn.getReferenceGameState().getTeams().length];

    try {
      // Create a list of Callable tasks for parallel execution
      List<Callable<int[]>> tasks = new LinkedList<>();
      for (int i = 0; i < config.numThreads; i++) {
        tasks.add(() -> {
          return simulate(simulateOn);
        });
      }

      // Submit tasks for parallel execution
      List<Future<int[]>> futures = executorService.invokeAll(tasks);
      for(int i=0; i<futures.size(); i++) {
        try {
          int[] wins = futures.get(i).get();
          for(int j=0; j<wins.length; j++) {
            winners[j] += wins[j];
          }
        } catch(Exception e) {}
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return winners;
  }


  /**
   * Simulates a game from a specific node to finish (or a maximum step value of Constants.MAX_STEPS simulation),
   * First checks if a node is in a terminal state, if thats the case the simulation ends and the result is returned
   * @param the node from which a game is going to be simulated
   * @return an array containing a number of wins for the team at position teamId
   */
  int[] simulate(TreeNode simulateOn){      
    getSimulationCounter().incrementAndGet();
    int isTerminal = isTerminal(simulateOn.getReferenceGameState());
    int[] winners = new int[this.teams];
    int count = config.MAX_STEPS;
    if(isTerminal >= 0) {
      winners[isTerminal] += count;
      return winners;
    }
    

    simulateOn = simulateOn.clone(simulateOn.getReferenceGameState().clone());

    for(;count > 0 && isTerminal == -1; count--, isTerminal = isTerminal(simulateOn.getReferenceGameState())) {
      oneMove(simulateOn, simulateOn, true);
    }
    if(isTerminal < 0) {  
      getSimulationCounter().decrementAndGet();
      getHeuristicCounter().incrementAndGet();
      winners[terminalHeuristic(simulateOn)] += 1;
    } else {
      winners[isTerminal] += count;
    }

    return winners;
  }


  /**
   * A heuristic to evaluate the winner of a given nodes gameState.
   * @param a node which will be analyzed
   * @return the best teams teamId (as an int)
   */
  int terminalHeuristic(TreeNode node) {
    Team[] teams = node.getReferenceGameState().getTeams();
    int[] points = new int[teams.length];

    for(int i=0; i<teams.length; i++) {
      if(teams[i] == null)
        continue;

      for(Piece p : teams[i].getPieces()) {
        points[i] += p.getDescription().getAttackPower() * config.attackPowerMultiplier;
        points[i] += 1 * config.pieceMultiplier;
        for(int j=0; j<teams.length; j++) {
          if(j == i || teams[j] == null) {
            continue;
          }
          //reward being close to enemy base
          points[i] += (this.maxDistance - distanceToBase(teams[j].getBase(), p.getPosition())) * config.distanceBaseMultiplier;
        }

        if(p.getDescription().getMovement().getDirections() != null) {
          for(int dir=0; dir<8; dir++)
            points[i] += MCTSUtilities.getReach(p.getDescription().getMovement().getDirections(), dir) * config.directionMultiplier;
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
    for(int i=0; i<points.length; i++)
      if(points[i] > points[max])
        max = i;

    return max;
  }

  /**
   * Calculates the euclidean distance between two 2D positions
   * @param base
   * @param piece
   * @return distance between base and piece
   */
  float distanceToBase(int[] base, int[] piece) {
    return (float) Math.sqrt(Math.pow(base[1]-piece[1], 2) + Math.pow(base[0]-piece[0], 2));
  }


  /**
   * Picks a (random) move.
   * @return a random chosen move out of an ArrayList containing possible moves.
   */
  int[] pickRandomMove(ArrayList<int[]> moveList) {
    return moveList.get(rand.nextInt(moveList.size()));
  }


  /**
   * Propagates the simulation result up the tree until the root element is reached.
   * @param node on which the simulation was executed
   * @param an int Array containing as many spaces as teams are left, a place in the Array corresponds to the teamId and contains the number of wins of that team.
   */
  void backpropagate(TreeNode child, int[] wins){
    while(child != null) {
      for(int i = 0; i<wins.length; i++) {
        child.getWins()[i] += wins[i];
      }
      child = child.getParent();
    }
  }


  /**
   * Checks if a game is in a terminal state.
   * 
   * @param a node to check if it is terminal
   * @return -1: the game is not in a terminal state
   *         0 - Integer.MAX_VALUE: winner team id
   *         -2: error
   */
  int isTerminal(ReferenceGameState gameState) {
    int teamsLeft = 0;
    removeTeamCheck(gameState);
    for(int i=0; i<gameState.getTeams().length; i++) {
      if(gameState.getTeams()[i] != null) {
        teamsLeft++;
      }
    }

    for(int i=gameState.getCurrentTeam(); teamsLeft > 1; i = MCTSUtilities.toNextTeam(gameState).getCurrentTeam()) {
      boolean canMove = false;
      for(int j=0; !canMove && j<gameState.getTeams()[i].getPieces().length; j++) {
        if(gameState.getTeams()[i].getFlags() < 1)
          continue;
        //only if a move can be made no exception is thrown
        try {
          MCTSUtilities.pickMoveComplex(gameState);
          canMove = true;
        } catch (Exception e) {} 
      }
      if(canMove) {
        return -1;
      } else if (!canMove){
        MCTSUtilities.removeTeam(gameState, i);
        teamsLeft--;
      }
    }

    if(teamsLeft <= 1) {
      for(Team team : gameState.getTeams())
        if(team != null)
          return Integer.parseInt(team.getId());
      return -2;
    }
    return -1;
  }

  /**
   * Checks if all possible children from a specific node are expanded.
   * @param parent node
   * @return true if all children are expanded
   */
  boolean isFullyExpanded(TreeNode parent) {
    return parent.getPossibleMoves().keySet().size() == 0;
  }


  /**
   * Checks all the parents children for their UCT value, returns the node with the highest value.
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
    
    for(int i=0; i<parent.getChildren().length; i++) {
      if(parent.getChildren()[i] == null)
        continue;

      uctCurrent = parent.getChildren()[i].getUCT(c);

      if(uctCurrent >= uctMax) {
        uctMax = uctCurrent;
        bestChild = parent.getChildren()[i];
      }
    }
    return bestChild;
  }

  /**
   * Checks all children from a given node for the best move, assuming all nodes are expanded and already simulated.
   * @param a node where all children will be checked for the best move
   * @return the TreeNode containing the best Move
   */
  TreeNode getRootBest(TreeNode root) {
    TreeNode bestChild = null;

    for(int i=0; i < root.getChildren().length; i++) {
      if(root.getChildren()[i] != null && root.getChildren()[i] == bestChild(root, 0))
        bestChild = root.getChildren()[i];
    }

    return bestChild;
  }


  /**
   * Simulates one move and returns a new node containing the new state.
   * @param alter node, this nodes GameState is altered
   * @param original node, the move made gets removed from it
   * @param if this is a simulation or expansion. If it's a simulation there are optimizations in TreeNode.
   * @return a child node containing the simulation result
   */
  void oneMove(TreeNode alter, TreeNode original, boolean simulate) {
//    alter.printGrids();
    if(simulate) {
      ReferenceMove move = getAndRemoveMoveHeuristicFromGrid(original);
      HashSet<Piece> updateThese = new HashSet<Piece>();
      Piece center = move.getPiece();
      int[] oldPos = center.getPosition();
      updateThese.add(center);
      MCTSUtilities.putNeighbouringPieces(updateThese, alter.getReferenceGameState().getGrid(), oldPos);
      alterGameStateAndGrid(alter.getReferenceGameState(), move);
      MCTSUtilities.putNeighbouringPieces(updateThese, alter.getReferenceGameState().getGrid(), center.getPosition());
//      System.out.println(move.getPiece().getId() + " moves to " + move.getNewPosition()[0] + "-" + move.getNewPosition()[1]);
      alter.updateGrids(updateThese);
//      alter.printGrids();
      
      //TODO putNeighbouringPieces methode entfernen, alte und neue position und piece übergeben, dann in treenode berechnen.
      
    } else {
      ReferenceMove move = getAndRemoveMoveHeuristic(original);
      try {
      move.setPiece(alter.getReferenceGameState().getGrid().getGrid()[move.getPiece().getPosition()[0]][move.getPiece().getPosition()[1]].getPiece());
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      alterGameStateAndGrid(alter.getReferenceGameState(), move);
      alter.initPossibleMovesAndChildren();
    }
  }   

  /**
   * A nodes possible moves get checked, the first one to fit the heuristic gets returned.
   * The heuristic does not check all moves if a move fits the heuristic.
   * If no move fits the heuristic, a random one gets returned.
   * @param parent
   * @return possible move
   */
  ReferenceMove getAndRemoveMoveHeuristicFromGrid(TreeNode parent) {
    for(Piece piece : parent.getReferenceGameState().getTeams()[parent.getReferenceGameState().getCurrentTeam()].getPieces()) {
      if(parent.getReferenceGameState().getGrid().getPieceVisions().get(piece) == null)
        System.out.println("weird null pointer in getAndRemove");
      for(int i=0; i<parent.getReferenceGameState().getGrid().getPieceVisions().get(piece).size(); i++) {
        int[] pos = parent.getReferenceGameState().getGrid().getPieceVisions().get(piece).get(i);
        if(MCTSUtilities.emptyField(parent.getReferenceGameState().getGrid(), pos)) {
          continue;
        }
        if(MCTSUtilities.otherTeamsBase(parent.getReferenceGameState().getGrid(), pos, piece.getPosition())) {
          if(ThreadLocalRandom.current().nextInt(10) != 3)  //90% chance to take a flag
            return new ReferenceMove(piece, pos);
        }
        if(!MCTSUtilities.occupiedBySameTeam(parent.getReferenceGameState(), piece.getPosition(), pos)
            && MCTSUtilities.occupiedByWeakerOpponent(parent.getReferenceGameState().getGrid().getPosition(pos[1], pos[0]).getPiece(), piece)) {
          if(ThreadLocalRandom.current().nextInt(2) < 1)  //50% chance to take a piece
            return new ReferenceMove(piece, pos);
        }
      }
    }
    return getRandomFromGrid(parent);
  }
  
  /**
   * Returns a valid random move from a TreeNode.
   * @param parent
   * @return random move
   */
  ReferenceMove getRandomFromGrid(TreeNode parent){
    ReferenceMove move = null;
    
    try {
      move = MCTSUtilities.pickMoveComplex(parent.getReferenceGameState());
    } catch (NoMovesLeftException e) {
      e.printStackTrace();
    } catch (InvalidShapeException e) {
      e.printStackTrace();
    }
    
    return move;
  }

  
  
  /**
   * A nodes possible moves get checked, the first one to fit the heuristic gets returned.
   * The heuristic does not check all moves if a move fits the heuristic.
   * If no move fits the heuristic, a random one gets returned.
   * The move gets removed from the parents possibleMoves.
   * @param parent
   * @return valid move
   */
  ReferenceMove getAndRemoveMoveHeuristic(TreeNode parent) {
    for(Piece piece : parent.getPossibleMoves().keySet()) {
      for(int i=0; i<parent.getPossibleMoves().get(piece).size(); i++) {
        int[] pos = parent.getPossibleMoves().get(piece).get(i);
        if(MCTSUtilities.emptyField(parent.getReferenceGameState().getGrid(), pos)) {
          continue;
        }
        if(MCTSUtilities.otherTeamsBase(parent.getReferenceGameState().getGrid(), pos, piece.getPosition())) {
          return createMoveDeleteIndex(parent, piece, i);
        }
        if(MCTSUtilities.occupiedByWeakerOpponent(parent.getReferenceGameState().getGrid().getPosition(pos[1], pos[0]).getPiece(), piece)) {
          return createMoveDeleteIndex(parent, piece, i);
        }
      }
    }

    return getAndRemoveMoveRandom(parent);
  }
  
  /**
   * Returns a valid random move from a TreeNode, removes it from the nodes possibleMoves.
   * @param parent
   * @return random move
   */
  ReferenceMove getAndRemoveMoveRandom(TreeNode parent){
    Piece key = (Piece)parent.getPossibleMoves().keySet().toArray()[rand.nextInt(parent.getPossibleMoves().keySet().size())];
    int randomMove = rand.nextInt(parent.getPossibleMoves().get(key).size());
    return createMoveDeleteIndex(parent, key, randomMove);
  }

  ReferenceMove createMoveDeleteIndex(TreeNode parent, Piece key, int index) {
    ReferenceMove move = new ReferenceMove(key, parent.getPossibleMoves().get(key).get(index));

    parent.getPossibleMoves().get(key).remove(index);
    if(parent.getPossibleMoves().get(key).size() <= 0) {
      parent.getPossibleMoves().remove(key);
    }

    return move;
  }

  /**
   * This method checks if a team got no more flags or no more pieces.
   * @param gameState
   */
  void removeTeamCheck(ReferenceGameState gameState) {
    for(int i=0; i<gameState.getTeams().length; i++) {
      if(gameState.getTeams()[i] == null) continue;
      if(gameState.getTeams()[i].getFlags() == 0 ||
          gameState.getTeams()[i].getPieces().length == 0) {
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
  public void alterGameStateAndGrid(ReferenceGameState gameState, ReferenceMove move) {
    GridObjectContainer occupant = gameState.getGrid().getPosition(move.getNewPosition()[1], move.getNewPosition()[0]);
    Piece picked = move.getPiece();
    gameState.getGrid().setPosition(null, picked.getPosition()[1], picked.getPosition()[0]);
    if(occupant == null) {
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), move.getNewPosition()[1], move.getNewPosition()[0]);
      picked.setPosition(move.getNewPosition());
    } else if(occupant.getObject() == GridObjects.piece) {
      gameState.getTeams()[occupant.getTeamId()].setPieces(
          Arrays.asList(gameState.getTeams()[occupant.getTeamId()].getPieces()).stream()
          .filter(p -> !p.getId().equals(occupant.getPiece().getId()))
          .toArray(Piece[]::new));
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), move.getNewPosition()[1], move.getNewPosition()[0]);
      picked.setPosition(move.getNewPosition());
    } else {
      gameState.getTeams()[occupant.getTeamId()].setFlags(gameState.getTeams()[occupant.getTeamId()].getFlags() -1);
      picked.setPosition(MCTSUtilities.respawnPiecePosition(gameState.getGrid(), gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.piece, gameState.getCurrentTeam(), picked), picked.getPosition()[1], picked.getPosition()[0]);
    }
    removeTeamCheck(gameState);
    gameState.setLastMove(move);
    MCTSUtilities.toNextTeam(gameState);
  }


  /**
   * prints some important values to the console
   * @param best move chosen by getRootBest() method
   */
  public String printResults(Move move) {
    StringBuilder sb = new StringBuilder();
    sb.append("Piece " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + "," + move.getNewPosition()[1]);
    sb.append("\nNodes expanded: " + getExpansionCounter() +", simulations till the end: " + getSimulationCounter() + ", heuristic used: " + getHeuristicCounter());
    sb.append("\nBest children:");
    //if not all children are expanded they cannot be sorted.
    try {
    Arrays.sort(getRoot().getChildren());
    } catch(NullPointerException npe) {};
    int n = 5;
    for(int i=0; i<(getRoot().getChildren().length > n ? n : getRoot().getChildren().length); i++) {
      if (getRoot().getChildren()[i] == null) {
        n += 1;
        continue;
      }
      Move rootMove = getRoot().getChildren()[i].getReferenceGameState().getLastMove().toMove();
      sb.append("\n   " + rootMove.getPieceId() + " to [" + rootMove.getNewPosition()[0] + "," + rootMove.getNewPosition()[1] + "]"
          + " winning chance: " + (getRoot().getChildren()[i].getV() * 100) + "% with " + getRoot().getChildren()[i].getNK() + " nodes" + ", uct: " + getRoot().getChildren()[i].getUCT(config.C) + " wins 0 " + getRoot().getChildren()[i].getWins()[0] + ", wins 1 " + getRoot().getChildren()[i].getWins()[1]);
    }
    return sb.toString();
  }
  
  @Override
  public AIConfig getConfig() {
    return this.config;
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