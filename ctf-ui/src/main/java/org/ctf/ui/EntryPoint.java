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
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Runnable startServer =
        () -> {
          TerminalCommandService.runCommandinShell("java -jar F:\\server.jar --server.port=8888");
        };
    if (Constants.ISJAR)
      if (!new File(Constants.JARRESOURCES).isDirectory()) {
        ResourceController.main(args);
      }
    executor.submit(startServer);
    App.main(args);
  }
}
