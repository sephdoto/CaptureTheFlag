package org.ctf.ui.data;

import java.util.ArrayList;
import org.ctf.shared.client.Client;

/**
 * All local clients and the main clients are hold here, to centralize the clients.
 * Operations like creating clients take place in this class.
 * Other classes should not save the clients themselves but use this classes getters.
 * 
 * @author sistumpf
 */
public class ClientStorage {
  /**
   * The main client which is responsible for updating the UI
   */
  private static Client mainClient;

  /**
   * List of all Human-Clients on one device
   */
  private static ArrayList<Client> localHumanClients = new ArrayList<Client>();
  
  /**
   * List of all AI-Clients on one device
   */
  private static ArrayList<Client> localAIClients = new ArrayList<Client>();
  
  
  //////////////////////////////////////////
  //          End of Data Storage         //
  //////////////////////////////////////////
  
  
  
  /**
   * Clears the local human client list, the local AI client list and sets the main client to null.
   * Then calls the Garbage Collector to free the RAM.
   * TODO mainclient wird erstmal nicht auf null gesetzt, es muss irgendeine Überprüfung für queued gamestates geben.
   * 
   * @author sistumpf
   */
  public static void clearAllClients() {
    localHumanClients.clear();
    localAIClients.clear();
//    setMainClient(null);
    System.gc();
  }
  
  /**
   * Adds a client to the list of local human clients.
   * 
   * @author sistumpf
   * @param client the human client
   */
  public static void addLocalHumanClient(Client client) {
    localHumanClients.add(client);
  }
  
  /**
   * Adds a client to the list of local AI clients.
   * 
   * @author sistumpf
   * @param client the AI client
   */
  public static void addLocalAIClient(Client client) {
    localAIClients.add(client);
  }
  
  /**
   * Pulls Data for all local AI and Human clients.
   *
   * @author sistumpf
   */
  public static void updateAllClients() {
    for (Client client : ClientStorage.getLocalHumanClients()) client.pullData();
    for (Client client : ClientStorage.getLocalAIClients()) client.pullData();
  }
  //////////////////////////////////////////
  //       End of Functional Methods      //
  //////////////////////////////////////////

  
  public static Client getMainClient() {
    return mainClient;
  }

  public static void setMainClient(Client mainClient) {
    if(ClientStorage.mainClient != null)
      ClientStorage.mainClient.enableGameStateQueue(false);
    if(mainClient != null)
      mainClient.enableGameStateQueue(true);
    ClientStorage.mainClient = mainClient;
  }
  
  public static ArrayList<Client> getLocalHumanClients() {
    return localHumanClients;
  }

  public static void setLocalHumanClients(ArrayList<Client> localHumanClients) {
    ClientStorage.localHumanClients = localHumanClients;
  }
  
  public static ArrayList<Client> getLocalAIClients() {
    return localAIClients;
  }

  public static void setLocalAIClients(ArrayList<Client> localAIClients) {
    ClientStorage.localAIClients = localAIClients;
  }
}