package org.ctf.ui;

import java.io.File;
import org.ctf.shared.constants.Constants;

/**
 * Accessor File to allow proper exposure of dependencies
 *
 * @author rsyed
 */
public class EntryPoint {

  public static void main(String[] args) {
    if(Constants.ISJAR)
      if(!new File(Constants.JARRESOURCES).isDirectory())
        return;
    App.main(args);
  }
}