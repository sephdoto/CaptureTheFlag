package org.ctf.ui;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.fileservices.ResourceController;

/**
 * Accessor File to allow proper exposure of dependencies
 *
 * @author rsyed
 */
public class EntryPoint {

  public static void main(String[] args) {
    if (Constants.ISJAR) 
      if (!new File(Constants.JARRESOURCES).isDirectory()) {
        new File(Constants.JARRESOURCES).mkdir();
        ResourceController.main(args);
      }
    App.main(args);
  }
}
