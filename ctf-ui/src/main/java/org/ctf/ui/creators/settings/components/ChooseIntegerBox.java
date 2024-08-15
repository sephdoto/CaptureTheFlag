package org.ctf.ui.creators.settings.components;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Abstract box for entering Integers.
 * Implementations of this class should be inside {@link IntegerBoxFactory}
 * 
 * @author sistumpf
 */
public abstract class ChooseIntegerBox extends GridPane implements ValueExtractable {
  private TextField content;
  private Text postfix;

  public ChooseIntegerBox(VBox settingsBox) {
    content = new TextField();
    postfix = new Text();

    content.setTextFormatter( new TextFormatter<> (c ->
    {
      if (c.getControlNewText().matches("-?\\d*")) {
        return c;
      } else {
        return null;
      }
    }));

    adjustBoxStyle(settingsBox);
    content.setText("" + getInitialValue());

    setUserData("integerBox");
  }

  protected void adjustBoxStyle(VBox settingsBox) {
    setAlignment(Pos.CENTER);
    content.setAlignment(Pos.CENTER);
    add(content, 0, 0);

    postfix.setFill(Color.WHITE);
    add(postfix, 1, 0);

    content.maxWidthProperty().bind(settingsBox.widthProperty().divide(7));
  };

  /**
   * Loads in the variables integer value from Constants when first loaded.
   * 
   * @return the integer value from Constants
   */
  abstract protected int getInitialValue();

  /**
   * Set a postfix to indicate what a TextField is representing
   * 
   * @param postfix preferable a short unit like "ms" or "s"
   */
  public void setPostfix(String postfix) {
    this.postfix.setText(" " + postfix);
  }

  @Override
  public Object getValue() {
    return Integer.parseInt(content.getText());
  }
}
