package org.ctf.ui.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.ui.App;
import org.ctf.ui.editor.EditorScene;
import org.ctf.ui.gameAnalyzer.AiAnalyserNew;
import org.ctf.ui.hostGame.CreateGameController;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import org.ctf.ui.hostGame.WaitingScene;
import org.ctf.ui.remoteGame.JoinScene;
import org.ctf.ui.remoteGame.WaveCollapseThread;
import org.ctf.ui.threads.ResizeFixThread;

/**
 * Main controller of the application. This Class controls what happens when clicking the buttons on
 * the HomeScreen
 *
 * @author sistumpf
 * @author mkrakows
 * @author aniemesc
 */
public class HomeSceneController {
  public PlayGameScreenV2 getPlayGameScreenV2() {
    return playGameScreenV2;
  }

  private Stage stage;
  String port;
  String serverID;
  String sessionID;
  ServerManager serverManager;
  // TestThread t;
  MapTemplate template;
  CreateGameScreenV2 createGameScreenV2;
  PlayGameScreenV2 playGameScreenV2;
  WaitingScene waitingScene;
  Client mainClient;
  String teamName;
  String teamTurn;
  public ObjectProperty<Color> lastcolor;
  boolean mainClientIsHuman;

  public void switchtoHomeScreen(ActionEvent e) {
    CheatboardListener.setLastScene(stage.getScene());
    Scene scene = App.getScene();
    stage = App.getStage();
    App.adjustHomescreen(stage.getScene().getWidth(), stage.getScene().getHeight());
    stage.setScene(scene);
    CheatboardListener.setSettings((StackPane) scene.getRoot(), scene);
  }

  public HomeSceneController(Stage stage) {
    this.stage = stage;
  }

  public void createGameSession() {
    System.out.println(serverID);
    System.out.println(port);
    serverManager = new ServerManager(new CommLayer(), new ServerDetails(serverID, port), template);
    if (serverManager.createGame()) {
      System.out.println("Session created");
    } else {
      System.out.println("No Session created");
    }
  }

  public void deleteGame() {
    serverManager.deleteGame();
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public void updateTeamsinWaitingScene(String text) {
    // waitingScene.setCUrrentTeams(text);
  }

  public void redraw(GameState state) {
    // playGameScreenV2.redrawGrid(state, this);
  }

  public ObjectProperty<Color> getLastcolor() {
    return lastcolor;
  }

  public void setLastcolor(ObjectProperty<Color> lastcolor) {
    this.lastcolor = lastcolor;
  }

  public void setTeamTurn(String s) {
    this.teamTurn = s;
    // playGameScreenV2.setTeamTurn(s);
  }

  public void switchToWaitGameScene(Stage stage) {
    CheatboardListener.setLastScene(stage.getScene());
    CreateGameController.initColorHashMap();
    waitingScene =
        new WaitingScene(
            this, stage.getWidth() - App.offsetWidth, stage.getHeight() - App.offsetHeight);
    stage.setScene(waitingScene);
    new ResizeFixThread(stage).start();
  }

  /**
   * Switches to the PlayGameScene, calls the WaveCollapseThread to generate and change the
   * background. Always calls the WaveCollapseThread, in case not our server is used to generate the
   * GameState.
   *
   * @author sistumpf
   * @param stage
   * @param mainClient
   * @param isRemote
   */
  public void switchToPlayGameScene(Stage stage, Client mainClient, boolean isRemote) {
    CheatboardListener.setLastScene(stage.getScene());
    // delete last grid
    File grid = new File(Constants.toUIPictures + File.separator + "grid.png");
    grid.delete();

    // update main client if necessary
    // TODO
    for (int i = 0; i < mainClient.getTeams().length; i++) {
      if (mainClient.getTeams()[i] == null) {
        System.out.println("team " + i + " is null??");
        //        while(mainClient.getTeams()[i] == null)
        mainClient.pullData();
      }
    }

    playGameScreenV2 =
        new PlayGameScreenV2(
            this,
            stage.getWidth() - App.offsetWidth,
            stage.getHeight() - App.offsetHeight,
            mainClient,
            isRemote);
    stage.setScene(playGameScreenV2);

    if (isRemote) {
      CreateGameController.initColorHashMapForRemote(mainClient);
    } else {
      CreateGameController.overWriteDefaultWithServerColors();
    }

    // generate new grid
    new WaveCollapseThread(mainClient.getGrid(), this).start();
  }

  public void switchToCreateGameScene(Stage stage) {
    CheatboardListener.setLastScene(stage.getScene());
    createGameScreenV2 =
        new CreateGameScreenV2(
            this, stage.getWidth() - App.offsetWidth, stage.getHeight() - App.offsetHeight);
    stage.setScene(createGameScreenV2);
    new ResizeFixThread(stage).start();
  }

  /**
   * Switches to a new instance of {@link JoinScene}.
   *
   * @author aniemesc
   * @param stage - Main stage of the application
   */
  public void switchToJoinScene(Stage stage) {
    CheatboardListener.setLastScene(stage.getScene());
    stage.setScene(
        new JoinScene(
            this, stage.getWidth() - App.offsetWidth, stage.getHeight() - App.offsetHeight));
    new ResizeFixThread(stage).start();
  }

  public void switchToAnalyzerScene(Stage stage) {
    CheatboardListener.setLastScene(stage.getScene());
    AiAnalyserNew scene = new AiAnalyserNew(this, stage.getWidth() - App.offsetWidth, stage.getHeight() - App.offsetHeight);
    if(scene.switched) {
      stage.setScene(scene);
      new ResizeFixThread(stage).start();
    }
  }

  /**
   * Switches to a new instance of {@link EditorScene}.
   *
   * @author aniemesc
   * @param stage - Main stage of the application
   */
  public void switchToMapEditorScene(Stage stage) {
    CheatboardListener.setLastScene(stage.getScene());
    stage.setScene(
        new EditorScene(
            this, stage.getWidth() - App.offsetWidth, stage.getHeight() - App.offsetHeight));
    new ResizeFixThread(stage).start();
  }

  public MapTemplate getTemplate() {
    return template;
  }

  public void setTemplate(MapTemplate template) {
    this.template = template;
  }

  public int getMaxNumberofTemas() {
    return template.getTeams();
  }

  public Stage getStage() {
    return stage;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getServerID() {
    if (serverID.equals("localhost")) {
      URL url;
      try {
        url = new URL("https://api.ipify.org");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String ipAddress = reader.readLine();
        System.out.println("Öffentliche IP-Adresse: " + ipAddress);
        reader.close();

        // return ipAddress;
        return InetAddress.getLocalHost().getHostAddress();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return serverID;
  }

  public void setServerID(String serverID) {
    this.serverID = serverID;
  }

  public ServerManager getServerManager() {
    return serverManager;
  }

  public String getSessionID() {
    return sessionID;
  }

  public void setSessionID(String sessionID) {
    this.sessionID = sessionID;
  }

  public void setServerManager(ServerManager serverManager) {
    this.serverManager = serverManager;
  }

  public void setMainClient(Client mainClient) {
    this.mainClient = mainClient;
  }

  public Client getMainClient() {
    return mainClient;
  }
}
