package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import javafx.scene.image.Image;

public class ImageGetter {

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
   * Loads a certain image from a certain folder.
   * Tries to load .png or .jpg, if they don't exists it loads a fallback image
   * 
   * @param type Type of the image
   * @param imageName Name of the image, without name ending (.png, .jpg, ...)
   * @return 
   */
  public static Image loadThemedImage(ImageType type, String imageName) {
    try {
      String location = new File(Constants.toUIPictures + Constants.theme.toString().toLowerCase() + File.separator + type.getFolderName()).toURI().toURL().toString().replace("file:/", "");
      if(new File(location + imageName + ".png").exists())
        return new Image("file:/" + location + imageName + ".png");
      else if(new File(location + imageName + ".jpg").exists())
        return new Image("file:/" + location + imageName + ".jpg");
      else 
        return new Image("file:/" + location + "Default" + ".png");
    } catch(Exception e) {
      e.printStackTrace();
      return loadFallbackImage(type);
    }
  }

  public static Image loadFallbackImage(ImageType type) {
    Image image = new Image(new File(
        Constants.toUIPictures + "fallback" + File.separator + type.getFolderName() + "missingImage.png")
        .toURI().toString());
    return image;
  }
}
