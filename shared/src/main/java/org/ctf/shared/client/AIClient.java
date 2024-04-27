package org.ctf.shared.client;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * Extension of Client with support for additional functionality needed by the AI to perform its
 * functions automatically
 *
 * @author rsyed
 */
public class AIClient extends Client {

  public AI selectedPlayer;
  public boolean enableLogging;
  public Analyzer analyzer;
  public MapTemplate sessionMapTemplate;
  public String alreadyCreatedSessionID;
  public String constructorSetTeamName;
  public boolean creatorMode;
  public boolean joinerMode;
  public String gameCreatedSessionID;

  /**
   * Base constructor.
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @param selected the AI Enum which is later used for the by the AI Controller class to call for
   *     moves
   * @author rsyed
   */
  AIClient(CommLayerInterface comm, String IP, String port, Boolean enableLogging, AI selected) {
    super(comm, IP, port, enableLogging);
    this.selectedPlayer = selected;
    this.enableLogging = enableLogging;
  }

  /**
   * Construtor called to configure this class to first create a game and then join it constructor.
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @param selected the AI Enum used by the AI Controller class to generate moves
   * @param mapTemplate the map desired for game creation
   * @param constructorSetTeamName the team name desired to be used
   * @author rsyed
   */
  public AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      Boolean enableLogging,
      AI selected,
      MapTemplate mapTemplate,
      String constructorSetTeamName) {
    this(comm, IP, port, enableLogging, selected);
    this.sessionMapTemplate = mapTemplate;
    this.constructorSetTeamName = constructorSetTeamName;
    this.creatorMode = true;
    this.joinerMode = false;
  }

  /**
   * Constructor called the the AI Client has to perform a Join function
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @param selected the AI Enum used by the AI Controller class to generate moves
   * @param gameIDString the Game Session ID to use when connecting
   * @param constructorSetTeamName the team name desired to be used
   * @author rsyed
   */
  public AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      Boolean enableLogging,
      AI selected,
      String gameIDString,
      String constructorSetTeamName) {
    this(comm, IP, port, enableLogging, selected);
    this.alreadyCreatedSessionID = gameIDString;
    this.constructorSetTeamName = constructorSetTeamName;
    this.creatorMode = false;
    this.joinerMode = true;
  }

  /**
   * Starts the Automation of the Client. Call it after object creation
   *
   * @author rsyed
   */
  public void startAutomation() {
    AIGameHandler();
  }

  /**
   * This function is called whenever the controller has to be updated with new Data.
   *
   * @author rsyed
   */
  public void updateController(AI_Controller controller) {
    controller.update(getCurrentState());
  }

  /**
   * Thread checks which mode the AI Client is configured for and calls the handler needed to start
   * a new thread and then perform the function asked for
   *
   * @author rsyed
   */
  public void AIGameHandler() {
    Thread firsThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  if (creatorMode) {
                    creatorHandler();
                    running = false;
                  } else if (joinerMode) {
                    joinHandler();
                    running = false;
                  }
                } catch (Exception e) {
                  System.out.println("Error Occured in First handoff");
                }
              }
            });
    firsThread.start();
  }

  /**
   * Thread which performs the Create function and then joins the session. Ultimately starts the
   * Handler which can make moves
   *
   * @author rsyed
   */
  private void creatorHandler() {
    Thread creatorThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  if (isServerActive()) {
                    createGame(sessionMapTemplate);
                    Thread.sleep(200);
                    this.gameCreatedSessionID = getCurrentGameSessionID();
                    joinGame(constructorSetTeamName);
                    Thread.sleep(200);

                    // TODO HANDOVER TO NORMAL WATCHER
                    AIPlayerStart();
                    running = false;
                  }

                } catch (Exception e) {
                  System.out.println("Error in Creator Handler");
                }
              }
            });
    creatorThread.start();
  }

  /**
   * Thread which joins the session and then starts the Handler which can make moves
   *
   * @author rsyed
   */
  public void joinHandler() {
    Thread joinThead =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  joinExistingGame(
                      serverInfo.getHost(),
                      serverInfo.getPort(),
                      alreadyCreatedSessionID,
                      constructorSetTeamName);

                  AIPlayerStart();
                  running = false;
                } catch (Exception e) {
                  System.out.println("Error in join Handler");
                }
              }
            });
    joinThead.start();
  }

  /**
   * Main thread for the AI Client. Refreshes data on its own and check if it its turn, on true
   * makes a move, pulls data anew from Server and updates the controller
   *
   * @author rsyed
   */
  public void AIPlayerStart() {
    AI_Controller controller = new AI_Controller(this.getCurrentState(), AI.MCTS);
    Thread gameThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  pullData();
                  if (isItMyTurn()) {
                    this.makeMove(controller.getNextMove());
                  }
                  Thread.sleep(100);
                  pullData();
                  updateController(controller);
                  Thread.sleep(500);
                } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  /**
   * Used to set a {@link MapTemplate} for use by the AIClient
   *
   * @author rsyed
   */
  public void setTemplate(MapTemplate mapTemplate) {
    this.sessionMapTemplate = mapTemplate;
  }

  /**
   * Used to get the Game Session ID from this object. Needed by the other Clients to perform their
   * join functions.
   *
   * @author rsyed
   */
  public String getSessionIDfromAI() {
    return currentGameSessionID;
  }
}
