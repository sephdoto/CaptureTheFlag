package org.ctf.ui;

import java.io.File;
import org.ctf.shared.constants.Constants;
import javafx.scene.image.Image;

public class ImageCreator {
  
  public static Image loadThemedImage(String name) {
    Image image = new Image(new File(
        Constants.toUIResources + "pictures" + File.separator + name + Constants.theme + ".png")
            .toURI().toString());
    return image;
  }
  
  public static Image loadStandardImage(String name) {
    Image image = new Image(new File(
        Constants.toUIResources + "pictures" + File.separator + name  + ".jpg")
            .toURI().toString());
    return image;
  }
  
  
  
  
  
  
}
