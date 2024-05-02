package org.ctf.shared.wave;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * The class to call when creating a background with the wave function collapse algorithm. How to
 * use: 
 * <p>
 * - Create a new instance of WaveFunctionCollapse with the String[][] grid representing the map as
 *  input 
 * </p>
 * <p>
 * - Call the getBackground method to get the finished image
 * </p>
 * @author ysiebenh
 */
public class WaveFunctionCollapse {
  
  final static int IMAGES_AMOUNT = 28;
  private int[][] grid;
  private boolean collapsed = false;
  private BufferedImage background;

  /**
   * Constructor that parses the String[][] grid (created by the GameEngine) into an integer which the
   * algorithm is going to use internally to keep track which image goes where.
   * 
   * @param grid
   */
  public WaveFunctionCollapse(String[][] grid) {
    int[][] intGrid = new int[grid.length][grid[0].length];
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        intGrid[y][x] = grid[y][x].equals("") ? 0 : 24;
      }
    }
    this.grid = generateBackground(intGrid);
  }

  /**
   * The actual wave function collapse algorithm takes place here. 
   * <p>
   * TODO Make this private once the algorithm works perfectly
   * </p>
   * 
   * @param grid the initial grid
   * @return the finished grid
   */
  public int[][] generateBackground(int[][] grid) {

    // TODO add backtracking
    long nowMillis = System.currentTimeMillis();    // Logging how long the process takes 
                                                    // TODO remove this later

    WaveGrid wGrid = new WaveGrid(grid, IMAGES_AMOUNT);         
    
    //The main algorithm:
    while (!collapsed) {
      LinkedList<Tile> possibleTiles = new LinkedList<Tile>();
      ArrayList<Tile> tileCopy = new ArrayList<Tile>(wGrid.tiles);

      tileCopy.sort(new Comparator<Tile>() {
        @Override
        public int compare(Tile a, Tile b) {
          return Integer.compare(a.options.size(), b.options.size());
        }
      });
      ArrayList<Tile> toRemove = new ArrayList<Tile>();
      for (Tile t : tileCopy) {
        if (t.options.size() == 0) {
          toRemove.add(t);
        }
      }
      for (Tile t : toRemove) {
        tileCopy.remove(t);
      }
      if (tileCopy.size() == 0) {
        break;
      }
      int bestEntropy = tileCopy.get(0).options.size();
      for (Tile t : wGrid.tiles) {
        if (t.options.size() == bestEntropy) {
          possibleTiles.add(t);
        }
      }
      if (possibleTiles.size() == 0) {
        collapsed = true;
        break;
      }
      Tile thisTile = possibleTiles.get((int) (Math.random() * possibleTiles.size()));
      thisTile.setValue(thisTile.options.get((int) (Math.random() * thisTile.options.size())));
    }

    try {
      background = gridToImg(wGrid.grid);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out
        .println(" generateBackground took " + (System.currentTimeMillis() - nowMillis) + "ms"); 
    return wGrid.grid;

  }
  
  private BufferedImage[] loadImages() throws IOException {
    BufferedImage[] images = new BufferedImage[IMAGES_AMOUNT+1];
      
        images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"p1.png"));
        
        images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"black.png"));
        //file = ImageIO
        //    .read(new File(Constants.toUIResources + "pictures" + File.separator + "p1.png"));

        //file = ImageIO.read(new File(Constants.toUIResources + "white.png"));
        //file = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"p2.png"));
        images[2] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r2.png"));
        images[3] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r2.png")),90);
        images[4] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r2.png")),180);
        images[5] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r2.png")),270);
        //file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),90);
        images[6] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r3.png")); 
        images[7] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r3.png")),90);
        //file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),180);
        images[8] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r4.png"));
        images[9] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r4.png")),90);
        images[10] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r4.png")),180);
        images[11] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r4.png")),270);
        //file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),270);
        images[12] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r5.png"));
        images[13] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r5.png")),90);
        images[14] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r5.png")),180);
        images[15] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r5.png")),270);
        //file = ImageIO.read(new File(Constants.toUIResources + "waterrock.png"));
        images[16] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r6.png"));
        images[17] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r6.png")),90);
        images[18] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r6.png")),180);
        images[19] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r6.png")),270);
        //file = ImageIO.read(new File(Constants.toUIResources + "edgewater.png"));
        images[20] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r7.png"));
        images[21] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r7.png")),90);
        images[22] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r7.png")),180);
        images[23] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r7.png")),270);
        //file = ImageIO.read(new File(Constants.toUIResources + "edgeroad.png"));
        images[24] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r8.png"));
        //file = ImageIO.read(new File(Constants.toUIResources + "edgerock.png"));
        images[25] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r9.png"));
        images[26] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r9.png")),90);
        images[27] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r9.png")),180);
        images[28] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"r9.png")),270);

    return images;
  }
  
  /**
   * Parses an integer grid into an image using the png files supplied in UIResources.
   * 
   * @param grid
   * @return the image defined by the grid as a BufferedImage
   * @throws IOException
   */
  private BufferedImage gridToImg(int[][] grid) throws IOException {
    long nowMillis = System.currentTimeMillis();
    BufferedImage result =
        new BufferedImage(40 * grid[0].length, 40 * grid.length, BufferedImage.TYPE_INT_ARGB);
    BufferedImage[] files = loadImages();
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        int color = grid[y][x];
        BufferedImage file = files[color];
        Graphics g = result.getGraphics();
        g.drawImage(file, x * 40, y * 40, x * 40 + 40, y * 40 + 40, 0, 0, 12, 12, null);
      }
    }
    System.out.println(" generateImageFromGrid took " + (System.currentTimeMillis() - nowMillis) + "ms");
    return result;
  }
  
  /**
   * Getter for the grid. 
   * @return
   */
  public int[][] getGrid() {
    return grid;
  }
  
  /**
   * used in the gridToImg method to turn pngs around. Taken from StackOverFlow
   * 
   * @see <a href=
   *      "https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java">Stackoverflow Reference</a>
   * 
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
    // g2d.setColor(Color.RED);
    // g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
    g2d.dispose();

    return rotated;
  }

  /**
   * Getter for the Background image. Call this method after creating the WaveFunctionCollapse
   * object to get the finished background
   * 
   * @return
   */
  public BufferedImage getBackground() {
    return background;
  }
}
