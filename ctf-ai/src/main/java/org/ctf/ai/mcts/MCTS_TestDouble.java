package org.ctf.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.ai.AI_Tools;
import org.ctf.ai.random.RandomAI;
import org.ctf.ai.AI_Constants;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

public class MCTS_TestDouble {
  Random rand;
  int teams;
  int maxDistance;
  public TreeNode root;
  public AtomicInteger simulationCounter;
  public AtomicInteger heuristicCounter;
  public AtomicInteger expansionCounter;
  ExecutorService executorService;

  public MCTS_TestDouble(TreeNode root) {
    this.root = root;
    this.rand = new Random();
    simulationCounter = new AtomicInteger();
    heuristicCounter = new AtomicInteger();
    expansionCounter = new AtomicInteger();
    this.teams = root.gameState.getTeams().length;
    this.maxDistance = (int)Math.round(Math.sqrt(Math.pow(root.gameState.getGrid().length, 2) + Math.pow(root.gameState.getGrid()[0].length, 2)));
    this.executorService  = Executors.newFixedThreadPool(AI_Constants.numThreads);
  }


  /**
   * starts a Monte Carlo Tree Search from a given state of the game,
   * if the given time runs out the best calculated move is returned.
   * @param time in milliseconds the algorithm is allowed to take
   * @param Constant C used in the UCT formula
   * @return the algorithms choice for the best move
   */
  public Move getMove(int milis, double C){
    long time = System.currentTimeMillis();

    while(System.currentTimeMillis() - time < milis){
      //Schritte des UCT abarbeiten
      TreeNode selected = selectAndExpand(root, C);
      backpropagate(selected, simulate(selected));
    }

    TreeNode bestChild = getRootBest(root);

    // Hier werden wichtige Daten zur Auswahl ausgegeben 
    //      printResults(bestChild);

    this.executorService.shutdown();
    return (bestChild.gameState.getLastMove());
  }


  /**
   * Selects a node to simulate on using the UCBk formula.
   * expands a children if a node in the chain has unexpanded ones.
   * @param parent node, from it on the nodes will be checked for one to simulate on
   * @param constant C used in UCBk formula
   * @return the node to simulate on
   */
  TreeNode selectAndExpand(TreeNode node, double C){
    while(isTerminal(node) == -1) {      
      if(!isFullyExpanded(node)){
        expansionCounter.incrementAndGet();
        return expand(node);
      } else {
        node = bestChild(node, C);
      }
    }
    return node;
  }


  /**
   * adds one child to the parent node, the child is identical to the parent but simulated one move further
   * @param the selected node which gets expanded
   * @return the new child
   * @return null if anything unforeseen happens
   */
  TreeNode expand(TreeNode parent){
    for(int i=0; i<parent.children.length; i++) {
      if(parent.children[i] == null) {
        TreeNode child = parent.clone(parent.copyGameState());
        oneMove(child, parent);
        parent.children[i] = child;
        return child;
      }
    }
    return null;
  }

