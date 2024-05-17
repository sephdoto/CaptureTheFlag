package org.ctf.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.ctf.shared.constants.Constants;
import javafx.scene.image.Image;

public class ImageCreator {

  /**
   * Loads pictures from a given folderName.
   * Picks a random picture out of the folder, allowing several backgrounds ...
   * 
   * @author sistumpf
   * @param  folderName Name of the requested picture types folder name, like "homescreen"
   * @return a fitting picture
   */
  public static Image loadThemedImage(String folderName) {
    String location = Constants.toUIResources + "pictures" + File.separator + Constants.theme.toString().toLowerCase() + File.separator + folderName + File.separator;
    if(new File(location).list().length > 0) {
      try {
        List<Path> list = Files.list(Path.of(location)).toList();
        return new Image(list.get(ThreadLocalRandom.current().nextInt(list.size())).toAbsolutePath().toUri().toString());
      } catch (IOException e) {
        e.printStackTrace();
        return loadDefaultImage();
      }
    }else {
      return loadDefaultImage();
    }
  }

  public static Image loadDefaultImage() {
    Image image = new Image(new File(
        Constants.toUIResources + "pictures" + File.separator + "fallback" + File.separator + "missingImage.png")
        .toURI().toString());
    return image;
  }






}
