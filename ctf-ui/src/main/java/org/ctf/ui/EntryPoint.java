package org.ctf.ui;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.fileservices.ResourceController;
import org.ctf.ui.controllers.ServerController;

/**
 * Accessor File to allow proper exposure of dependencies
 *
 * @author rsyed
 */
public class EntryPoint {

  public static void main(String[] args) {
    if (Constants.ISJAR && !new File(Constants.JARRESOURCES).isDirectory()) {
      ResourceController.main(args);
    }  
    //Two liner to Start a Server
    ServerController sc = new ServerController();
    boolean isStarted = sc.startServer("9999");
    if(!isStarted){
     System.out.println("Server aint on bruh");
    }

    String[] args2 = new String[] {};
    App.main(args2);
  }
}
