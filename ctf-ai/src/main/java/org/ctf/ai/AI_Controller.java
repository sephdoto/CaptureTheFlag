package org.ctf.ai;

import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.ai.AI_Tools.NoMovesLeftException;
import org.ctf.client.constants.Constants.AI;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.Piece;
import org.ctf.client.state.Team;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.tools.JSON_Tools;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;

/**
 * @author sistumpf This class requests a GameState from the server, uses one of the implemented AIs
 *     to generate the next move and finally returns said move back to the server.
 */
public class AI_Controller {

  public static Move getNextMove(GameState gameState, AI ai)
      throws NoMovesLeftException, InvalidShapeException {
    switch (ai) {
      case RANDOM:
        return RandomAI.pickMoveComplex(gameState);
      case SIMPLE_RANDOM:
        return RandomAI.pickMoveSimple(gameState);
      default:
        return RandomAI.pickMoveComplex(gameState);
    }
  }

  // used for testing
  public static void main(String[] args) {
    try {
      Move nextMove = getNextMove(getTestState(), AI.RANDOM);

      System.out.println(
          nextMove.getPieceId()
              + " moves from ("
              + java.util.stream.IntStream.of(
                      ((Piece)
                              (Arrays.asList(getTestState().getTeams()[1].getPieces()).stream()
                                  .filter(p -> p.getId().equals(nextMove.getPieceId()))
                                  .toArray()[0]))
                          .getPosition())
                  .mapToObj(String::valueOf)
                  .collect(java.util.stream.Collectors.joining(","))
              + ") to ("
              + nextMove.getNewPosition()[0]
              + ","
              + nextMove.getNewPosition()[1]
              + ")");
    } catch (NoMovesLeftException | InvalidShapeException e) {
      e.printStackTrace();
    }

    GameState gameState = getTestState();
    String pieceId = gameState.getTeams()[1].getPieces()[1].getId();
    ArrayList<int[]> moves = AI_Tools.getPossibleMoves(gameState, pieceId);
    moves.stream().forEach(i -> System.out.println(i[0] + "." + i[1] + " , "));
  }

  /**
   * Creates a test GameState from the example Map.
   *
   * @return GameState
   */
  public static GameState getTestState() {
    MapTemplate mt = getTestTemplate();
    Team team1 = new Team();
    team1.setBase(new int[] {0, 0});
    team1.setColor("red");
    team1.setFlag(new int[] {0, 0});
    team1.setId("0");

    Team team2 = new Team();
    team2.setBase(new int[] {9, 9});
    team2.setColor("blue");
    team2.setFlag(new int[] {9, 9});
    team2.setId("1");

    Piece[] pieces1 = new Piece[8];
    for (int i = 0; i < 8; i++) {
      pieces1[i] = new Piece();
      pieces1[i].setDescription(mt.getPieces()[1]);
      pieces1[i].setId("p:0_" + (i + 1));
      if (i < 2) pieces1[i].setPosition(new int[] {1, 4 + i});
      else pieces1[i].setPosition(new int[] {2, i});
      pieces1[i].setTeamId(team1.getId());
    }
    team1.setPieces(pieces1);

    Piece[] pieces2 = new Piece[8];
    for (int i = 0; i < 8; i++) {
      pieces2[i] = new Piece();
      pieces2[i].setDescription(mt.getPieces()[1]);
      pieces2[i].setId("p:1_" + (i + 1));
      if (i < 6) pieces2[i].setPosition(new int[] {7, 2 + i});
      else pieces2[i].setPosition(new int[] {8, i - 2});
      pieces2[i].setTeamId(team1.getId());
    }
    team2.setPieces(pieces2);

    Move lastMove = new Move();
    lastMove.setNewPosition(null);
    lastMove.setPieceId(null);

    GameState testState = new GameState();
    testState.setCurrentTeam(1);
    String[][] example =
        new String[][] {
          {"b:0", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", pieces1[0].getId(), pieces1[1].getId(), "", "", "", ""},
          {
            "",
            "",
            pieces1[2].getId(),
            pieces1[3].getId(),
            pieces1[4].getId(),
            pieces1[5].getId(),
            pieces1[6].getId(),
            pieces1[7].getId(),
            "",
            ""
          },
          {"", "", "", "", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "b", "", ""},
          {"", "", "", "b", "", "", "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", ""},
          {
            "",
            "",
            pieces2[0].getId(),
            pieces2[1].getId(),
            pieces2[2].getId(),
            pieces2[3].getId(),
            pieces2[4].getId(),
            pieces2[5].getId(),
            "",
            ""
          },
          {"", "", "", "", pieces2[6].getId(), pieces2[7].getId(), "", "", "", ""},
          {"", "", "", "", "", "", "", "", "", "b:1"}
        };
    testState.setGrid(example);
    testState.setLastMove(lastMove);
    testState.setTeams(new Team[] {team1, team2});

    return testState;
  }

  /**
   * Returns the test MapTemplate from the resource folder.
   *
   * @return MapTemplate
   */
  @SuppressWarnings("deprecation")
  public static MapTemplate getTestTemplate() {
    MapTemplate mt = new MapTemplate();
    try {
      mt = JSON_Tools.readMapTemplate("10x10_2teams_example");
    } catch (MapNotFoundException e) {
      e.printStackTrace();
    }
    return mt;
  }
}
