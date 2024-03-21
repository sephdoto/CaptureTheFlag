package org.ctf.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

public class MCTS {
  Random rand;
  public TreeNode root;
  public int simulationCounter;
  public int heuristicCounter;
  public int expansionCounter;

  public MCTS(TreeNode root) {
    this.root = root;
    this.rand = new Random();
  }


  /**
   * starts a Monte Carlo Tree Search from a given state of the game,
   * if the given time runs out the best calculated move is returned.
   * @param time in milliseconds the algorithm is allowed to take
   * @param Constant C used in the UCT formula
   * @return the algorithms choice for the best move
   */
  public Move getMove(int milis, float C){
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
  TreeNode selectAndExpand(TreeNode node, float C){
    while(isTerminal(node) == -1) {
      if(!isFullyExpanded(node)){
        expansionCounter++;
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
        //TODO  aus oneRandomMove entfernt, sollte unwichtig sein aber marken     parent.children[childPosition] = child;

        return selected.children[i];
      }
    }
    return null;
  }


  /**
   * simulates a game from a specific node to finish (or a maximum step value of Constants.MAX_STEPS simulation),
   * first checks if a node is in a terminal state, if thats the case the simulation ends and the result is returned
   * @param the node from which a game is going to be simulated
   * @return true if player A wins the simulation (either by getting more beans or player B having no moves left), 
   *         false if player B wins the simulation (either by getting more beans or player A having no moves left)
   *         default case is a heuristic. if it returns value > 0, player A is winning
   */
  int[] simulate(TreeNode simulateOn){      
    int isTerminal = isTerminal(simulateOn);
    int[] winners = new int[simulateOn.gameState.getTeams().length];
    
    //TODO multithreadding
    
    for(int i=0; i < Constants.MAX_STEPS && isTerminal == -1; i++, isTerminal = isTerminal(simulateOn)) {
      simulateOn = oneRandomMove(simulateOn);
    }
    if(isTerminal < 0) {
      simulationCounter++;
      winners[terminalHeuristic(simulateOn)] += 1;
    } else {
      heuristicCounter++;
      winners[isTerminal] += 1;
    }
    
    return winners;
  }


  /**
   * a heuristic to evaluate the winner of a given nodes gameState.
   * the heuristics choice depends on the games phase: start-, middle- or end-game
   * @param a node which will be analyzed
   * @return an Integer the describes the game,
   *          >0: player A got a better position
   *          =<0: player B got a better position
   */
  int terminalHeuristic(TreeNode node) {
    return 0;
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
      for(int i : wins) {
        child.wins[i] += wins[i];
      }
      child = child.parent;
    }
  }


  /**
   * checks if a game is in a terminal state.
   * generates an array with a players 6 fields and their number of beans,
   * checks if any of the players have more than half the beans needed to win or have any moves left
   * @param a node to check if it is terminal
   * @return -1 if the game is not in a terminal state
   * 		   0 - Integer.MAX_VALUE winner team id
   */
  int isTerminal(TreeNode node) {
    //TODO : wenn ein Team verloren hat wird es aus der Teams liste des GameStates entfernt
    //TODO Node Moves left check richtig hier??
    //nodeNoMovesLeft(node);
    
    if(node.gameState.getTeams().length == 1)
      return Integer.parseInt(node.gameState.getTeams()[0].getId());

    return -1;
  }
  
  void nodeNoMovesLeft(TreeNode node) {
    //TODO alter node so the team without a move gets removed. 
    // its the next teams turn then.
    // if it got no moves left, repeat
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
  TreeNode bestChild(TreeNode parent, float c) {
    float uctCurrent;
    float uctMax = 0;
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
    String key = "";
    try {
    key = parent.possibleMoves.keySet().toArray()[rand.nextInt(parent.possibleMoves.keySet().size())].toString();	//TODO test
    } 
    catch (Exception e) {
      parent.printMe(""); System.out.println(parent.possibleMoves.keySet().size());} 
    int randomMove = rand.nextInt(parent.possibleMoves.get(key).size());
    int[] movePos = parent.possibleMoves.get(key).get(randomMove);	//TODO test	
    parent.possibleMoves.get(key).remove(randomMove);
    if(parent.possibleMoves.get(key).size() == 0) {
      parent.possibleMoves.remove(key);
    }
    Move move = new Move();
    move.setPieceId(key);
    move.setNewPosition(movePos);

    alterGameState(gameState, move);
    removeTeamCheck(gameState);

    TreeNode child = parent.clone(gameState);

    return child;
  }   

  /**
   * This method checks if a team got no more flags or no more pieces.
   * @param gameState
   */
  void removeTeamCheck(GameState gameState) {
    for(int i=0; i<gameState.getTeams().length; i++) {
      if(gameState.getTeams()[i].getFlags() == 0 ||
          gameState.getTeams()[i].getPieces().length == 0) {
        Team[] teams = new Team[gameState.getTeams().length -1];
        for(int j=0; j<gameState.getTeams().length; j++) {
          if(j < i) {
            teams[j] = gameState.getTeams()[j];
          } else if (j > i) {
            teams[j-1] = gameState.getTeams()[j];
          }
        }
        gameState.setTeams(teams);
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
  public void printResults(int move) {
    System.out.println("Knoten expandiert: " + expansionCounter +"\nSimulationen bis zum Ende: " + simulationCounter + ", Heuristik angewendet: " + heuristicCounter + ", Move: " +"gotta implement");
    for(int i=0; i<root.children.length; i++) {
      if(root.children[i] != null)
        System.out.println("child "+ i + " Gewinnchance: " + Math.round(root.children[i].getV()* 1000000)/10000. + "% bei " + root.children[i].getNK() + " Knoten");
    }
  }
}
