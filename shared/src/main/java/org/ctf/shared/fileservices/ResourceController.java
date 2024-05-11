package org.ctf.shared.fileservices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import org.ctf.shared.constants.Constants;

public class ResourceController {
  Logger logger = Logger.getLogger(getClass().getName());

  /**
   * Copies a directory from inside a JAR file to a target directory.
   *
   * @param sauce The JAR file
   * @param path The path to the directory inside the JAR file. Starts like "folder1\\folder2"
   * @param target The target directory. Pass it a new File instance
   */
  public void copyDirectoryFromJar(JarFile sauce, String[] path, File[] target) {
    try {
      Enumeration<JarEntry> jarEntries = sauce.entries();
      for(int i = 0;i<path.length;i++){
        String newpath = String.format("%s/", path[i]);
        while (jarEntries.hasMoreElements()) {
          JarEntry entry = jarEntries.nextElement();
          if (entry.getName().startsWith(newpath) && !entry.isDirectory()) {
            File destination = new File(target[i], entry.getName().substring(newpath.length()));
            File parent = destination.getParentFile();
            if (parent != null) {
              parent.mkdirs();
            }
            this.writeToFile(sauce.getInputStream(entry), destination);
          }
        }
      }
    } catch (IOException e) {
      logger.info("Could not write file in copyDirectoryFromJar");
    }
  }

  /**
   * The JAR file containing the given class.
   *
   * @param clazz The class
   * @return Optional of JAR. Else its empty
   * @throws Exception If there is an error reading the file. Caught and given out in the logger
   */
  public Optional<JarFile> jar(Class<?> clazz) {
    Optional<JarFile> opt = Optional.empty();
    try {
      String path = String.format("/%s.class", clazz.getName().replace('.', '/'));
      URL url = clazz.getResource(path);
      if (url != null) {
        String jar = url.toString();
        int oshit = jar.indexOf('!');
        if (jar.startsWith(Constants.JAR_PREFIX) && oshit != -1) {
          opt = Optional.of(new JarFile(jar.substring(Constants.JAR_PREFIX.length(), oshit)));
        }
      }
    } catch (Exception e) {
      logger.info("Finding a jar failed in ResourceController");
    }
    return opt;
  }

  /**
   * Writes an input stream to a file.
   *
   * @param input The input stream
   * @param target The target file
   * @throws IOException If there is an read/write error. Caught and given out in the logger
   */
  private void writeToFile(InputStream input, File target) {
    try (OutputStream output = Files.newOutputStream(target.toPath());) {
      byte[] buffer = new byte[Constants.BUFFER_SIZE];
      int length = input.read(buffer);
      while (length > 0) {
        output.write(buffer, 0, length);
        length = input.read(buffer);
      }
      input.close();
    } catch (Exception e) {
      logger.info("writeToFile Failed in ResourceController");
    }
  }

  public static void main(String[] args) {

    try {
      JarFile jFile = new JarFile(Constants.JARPARENTFOLDER + Constants.JARNAME);
      ResourceController rc = new ResourceController();
      rc.copyDirectoryFromJar(jFile, new String[]{Constants.RESOURCEFOLDERNAME}, new File[]{new File(Constants.JARRESOURCES)});

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
