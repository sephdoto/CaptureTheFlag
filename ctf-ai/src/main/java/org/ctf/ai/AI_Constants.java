package org.ctf.ai;

/**
 * This class contains Constants used only in the AI.
 * @author sistumpf
 */
public class AI_Constants {
  //package ai.mcts, classes MCTS & TreeNode
  public static final double C = Math.E/2;                   //used to calculate UCT
  public static final int MAX_STEPS = 100;                    //maximum of possible simulation steps the algorithm is allowed to take
  public static final int numThreads = Runtime.getRuntime().availableProcessors() /2;                    
  
  //package ai.mcts, class MCTS. used for heuristic
  public static final int attackPowerMultiplier = 3;         //how much the pieces attack power is valued
  public static final int pieceMultiplier = 10;               //how much having one piece is valued
  public static final int flagMultiplier = 1000;              //how much having a flag is valued
  public static final int directionMultiplier = 2;           //how much a pieces reach into a certain direction is valued
  public static final int shapeReachMultiplier = 1;          //for valuing a shape Similar to a Direction movement. Calculated as 8 * this value (instead of 8 directions)
  public static final int distanceBaseMultiplier = 10;        //how much a pieces distance to the enemies base is weighted (higher = near enemies base is better)
}
