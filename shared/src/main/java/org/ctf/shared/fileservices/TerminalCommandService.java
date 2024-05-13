package org.ctf.shared.fileservices;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class TerminalCommandService {
  static final String SPACE = " ";
  Logger logger = Logger.getLogger(getClass().getName());

  public static void main(String[] args) throws IOException {
    Process proc =
        Runtime.getRuntime()
            .exec(
                "jar -uvf \"F:\\Test Folder\\app.jar\" -C \"F:\\Test Folder\\r"
                    + "esources_ctf_team_14\" resources");

    proc.getInputStream().transferTo(System.out);
    proc.getErrorStream().transferTo(System.out);
    // String path = "cmd \"F:\\Test Folder\"";
    // "F:\\Test Folder\\";
    // path = path.replaceAll(" ", "\\\\ ");
    // System.out.println(path);
    // listJarFiles(path, "app.jar");
  }

  public static void addFileToJar(File file, String path) {
    String commandBase = "jar" + SPACE + "uf" + SPACE + "path";
    runCommandinShell(commandBase);
  }

  public static void listJarFiles(String path, String fileName) {
    String commandBase = "jar" + SPACE + "tf" + SPACE + path + "\\" + fileName;
    runCommandinShell(commandBase);
  }

  public static void runCommandinShell(String command) {
    try {
      Process proc = Runtime.getRuntime().exec(command);
      proc.getInputStream().transferTo(System.out);
      proc.getErrorStream().transferTo(System.out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
