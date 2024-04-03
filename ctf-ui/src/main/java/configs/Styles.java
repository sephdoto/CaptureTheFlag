package configs;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Styles {
	 private static final String NORMAL_BUTTON_STYLE = "-fx-background-color:"
		      + " linear-gradient(#5a5c5e, #3e3f41);"
		      + " -fx-background-radius: 20; -fx-border-radius: 20;"
		      + " -fx-text-fill: #FFFFFF";

		  private static final String HOVER_BUTTON_STYLE =
		      "-fx-background-color: linear-gradient(#6a6c6e, #4e4f51);"
		          + " -fx-background-radius: 20; -fx-border-radius: 20;"
		          + "-fx-text-fill: #FFFFFF";
		  
		  public static void applyButtonStyle(Button button) {
			    button.setStyle(NORMAL_BUTTON_STYLE);
			    button.hoverProperty().addListener((observable, oldValue, newValue) -> {
			      if (newValue) {
			        button.setStyle(HOVER_BUTTON_STYLE);
			      } else {
			        button.setStyle(NORMAL_BUTTON_STYLE);
			      }
			    });
			  }
		  
		  public static Path generateBackArrow() {
			    Path arrow = new Path();
			    arrow.getElements().add(new MoveTo(10, 15));
			    arrow.getElements().add(new LineTo(30, 0));
			    arrow.getElements().add(new MoveTo(30, 30));
			    arrow.getElements().add(new LineTo(10, 15));
			    arrow.setStrokeWidth(3);
			    arrow.setStroke(Color.WHITE);
			    arrow.setFill(Color.TRANSPARENT);
			    return arrow;
			  }
}

