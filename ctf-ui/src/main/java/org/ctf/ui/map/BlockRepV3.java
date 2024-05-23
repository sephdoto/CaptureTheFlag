package org.ctf.ui.map;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Visual Representation of a block as a black-rectangle
 * 
 * @author Manuel Krakowski
 */
public class BlockRepV3 extends Region {
  private Rectangle rc;

  public BlockRepV3() {
    rc = new Rectangle();
    rc.setFill(Color.BLACK);
    rc.setStroke(Color.BLACK);
    rc.setStrokeWidth(1);
    getChildren().add(rc);
  }

  /**
   * Sets the block invisible when it's not needed because the blocks are drawn in the
   * background-image
   * 
   * @author Manuel Krakowski
   */
  public void setOpacitytoZero() {
    rc.setOpacity(0);
  }

  /**
   * Aligns the rectangle which represents the block in the region
   * 
   * @author Manuel Krakowski
   */
  protected void layoutChildren() {
    super.layoutChildren();
    rc.setWidth(this.getWidth());
    rc.setHeight(this.getHeight());

  }
}