  int[] multiSimulate(TreeNode simulateOn) { 
    int[] winners = new int[simulateOn.gameState.getTeams().length];

    try {
      // Create a list of Callable tasks for parallel execution
      List<Callable<int[]>> tasks = new LinkedList<>();
      for (int i = 0; i < AI_Constants.numThreads; i++) {
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
   * @return true if player A wins the simulation (either by getting more beans or player B having no moves left), 
   *         false if player B wins the simulation (either by getting more beans or player A having no moves left)
   *         default case is a heuristic. if it returns value > 0, player A is winning
   */
  int[] simulate(TreeNode simulateOn){      
    simulationCounter.incrementAndGet();
    int isTerminal = isTerminal(simulateOn);
    int[] winners = new int[this.teams];
    int count = AI_Constants.MAX_STEPS;
    if(isTerminal >= 0) {
//      winners[isTerminal] += count;
      winners[isTerminal] += 1;
      return winners;
    }

    simulateOn = simulateOn.clone(simulateOn.copyGameState());

    for(;count > 0 && isTerminal == -1; count--, isTerminal = isTerminal(simulateOn)) {
      oneMove(simulateOn, simulateOn);
      removeTeamCheck(simulateOn.gameState);
    }
    if(isTerminal < 0) {  
      simulationCounter.decrementAndGet();
      heuristicCounter.incrementAndGet();
      winners[terminalHeuristic(simulateOn)] += 1;
    } else {
      //TODO: count zum Testen durch isTerminal ersetzen
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
    Team[] teams = node.gameState.getTeams();
    int[] points = new int[teams.length];

    for(int i=0; i<teams.length; i++) {
      if(teams[i] == null)
        continue;

      for(Piece p : teams[i].getPieces()) {
        points[i] += p.getDescription().getAttackPower() * AI_Constants.attackPowerMultiplier;
        points[i] += 1 * AI_Constants.pieceMultiplier;
        for(int j=0; j<teams.length; j++) {
          if(j == i || teams[j] == null) {
            continue;
          }
          //reward being close to enemy base
          points[i] += (this.maxDistance - distanceToBase(teams[j].getBase(), p.getPosition())) * AI_Constants.distanceBaseMultiplier;
        }

        if(p.getDescription().getMovement().getDirections() != null) {
          for(int dir=0; dir<8; dir++)
            points[i] += AI_Tools.getReach(p.getDescription().getMovement().getDirections(), dir) * AI_Constants.directionMultiplier;
        } else {
          points[i] += 8 * AI_Constants.shapeReachMultiplier;
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

      points[i] += teams[i].getFlags() * AI_Constants.flagMultiplier;
    }

    int max = 0;
    for(int i=0; i<points.length; i++)
      if(points[i] > points[max])
        max = i;

    return max;
  }

  float distanceToBase(int[] base, int[] piece) {
    return (float) Math.sqrt(Math.pow(base[1]-piece[1], 2) + Math.pow(base[0]-piece[0], 2));
  }


  /**
   * picks a (random) move to make a move on
   * @return a random chosen move out of an ArrayList containing possible moves.
   */
  int[] pickRandomMove(ArrayList<int[]> moveList) {
    return moveList.get(rand.nextInt(moveList.size()));
  }


  /**
   * propagates the simulation result up the tree until the root element is reached
   * @param node on which the simulation was executed
   * @param an int Array containing as many spaces as teams are left, a place in the Array corresponds to the teamId and contains the number of wins of that team.
   */
  void backpropagate(TreeNode child, int[] wins){
    while(child != null) {
      for(int i = 0; i<wins.length; i++) {
        child.wins[i] += wins[i];
      }
      child = child.parent;
    }
  }


  /**
   * Checks if a game is in a terminal state.
   * 
   * @param a node to check if it is terminal
   * @return -1 if the game is not in a terminal state
   * 		   0 - Integer.MAX_VALUE winner team id
   */
  int isTerminal(TreeNode node) {
    int teamsLeft = 0;
    for(int i=0; i<node.gameState.getTeams().length; i++) {
      if(node.gameState.getTeams()[i] != null) {
        teamsLeft++;
      }
    }

    for(int i=node.gameState.getCurrentTeam(); teamsLeft > 1; i = AI_Tools.toNextTeam(node.gameState).getCurrentTeam()) {
      boolean canMove = false;
      for(int j=0; !canMove && j<node.gameState.getTeams()[i].getPieces().length; j++) {
        //only if a move can be made no exception is thrown
        try {
          RandomAI.pickMoveComplex(node.gameState);
          canMove = true;
        } catch (Exception e) {} 
      }
      if(canMove) {
        return -1;
      } else if (!canMove){
        AI_Tools.removeTeam(node.gameState, i);
        teamsLeft--;
      }
    }

    if(teamsLeft <= 1) {
      for(Team team : node.gameState.getTeams())
        if(team != null)
          return Integer.parseInt(team.getId());
    }
    return -1;
  }

  /**
   * Checks if all possible children from a specific node are expanded
   * @param parent node
   * @return true if all children are expanded
   */
  boolean isFullyExpanded(TreeNode parent) {
    return parent.possibleMoves.keySet().size() == 0;
  }


  /**
   * checks all the parents children for their UCBk value, returns the node with the highest value
   * = "BestChild" from the pseudo-code
   * @param parent node
   * @return the child node with the highest UCT value
   */
  TreeNode bestChild(TreeNode parent, double c) {
    double uctCurrent;
    double uctMax = 0;
    TreeNode bestChild = null;

    for(int i=0; i<parent.children.length; i++) {
      if(parent.children[i] == null)
        continue;

      uctCurrent = parent.children[i].getUCT(c);

      if(uctCurrent >= uctMax) {
        uctMax = uctCurrent;
        bestChild = parent.children[i];
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

    for(int i=0; i < root.children.length; i++) {
      if(root.children[i] != null && root.children[i] == bestChild(root, 0))
        bestChild = root.children[i];
    }

    return bestChild;
  }


  /**
   * Simulates one move and returns a new node containing the new state.
   * @param alter node, this nodes GameState is altered
   * @param original node, the move made gets removed from it
   * @return a child node containing the simulation result
   */
  void oneMove(TreeNode alter, TreeNode original) {
    alterGameState(alter.gameState, getAndRemoveMoveHeuristic(original));
    alter.initPossibleMovesAndChildren();
  }   

  @SuppressWarnings("unlikely-arg-type")
  Move getAndRemoveMoveHeuristic(TreeNode parent) {
    for(String key : parent.possibleMoves.keySet()) {
      Piece picked = Arrays.asList(parent.gameState.getTeams()[parent.gameState.getCurrentTeam()].getPieces()).stream().filter(p -> p.getId().equals(key)).findFirst().get();
      for(int i=0; i<parent.possibleMoves.get(key).size(); i++) {
        int[] pos = parent.possibleMoves.get(key).get(i);
        if(parent.gameState.getGrid()[pos[0]][pos[1]].contains("b:") && 
            !parent.gameState.getGrid()[pos[0]][pos[1]].split("b:")[1].equals(parent.gameState.getCurrentTeam())) {
          return createMoveDeleteIndex(parent, key, i);
        }
        if(parent.gameState.getGrid()[pos[0]][pos[1]].contains("p:")&& 
            !parent.gameState.getGrid()[pos[0]][pos[1]].split("p:")[1].split("_")[0].equals(parent.gameState.getCurrentTeam())) {
          if(AI_Tools.validPos(pos, picked, parent.gameState))
            return createMoveDeleteIndex(parent, key, i);
        }
      }
    }

    return getAndRemoveMoveRandom(parent);
  }

  Move getAndRemoveMoveRandom(TreeNode parent){
    String key = parent.possibleMoves.keySet().toArray()[rand.nextInt(parent.possibleMoves.keySet().size())].toString();
    int randomMove = rand.nextInt(parent.possibleMoves.get(key).size());

    return createMoveDeleteIndex(parent, key, randomMove);
  }

  Move createMoveDeleteIndex(TreeNode parent, String key, int index) {
    Move move = new Move();
    move.setPieceId(key);
    move.setNewPosition(parent.possibleMoves.get(key).get(index));

    parent.possibleMoves.get(key).remove(index);
    if(parent.possibleMoves.get(key).size() <= 0) {
      parent.possibleMoves.remove(key);
    }

    return move;
  }

  /**
   * This method checks if a team got no more flags or no more pieces.
   * @param gameState
   */
  void removeTeamCheck(GameState gameState) {
    for(int i=0; i<gameState.getTeams().length; i++) {
      if(gameState.getTeams()[i] == null)
        continue;
      if(gameState.getTeams()[i].getFlags() == 0 ||
          gameState.getTeams()[i].getPieces().length == 0) {
        AI_Tools.removeTeam(gameState, i--);
      }
    }
  }

  /**
   * A given GameState is updated with a given move.
   * This method is pretty much a replica of the makeMove method from the server.
   * @param gameState
   * @param move
   */
  void alterGameState(GameState gameState, Move move) {
    String occupant = gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]];
    Piece picked = Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream().filter(p -> p.getId().equals(move.getPieceId())).findFirst().get();
    int[] oldPos = picked.getPosition();

    gameState.getGrid()[oldPos[0]][oldPos[1]] = "";

    if (occupant.contains("p:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setPieces(
          Arrays.asList(gameState.getTeams()[occupantTeam].getPieces()).stream()
          .filter(p -> !p.getId().equals(occupant))
          .toArray(Piece[]::new));
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    } 
    else if (occupant.contains("b:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setFlags(gameState.getTeams()[occupantTeam].getFlags() -1);
      picked.setPosition(AI_Tools.respawnPiecePosition(gameState, gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid()[picked.getPosition()[0]][picked.getPosition()[1]] = picked.getId();
    } 
    else {    
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    }
    gameState.setLastMove(move);
    AI_Tools.toNextTeam(gameState);
  }


  /**
   * prints some important values to the console
   * @param best move chosen by getRootBest() method
   */
  public String printResults(Move move) {
    StringBuilder sb = new StringBuilder();
    sb.append("Piece " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + "," + move.getNewPosition()[1]);
    sb.append("\nNodes expanded: " + expansionCounter +", simulations till the end: " + simulationCounter + ", heuristic used: " + heuristicCounter);
    sb.append("\nBest children:");
    //if not all children are expanded they cannot be sorted.
    try {
    Arrays.sort(root.children);
    } catch(NullPointerException npe) {};
    int n = 5;
    for(int i=0; i<(root.children.length > n ? n : root.children.length); i++) {
      if (root.children[i] == null) {
        n += 1;
        continue;
      }
      Move rootMove = root.children[i].gameState.getLastMove();
      sb.append("\n   " + rootMove.getPieceId() + " to [" + rootMove.getNewPosition()[0] + "," + rootMove.getNewPosition()[1] + "]"
          + " winning chance: " + (root.children[i].getV() * 100) + "% with " + root.children[i].getNK() + " nodes" + ", uct: " + root.children[i].getUCT(AI_Constants.C) + " wins 0 " + root.children[i].wins[0] + ", wins 1 " + root.children[i].wins[1]);
    }
    return sb.toString();
  }
}