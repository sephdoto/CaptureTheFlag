package org.ctf.ui.creators.settings.components;

/**
 * Marks a class as value holding, enabling the extraction of a certain value.
 * 
 * @author sistumpf
 */
public interface ValueExtractable {
  /**
   * @return the main value of the component, set by the user.
   */
  public Object getValue();
}
