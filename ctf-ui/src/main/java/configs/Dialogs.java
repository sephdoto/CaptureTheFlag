package configs;

import java.util.Optional;

import org.ctf.ui.CreateGameScreen;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;

public class Dialogs {
	
	 
	public static boolean showYesNoDialog(String title, String message) {
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
	    dialogPane.setStyle("-fx-background-color: lightblue;"
	        + " -fx-border-color: white;"
	        + " -fx-border-width: 1;"
	        + " -fx-border-style: solid;"
	        + " -fx-text-fill: white");
	    
	   dialogPane.getChildren().stream()
        .filter(node -> node instanceof javafx.scene.control.Label)
        .forEach(node -> ((javafx.scene.control.Label) node).setStyle("-fx-text-fill: red;"
        															+ "-fx-font-size: 15;"
        															+ "-fx-font-weight: bold"));


	    // Apply styles to buttons
	    Button yesButtonNode = (Button) dialogPane.lookupButton(yesButton);
	    Button noButtonNode = (Button) dialogPane.lookupButton(noButton);

	    for (Button button : new Button[]{yesButtonNode, noButtonNode}) {
	      CreateGameScreen.applyButtonStyle(button,"white");
	    }

	    Optional<ButtonType> result = alert.showAndWait();
	    return result.isPresent() && result.get() == yesButton;
	  }
	
	
	
	public static void showExceptionDialog(String title, String message) {
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(message);

	    // Set styles directly in JavaFX
	    DialogPane dialogPane = alert.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: lightblue;"
		        + " -fx-border-color: white;"
		        + " -fx-border-width: 1;"
		        + " -fx-border-style: solid;"
		        + " -fx-text-fill: white");

	    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
	    CreateGameScreen.applyButtonStyle(okButton,"white");
	    alert.showAndWait();
	  }
	
	public static AIPower showChooseAi() {
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Choose Ai Power");
	    alert.setHeaderText(null);
	    alert.setContentText("Choose the power of the Ai");
	    

	    // Set custom button types
	    ButtonType easyButton = new ButtonType("easy");
	    ButtonType mediumButton = new ButtonType("medium");
	    ButtonType strongButton = new ButtonType("strong");

	    
	    alert.getButtonTypes().setAll(easyButton, mediumButton, strongButton);

	    // Set styles directly in JavaFX
	    DialogPane dialogPane = alert.getDialogPane();
	    dialogPane.setStyle("-fx-background-color: lightblue;"
	        + " -fx-border-color: white;"
	        + " -fx-border-width: 1;"
	        + " -fx-border-style: solid;"
	        + " -fx-text-fill: white");
	    
	   dialogPane.getChildren().stream()
        .filter(node -> node instanceof javafx.scene.control.Label)
        .forEach(node -> ((javafx.scene.control.Label) node).setStyle("-fx-text-fill: red;"
        															+ "-fx-font-size: 15;"
        															+ "-fx-font-weight: bold"));


	    // Apply styles to buttons
	    Button easyButtonNode = (Button) dialogPane.lookupButton(easyButton);
	    Button mediButtonNode = (Button) dialogPane.lookupButton(mediumButton);
	    Button strongButtonNode = (Button) dialogPane.lookupButton(strongButton);

	    

	    for (Button button : new Button[]{easyButtonNode, mediButtonNode, strongButtonNode}) {
	      CreateGameScreen.applyButtonStyle(button,"white");
	    }

	    Optional<ButtonType> result = alert.showAndWait();
	    
	    if ((result.get() == easyButton) && result.isPresent()) {
			return AIPower.easy;
		}else if ((result.get() == mediumButton) && result.isPresent()) {
			return AIPower.medium;
		}else if ((result.get() == strongButton) && result.isPresent()) {
			return AIPower.strong;
		}else {
			return AIPower.NoAIChosen;
		}
	    
	  }
}
