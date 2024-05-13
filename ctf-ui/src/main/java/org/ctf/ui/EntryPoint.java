package org.ctf.ui;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.fileservices.ResourceController;
import org.ctf.shared.fileservices.TerminalCommandService;
import com.sun.media.jfxmedia.logging.Logger;
import de.unimannheim.swt.pse.ctf.CtfApplication;

/**
 * Accessor File to allow proper exposure of dependencies
 *
 * @author rsyed
 */
public class EntryPoint {

  public static void main(String[] args) {
    //    ExecutorService executor = Executors.newSingleThreadExecutor();
    //    Runnable startServer =
    //        () -> TerminalCommandService.runCommandinShell(Constants.START_SERVER_JAR_COMMAND);
    if (Constants.ISJAR)
      if (!new File(Constants.JARRESOURCES).isDirectory()) {
        ResourceController.main(args);
      }
    //    executor.submit(startServer);
    String[] args2 = new String[] {"--server.port=8888"};
    try {
      CtfApplication.main(args2);
    } catch (Exception e) {
      e.printStackTrace();
    }
    App.main(args2);
  }
}
