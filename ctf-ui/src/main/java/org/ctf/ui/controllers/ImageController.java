package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.Themes;
import org.springframework.util.StringUtils;
import javafx.scene.image.Image;

public class ImageController {

  /**
   * Loads a certain image from a certain folder. Uses a specified theme.
   * Tries to load .png or .jpg, if they don't exists it loads a fallback image
   * 
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return 
   */
  public static Image loadImage(ImageType type, Themes theme, String imageName) {
    try {
      String location = new File(Constants.toUIPictures + theme.toString().toLowerCase() + File.separator + type.getFolderName()).toURI().toURL().toString();

      try {
        return new Image(location + imageName + ".png");
      } catch (Exception e) {e.printStackTrace();}
      try {
        return new Image(location + imageName + ".jpg");
      } catch (Exception e) {e.printStackTrace();}
      try {
        return new Image(location + "Default" + ".png");
      } catch(Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }      
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
    Path targetPath = Paths.get(
        Constants.toUIPictures + 
        theme.toString().toLowerCase() + 
        File.separator + 
        type.getFolderName() + 
        pieceName + 
        "." + 
        StringUtils.getFilenameExtension(image.getPath()
            ));

    try {
      Files.copy(sourcePath, targetPath);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  /**
   * Loads pictures from a given folderName.
   * Picks a random picture out of the folder, allowing several backgrounds ...
   * 
   * @author sistumpf
   * @param  folderName Name of the requested picture types folder name, like "homescreen"
   * @return a fitting picture
   */
  public static Image loadRandomThemedImage(ImageType type) {
    String location = Constants.toUIPictures + Constants.theme.toString().toLowerCase() + File.separator + type.getFolderName();
    if(new File(location).list().length > 0) {
      try {
        List<Path> list = Files.list(Path.of(location)).toList();
        return new Image(list.get(ThreadLocalRandom.current().nextInt(list.size())).toAbsolutePath().toUri().toString());
      } catch (Exception e) {
        e.printStackTrace();
        return loadFallbackImage(type);
      }
    }else {
      return loadFallbackImage(type);
    }
  }

  /**
   * Loads a certain image from a certain folder. Uses the current theme.
   * Tries to load .png or .jpg, if they don't exists it loads a fallback image
   * 
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return 
   */
  public static Image loadThemedImage(ImageType type, String imageName) {
    return loadImage(type, Constants.theme, imageName);
  }

  /**
   * Loads a "missing image" image, to show that something went extremely wrong or an image has not been imported yet.
   * 
   * @author sistumpf
   * @param type The fallback images type
   * @return the fallback image
   */
  public static Image loadFallbackImage(ImageType type) {
    Image image = new Image(new File(
        Constants.toUIPictures + "fallback" + File.separator + type.getFolderName() + "missingImage.png")
        .toURI().toString());
    return image;
  }
}
