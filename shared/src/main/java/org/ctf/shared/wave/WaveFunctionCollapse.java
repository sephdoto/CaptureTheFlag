package org.ctf.shared.wave;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Instantiate this class when creating a background with the wave function collapse algorithm. Idea
 * for the algorithm:
 * <a href= "https://github.com/mxgmn/WaveFunctionCollapse/tree/master?tab=readme-ov-file">Github
 * WaveFunctionCollapse</a>. 
 * 
 * <p>
 * How to use:
 * </p>
 * <p>
 * - Create a new instance of WaveFunctionCollapse with the String[][] grid representing the map and
 * the theme as input
 * </p>
 * <p>
 * - Call the getBackground method to get the finished image
 * </p>
 * 
 * @author ysiebenh
 */
public class WaveFunctionCollapse {
  

  // **************************************************
  // Fields
  // **************************************************


  static int imagesAmount;   // saves the amount of sprites used for background creation
                                    // (different for each pattern)
  
  static int imageSize;      // saves the size of each sprite
  
  private String[][] ogGrid;        // saves the original String grid
  
  private int[][] grid; // this grid is only filled once the WFC algorithm is fully done and then
                        // used to parse the Integer representation into a BufferedImage
  
  private boolean collapsed;        // Once the WFC algorithm is done this is set to true
  
  private BufferedImage background; // the final image
    
  private Themes theme;
  private BufferedImage block;  
  private BufferedImage base;
  

  // **************************************************
  // Constructor
  // **************************************************
  
  /**
   * Constructor that parses the String[][] grid (created by the GameEngine) into an integer which the
   * algorithm is going to use internally to keep track which image goes where.
   * 
   * @param grid
   * @param theme 
   */
  public WaveFunctionCollapse(String[][] grid, Themes theme) {
    ogGrid = grid;
    collapsed = false;
    
    this.theme = theme;
    if(theme == Themes.STARWARS) {
      imagesAmount = 36;
      imageSize = 14;
    }
    else if(theme == Themes.BAYERN) {
      imagesAmount = 4;
      imageSize = 42;
    }
    else if(theme == Themes.LOTR) {
 
      imagesAmount = 9;
      imageSize = 64;
      /*
      imagesAmount = 48;
      imageSize = 48;
      */
    }
    
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

    // Place the Pieces, Bases and Blocks if we want to use them as input for the algorithm
    if (theme == Themes.STARWARS) {
      for (int y = 0; y < grid.length; y++) {
        for (int x = 0; x < grid[0].length; x++) {
          if (grid[y][x] == null) {
            intGrid[y * 3 + 1][x * 3 + 1] = 0;
          } else if (grid[y][x].contains("p") || grid[y][x].contains("b:")) { //players and base
            intGrid[y * 3][x * 3] = 1;
            intGrid[y * 3][x * 3 + 1] = 1;
            intGrid[y * 3][x * 3 + 2] = 1;

            intGrid[y * 3 + 1][x * 3] = 1;
            intGrid[y * 3 + 1][x * 3 + 1] = 1;
            intGrid[y * 3 + 1][x * 3 + 2] = 1;

            intGrid[y * 3 + 2][x * 3] = 1;
            intGrid[y * 3 + 2][x * 3 + 1] = 1;
            intGrid[y * 3 + 2][x * 3 + 2] = 1;
          }
          /*
           * else if(grid[y][x].equals("b")) {
           * 
           * intGrid[y*3][x*3] = 2; intGrid[y*3][x*3+1] = 2; intGrid[y*3][x*3+2] = 2;
           * 
           * intGrid[y*3+1][x*3] = 2; intGrid[y*3+1][x*3+1] = 2; intGrid[y*3+1][x*3+2] = 2;
           * 
           * intGrid[y*3+2][x*3] = 2; intGrid[y*3+2][x*3+1] = 2; intGrid[y*3+2][x*3+2] = 2; }
           */
        }
      }
    } else if (theme == Themes.LOTR) {
      intGrid = new int[grid.length*3][grid[0].length*3];
      for (int y = 0; y < intGrid.length; y++) {
        for (int x = 0; x < intGrid[0].length; x++) {
          if (grid[y/3][x/3] == null) {
            intGrid[y/3][x/3] = 0;
          }
          else if (grid[y/3][x/3].contains("p") || grid[y/3][x/3].contains("b:")  ) {            
            intGrid[y/3*3+2][x] = 1;
          }
          else if(grid[y/3][x/3].equals("b")) {
            intGrid[y][x] = 1;
          }
          }
        }
    }
    return intGrid;
  }

  
  /**
   * The Wave Function Collapse algorithm. Generates a pattern from an initial set of images
   * and rules.
   * <p>
   * First the algorithm assigns each tile on the grid a distinct set of possible images based on a
   * set of rules. Then it chooses the tile with the fewest possible options (if there are multiple
   * a random one is selected) and assigns it one value from its options set (randomly). It does
   * this until there are no more tiles left. Right now there are no backtracking capabilities which
   * means sometimes there are still tiles left empty in the end.
   * </p>
   * 
   * @param grid the initial grid
   * @return the finished grid
   */
  private int[][] generateBackground(int[][] grid) {

    WaveGrid wGrid = new WaveGrid(grid, imagesAmount, theme);       //creating the Grid  
    
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

      ArrayList<Tile> toRemove = new ArrayList<Tile>(); // removing all collapsed tiles (in two
                                                        // loops to avoid
                                                        // ConcurrentModificationException) 
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

      int bestEntropy = tileCopy.get(0).options.size(); // storing the lowest options value
      for (Tile t : tileCopy) {
        if (t.options.size() == bestEntropy) { // putting all the tiles with the fewest options into
                                               // one list
          chosenTiles.add(t);
        }
      }
     
      //Choosing which tile to collapse at random 
      Tile thisTile = chosenTiles.get((int) (Math.random() * chosenTiles.size()));

      // two approaches how to handle a dead end (no possible images left for a particular tile)
      if (thisTile.options.size() == 0) {
        if (false && this.theme == Themes.LOTR) {
          return generateBackground(grid);  // option 1: just start over. works for
                                            // some patterns. takes wayy too long for others
         }
        else {
          thisTile.collapsed = true;        // option 2: just skip the tile that does not work 
          continue;
                                            // option 3 would be backtracking 
                                            // but I could not get it to work
        }
      }

      // choose which tile to use at random ( with weights assigned in Tile.getWeights() )
      thisTile.setValue(thisTile.options.get(randomWithWeights(thisTile.options.size(), thisTile.getWeights())));
    }
    
