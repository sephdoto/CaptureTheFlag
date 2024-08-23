package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.Themes;
import org.springframework.util.StringUtils;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

/**
 * Tools for loading and saving images from/to their corresponding place in ui.resources.
 *
 * @author sistumpf
 */
public class ImageController {
  /**
   * Checks if a picture already exists in resources and hence cannot be changed
   *
   * @author sistumpf
   * @param type the type the image belongs to
   * @param theme the theme the image got chosen for
   * @param imageName the name of the picture without extension
   * @return true if no picture exists yet
   */
  public static boolean canBeChanged(ImageType type, Themes theme, String imageName) {
    File location =
        new File(
            Constants.toUIPictures
                + theme.toString().toLowerCase()
                + File.separator
                + type.getFolderName());
    for (String extension : getAllExtensionsIn(location)) {
      if (Files.exists(
          Paths.get(location.getAbsolutePath() + File.separator + imageName + "." + extension)))
        return false;
    }
    return true;
  }

  /**
   * Loads a certain image from a certain folder. Uses a specified theme. Tries to load the picture,
   * if it doesn't exists it tries a default and in the worst case a fallback image
   *
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return
   */
  public static Image loadImage(ImageType type, Themes theme, String imageName) {
    //    System.out.println(Constants.toUIPictures + theme.toString().toLowerCase() +
    // File.separator + type.getFolderName()+ imageName);
    try {
      File locationFile =
          new File(
              Constants.toUIPictures
                  + theme.toString().toLowerCase()
                  + File.separator
                  + type.getFolderName());
      String location = locationFile.toURI().toURL().toString();

      // try loading the image with all available extensions
      for (String extension : getAllExtensionsIn(locationFile)) {
        if (new File(locationFile.getAbsolutePath() + File.separator + imageName + "." + extension)
            .exists()) return new Image(location + imageName + "." + extension);
      }
      // try loading the default image if the previous step didn't work
      try {
        return new Image(location + "Default" + ".png");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // load fallback image if something REALLY failed
    return loadFallbackImage(type);
  }

  /**
   * Saves an image given by a file to a location inside the resource folder.
   *
   * @author sistumpf
   * @param image a file pointing at the image
   * @param type the type the image belongs to
   * @param theme the theme the image got chosen for
   * @param pieceName name of the piece
   * @return true if the image got saved
   */
  public static boolean saveImage(File image, ImageType type, Themes theme, String pieceName) {
    Path sourcePath = image.toPath();
    Path targetPath =
        Paths.get(
            Constants.toUIPictures
                + theme.toString().toLowerCase()
                + File.separator
                + type.getFolderName()
                + pieceName
                + "."
                + StringUtils.getFilenameExtension(image.getPath()));

    try {
      Files.copy(sourcePath, targetPath);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Loads pictures from a given folderName. Picks a random picture out of the folder, allowing
   * several backgrounds ...
   *
   * @author sistumpf
   * @param type the ImageType
   * @return a fitting picture
   */
  public static Image loadRandomThemedImage(ImageType type) {
    String location =
        Constants.toUIPictures
            + Constants.theme.toString().toLowerCase()
            + File.separator
            + type.getFolderName();
    if (new File(location).list().length > 0) {
      try {
        List<Path> list = Files.list(Path.of(location)).toList();
        return new Image(
            list.get(ThreadLocalRandom.current().nextInt(list.size()))
                .toAbsolutePath()
                .toUri()
                .toString());
      } catch (Exception e) {
        e.printStackTrace();
        return loadFallbackImage(type);
      }
    } else {
      return loadFallbackImage(type);
    }
  }

  /**
   * Uses loadThemedImage to load an image as a background
   * 
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return the image as an instance of Background
   */
  public static Background loadThemesBackgroundImage(ImageType type, String imageName) {
    return new Background(
        new BackgroundImage(
            loadThemedImage(type, imageName),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(1, 1, true, true, true, true)
            )
        );
  }
  
  /**
   * Loads a certain image from a certain folder. Uses the current theme. Tries to load .png or
   * .jpg, if they don't exists it loads a fallback image
   *
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return the requested image
   */
  public static Image loadThemedImage(ImageType type, String imageName) {
    return loadImage(type, Constants.theme, imageName);
  }

  /**
   * Loads a "missing image" image, to show that something went extremely wrong or an image has not
   * been imported yet.
   *
   * @author sistumpf
   * @param type The fallback images type
   * @return the fallback image
   */
  public static Image loadFallbackImage(ImageType type) {
    Image image =
        new Image(
            new File(
                    Constants.toUIPictures
                        + "fallback"
                        + File.separator
                        + type.getFolderName()
                        + "missingImage.png")
                .toURI()
                .toString());
    return image;
  }

  /**
   * Returns all file extensions in a given directory
   *
   * @author sistumpf
   * @param dir the directory which gets scanned for the extensions
   * @return all possible extensions
   */
  private static HashSet<String> getAllExtensionsIn(File dir) {
    HashSet<String> extSet = new HashSet<String>();
    if (!dir.isDirectory()) return extSet;

    for (File file : dir.listFiles()) extSet.add(StringUtils.getFilenameExtension(file.getPath()));

    return extSet;
  }
}
