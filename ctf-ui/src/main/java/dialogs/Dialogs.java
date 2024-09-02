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
    Platform.runLater(() -> new Dialog(title, message, ms, run).show());
  }

  /**
   * Opens a new {@link Dialog} with title and message
   * 
   * @author sistumpf
   * @param title the Dialogs title
   * @param message the Dialogs message
   * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
   * @param okButtonText name of the button that closes the Dialog
   * @param nextButtonText the functional buttons name
   * @param run several Runnables which will be executed when a user clicks button the nextButtonText describes
   */

  public static void openDialogTwoButtons(String title, String message, int ms, String okButtonText, String nextButtonText, Runnable ... run) {
      Platform.runLater(() -> getDialogTwoButtons(title, message, ms, okButtonText, nextButtonText, run).show());
    }
  /**
   * Creates a new {@link Dialog} with title and message and three buttons.
   * The left button runs leftRunnable, the middleButton runs all middleRunnables and the right button just closes the window.
   * 
   * @author sistumpf
   * @param title the Dialogs title
   * @param message the Dialogs message
   * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
   * @param leftButtonText name of the left button
   * @param leftRunnable  whatever the left button executes
   * @param middleButtonText name of the middle button
   * @param rightButtonText name of the right button, which just closes the window
   * @param run several Runnables which will be executed when a user clicks the middle button
   */
  public static void openDialogThreeButtons(String title, String message, int ms, String leftButtonText, Runnable leftRunnable, String middleButtonText, String rightButtonText, Runnable ... middleRunnables) {
    Platform.runLater(() -> getDialogThreeButtons(title, message, ms, leftButtonText, leftRunnable, middleButtonText, rightButtonText, middleRunnables).show());
  }
  
  /**
   * Creates a new {@link Dialog} with title and message and three buttons.
   * The left button runs leftRunnable, the middleButton runs all middleRunnables and the right button just closes the window.
   * Does not open the Dialog.
   * 
   * @author sistumpf
   * @param title the Dialogs title
   * @param message the Dialogs message
   * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
   * @param leftButtonText name of the left button
   * @param leftRunnable  whatever the left button executes
   * @param middleButtonText name of the middle button
   * @param rightButtonText name of the right button, which just closes the window
   * @param run several Runnables which will be executed when a user clicks the middle button
   */
  public static Dialog getDialogThreeButtons(String title, String message, int ms, String leftButtonText, Runnable leftRunnable, String middleButtonText, String rightButtonText, Runnable ... middleRunnables) {
    Dialog threeButtons = getDialogTwoButtons(title, message, ms, rightButtonText, middleButtonText, middleRunnables); 

    //add next button, then modify it
    ButtonType left = ButtonType.YES;
    threeButtons.getButtonTypes().add(left);
    Button leftB = (Button) threeButtons.getDialogPane().lookupButton(ButtonType.YES);
    leftB.setText(leftButtonText);
    leftB.setOnAction(e -> {
      threeButtons.cleanClose();
      leftRunnable.run();
      e.consume();
    });
    return threeButtons;
  }
  
  /**
   * Creates a new {@link Dialog} with title and message and two buttons, from which nextButton(the left one) gets all the run Runnables applied.
   * Does not open the Dialog.
   * 
   * @author sistumpf
   * @param title the Dialogs title
   * @param message the Dialogs message
   * @param ms milliseconds till the Dialog automatically closes, <0 to disable auto close
   * @param okButtonText name of the button that closes the Dialog
   * @param nextButtonText the functional buttons name
   * @param run several Runnables which will be executed when a user clicks the button nextButtonText describes
   */
  public static Dialog getDialogTwoButtons(String title, String message, int ms, String okButtonText, String nextButtonText, Runnable ... run) {
    Dialog twoButtons = new Dialog(title, message, ms, run); 

    //remove close Button from executing Runnables
    Button close = (Button) twoButtons.getDialogPane().lookupButton(ButtonType.OK);
    close.setText(okButtonText);
    close.setOnAction(e -> {
      twoButtons.cleanClose(); 
      e.consume();
    });

    //add next button, then modify it
    ButtonType next = ButtonType.NO;
    twoButtons.getButtonTypes().add(next);
    Button nextB = (Button) twoButtons.getDialogPane().lookupButton(ButtonType.NO);
    nextB.setText(nextButtonText);
    if(twoButtons.getTimeThread() != null) twoButtons.getTimeThread().setOriginalButtonText(okButtonText);
    nextB.setOnAction(e -> {
      twoButtons.cleanClose();
      for(Runnable r : run)
        r.run();
      e.consume();
    });
    return twoButtons;
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
    }

    /**
     * Closes the Dialog cleanly, with restoring Listeners and shit.
     * For some reason there can only be 1 button or close() does not work. LOL
     */
    synchronized public void cleanClose() {
      if(isOpen) {
        isOpen = false;
        if(getTimeThread() != null)
          getTimeThread().interrupt();
        cleanUp();
        openInstances--;
        for(int i=getDialogPane().getButtonTypes().size(); i>1; i--)
          getDialogPane().getButtonTypes().remove(1);
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
        getTimeThread().start();
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
          if(getTimeThread() != null)
            getTimeThread().halt(inDialogBounds(nativeEvent.getX(), nativeEvent.getY()));
        }
      };
    }

    public TimeThread getTimeThread() {
      return timeThread;
    }

    /**
     * A Thread for closing a Dialog after a certain Time in ms.
     * Also displays the time till the Dialog gets closed as a progress bar.
     */
    class TimeThread extends Thread{
      boolean running;
      boolean halt;
      int ms;
      String originalText;

      /**
       * @param ms time in milliseconds till the Thread closes the Dialog
       */
      public TimeThread(int ms) {
        this.ms = ms;
        running = true;
        halt = false;
        originalText = ((Button) getDialogPane().lookupButton(ButtonType.OK)).getText();
      }

      public void setOriginalButtonText(String text) {
        originalText = text;
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
            Platform.runLater(() -> close.setText(originalText + "(" + seconds + ")"));
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
