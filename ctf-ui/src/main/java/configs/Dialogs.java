package configs;

import java.util.Optional;

import org.ctf.ui.CreateGameScreen;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import test.CreateTextGameStates;
import javafx.scene.control.Alert.AlertType;

public class Dialogs {
	
	 
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
	      CreateGameScreen.applyButtonStyle(button);
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
	    CreateGameScreen.applyButtonStyle(okButton);
	    alert.showAndWait();
	  }
}
