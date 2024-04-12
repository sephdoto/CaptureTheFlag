package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
  // package map, class JSON_Tools
  public static String mapTemplateFolder =
      Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + File.separator
          + "cfp-service-main"
          + File.separator
          + "src"
          + File.separator
          + "main"
          + File.separator
          + "resources"
          + File.separator
          + "maptemplates"
          + File.separator;

  /**
   * Constants needed to make the base URI of the restClient
   *
   * @author rsyed
   */
  public static final String remoteIP = "localhost";

  public static final String remotePort = "8888";
  public static final String remoteBinder = "/api/";

  public enum AI {
    HUMAN,
    RANDOM,
    SIMPLE_RANDOM
  }

  public enum Port {
    DEFAULTPORT("8888");

    public final String label;

    private Port(String label) {
      this.label = label;
    }
  }
}
