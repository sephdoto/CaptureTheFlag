package org.ctf.shared.fileservices;

import java.io.File;

/**
 * Interface outlining the basic functionality the JAR File Services have have to provide
 *
 * @author rsyed
 */
public interface JARFileServices {

  /**
   * Fetches a file specifed in the input params with the specific file extension
   *
   * @param fileName the filename you want to load
   * @param extension the extension of the fileName
   * @returns
   * @throws
   * @throws
   * @throws
   */
  public File getFile(String fileName, String extension);

  /**
   * Saves a file specifed in the input params with the specific file extension in the Jar
   *
   * @param file the file to save
   * @param dir the directory where the file has to be saved
   * @param fileName the name of the file
   * @param extension the extension of the file
   * @returns True if save was success, False on faliure
   * @throws
   * @throws
   * @throws
   */
  public boolean saveFile(File file, String dir, String fileName, String extension);
}
