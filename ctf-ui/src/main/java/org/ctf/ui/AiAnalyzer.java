package org.ctf.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;



public class AiAnalyzer extends Scene {
	
	 //Controller which is used to switch to the play-game-scene
	  private HomeSceneController hsc;
	  
	  //Containers and Labels which need to be accessed from different methods
	  private StackPane root;
	  private Label clipboardInfo;
	  private VBox leftBox;
	  
	  private ObjectProperty<Font> popUpLabel;
	  private ObjectProperty<Font> leaveButtonText; 
		
		
	public AiAnalyzer(HomeSceneController hsc, double width, double height) {
	    super(new StackPane(), width, height);
	    this.hsc = hsc;
	    manageFontSizes();
	    this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
	    this.root = (StackPane) this.getRoot();
	    this.getStylesheets().add(getClass().getResource("ComboBox.css").toExternalForm());
	    this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
	    popUpLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));
		leaveButtonText = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/80));
	    createLayout();
	  }
	
	private void manageFontSizes() {
	    widthProperty()
	        .addListener(
	            new ChangeListener<Number>() {
	              public void changed(
	                  ObservableValue<? extends Number> observableValue,
	                  Number oldWidth,
	                  Number newWidth) {
	            	  popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
	  					leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
	              }
	            });
	  }
	
	/**
	   * Creates the whole layout of the scene
	   * @author Manuel Krakowski
	   */
	  private void createLayout() {
	    root.getStyleClass().add("join-root");
	    root.prefHeightProperty().bind(this.heightProperty());
	    root.prefWidthProperty().bind(this.widthProperty());
	    
	    }
	
	
}
