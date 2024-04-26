package org.ctf.ui;

import java.util.Iterator;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	Text text;;
	VBox testBox;

	public WaitingScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
	}
	
	private void createLayout(){
		 testBox = new VBox();
		
		 createColorPicker();
		root.getChildren().add(testBox);
	}
	
	
	private void createImage() {
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
		ImageView mpv = new ImageView(mp);
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
	    colorPicker.showingProperty().addListener((obs,b,b1)->{
	        if(b1){
	            PopupWindow popupWindow = getPopupWindow();
	            Node popup = popupWindow.getScene().getRoot().getChildrenUnmodifiable().get(0);
	            popup.lookupAll(".color-rect").stream()
	                .forEach(rect->{
	                    Color c = (Color)((Rectangle)rect).getFill();
	                    Tooltip.install(rect.getParent(), new Tooltip("Custom tip for "+c.toString()));
	                });
	        }
	    });
	
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
	
	private StackPane ColorPickertest() {
		// Erstelle einen ColorPicker
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);

        // Verstecke die ChoiceBox des ColorPickers
        colorPicker.setOpacity(0); // Setze die Opazität auf 0, um die ChoiceBox unsichtbar zu machen

        // Füge der Farbpalette einen Rahmen hinzu
        Rectangle colorPaletteBorder = new Rectangle(200, 200, Color.TRANSPARENT); // Anpassen der Größe nach Bedarf
        colorPaletteBorder.setStroke(Color.BLACK); // Farbe des Rahmens

        // Erstelle ein StackPane und füge die Farbpalette und den Rahmen hinzu
        StackPane root = new StackPane(colorPicker, colorPaletteBorder);
        return root;
	}
	private PopupWindow getPopupWindow() {
	    @SuppressWarnings("deprecation") final Iterator<Window> windows = (Iterator<Window>) Window.getWindows();
	    while (windows.hasNext()) {
	        final Window window = windows.next();
	        if (window instanceof PopupWindow) {
	            return (PopupWindow)window;
	        }
	    }
	    return null;
	}
	
}
