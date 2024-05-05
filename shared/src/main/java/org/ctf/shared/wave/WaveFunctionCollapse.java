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
import java.util.Stack;

/**
 * Initiate this class when creating a background with the wave function collapse algorithm. How to
 * use:
 * <p>
 * - Create a new instance of WaveFunctionCollapse with the String[][] grid representing the map as
 * input
 * </p>
 * <p>
 * - Call the getBackground method to get the finished image
 * </p>
 * 
 * @author ysiebenh
 */
public class WaveFunctionCollapse {
  
  final public static int IMAGES_AMOUNT = 36;
  final public static int IMAGE_SIZE = 14;
  private int iterationsCounter;
  private String[][] ogGrid;
  private int[][] grid;
  private boolean collapsed;
  private BufferedImage background;
  private Stack<WaveGrid> lastStates;
  

  /**
   * Constructor that parses the String[][] grid (created by the GameEngine) into an integer which the
   * algorithm is going to use internally to keep track which image goes where.
   * 
   * @param grid
   */
  public WaveFunctionCollapse(String[][] grid) {
    ogGrid = grid;
    lastStates = new Stack<WaveGrid>();
    collapsed = false;
    int[][] intGrid = stringToInt(grid);
    this.grid = generateBackground(intGrid);
    //this.grid = generateBackgroundRecursive(new WaveGrid(intGrid, IMAGES_AMOUNT));
    try {
      background = gridToImg(this.grid);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Generates an integer grid that is three times the size of the original String grid and fills it
   * with the String values.
   * 
   * @param grid
   * @return
   */
  private int[][] stringToInt(String[][] grid) {
    int[][] intGrid = new int[grid.length * 3][grid[0].length * 3];
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[0].length; x++) {
        if(grid[y][x] == null) {
          intGrid[y*3 + 1][x*3 +1] = 0;
        }
        else if (grid[y][x].contains("p") ) {
          intGrid[y*3][x*3] = 1;
          intGrid[y*3][x*3+1] = 1;
          intGrid[y*3][x*3+2] = 1;
          
          intGrid[y*3+1][x*3] = 1;
          intGrid[y*3+1][x*3+1] = 1;
          intGrid[y*3+1][x*3+2] = 1;
          
          intGrid[y*3+2][x*3] = 1;
          intGrid[y*3+2][x*3+1] = 1;
          intGrid[y*3+2][x*3+2] = 1;
        }
        /*
        else if(grid[y][x].equals("b")) {
          
          intGrid[y*3][x*3] = 2;
          intGrid[y*3][x*3+1] = 2;
          intGrid[y*3][x*3+2] = 2;
          
          intGrid[y*3+1][x*3] = 2;
          intGrid[y*3+1][x*3+1] = 2;
          intGrid[y*3+1][x*3+2] = 2;
          
          intGrid[y*3+2][x*3] = 2;
          intGrid[y*3+2][x*3+1] = 2;
          intGrid[y*3+2][x*3+2] = 2;
        }
        */
      }
    }
    return intGrid;
  }