    //see fillTheGaps() description 
    if(theme == Themes.STARWARS) {
      fillTheGaps(wGrid);
    }

    return wGrid.grid;

  }
  
  /**
   * The original circuit pattern did not allow for inner corners and they do not work well with the
   * pattern when added to the WFC algorithm. Therefore they are placed after the algorithm is
   * finished only where absolutely necessary. Used in the Star Wars Theme.
   * 
   * @param wGrid
   */
  void fillTheGaps (WaveGrid wGrid) {
    for(Tile t : wGrid.tiles) {
      if (t.getValue() == 0) {
        if (t.getLeftNeighbor() != null && t.getLeftNeighbor().getValue() == 1
            && t.getLowerNeighbor() != null && t.getLowerNeighbor().getValue() == 1) {
          t.setValueSimple(37);
        }
       if (t.getUpperNeighbor() != null && t.getUpperNeighbor().getValue() == 1 
           && t.getLeftNeighbor() != null && t.getLeftNeighbor().getValue() == 1) {
         t.setValueSimple(38);
       }
       if (t.getUpperNeighbor() != null && t.getUpperNeighbor().getValue() == 1 
           && t.getRightNeighbor() != null && t.getRightNeighbor().getValue() == 1) {
         t.setValueSimple(39);
       }
       if (t.getRightNeighbor() != null && t.getRightNeighbor().getValue() == 1 
           && t.getLowerNeighbor() != null && t.getLowerNeighbor().getValue() == 1) {
         t.setValueSimple(40);
       }
      }
    }
  }

  
  /**
   * Recursive version of the Wave Function Collapse algorithm. Maybe this will be useful to
   * implement backtracking. For now, deprecated.
   *
   * @param grid the initial grid
   * @return the finished grid
   */
  @Deprecated
  private int[][] generateBackgroundRecursive(WaveGrid waveGrid) {
    WaveGrid wGrid = new WaveGrid(waveGrid.grid, imagesAmount, this.theme);
    wGrid.tiles = new ArrayList<Tile>(waveGrid.tiles);
    wGrid.grid = waveGrid.grid;
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
  
  /**
   * Controller method used in the gridToImg method to choose which images to load according to the
   * theme used.
   * 
   * @param theme
   * @return 
   * @throws IOException
   */
  private BufferedImage[] loadImages(Themes theme) throws IOException{

    if(theme == Themes.STARWARS) {
      return loadSWImages();
    }
    else if(theme == Themes.BAYERN) {
      return loadBayernImages();
    }
    else if(theme == Themes.LOTR) {
      return loadKnotImages();
    }
    else return null;
  }
  
  /**
   * Preloads the images used in the Star Wars pattern. The pattern was originally inspired by a pattern called "circuits" designed by 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadSWImages() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount+6];

        
        images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"c2.png"));
        
        for(int i = 1, c = 1; c <= imagesAmount+4; i++) {
          if(i == 1 || i == 2) {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png"));
          }
          else if(i == 3 || i == 5 || i == 6 || i == 9 || i == 10 || i == 12 || i == 14) {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png"));
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png")),90);
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png")),180);
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png")),270);
          }
          else {
            images[c++] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png")); 
            images[c++] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "c" + i + ".png")),90);
           
          }
        }
    return images;
  }
  
  /**
   * Preloads the images used in the Lord of the Rings pattern. The pattern was originally drawn by
   * Hermann Hillmann and found on the WaveFunctionCollapse github page.
   * 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadLOTRImages() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"l1.png"));
    block = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tree.png"));

    for(int i = 1; i <= 48; i++) {
      images[i] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"l" + i + ".png"));
    }
    
    return images;
  }
  
  /**
   * Preloads the images used in the Bayern pattern. 
   * 
   * @return
   * @throws IOException
   */
  private BufferedImage[] loadBayernImages() throws IOException {
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    block = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"noweed.png"));
    base = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"Ei.png"));
    images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"p1.png"));
    images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"Bayern.png"));
    images[2] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"Bayern.png")),90);
    images[3] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"Bayern.png")),180);
    images[4] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"Bayern.png")),270);
    return images;
    
  }
  
  @Deprecated
  private BufferedImage[] loadRoomImages() throws IOException {
    //Imageamount = 28
    
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    block = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tree.png"));

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
  
  private BufferedImage[] loadKnotImages() throws IOException{
    BufferedImage[] images = new BufferedImage[imagesAmount+1];
    block = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"tree.png"));

      images[0] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p1.png"));

      images[1] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p1.png"));
      images[2] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png"));
      images[3] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),90);
      images[4] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),180);
      images[5] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p2.png")),270);
      
      images[6] = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p3.png"));
      images[7] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p3.png")),90);
      images[8] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p3.png")),180);
      images[9] = this.rotateImageByDegrees(ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator + "p3.png")),270);
      
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

    BufferedImage result =
        new BufferedImage(imageSize * grid[0].length, imageSize * grid.length, BufferedImage.TYPE_INT_ARGB);
    BufferedImage[] files = loadImages(this.theme);
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        int color = grid[y][x];
        BufferedImage file = files[color];
        Graphics g = result.getGraphics();
        g.drawImage(file, x * imageSize, y * imageSize, x * imageSize + imageSize, y * imageSize + imageSize, 0, 0, imageSize, imageSize, null);
      }
    }


    for (int y = 0; y < ogGrid.length; y++) {
      for (int x = 0; x < ogGrid[y].length; x++) {
        if (ogGrid[y][x].equals("b")) {
          Graphics g = result.getGraphics();
          g.drawImage(block, x * imageSize * 3, y * imageSize * 3,
              x * imageSize * 3 + imageSize * 3, y * imageSize * 3 + imageSize * 3, 0, 0,
              42, 42, null);
        }
        if (ogGrid[y][x].contains("b:") && theme == Themes.BAYERN) {
          Graphics g = result.getGraphics();
          g.drawImage(base, x * imageSize * 3, y * imageSize * 3,
              x * imageSize * 3 + imageSize * 3, y * imageSize * 3 + imageSize * 3, 0, 0,
              126, 126, null);
        }
      }
    }
    
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
   * Used in the gridToImg method to turn pngs around. Taken from StackOverFlow
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
