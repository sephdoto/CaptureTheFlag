package org.ctf.shared.wave;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class WaveFunctionCollapse {
  private int[][] grid;
  private boolean collapsed = false;
  
  public WaveFunctionCollapse(String[][] grid) {
    
  }

  public int[][] generateBackground(int[][] grid){
    WaveGrid wGrid = new WaveGrid(grid);
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
    return wGrid.grid;
    
  }
	
  public BufferedImage gridToImg(int[][] grid) throws IOException {
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
            //file = ImageIO.read(new File(Constants.toUIResources + "black.png"));
            file = ImageIO.read(new File(Constants.toUIResources + "pictures" + File.separator +"p1.png"));
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
    return result;
  }
  
  public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
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
    //g2d.setColor(Color.RED);
    //g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
    g2d.dispose();

    return rotated;
}
}
