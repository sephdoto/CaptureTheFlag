package org.ctf.ui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.fileservices.ResourceController;
import org.ctf.ui.controllers.CheatboardListener;
import org.jnativehook.GlobalScreen;
import com.sun.javafx.util.Logging;

/**
 * Accessor File to allow proper exposure of dependencies
 *
 * @author rsyed, sistumpf
 */
public class EntryPoint {
  public static CheatboardListener cbl;


  public static void main(String[] args) {
    // suppress logging (red javafx text, keylogger text, jaudiotagger text)
    java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
    logger.setLevel(java.util.logging.Level.OFF);
    Logging.getJavaFXLogger().disableLogging();
    Logging.getCSSLogger().disableLogging();
    var pin = new Logger[]{ Logger.getLogger("org.jaudiotagger") };
    for (Logger l : pin)
      l.setLevel(Level.OFF);
    logger.setUseParentHandlers(false);

    if (Constants.ISJAR && !new File(Constants.JARRESOURCES).isDirectory()) {
      ResourceController.main(args);
    }

    String[] args2 = new String[] {};
    boolean easterEggs = true;
    for(int i=0; i<args.length; i++) {
      if(args[i].equals("--disableEasterEggs"))
        easterEggs = false;
    }
    if(easterEggs)
      cbl = new CheatboardListener();

    App.main(args2);
  }
}
