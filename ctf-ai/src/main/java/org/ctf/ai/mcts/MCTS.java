package org.ctf.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.ai.RandomAI;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

public class MCTS {
  Random rand;
  int teams;
  int maxDistance;
  public TreeNode root;
  public AtomicInteger simulationCounter;
  public AtomicInteger heuristicCounter;
  public AtomicInteger expansionCounter;

  public MCTS(TreeNode root) {
    this.root = root;
    this.rand = new Random();
    simulationCounter = new AtomicInteger();
    heuristicCounter = new AtomicInteger();
    expansionCounter = new AtomicInteger();
    this.teams = root.gameState.getTeams().length;
    this.maxDistance = (int)Math.round(Math.sqrt(Math.pow(root.gameState.getGrid().length, 2) + Math.pow(root.gameState.getGrid()[0].length, 2)));
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
  TreeNode expand(TreeNode selected){
    for(int i=0; i<selected.children.length; i++) {
      if(selected.children[i] == null) {
        selected.children[i] = oneRandomMove(selected);
        return selected.children[i];
      }
    }
    return null;
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
    int isTerminal = isTerminal(simulateOn);
    int[] winners = new int[this.teams];
    int count = Constants.MAX_STEPS;
    //TODO multithreadding
    
    for(;count > 0 && isTerminal == -1; count--, isTerminal = isTerminal(simulateOn)) {
      simulateOn = oneRandomMove(simulateOn);
      removeTeamCheck(simulateOn.gameState);
    }
    if(isTerminal < 0) {
      heuristicCounter.incrementAndGet();
      winners[terminalHeuristic(simulateOn)] += 1;
    } else {
      simulationCounter.incrementAndGet();
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
    int[][] bases = new int[teams.length][2];
    for(int i=0; i<bases.length; i++)
      bases[i] = teams[i].getBase();
    
    for(int i=0; i<teams.length; i++) {
      for(Piece p : teams[i].getPieces()) {
        points[i] += p.getDescription().getAttackPower() * Constants.attackPowerMultiplier;
        points[i] += 1 * Constants.pieceMultiplier;
        for(int j=0; j<teams.length; j++) {
          if(j == i)
            continue;
          points[i] += (this.maxDistance - Math.sqrt(Math.pow(teams[j].getBase()[1]-p.getPosition()[1], 2) 
              + Math.pow(teams[j].getBase()[0]-p.getPosition()[0], 2))) * Constants.distanceBaseMultiplier;
        }        
        
        if(p.getDescription().getMovement().getDirections() != null) {
          for(int dir=0; dir<8; dir++)
            points[i] += AI_Tools.getReach(p.getDescription().getMovement().getDirections(), dir) * Constants.directionMultiplier;
        } else {
          points[i] += 8 * Constants.shapeReachMultiplier;
        }
      }
      points[i] += teams[i].getFlags() * Constants.flagMultiplier;
    }

    int max = 0;
    for(int i=0; i<points.length; i++)
      if(points[i] > points[max])
        max = i;
    
    return max;
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
    for(int i=0; i<node.gameState.getTeams().length; i++) {
      if(node.gameState.getTeams().length == 1)
        return Integer.parseInt(node.gameState.getTeams()[0].getId());
      
      int currentTeam = (node.gameState.getCurrentTeam() + i) % node.gameState.getTeams().length;
      boolean canMove = false;
      for(int j=0; !canMove && j<node.gameState.getTeams()[node.gameState.getCurrentTeam()].getPieces().length; j++) {
        //only if a move can be made no exception is thrown
        try {
          RandomAI.pickMoveComplex(node.gameState);
          canMove = true;
        } catch (NoMovesLeftException e) {} 
        catch (InvalidShapeException e) {}
      }
      if(canMove) {
        return -1;
      } else {
        AI_Tools.removeTeam(node.gameState, currentTeam);
        i = -1;
      }
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
   * also adds the new node to the parent nodes children, its place in the Array
   * is the move made to get from the parent to the child (= field %6)
   * @param parent node
   * @param the move in form of the selected element in the parents array
   * @return a child node containing the simulation result
   */
  TreeNode oneRandomMove(TreeNode parent) {
    GameState gameState = parent.copyGameState();
    Move move = getAndRemoveMove(parent);
    alterGameState(gameState, move);

    TreeNode child = parent.clone(gameState);

    return child;
  }   
  
  Move getAndRemoveMove(TreeNode parent){
    String key = parent.possibleMoves.keySet().toArray()[rand.nextInt(parent.possibleMoves.keySet().size())].toString();
    int randomMove = rand.nextInt(parent.possibleMoves.get(key).size());  
    int[] movePos = parent.possibleMoves.get(key).get(randomMove);
    parent.possibleMoves.get(key).remove(randomMove);
    if(parent.possibleMoves.get(key).size() <= 0) {
      parent.possibleMoves.remove(key);
    }
    Move move = new Move();
    move.setPieceId(key);
    move.setNewPosition(movePos);

    return move;
  }

  /**
   * This method checks if a team got no more flags or no more pieces.
   * @param gameState
   */
  void removeTeamCheck(GameState gameState) {
    for(int i=0; i<gameState.getTeams().length && gameState.getTeams().length > 1; i++) {
      if(gameState.getTeams()[i].getFlags() == 0 ||
          gameState.getTeams()[i].getPieces().length == 0) {
        AI_Tools.removeTeam(gameState, i);
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

    gameState.setCurrentTeam((gameState.getCurrentTeam() + 1) % gameState.getTeams().length);
    gameState.setLastMove(move);
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
    Arrays.sort(root.children);
    for(int i=0; i<(root.children.length > 5 ? 5 : root.children.length); i++) {
      Move rootMove = root.children[i].gameState.getLastMove();
      sb.append("\n   " + rootMove.getPieceId() + " to [" + rootMove.getNewPosition()[0] + "," + rootMove.getNewPosition()[1] + "]"
      + " winning chance: " + (root.children[i].getV() * 100) + "% with " + root.children[i].getNK() + " nodes" + ", uct: " + root.children[i].getUCT(Constants.C) + " wins 0 " + root.children[i].wins[0] + ", wins 1 " + root.children[i].wins[1]);
    }
    return sb.toString();
  }
}
