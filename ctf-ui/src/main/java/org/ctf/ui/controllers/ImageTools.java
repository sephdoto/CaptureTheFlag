package org.ctf.ui.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import org.ctf.shared.constants.Constants;

/**
 * Object for reading and writing image Files
 *
 * @author rsyed
 */
public class ImageTools {

  private BufferedImage bi;
  LocalDateTime localDateTime;

  /**
   * This method opens up a dialog on call and returns a true if read was successful. Call
   * getLoadedImage() to get the {@link BufferedImage} object
   *
   * @author rsyed
   */
  public boolean LoadImage() {
    try {
      final JFileChooser fc = new JFileChooser(Constants.dataBankPath);
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fc.showOpenDialog(null);
      bi = ImageIO.read(fc.getSelectedFile());
    } catch (IOException e) {
      System.out.println("Image could not be read");
      return false;
    }
    return true;
  }

  /**
   * Returns the {@link BufferedImage} object which was read using LoadImage()
   *
   * @author rsyed
   */
  public BufferedImage getLoadedImage() {
    return this.bi;
  }

  /**
   * Saves the {@link BufferedImage} object which was passed to it. Returns a boolean for
   * confirmation. True on success, false on failure. Saves the file with the current Time stamp as
   * name
   *
   * @param saveThis The buffered Image object to save
   * @author rsyed
   */
  public boolean SaveImage(BufferedImage saveThis) {
    localDateTime = LocalDateTime.now();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
    String fileName = localDateTime.format(df);
    try {
      ImageIO.write(saveThis, "png", new File(Constants.dataBankPath + fileName + ".png"));
    } catch (IOException ex) {
      return false;
    }
    return true;
  }
}
