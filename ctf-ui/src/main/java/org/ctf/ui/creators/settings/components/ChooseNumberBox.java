package org.ctf.ui.creators.settings.components;

import org.ctf.ui.data.Formatter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Abstract box for entering Numbers.
 * 
 * @author sistumpf
 */
public abstract class ChooseNumberBox<T> extends GridPane implements ValueExtractable {
  protected TextField content;
  private Text postfix;
  private SimpleObjectProperty<Font> textSize = new SimpleObjectProperty<Font>(Font.getDefault());
  
  public ChooseNumberBox(VBox settingsBox) {
    content = new TextField();
    content.getStyleClass().add("choose-number-box");
    postfix = new Text();
    postfix.getStyleClass().add("choose-number-box-postfix");
    
    applyFormatter();
    
    adjustBoxStyle(settingsBox);
    content.setText("" + getInitialValue());
    
    content.widthProperty().addListener((change, oldValue, newValue) -> textSize.set(Font.font(newValue.doubleValue() / 6)));
    content.prefHeightProperty().bind(settingsBox.heightProperty().divide(15));
    content.minWidthProperty().bind(content.textProperty().length().multiply(12));
    content.fontProperty().bind(textSize);
    postfix.fontProperty().bind(textSize);

    setUserData("doubleBox");
  }

  protected void adjustBoxStyle(VBox settingsBox) {
    setAlignment(Pos.CENTER);
    content.setAlignment(Pos.CENTER);
    add(content, 0, 0);

    add(postfix, 1, 0);
    
    content.maxWidthProperty().bind(settingsBox.widthProperty().divide(7));
  };
  
  /**
   * Loads in the variables double value from Constants when first loaded.
   * 
   * @return the Double value from Constants
   */
  abstract protected T getInitialValue();
  
  /**
   * Applies a formatter to the TextField, to limit the inputs.
   * The use of {@link Formatter} is recommended.
   */
  abstract protected void applyFormatter();
  
  /**
   * Set a postfix to indicate what a TextField is representing
   * 
   * @param postfix preferable a short unit like "ms" or "s"
   */
  public void setPostfix(String postfix) {
    this.postfix.setText("  " + postfix);
  }
  
  @Override
  abstract public T getValue();
}
