package org.ctf.shared.constants;

import org.ctf.shared.constants.Enums.AIConfigs;

public class Descriptions {
  public static String describe(AIConfigs what) {
    switch(what) {
      //AIs
      case RANDOM:
        return "An AI that only does valid random moves.";
      case MCTS: 
        return "A very lightweight Monte Carlo Tree Search implementation that uses a light playout. "
            + "It does not use multithreadding and tries to minimize RAM usage. "
            + "It is recommended if many AIs are started on the same computer.";
      case IMPROVED: 
        return "A Monte Carlo Tree Search implementation that uses a light playout and improves Data Structures. "
            + "It does not use multithreadding. The RAM usage might be higher than normal MCTS.";
      case EXPERIMENTAL:
        return "A Monte Carlo Tree Search implementation that uses a heavy playout and improved Data Structures. "
            + "It uses multithreadding and due to fundamentally different algorithms an enourmous ammount of RAM. "
            + "Due to time limitations it couldn't be optimised perfectly and might be worse than the other MCTS. ";
      // AI_Config
      case C:
        return "A value used in the UCT formular that describes if the MCTS tree is build based on exploration or exploitation. "
            + "A higher value encourages exploration whereas a lower value favours exploitation. "
            + "Exploration means expanding nodes which might not have the most simulated wins, exploitation means expanding "
            + "the nodes with the most wins.";
      case MAX_STEPS:
        return "The maximum ammount of steps a simulation is allowed to take before the heuristic is used to evaluate a GameState. "
            + "More steps usually means less expanded nodes, a higher amount of steps is better for an AI with a heavy playout.";
      case NUM_THREADS:
        return "The number of Threads used for multithreadded simulations. It should not be more than the processors cores. "
            + "A higher number of threads should be used if only one AI is running to improve simulation results.";
      case ATTACK_POWER_MUL:
        return "Multiplies every piece's attack power in a team and adds it to the heuristic result. "
            + "A higher multiplier values strong pieces.";
      case PIECE_MUL:
        return "Multiplies a teams number of pieces and adds it to the heuristic result. "
            + "A higher multiplier values fewer losses.";
      case FLAG_MUL:
        return "Multiplies a teams number of flags and adds it to the heuristic result. "
            + "A higher multiplier values wins by capturing flags.";
      case DIRECTION_MUL:
        return "Multiplies a teams piece's reach in all directions and adds it to the heuristic result. "
            + "A higher multiplier values agile pieces.";
      case SHAPE_REACH_MUL:
        return "Gets multiplied by 8 (instead of 8 directions in directionMultiplier) for every shape moving piece. "
            + "A higher multiplier values agile shape-pieces over direction movement pieces.";
      case BASE_DISTANCE_MUL: 
        return "Multiplies a modified result of the euclidean distance from a teams pieces to all other bases. "
            + "A higher multiplier treats enemies bases as magnets, being close to an enemies base gets valued.";
      default: return "no description available";
    }
  }
}
