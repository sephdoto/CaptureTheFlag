package org.ctf.ui.data;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Formatters for reuse
 * 
 * @author sistumpf
 */
public class Formatter {
  /**
   * Applies an Integer Formatter to a TextField, only allows Integer Inputs
   * 
   * @author sistumpf
   * @param field TextField to apply the Formatter to
   * @param minInt the smallest allowed Integer, null if there is none
   * @param maxInt the biggest allowed Integer, null if there is none
   */
  public static void applyIntegerFormatter(TextField field, Integer minInt, Integer maxInt) {
    String integerFormat = "-?\\d*";
    String positiveIntegerFormat = "\\d*";
    
    field.setTextFormatter( new TextFormatter<> (c -> {
      if (c.getControlNewText().equals("")) {
        field.setStyle("-fx-display-caret: false;");
        return c;
      } else if (c.getControlNewText().matches((minInt != null) ? (minInt >= 0 ? positiveIntegerFormat : integerFormat) : integerFormat) ) {
        String text = c.getControlNewText();
        try { 
          if(minInt != null && Integer.parseInt(c.getControlNewText()) < minInt) {
            text = "" + minInt;
          } else if(maxInt != null && Integer.parseInt(c.getControlNewText()) > maxInt) {
            text = "" + maxInt;
          } else  {
            field.setStyle("-fx-display-caret: true;");
            return c;
          }
        } catch (NumberFormatException nfe) {
            text = maxInt == null ? "" : ""+maxInt;
          }

        setText(field, text);
        return null;
      }
      return null;
    }));
  }
  
  /**
   * Sets a TextFields text.
   * Unregisters and re-registers the TextFormatter whilst changing the text.
   * 
   * @author sistumpf
   * @param field TextField to change the Text on
   * @param text the String which gets set into field
   */
  private static void setText(TextField field, String text) {
    var formatter = field.getTextFormatter();
    field.setTextFormatter(null);
    field.setText(text);
    field.setStyle("-fx-display-caret: true;");
    field.positionCaret(field.getText().length());
    field.setTextFormatter(formatter);
  }

}
