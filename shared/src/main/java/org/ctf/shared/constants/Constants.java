package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

/**
 * Constants class to hold control variables
 * @author sistumpf
 */
public class Constants {
	//package map, class JSON_Tools
	public static String mapTemplateFolder = Paths.get("").toAbsolutePath().toString().split("cfp14")[0]+"cfp14"+File.separator+File.separator+"cfp-service-main"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"maptemplates"+File.separator;
	
	
	//package ai.mcts, classes MCTS & TreeNode
	public static final double C = Math.E/2;             //used to calculate UCT
	public static final int TIME = 5000;                       //time in milliseconds the algorithm is allowed to take
	public static final int MAX_STEPS = 50;                    //maximum of possible simulation steps the algorithm is allowed to take
	
	//package ai.mcts, class MCTS. used for heuristic
	public static final int attackPowerMultiplier = 5;         //how much the pieces attack power is valued
	public static final int pieceMultiplier = 10;               //how much having one piece is valued
	public static final int flagMultiplier = 100;              //how much having a flag is valued
	public static final int directionMultiplier = 2;           //how much a pieces reach into a certain direction is valued
	public static final int shapeReachMultiplier = 1;          //for valuing a shape Similar to a Direction movement. Calculated as 8 * this value (instead of 8 directions)
	public static final int distanceBaseMultiplier = 20;        //how much a pieces distance to the enemies base is weighted (higher = near enemies base is better)
	
	/**
 	* Constants needed to make the base URI of the restClient 
 	* @author rsyed
 	*/
	public static final String remoteIP = "localhost";
	public static final String remotePort = "8888";
	public static final String remoteBinder = "/api/";

	public enum AI {
	  RANDOM, SIMPLE_RANDOM
	}
}

