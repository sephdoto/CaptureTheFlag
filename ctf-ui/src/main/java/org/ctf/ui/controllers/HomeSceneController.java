package org.ctf.ui.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

/**
 * Main controller of the application. This Class controls what happens when clicking the buttons on
 * the HomeScreen
 *
 * @author sistumpf
 * @author mkrakows
 * @author aniemesc
 */
public class HomeSceneController {
  String port;
  String serverID;
  String sessionID;
  ServerManager serverManager;
  // TestThread t;
  MapTemplate template;
  String teamName;
  String teamTurn;
  public ObjectProperty<Color> lastcolor;
  boolean mainClientIsHuman;

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

  public MapTemplate getTemplate() {
    return template;
  }

  public void setTemplate(MapTemplate template) {
    this.template = template;
  }

  public int getMaxNumberofTemas() {
    return template.getTeams();
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
}
