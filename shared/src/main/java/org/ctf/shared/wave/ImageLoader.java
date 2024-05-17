package org.ctf.shared.wave;


import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;

/**
 * Helps with loading the images. Used in WaveFunctionCollapse -> gridToImg.
 * 
 * @author ysiebenh
 */
class ImageLoader {
 
  // **************************************************
  // Fields
  // **************************************************
  
  private int imagesAmount;
 
  // **************************************************
  // Constructor
  // **************************************************
  
  ImageLoader(){
    this.imagesAmount = WaveFunctionCollapse.imagesAmount;
  }

  /**
   * Controller used in the gridToImg method to choose which images to load according to the
   * theme used.
   * 
   * @param theme
   * @return 
   * @throws IOException
   */
  BufferedImage[] loadImages(Themes theme) throws IOException{

    if(theme == Themes.STARWARS) {
      return loadSWImages();
    }
    else if(theme == Themes.BAYERN) {
      return loadBayernImages();
    }
    else if(theme == Themes.LOTR) {
      return loadLOTRImages();
    }
    else return null;
  }
  
  /**
   * Pre-loads the images used in the Star Wars pattern. The pattern was originally inspired by a pattern called "circuits" designed by 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadSWImages() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount+6];

    WaveFunctionCollapse.instance
        .setBlock(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
            + "starwars" + File.separator + "WaveFunctionTiles" + File.separator + "Block.png")));
    WaveFunctionCollapse.instance
        .setBase(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
            + "starwars" + File.separator + "WaveFunctionTiles" + File.separator + "BaseSW.png")));
    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
        + "starwars" + File.separator + "WaveFunctionTiles" + File.separator + "c2.png"));

    for (int i = 1, c = 1; c <= imagesAmount + 4; i++) {
      if (i == 1 || i == 2) {
        images[c++] =
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png"));
      } else if (i == 3 || i == 5 || i == 6 || i == 9 || i == 10 || i == 12 || i == 14) {
        images[c++] = ImageIO.read(
            new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png"));
        images[c++] = this.rotateImageByDegrees(
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png")),
            90);
        images[c++] = this.rotateImageByDegrees(
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png")),
            180);
        images[c++] = this.rotateImageByDegrees(
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png")),
            270);
      } else {
        images[c++] =
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png"));
        images[c++] = this.rotateImageByDegrees(
            ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "starwars"
                + File.separator + "WaveFunctionTiles" + File.separator + "c" + i + ".png")),
            90);

      }
    }
    return images;
  }
  
  /**
   * Pre-loads the images used in the Lord of the Rings pattern. The pattern was originally drawn by
   * Hermann Hillmann and found on the WaveFunctionCollapse github page (see header).
   * 
   * @return
   * @throws IOException
   */
  @Deprecated
  private BufferedImage[] loadLOTRImagesOld() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"l1.png"));
    WaveFunctionCollapse.instance.setBlock(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tree.png")));
    WaveFunctionCollapse.instance.setBase(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tuning1.png")));

    for(int i = 1; i <= 48; i++) {
      images[i] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"l" + i + ".png"));
    }
    
    return images;
  }
  
  /**
   * Pre-loads the images used in the Bayern pattern. 
   * 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadBayernImages() throws IOException {
    int franken = (int) (Math.random() * 20);
    String bayern = franken == 1 ? "Franken.png" : "Bayern.png"; // Franken Easteregg
    BufferedImage[] images = new BufferedImage[imagesAmount + 1];
    WaveFunctionCollapse.instance
      .setBlock(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator 
           + "bayern" + File.separator + "WaveFunctionTiles" + File.separator + "noweed.png")));
    
    WaveFunctionCollapse.instance
      .setBase(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
            + "bayern" + File.separator + "WaveFunctionTiles" + File.separator + "Ei.png")));
    
    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
        + "bayern" + File.separator + "WaveFunctionTiles" + File.separator + bayern));
    
    images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
        + "bayern" + File.separator + "WaveFunctionTiles" + File.separator + bayern));
    
    images[2] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "bayern"
            + File.separator + "WaveFunctionTiles" + File.separator + bayern)),
        90);
    
    images[3] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "bayern"
            + File.separator + "WaveFunctionTiles" + File.separator + bayern)),
        180);
    
    images[4] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "bayern"
            + File.separator + "WaveFunctionTiles" + File.separator + bayern)),
        270);
    
    return images;

  }
  
  @Deprecated
  private BufferedImage[] loadRoomImages() throws IOException {
    //Image amount = 28
    
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    WaveFunctionCollapse.instance.setBlock(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tree.png")));

        images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"black.png"));
        images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"black.png"));

        for(int i = 2, c = 2; c <= imagesAmount; i++) {
          if(i == 8) {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png"));
          }
          else if(i == 2 || i == 4 || i == 5 || i == 6 || i == 7 || i == 9) {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png"));
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png")),90);
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png")),180);
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png")),270);
          }
          else {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png")); 
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "r" + i + ".png")),90);
           
          }
        }
    return images;
  }
  
  /**
   * Pre-loads the images used in the Lord of the Rings pattern.
   * 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadLOTRImages() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount + 1];
    WaveFunctionCollapse.instance
        .setBlock(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
            + "lotr" + File.separator + "WaveFunctionTiles" + File.separator + "tree.png")));
    WaveFunctionCollapse.instance
        .setBase(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator
            + "lotr" + File.separator + "WaveFunctionTiles" + File.separator + "tree.png")));

    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
        + File.separator + "WaveFunctionTiles" + File.separator + "p1.png"));

    images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
        + File.separator + "WaveFunctionTiles" + File.separator + "p1.png"));
    images[2] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
        + File.separator + "WaveFunctionTiles" + File.separator + "p2.png"));
    images[3] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p2.png")),
        90);
    images[4] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p2.png")),
        180);
    images[5] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p2.png")),
        270);

    images[6] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
        + File.separator + "WaveFunctionTiles" + File.separator + "p3.png"));
    images[7] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p3.png")),
        90);
    images[8] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p3.png")),
        180);
    images[9] = this.rotateImageByDegrees(
        ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "lotr"
            + File.separator + "WaveFunctionTiles" + File.separator + "p3.png")),
        270);

    return images;
  }

  /**
   * Used to turn pngs around. Taken from StackOverFlow
   * 
   * @see <a href=
   *      "https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java">Stackoverflow Reference</a>
   * 
   * TODO add author to credits
   * @param img the image to be rotated
   * @param angle the angle by which it wants to be rotated
   * @return the rotated image as BufferedImage
   */
  private BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
    double rads = Math.toRadians(angle);
    double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
    int w = img.getWidth();
    int h = img.getHeight();
    int newWidth = (int) Math.floor(w * cos + h * sin);
    int newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = rotated.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

    int x = w / 2;
    int y = h / 2;

    at.rotate(rads, x, y);
    g2d.setTransform(at);
    g2d.drawImage(img, 0, 0, null);
    g2d.dispose();

    return rotated;
  }
}
