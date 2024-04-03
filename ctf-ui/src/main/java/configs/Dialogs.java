package configs;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

public class Dialogs {
	
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
	public static boolean showConfirmationDialog(String title, String message) {
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);

	    // Set custom button types
	    ButtonType yesButton = new ButtonType("Yes");
	    ButtonType noButton = new ButtonType("No");
	    alert.getButtonTypes().setAll(noButton, yesButton);

	    // Set styles directly in JavaFX
	    DialogPane dialogPane = alert.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: linear-gradient(to top, #ffffff, #f2f2f2);"
	        + " -fx-border-color: #bbb;"
	        + " -fx-border-width: 1;"
	        + " -fx-border-style: solid;");
	    dialogPane.lookup(".label").setStyle("-fx-font-size: 14;"
	        + " -fx-font-weight: bold;"
	        + " -fx-text-fill: #444;");

	    // Apply styles to buttons
	    Button yesButtonNode = (Button) dialogPane.lookupButton(yesButton);
	    Button noButtonNode = (Button) dialogPane.lookupButton(noButton);

	    for (Button button : new Button[]{yesButtonNode, noButtonNode}) {
	      applyButtonStyle(button);
	    }

	    Optional<ButtonType> result = alert.showAndWait();
	    return result.isPresent() && result.get() == yesButton;
	  }
	public static void showErrorDialog(String title, String message) {
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);

	    // Set styles directly in JavaFX
	    DialogPane dialogPane = alert.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: linear-gradient(to top, #ffffff, #f2f2f2);"
	        + " -fx-border-color: #bbb;"
	        + " -fx-border-width: 1;"
	        + " -fx-border-style: solid;");
	    dialogPane.lookup(".label").setStyle("-fx-font-size: 14;"
	        + " -fx-font-weight: bold;"
	        + " -fx-text-fill: #444;");

	    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
	    applyButtonStyle(okButton);
	    alert.showAndWait();
	  }
}