  /**
   * The actual wave function collapse algorithm. Generates a pattern from an initial set of images
   * and rules.
   * <p>
   * TODO Make this private once the algorithm works perfectly
   * </p>
   * 
   * @param grid the initial grid
   * @return the finished grid
   */
  private int[][] generateBackground(int[][] grid) {
   
    // TODO add backtracking
    
    //timing the method TODO remove when done 
    long nowMillis = System.currentTimeMillis();

    //creating the Grid
    WaveGrid wGrid = new WaveGrid(grid, IMAGES_AMOUNT);         
    
    //The main algorithm:
    while (!collapsed) {
      LinkedList<Tile> chosenTiles = new LinkedList<Tile>();        //to store the tiles with the lowest entropy
      ArrayList<Tile> tileCopy = new ArrayList<Tile>(wGrid.tiles);  //create a copy of all the tiles 

      tileCopy.sort(new Comparator<Tile>() {                        //sorting all the tiles by entropy 
        @Override
        public int compare(Tile a, Tile b) {
          return Integer.compare(a.options.size(), b.options.size());
        }
      });
      
     
      ArrayList<Tile> toRemove = new ArrayList<Tile>(); // removing all collapsed tiles
      for (Tile t : tileCopy) {
        if (t.collapsed) {
          toRemove.add(t);
        }
      }
      for (Tile t : toRemove) {
        tileCopy.remove(t);
      }                                                 // Done removing tiles
      
      if (tileCopy.size() == 0) {                       // if no more tiles are unplaced we are done
        collapsed = true;
        break;
      }

      int bestEntropy = tileCopy.get(0).options.size(); // choosing the tile with the lowest entropy
      for (Tile t : tileCopy) {
        if (t.options.size() == bestEntropy) {
          chosenTiles.add(t);
        }
      }
      
      
      
      //Choosing which tile to collapse and which image to use at random 
      Tile thisTile = chosenTiles.get((int) (Math.random() * chosenTiles.size()));

      //Backtracking for dummies: If dead end is reached --> start over
      //Alternative: just skip the tile that doesnt work (current state)
      if (thisTile.options.size() == 0) {

        //return generateBackground(grid);
        
        thisTile.collapsed = true;
        continue;
        
      }
      
      //thisTile.setValue(thisTile.options.get((int) (Math.random() * thisTile.options.size())));
      //lastTile.push(thisTile);
      thisTile.setValue(thisTile.options.get(randomWithWeights(thisTile.options.size(), thisTile.getWeights())));
    }
    
    //filling in the gaps because the original pattern does not work with corners 
    for(Tile t : wGrid.tiles) {
      if (t.getValue() == 0) {
        if (t.getLeftNeighbor() != null && t.getLeftNeighbor().getValue() == 1
            && t.getLowerNeighbor() != null && t.getLowerNeighbor().getValue() == 1) {
          t.setValueSimple(38);
        }
       if (t.getUpperNeighbor() != null && t.getUpperNeighbor().getValue() == 1 
           && t.getLeftNeighbor() != null && t.getLeftNeighbor().getValue() == 1) {
         t.setValueSimple(39);
       }
       if (t.getUpperNeighbor() != null && t.getUpperNeighbor().getValue() == 1 
           && t.getRightNeighbor() != null && t.getRightNeighbor().getValue() == 1) {
         t.setValueSimple(40);
       }
       if (t.getRightNeighbor() != null && t.getRightNeighbor().getValue() == 1 
           && t.getLowerNeighbor() != null && t.getLowerNeighbor().getValue() == 1) {
         t.setValueSimple(41);
       }
      }
    }
    //timing the method TODO remove later 
    System.out
        .println(" generateBackground took " + (System.currentTimeMillis() - nowMillis) + "ms"); 
    return wGrid.grid;

  }
  
  /**
   * The actual wave function collapse algorithm. Generates a pattern from an initial set of images
   * and rules. Recursive version.
   * <p>
   * TODO Make this private once the algorithm works perfectly
   * </p>
   * 
   * @param grid the initial grid
   * @return the finished grid
   */
  @Deprecated
  public int[][] generateBackgroundRecursive(WaveGrid waveGrid) {
    WaveGrid wGrid = new WaveGrid(waveGrid.grid, IMAGES_AMOUNT);
    wGrid.tiles = new ArrayList<Tile>(waveGrid.tiles);
    wGrid.grid = waveGrid.grid;
    System.out.println("Step: " + iterationsCounter++);
    LinkedList<Tile> possibleTiles = new LinkedList<Tile>();      
    ArrayList<Tile> tileCopy = new ArrayList<Tile>(wGrid.tiles); //create a copy of all the tiles 
    
    //sorting all the tiles by entropy 
    tileCopy.sort(new Comparator<Tile>() {
      @Override
      public int compare(Tile a, Tile b) {
        return Integer.compare(a.options.size(), b.options.size());
      }
    });
    // removing all tiles with value zero from the copy 
    ArrayList<Tile> toRemove = new ArrayList<Tile>();
    for (Tile t : tileCopy) {
        if(t.collapsed) {
          toRemove.add(t);
        }
    }
    for (Tile t : toRemove) {
      tileCopy.remove(t);
    }
    //Done removing tiles
    
    //if no more tiles are unplaced boom done 
    if (tileCopy.size() == 0) {
      collapsed = true;
      return wGrid.grid;
    }
    
    //choosing the tile with the lowest entropy 
    int bestEntropy = tileCopy.get(0).options.size();
    for (Tile t : tileCopy) {
      if (t.options.size() == bestEntropy) {
        possibleTiles.add(t);
      }
    }
    
    //if the size of the possible tiles is zero then we are done
    if (possibleTiles.size() == 0) {
      collapsed = true;
      return wGrid.grid;
    }
    
    //Choosing which tile to collapse and which image to use at random 
    Tile thisTile = possibleTiles.get((int) (Math.random() * possibleTiles.size()));
    
    //what do we do here? backtrack?
    if(thisTile.options.size() == 0) {
      System.out.println("X = " + thisTile.getX() + " Y = " + thisTile.getY());
      return null;
    }
    
    LinkedList<Integer>optionSaveDynamic = new LinkedList<Integer>(thisTile.options);
    LinkedList<Integer>optionSaveStable = new LinkedList<Integer>(thisTile.options);

    int chosenValue = thisTile.options.get(((int) (Math.random() * thisTile.options.size())));
    thisTile.setValue(chosenValue);
   
    while(this.generateBackgroundRecursive(wGrid) == null) {
      optionSaveDynamic.remove(Integer.valueOf(chosenValue));
      if(optionSaveDynamic.size() == 0) {
        wGrid.grid[thisTile.getY()][thisTile.getX()] = 0;
        thisTile.setValue(0);
        thisTile.options = new ArrayList<Integer>(optionSaveStable);
        thisTile.collapsed = false;
        return null;
      }
      chosenValue = (optionSaveDynamic.get((int) (Math.random() * optionSaveDynamic.size())));
      thisTile.setValue(chosenValue);
    }
      
      
      //System.out.println("X = " + thisTile.getX() + " Y = " + thisTile.getY());
      return wGrid.grid;
      
    
  }
  
