package org.ctf.ui;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.fileservices.ResourceController;
import org.ctf.shared.fileservices.TerminalCommandService;

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

    // Manual Starting the JAR code
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Runnable startServer =
        () -> {
          TerminalCommandService.runCommandinShell(
              "java -jar " + Constants.toUIResources + "server.jar" + "--server.port=8888");
        };

    // END OF Manual Starting the JAR code

    // If import works properly
    /* ServerController sc = new ServerController();
       boolean isStarted = sc.startServer("8888");
       if(!isStarted){
        System.out.println("Server aint on bruh");
       }
    */
    String[] args2 = new String[] {};
    App.main(args2);
  }
}
