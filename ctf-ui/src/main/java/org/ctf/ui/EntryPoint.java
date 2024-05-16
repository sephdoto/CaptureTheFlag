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
    if (Constants.ISJAR && !new File(Constants.JARRESOURCES).isDirectory()) {
      ResourceController.main(args);
    }

    String[] args2 = new String[] {};
    App.main(args2);
  }
}
