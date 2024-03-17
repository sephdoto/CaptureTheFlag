package org.ctf.ai.mcts;

import java.util.Random;
import org.ctf.shared.constants.Constants;

public class MCTS {
    Random rand;
    public TreeNode root;
    public int player;      //player A if player==0, player B if player==6
    public int simulationCounter;
    public int heuristicCounter;
    public int expansionCounter;

    public MCTS(TreeNode root, int offset) {
        this.player = offset;
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
    public int getState(int milis, float C){
        long time = System.currentTimeMillis();

        while(System.currentTimeMillis() - time < milis){
            //Schritte des UCT abarbeiten
            TreeNode selected = selectAndExpand(root, C);
            backpropagate(selected, simulate(selected));
        }

        int bestChild = getRootBest(root);
         
        // Hier werden wichtige Daten zur Auswahl ausgegeben 
//      printResults(bestChild);
        
        return (bestChild);
    }


    /**
     * Selects a node to simulate on using the UCBk formula.
     * expands a children if a node in the chain has unexpanded ones.
     * @param parent node, from it on the nodes will be checked for one to simulate on
     * @param constant C used in UCBk formula
     * @return the node to simulate on
     */
    TreeNode selectAndExpand(TreeNode node, float C){
         /*
         / prueft alle 6 Felder des Spielers ob Zuege moeglich sind, wenn ja wird die Schleife ausgefuehrt
         */
        while(isTerminal(node) != 2) {
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
        for(int i=0; i < selected.children.length; i++) {
            if(selected.children[i] == null && selected.gameState[i + (selected.player ? 0 : Constants.CHILDREN)] != 0) {
                selected.children[i] = oneMove(selected, i + (selected.player ? 0 : Constants.CHILDREN));           
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
    boolean simulate(TreeNode simulateOn){      
        int isTerminal = isTerminal(simulateOn);
        
        for(int i=0; i < Constants.MAX_STEPS && isTerminal == 4; i++, isTerminal = isTerminal(simulateOn)) {
            int field = pickField(simulateOn);
            simulateOn = this.oneMove(simulateOn, field);
        }
    
        switch(isTerminal) {
            case 0:                                         //Spieler A hat mehr als die Haelfte der Punkte
                simulationCounter++; 
                return true;
            case 1:                                         //Spieler B hat mehr als die Haelfte der Punkte
                simulationCounter++; 
                return false;
            case 2:                                         //aktueller Spieler hat keine Bohnen mehr
                simulationCounter++;
                if((simulateOn.player ? simulateOn.pointsP1 : simulateOn.pointsP2) > Constants.HALF_THE_BEANS)
                    return simulateOn.player;               //aktueller Spieler hat mehr als die Haelfte aller Punkte
                else
                    return !simulateOn.player;              //aktueller Spieler hat weniger als die Haelfte aller Punkte
            default:                                        //kein Endzustand: Heuristik bewertet Spielzustand
                heuristicCounter++;
                int score = terminalHeuristic(simulateOn);
                return score == 0 ? !simulateOn.player : score > 0;
            }
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
     * picks a (random) field to make a move on
     * @return an int between 0 and Constants.Children (if it's player As turn)
     *         an int between Constants.Children and 2 * Constants.Children (if it's player Bs turn)
     */
    short pickField(TreeNode simulateOn) {
        short field;
        do {
            field = (short) (rand.nextInt(Constants.CHILDREN) + (simulateOn.player ? 0 : Constants.CHILDREN));
        } while(simulateOn.gameState[field] == 0);
        return field;
    }


    /**
     * propagates the simulation result up the tree until the root element is reached
     * @param node on which the simulation was executed
     * @param winner of the simulation, winner == true if player A won
     */
    void backpropagate(TreeNode child, boolean winner){
        while(child != null) {
            if(winner) {
                child.winsP1++;
            } else {
                child.winsP2++;
            }
            child = child.parent;
        }
    }


    /**
     * checks if a game is in a terminal state.
     * generates an array with a players 6 fields and their number of beans,
     * checks if any of the players have more than half the beans needed to win or have any moves left
     * @param a node to check if it is terminal
     * @return 0 if player A won by gaining more beans
     *         1 if player B won by gaining more beans
     *         2 if player has no more moves left
     *         4 if the node is not terminal
     */
    byte isTerminal(TreeNode node) {
        if(getMoves(node) == 0) {
            return 2;
        } else if(node.pointsP1 > Constants.HALF_THE_BEANS) {
            return 0;
        } else if(node.pointsP2 > Constants.HALF_THE_BEANS) {
            return 1;
        } else {
            return 4;
        }
    }

    /**
     * checks how many moves are possible on a nodes gameState.
     * uses the nodes attribute "player" which stores if it is player As or Bs turn
     * to select the right side of the board for checking
     * @param TreeNode to be checked
     * @return number of possible moves from the given state
     */
    int getMoves(TreeNode node) {
        int possibleMoves = 0;
        for(int i=0; i < node.children.length; i++) {
            if(node.gameState[i + (node.player ? 0 : Constants.CHILDREN)] != 0) {
                possibleMoves++;
            }
        }
        return possibleMoves;
    }


    /**
     * checks if all possible children from a specific node are expanded
     * @param parent node
     * @return true if all children are expanded
     */
    boolean isFullyExpanded(TreeNode parent) {
        return numberOfChildren(parent) == getMoves(parent);
    }


    /**
     * checks how many children nodes are expanded on a given parent node
     * @param parent
     * @return number of expanded children
     */
    int numberOfChildren(TreeNode parent) {
        int expandedChildren = 0;
        for(TreeNode node : parent.children) {
            if(node == null)
                expandedChildren--;
        }
        return expandedChildren;
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
     * checks all children from a given node for the best move, assuming all nodes are expanded and already simulated.
     * a move is identical to the childrens place in the root nodes children array:
     * if children[0] is the best state the best move is 1, for children[1] the best move is 2, ...
     * @param a node where all children will be checked for the best move
     * @return the best move as an int 0-5
     */
    int getRootBest(TreeNode root) {
        int bestChild = 0;
                
        for(int i=0; i < root.children.length; i++) {
            if(root.children[i] != null && root.children[i] == bestChild(root, 0))
                bestChild = i;
        }
        
        return bestChild;
    }


    /**
     * simulates one move and returns a new node containing the new state.
     * also adds the new node to the parent nodes children, its place in the Array
     * is the move made to get from the parent to the child (= field %6)
     * @param parent node
     * @param the move in form of the selected element in the parents array
     * @return a child node containing the simulation result
     */
    TreeNode oneMove(TreeNode parent, int field) {
        TreeNode child = parent.clone();

        child.gameState[field] = 0;
        for(int i=field+1; i < parent.gameState[field] + field+1; i++) {
            child.gameState[i % child.gameState.length]++;
        }

        for(int i=0; i < child.gameState.length; i++) {
            int beansInField = child.gameState[(parent.gameState[field] + field - i) % 12];
            if(beansInField == 2 || beansInField == 4 || beansInField == 6){
                if(parent.player) {
                    child.pointsP1 += beansInField;
                } else {
                    child.pointsP2 += beansInField;
                }
                child.gameState[(parent.gameState[field] + field - i) % 12] = 0;
            } else {
                break;
            }
        }
        
        return child;
    }   
    
    
    /**
     * prints some important values to the console
     * @param best move chosen by getRootBest() method
     */
    public void printResults(int move) {
        System.out.println("Knoten expandiert: " + expansionCounter +"\nSimulationen bis zum Ende: " + simulationCounter + ", Heuristik angewendet: " + heuristicCounter + ", gewähltes Feld: " + (move + player +1));
        for(int i=0; i<root.children.length; i++) {
            if(root.children[i] != null)
                System.out.println("child "+ i + " Gewinnchance: " + Math.round(root.children[i].getV()* 1000000)/10000. + "% bei " + root.children[i].getNK() + " Knoten");
        }
    }
}