  private BufferedImage[] loadImages() throws IOException {
    BufferedImage[] images = new BufferedImage[IMAGES_AMOUNT+6];
      
        images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c2.png"));
        
        images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c1.png"));
        images[2] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c2.png"));
        
        images[3] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c3.png"));
        images[4] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c3.png")),90);
        images[5] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c3.png")),180);
        images[6] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c3.png")),270);

        images[7] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c4.png")); 
        images[8] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c4.png")),90);
       
        images[9] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c5.png"));
        images[10] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c5.png")),90);
        images[11] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c5.png")),180);
        images[12] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c5.png")),270);
        
        images[13] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c6.png"));
        images[14] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c6.png")),90);
        images[15] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c6.png")),180);
        images[16] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c6.png")),270);
        //file = ImageIO.read(new File(Constants.toUIResources + "waterrock.png"));
        images[17] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c7.png"));
        images[18] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c7.png")),90);
        
        images[19] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c8.png"));
        images[20] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c8.png")),90);
        //file = ImageIO.read(new File(Constants.toUIResources + "edgewater.png"));
        images[21] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c9.png"));
        images[22] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c9.png")),90);
        images[23] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c9.png")),180);
        images[24] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c9.png")),270);

        images[25] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c10.png"));
        images[26] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c10.png")),90);
        images[27] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c10.png")),180);
        images[28] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c10.png")),270);

        images[29] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c11.png"));
        images[30] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c11.png")),90);

        images[31] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c12.png"));
        images[32] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c12.png")),90);
        images[33] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c12.png")),180);
        images[34] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c12.png")),270);

        images[35] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c13.png"));
        images[36] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c13.png")),90);
        
        images[37] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"block.png"));

        images[38] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c14.png"));
        images[39] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c14.png")),90);
        images[40] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c14.png")),180);
        images[41] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c14.png")),270);
         

        
    return images;
  }
  
  private BufferedImage[] loadRoomImages() throws IOException {
    //Imageamount = 28
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
        new BufferedImage(IMAGE_SIZE * grid[0].length, IMAGE_SIZE * grid.length, BufferedImage.TYPE_INT_ARGB);
    BufferedImage[] files = loadImages();
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        int color = grid[y][x];
        BufferedImage file = files[color];
        Graphics g = result.getGraphics();
        g.drawImage(file, x * IMAGE_SIZE, y * IMAGE_SIZE, x * IMAGE_SIZE + IMAGE_SIZE, y * IMAGE_SIZE + IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
      }
    }
    
    for(int y = 0; y < ogGrid.length; y++) {
      for(int x = 0; x < ogGrid[y].length; x++) {
        if(ogGrid[y][x].equals("b")) {
          Graphics g = result.getGraphics();
          g.drawImage(files[37], x * IMAGE_SIZE * 3, y * IMAGE_SIZE * 3, x * IMAGE_SIZE * 3 + IMAGE_SIZE * 3, y * IMAGE_SIZE * 3 + IMAGE_SIZE * 3, 0, 0, IMAGE_SIZE * 3, IMAGE_SIZE * 3, null);
        }
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
  
  public static int randomWithWeights(int max, int[] weights) {
    int total = 0;
    int value = 0;
    
    for(int x : weights) {
      total += x;
    }
     int rand = (int) Math.ceil(Math.random() * total);
     
     int c = 0;
     
     for(int i = 0; i < weights.length; i++) {
       c += weights[i];
       
       if(c >= rand) {
         return i;
       }
     }
     return 0;
  }
}
