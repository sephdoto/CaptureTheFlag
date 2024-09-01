package dialogs;

import java.net.MalformedURLException;
import java.nio.file.Paths;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
   * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
   * @param run several Runnables which will be executed when a user clicks "OK"
   */
  public static void openDialog(String title, String message, int ms, Runnable ... run) {
    Platform.runLater(() -> new Dialog(title, message, ms, run));
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
    /** How many Dialogs are open right now **/
    static int openInstances = 0;
    /** Thread that auto-closes the Dialog **/
    private TimeThread timeThread;
    /** Keeping track of the Dialog being open to suppress race conditions **/
    private boolean isOpen = true;

    /**
     * Creates an Alert with transparent background, displaying title and message.
     * Also applies CSS sheets to the Scene.
     * 
     * @param title to show above the message
     * @param message the message displayed in the Alerts body
     * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
     * @param run several Runnables which will be executed when a user clicks "OK"
     */
    public Dialog(String title, String message, int ms, Runnable ... run) {
      super(AlertType.NONE, "", ButtonType.OK);
      setX(SceneHandler.getMainStage().getX() + App.offsetWidth);
      setY(SceneHandler.getMainStage().getY() + 20 * openInstances + App.offsetHeight);
      openInstances++;
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
//      setContentText(message);
      Text text = new Text(message);
      text.getStyleClass().add("content-text");
      text.setWrappingWidth(400);
      StackPane wrapper = new StackPane(text);
      wrapper.getStyleClass().add("content-wrapper");
      getDialogPane().setContent(wrapper);

      Button close = (Button) getDialogPane().lookupButton(ButtonType.OK);
      close.setOnAction(e -> {
        cleanClose(); 
        e.consume();
        for(Runnable r : run)
          r.run();
        });
      
      startTimer(ms);
      
      show();
    }

    /**
     * Closes the Dialog cleanly, with restoring Listeners and shit
     */
    synchronized public void cleanClose() {
      if(isOpen) {
        isOpen = false;
        if(timeThread != null)
          timeThread.interrupt();
        cleanUp();
        openInstances--;
        close();
      }
    }

    /**
     * Starts a {@link TimeThread} that automatically closes the Dialog after a while.
     * 
     * @param ms milliseconds till the Dialog auto-closes
     */
    private void startTimer(int ms) {
      if(ms >= 0) {
        timeThread = new TimeThread(ms);
        timeThread.start();
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
     * Removes JNativeHook Listeners and restores the focus Listener on main Stage,
     * if there is only 1 Dialog Window left.
     */
    private void cleanUp() {
      if(openInstances <= 1) {
        SceneHandler.getMainStage().focusedProperty().addListener(App.focusListener);
        GlobalScreen.removeNativeMouseListener(clickListener);
        GlobalScreen.removeNativeMouseMotionListener(moveListener);
      }
    }

    /**
     * Disables the focus Listener on main Stage to have a global JNativeHook listener,
     * registers the nativeHook in case it was not in focus,
     * then adds JNativeHook Mouse listeners.
     */
    private void addListeners() {
      if(openInstances <= 1) {
        SceneHandler.getMainStage().focusedProperty().removeListener(App.focusListener);
        EntryPoint.cbl.registerNativeHook();
      }
      GlobalScreen.addNativeMouseListener(clickListener);
      GlobalScreen.addNativeMouseMotionListener(moveListener);
    }

    /**
     * Creates clickListener and moveListener to change the Dialogs position on Screen,
     * and to halt the TimeThread in case the Dialog is in focus.
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
        @Override
        public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
          if(timeThread != null)
            timeThread.halt(inDialogBounds(nativeEvent.getX(), nativeEvent.getY()));
        }
      };
    }

    /**
     * A Thread for closing a Dialog after a certain Time in ms.
     * Also displays the time till the Dialog gets closed as a progress bar.
     */
    class TimeThread extends Thread{
      boolean running;
      boolean halt;
      int ms;

      /**
       * @param ms time in milliseconds till the Thread closes the Dialog
       */
      public TimeThread(int ms) {
        this.ms = ms;
        running = true;
        halt = false;
      }

      @Override
      public void run() {
        double transparencyLeft = 1;
        double transparencyMiddle = 1;
        double transparencyRight = 1;
        int cycle = 0;
        while((int)Math.round((double)ms / Constants.UIupdateTime) >= cycle && running) {
          //adjust transparency, if time is not stopped
          if(!halt) {
            transparencyRight = 1 - 3 * (cycle / ((double)ms / Constants.UIupdateTime));
            if(transparencyRight <= 0)
              transparencyMiddle = 2 - 3 * (cycle / ((double)ms / Constants.UIupdateTime));
            if(transparencyMiddle <= 0)
              transparencyLeft = 3 - 3 * (cycle / ((double)ms / Constants.UIupdateTime));
            cycle++;

            Button close = (Button) getDialogPane().lookupButton(ButtonType.OK);
            final int seconds = (((int)Math.round((double)ms / Constants.UIupdateTime) - cycle)  * Constants.UIupdateTime) / 1000;
            Platform.runLater(() -> close.setText("OK (" + seconds + ")"));
          }
          //find button and set Style
          Region buttonContainer = (Region) getDialogPane().lookup(".header-panel .label");
          if (buttonContainer != null) {
            try {
            buttonContainer.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                    + "rgba(53,89,119, " + transparencyLeft + "), "
                    + "rgba(53,89,119, " + transparencyMiddle + "), "
                    + "rgba(53,89,119, " + transparencyRight + ")); "
                    + "-fx-alignment: center; "
                    + "-fx-background-radius:10px; "
                    + "-fx-text-fill:white; "
                    + "-fx-font-size:15.0px; "
                );
            } catch (Exception e) {
              System.err.println("Error css in Dialogs");
            }
          }
          try {
            Thread.sleep(Constants.UIupdateTime);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        Platform.runLater(() -> cleanClose());
      }
      @Override
      public void interrupt() {
        running = false;
      }
      /**
       * Can halt the time till the Dialog gets automatically closed.
       * 
       * @param halt true to halt the Thread
       */
      public void halt(boolean halt) {
        this.halt = halt;
      }
    }
  }
}
