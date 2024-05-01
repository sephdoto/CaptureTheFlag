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
  
  final static int IMAGES_AMOUNT = 5;
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
        intGrid[y][x] = grid[y][x].equals("") ? 0 : 1;
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
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        BufferedImage file = null;
        int color = grid[y][x];
        switch (color) {
          case 0:
            break;
          case 1:
            // file = ImageIO.read(new File(Constants.toUIResources + "black.png"));
            file = ImageIO
                .read(new File(Constants.toUIResources + "pictures" + File.separator + "p1.png"));
            break;
          case 2:
            //file = ImageIO.read(new File(Constants.toUIResources + "white.png"));
            file = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"p2.png"));
            break;
          case 3:
            file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),90);
            break;
          case 4: 
            file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),180);
            break;
          case 5: 
            file = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),270);
            break;
            /*
          case 6:
            file = ImageIO.read(new File(Constants.toUIResources + "waterrock.png"));
            break;
          case 7: 
            file = ImageIO.read(new File(Constants.toUIResources + "edgewater.png"));
            break;
          case 8: 
            file = ImageIO.read(new File(Constants.toUIResources + "edgeroad.png"));
            break;
          case 9: 
            file = ImageIO.read(new File(Constants.toUIResources + "edgerock.png"));
            break;
            
            */
          default:
            break;
        }
        Graphics g = result.getGraphics();
        g.drawImage(file, x * 40, y * 40, x * 40 + 40, y * 40 + 40, 0, 0, 40, 40, null);
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
