package dialogs;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Optional;
import org.ctf.shared.constants.Constants;
import org.ctf.ui.App;
import org.ctf.ui.EntryPoint;
import org.ctf.ui.data.SceneHandler;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputAdapter;
import org.jnativehook.mouse.NativeMouseMotionAdapter;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Creates CSS styled Alerts with title and messages.
 * The Alerts have lost their function and are completely overhauled, to fit the application.
 * 
 * @author sistumpf
 */
public class Dialogs {
  /**
   * Opens a new {@link Dialog} with title and message
   * 
   * @author sistumpf
   * @param title the Dialogs title
   * @param message the Dialogs message
   */
  public static void openDialog(String title, String message) {
    Platform.runLater(() -> new Dialog(title, message));
  }

  /**
   * CSS Styled Alert implementation that always stays in focus,
   * is movable by clicking and dragging,
   * and does not stop background activity.
   * 
   * @author sistumpf
   */
  private static class Dialog extends Alert {
    /** How many pixels the mouse is into the Dialog Window, left side is always 0 **/
    private double xOffset = 0; 
    /** How many pixels the mouse is into the Dialog Window, upper side is always 0 **/
    private double yOffset = 0;
    /** True if the mouse is pressed down and the Dialog Window is allowed to be moved **/
    private boolean move;
    /** Listener to check if the mouse clicked onto the Dialog Window **/
    NativeMouseInputAdapter clickListener;
    /** Listener to determine how to move the Dialog Window with the moving Mouse **/
    NativeMouseMotionAdapter moveListener;

    /**
     * Creates an Alert with transparent background, displaying title and message.
     * Also applies CSS sheets to the Scene.
     * 
     * @param title to show above the message
     * @param message the message displayed in the Alerts body
     */
    public Dialog(String title, String message) {
      super(AlertType.NONE);
      initModality(Modality.NONE);
      initStyle(StageStyle.TRANSPARENT);        
      ((Stage) getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);

      getDialogPane().getScene().setFill(Color.TRANSPARENT);
      try {
        getDialogPane().getStylesheets()
        .add(Paths.get(Constants.toUIStyles + "dialogs.css").toUri().toURL().toString());
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      getDialogPane().getStyleClass().add("dialog-pane");
      
      initListeners();
      addListeners();
      
      setHeaderText(title);
      setContentText(message);
      
      ButtonType button = new ButtonType("OK"); 
      getButtonTypes().setAll(button);

      Optional<ButtonType> result = showAndWait();
      if (result.get() == button){
        cleanUp();
        close();
      } 
    }

    /**
     * True if x and y are within this Dialogs Window bounds
     * 
     * @param x x coordinate on the Screen
     * @param y y coordinate on the Screen
     * @return true if x and y are in bounds
     */
    private boolean inDialogBounds(int x, int y) {
      return 
          x >= getX() && x <= getX() + getWidth() &&
          y >= getY() && y <= getY() + getHeight();
    }
    
    /**
     * Removes JNativeHook Listeners and restores the focus Listener on main Stage
     */
    private void cleanUp() {
      SceneHandler.getMainStage().focusedProperty().addListener(App.focusListener);
      GlobalScreen.removeNativeMouseListener(clickListener);
      GlobalScreen.removeNativeMouseMotionListener(moveListener);
    }
    
    /**
     * Disables the focus Listener on main Stage to have a global JNativeHook listener,
     * registers the nativeHook in case it was not in focus,
     * then adds JNativeHook Mouse listeners.
     */
    private void addListeners() {
      SceneHandler.getMainStage().focusedProperty().removeListener(App.focusListener);
      EntryPoint.cbl.registerNativeHook();
      GlobalScreen.addNativeMouseListener(clickListener);
      GlobalScreen.addNativeMouseMotionListener(moveListener);
    }
    
    /**
     * Creates clickListener and moveListener to change the Dialogs position on Screen
     */
    private void initListeners() {
      clickListener = new NativeMouseInputAdapter() {
        @Override
        public void nativeMousePressed(NativeMouseEvent nativeEvent) {
          if(inDialogBounds(nativeEvent.getX(), nativeEvent.getY())) {
            xOffset = nativeEvent.getX() - getX();
            yOffset = nativeEvent.getY() - getY();
            move = true;
          }
        }
        
        @Override
        public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
          move = false;
        }
      };

      moveListener = new NativeMouseMotionAdapter() {
        @Override
        public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
          if(move) {
            setX(nativeEvent.getX() - xOffset);
            setY(nativeEvent.getY() - yOffset);
          }
        }
      };
    }
  }
}
