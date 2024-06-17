package org.ctf.ui.threads;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Platform;

/**
 * Alters an objects text to display an animation with dots.
 * Dots are placed in random locations of the text String to make it more entertaining to watch.
 * 
 * @author sistumpf
 */
public class PointAnimation extends Thread {
  private Object object;
  private String string;
  private int numberOfPoints;
  private int refreshTimeMS;
  private Random random;
  private boolean active = true;
  
  /**
   * 
   * @param object an Object that contains a "setText(String)" Method
   * @param string the original String, the dots are put behind it
   * @param numberOfPoints how many dots are placed at max
   * @param refreshTimeMS time in ms to switch between animation frames
   */
  public PointAnimation(Object object, String string, int numberOfPoints, int refreshTimeMS) {
    this.object = object;
    this.string = string;
    this.numberOfPoints = numberOfPoints;
    this.refreshTimeMS = refreshTimeMS;
    random = new Random(string.hashCode());
  }
  
  /**
   * Stops the animation and adjusts the text to an error message
   */
  @Override
  public void interrupt() {
    Platform.runLater(() ->  {
      try {
        object.getClass().getMethod("setText", String.class).invoke(object, ("action interrputed"));
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
          | NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
    });
    this.active = false;
  }
  
  /**
   * Changes the button text to display the dot animation.
   */
  @Override
  public void run() {
    ArrayList<Integer> pointPositions = new ArrayList<Integer>();
    ArrayList<Integer> allPlaces = new ArrayList<Integer>();
    for(int i=0; i<numberOfPoints; i++) allPlaces.add(i);
    
    for(int pivot=0; active; pivot++) {
      ArrayList<Integer> removePlaceList = new ArrayList<Integer>();
      removePlaceList.addAll(allPlaces);
      pivot %= numberOfPoints +1;
      StringBuilder points = new StringBuilder();
      
      //find positions to place the dots
      for(int j=0; j<pivot; j++) {
        int random = this.random.nextInt(removePlaceList.size());
        pointPositions.add(removePlaceList.get(random));
        removePlaceList.remove(random);
      }
      pointPositions.sort(null);
      //place the dots with spaces in between
      for(int j=0; j<numberOfPoints; j++) {
        if(pointPositions.size() < 1 || pointPositions.get(0) != j) {
          points.append("  ");
        } else {
          points.append(". "); 
          pointPositions.remove(0);
        }
        
      }

      Platform.runLater(() ->  {
        try {
          object.getClass().getMethod("setText", String.class).invoke(object, (string + points.toString()));
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e) {
          this.active = false;
          e.printStackTrace();
        }
      });
      
      try {
        Thread.sleep(refreshTimeMS);
      } catch (InterruptedException e) { e.printStackTrace(); }
      random.setSeed(random.nextInt(Integer.MAX_VALUE));
      pointPositions.clear();
    }
  }
}