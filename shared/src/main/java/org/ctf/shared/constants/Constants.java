package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

public class Constants {
	//package map, class JSON_Tools
	public static String mapTemplateFolder = Paths.get("").toAbsolutePath().toString().split("cfp14")[0]+"cfp14"+File.separator+File.separator+"cfp-service-main"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"maptemplates"+File.separator;
	
	
	//package ai.mcts, classrs MCTS & TreeNode
	public static final float C = (float)Math.E/2;             //used to calculate UCT
	public static final int TIME = 5000;           //time in milliseconds the algorithm is allowed to take
	public static final int MAX_STEPS = 100;       //maximum of possible simulation steps the algorithm is allowed to take
	
	//package client 
	public static final String remoteIP = "localhost";
	public static final String remotePort = "8888";

	public enum AI {
	  RANDOM, SIMPLE_RANDOM
	}
}

