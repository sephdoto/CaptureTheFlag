package configs;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

public class Dialogs {
	public static void showExceptionDialog(String title, String message) {
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);

	  
	    DialogPane dialogPane = alert.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: lightblue;"
		        + " -fx-border-color: white;"
		        + " -fx-border-width: 1;"
		        + " -fx-border-style: solid;"
		        + " -fx-text-fill: white");

	    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
	    //CreateGameScreen.applyButtonStyle(okButton,"white");
	    alert.showAndWait();
	  }
}
