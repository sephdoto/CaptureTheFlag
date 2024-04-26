package org.ctf.ui;

import java.util.Iterator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.scene.Node;


public class WaitingScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;
	StackPane right;
	Text text;
	VBox testBox;
	private  ObjectProperty<Color> sceneColorProperty = 
		        new SimpleObjectProperty<>(Color.BLUE);

	public WaitingScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
		 this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
	        this.setOnMouseClicked(e->{
	            if(e.getButton().equals(MouseButton.PRIMARY)){
	                MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
	                myCustomColorPicker.setCurrentColor(sceneColorProperty.get());

	                CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
	                itemColor.getStyleClass().add("custom-menu-item");
	                itemColor.setHideOnClick(false);
	                sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
	                ContextMenu contextMenu = new ContextMenu(itemColor);
	                contextMenu.setOnHiding(t->sceneColorProperty.unbind());
	                contextMenu.show(this.getWindow(),e.getScreenX(),e.getScreenY());
	            }
	        });
	}
	
	private void createLayout(){
		 testBox = new VBox();
		 Rectangle rect = new Rectangle(400,400);
	        rect.fillProperty().bind(sceneColorProperty);
	        root.getChildren().add(rect);
		 createColorPicker();
		root.getChildren().add(testBox);
	}
	
	private void createFigure() {
		  final SVGPath svg = new SVGPath();
	        svg.setContent("M70,50 L90,50 L120,90 L150,50 L170,50"
	            + "L210,90 L180,120 L170,110 L170,200 L70,200 L70,110 L60,120 L30,90"
	            + "L70,50");
	        svg.setStroke(Color.DARKGREY);
	        svg.setStrokeWidth(2);
	        svg.setEffect(new DropShadow());
	       // svg.setFill(colorPicker.getValue());
	}
	
	private ColorPicker createColorPicker() {
		ColorPicker colorPicker = new ColorPicker() {
	        @Override
	        public void hide() {
	            super.hide();
	            testBox.getChildren().remove(this);
	            
	        }
	    };

	    colorPicker.setValue(Color.BLUE);
	    colorPicker.setVisible(false);
	    colorPicker.getStyleClass().add("color-palette");
	   
	
	    Label label = new Label("Series name");

	    label.textFillProperty().bind(colorPicker.valueProperty());

	    testBox.getChildren().add(label);

	    label.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

	        testBox.getChildren().add(colorPicker);
	        colorPicker.show();
	       // colorPicker.layoutYProperty().bind(label.heightProperty());
	    });
	        return colorPicker;
	}
	
	

	 
}
