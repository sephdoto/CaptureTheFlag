package org.ctf.ui;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.tools.JsonTools;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.creators.InfoPaneCreator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;



public class AiAnalyserNew extends Scene {
    
     //Controller which is used to switch to the play-game-scene
      private HomeSceneController hsc;
      
      //Containers and Labels which need to be accessed from different methods
      private StackPane root;
      private Label clipboardInfo;
      private VBox leftBox;
      private StackPane showMapBox;
      private GamePane gm;
      private ScrollPane scroller;
      private VBox content;
      private double[] savedscrollvalues;
      
      private ObjectProperty<Font> popUpLabel;
      private ObjectProperty<Font> leaveButtonText;
      private ObjectProperty<Font> moveTableHeader; 
      private ObjectProperty<Font> moveTableContent; 
      SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(this.getWidth()*0.01));
      HBox[] rows;
      private final int totalmoves = 60;
      private int scrollBackIndicator;
      int currentMove;
      



        
        
    public AiAnalyserNew(HomeSceneController hsc, double width, double height) {
        super(new StackPane(), width, height);
        this.hsc = hsc;
        manageFontSizes();
        rows = new HBox[totalmoves];
        scrollBackIndicator = 0;
        this.root = (StackPane) this.getRoot();
        currentMove = 0;
        savedscrollvalues = new double[totalmoves];
        try {
          this.getStylesheets().add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
          this.getStylesheets().add(Paths.get(Constants.toUIStyles + "ComboBox.css").toUri().toURL().toString());
          this.getStylesheets().add(Paths.get(Constants.toUIStyles + "color.css").toUri().toURL().toString());
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }

     
        popUpLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));
        leaveButtonText = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/80));
        moveTableHeader = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));
        moveTableContent = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/60));


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
                      moveTableHeader.set(Font.font(newWidth.doubleValue() / 50));
                      moveTableContent.set(Font.font(newWidth.doubleValue() / 60));
                      padding.set(new Insets(newWidth.doubleValue()*0.01));
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
        VBox mainVBox = createMainBox(root);
        mainVBox.getChildren().add(createHeader());
        HBox sep = createMiddleHBox(mainVBox);
        sep.getChildren().add(createProgressBar(sep));

        sep.getChildren().add(createMapBox(sep));
        sep.getChildren().add(createAllMovesVBox(sep));
        mainVBox.getChildren().add(sep);
        root.getChildren().add(mainVBox);
        }
      
      
      private VBox createProgressBar(HBox parent) {
     VBox progresscontainer = new VBox();
     progresscontainer.setAlignment(Pos.CENTER);
     progresscontainer.prefWidthProperty().bind(parent.widthProperty().multiply(0.1));
     progresscontainer.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
     progresscontainer.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));
     VBox progressBar = new VBox();
     progressBar.setPadding(new Insets(progressBar.getHeight()*0.01));
     progressBar
     .widthProperty()
     .addListener(
         (observable, oldValue, newValue) -> {
           double newPadding = newValue.doubleValue()*0.01;
           progressBar.setPadding(new Insets(newPadding,newPadding, newPadding, newPadding));
         });
     progressBar.getStyleClass().add("option-pane");
     //progressBar.setAlignment(Pos.BOTTOM_CENTER);
     Tooltip tooltip = new Tooltip("Expandierte Knoten:" + "\n" + "angewendete Heuristiken:" + "\n" + "Angewendete Simulationen:" );
     tooltip.setStyle("-fx-background-color: blue");
     Duration delay = new Duration(1);
     tooltip.setShowDelay(delay);
     Duration displayTime = new Duration(10000);
     tooltip.setShowDuration(displayTime);
     tooltip.setFont(new Font(15));
     progressBar.setPickOnBounds(true);
     Tooltip.install(progressBar, tooltip);
     progressBar.prefWidthProperty().bind(progresscontainer.widthProperty().divide(2));
     progressBar.maxWidthProperty().bind(progresscontainer.widthProperty().divide(2));
     progressBar.prefHeightProperty().bind(progresscontainer.heightProperty());
     progresscontainer.getChildren().add(progressBar);
     VBox progress = new VBox();
     progress.prefHeightProperty().bind(progressBar.heightProperty().multiply(0.85));
     progress.prefWidthProperty().bind(progressBar.widthProperty());
     progress.getStyleClass().add("progress-pane");
     Label l = new Label("0.85");
     l.fontProperty().bind(moveTableContent);
//     Rotate rotate = new Rotate(90, 0, 0);
//     Scale scaleY = new Scale(1, -1);
//     l.getTransforms().addAll(rotate, scaleY);
     l.getStyleClass().add("vertical-label");
     
     progress.getChildren().add(l);

     progressBar.getChildren().add(progress);
     return progresscontainer;

     
        
     }
      
      /**
       * Creates a Vbox which is used to devide the Scene into two patrs, one for the header and one for the content
       * @author Manuel Krakowski
       * @param parent: Stackpane in which the Vbox is placed for relative resizing
       * @return Vbox
       */
      private VBox createMainBox(StackPane parent) {
        VBox mainBox = new VBox();
        mainBox.prefHeightProperty().bind(parent.heightProperty());
        mainBox.prefWidthProperty().bind(parent.widthProperty());
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setSpacing(30);
        mainBox
            .widthProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  double newSpacing = newValue.doubleValue() * 0.02;
                  //double newPadding = newValue.doubleValue()*0.04;
                  mainBox.setSpacing(newSpacing);
                  //mainBox.setPadding(new Insets(0,0, newPadding, 0));
                });
        return mainBox;
      }
      
      /**
       * Creates the upper part of the scene which includes just one Image with the Text: 'Lobby'
       * @author Manuel Krakowski
       * @return ImageView containing the word 'Lobby'
       */
      private ImageView createHeader() {
        Image mp = ImageController.loadThemedImage(ImageType.MISC, "GameAnalyzerHeader");
        ImageView mpv = new ImageView(mp);
        mpv.fitHeightProperty().bind(root.heightProperty().multiply(0.1));
        mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.7));
        mpv.setPreserveRatio(true);
        return mpv;
      }
      
      
      /**
       * Creates a HBox which devides the middle part of the screen into two pats vertically
       * @author Manuel Krakowski
       * @param parent: main Vbox in which it is placed used for relaive resizing
       * @return seperator-Hbox
       */
      private HBox createMiddleHBox(VBox parent) {
        HBox sep = new HBox();
        sep.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
        sep.prefWidthProperty().bind(parent.widthProperty());
        sep.setAlignment(Pos.TOP_CENTER);
        sep.widthProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  double newSpacing = newValue.doubleValue() * 0.03;
                  sep.setSpacing(newSpacing);
                });
        return sep;
      }
      
      private VBox createMapBox(HBox parent) {
        VBox mapBox = new VBox();
        mapBox.prefHeightProperty().bind(parent.heightProperty());
        mapBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.5));
        //mapBox.setStyle("-fx-background-color: blue");
        mapBox.heightProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.04;
              mapBox.setSpacing(newSpacing);
            });
           mapBox.getChildren().add(createShowMapPane("p1", mapBox));

        mapBox.getChildren().add(createControlMapBox(mapBox));
        return mapBox;
      }
      
      private HBox createControlMapBox(VBox parent) {
        HBox h = new HBox();
        h.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
        h.setAlignment(Pos.CENTER);
        h.prefWidthProperty().bind(parent.widthProperty());
        h.widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.04;
              h.setSpacing(newSpacing);
            });
        Button b = new Button();
        b.prefHeightProperty().bind(h.heightProperty().multiply(1));
        b.prefWidthProperty().bind(h.widthProperty().divide(10));
        b.setOnAction(e -> {
          perfromNextClick();
      });
        b.getStyleClass().add("triangle-button");
        b.fontProperty().bind(leaveButtonText);
        Button rec = new Button("Show AI's Choice");
        rec.prefHeightProperty().bind(h.heightProperty().multiply(1));
        rec.prefWidthProperty().bind(h.widthProperty().divide(4));
        rec.getStyleClass().add("rectangle-button");
        rec.fontProperty().bind(leaveButtonText);
        Button leftRec = new Button("");
        leftRec.setOnAction(e -> {
          perfomBackClick();
      });
        leftRec.prefHeightProperty().bind(h.heightProperty().multiply(1));
        leftRec.prefWidthProperty().bind(h.widthProperty().divide(10));
        leftRec.getStyleClass().add("triangle-button-left");
        leftRec.fontProperty().bind(leaveButtonText);
        h.getChildren().addAll(leftRec,rec,b);
        return h;
      }
      
      private void perfomBackClick() {
        if(currentMove > 0 ) {
        rows[currentMove].getStyleClass().clear();
        rows[--currentMove].getStyleClass().add("blue-glow-hbox");
       if((currentMove %5 == 0)) {
         //scroller.setVvalue(savedscrollvalues[scrollBackIndicator--]);
         System.out.println(scrollBackIndicator);
       }
        }
        
      }
      
      private void perfromNextClick() {
        if(currentMove < 59) {
        rows[currentMove].getStyleClass().clear();
        rows[++currentMove].getStyleClass().add("blue-glow-hbox");
       if((currentMove %5 == 0 )) {
       //savedscrollvalues[scrollBackIndicator++] = scroller.getVvalue();
       System.out.println(scrollBackIndicator);
       scrollToLabel(scroller, content, content.getChildren().get(currentMove-5)); 
       }
       }
        
      }
      
      private void perfromShowAiBestMove() {
        
      }
      
      private StackPane createShowMapPane(String name, VBox parent) {
          showMapBox = new StackPane();
          showMapBox.getStyleClass().add("option-pane");
          showMapBox.prefWidthProperty().bind(parent.widthProperty());
          showMapBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
          //showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.45));
          //showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.65));
          //showMapBox.getStyleClass().add("show-GamePane");
          showMapBox.paddingProperty().bind(padding);
          GameState state = StroeMaps.getMap(name);
          gm = new GamePane(state,true);
          StackPane.setAlignment(gm, Pos.CENTER);
          gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
          gm.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.6));
          showMapBox.getChildren().add(gm);
          return showMapBox;
        }
      
      private void showCurrentMove() {
        
      }
      
      /**
       * Creates the right side of the screen containing a header and a scrollPane with all moves
       * @author Manuel Krakowski
       * @param parent: used for relative resizing
       * @return 
       */
      private VBox createAllMovesVBox(HBox parent) {
        leftBox = new VBox();
        leftBox.setAlignment(Pos.TOP_CENTER);
        leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.3));
        leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
        leftBox.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));

       // leftBox.setStyle("-fx-background-color: green");
        leftBox.heightProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.03;
              //double newPadding = newValue.doubleValue() * 0.08;
              leftBox.setSpacing(newSpacing);
              //leftBox.setPadding(new Insets(newPadding, 0, newSpacing, 0));
            });
       leftBox.getChildren().add(createHeaderLabel("Moves", leftBox));
       leftBox.getChildren().add(createScrollPane(leftBox));
       //leftBox.getChildren().add(createTopCenter());
       //leftBox.getChildren().add(createWholeTable(leftBox));
       //leftBox.getChildren().add(createLeave());
        return leftBox;
      }
      
      /**
       * Creates a header-label for the table
       * @param : text of the label
       * @param h: parent used for relative resizing
       * @return header-label
       */
      private Label createHeaderLabel(String text, VBox parent) {
            Label l = new Label(text);
            l.setTextFill(Color.GOLD);
            //l.getStyleClass().add("lobby-header-label");
            l.setAlignment(Pos.CENTER);
            l.prefWidthProperty().bind(parent.widthProperty());
            l.fontProperty().bind(moveTableHeader);
            return l;
          }
      
      /**
       * Creates the Content of the table with all the players currently in the waiting room
       * @author Manuel Krakowski
       * @param parent: used for relative resizing
       * @return Scrollpane with current players
       */
      private ScrollPane createScrollPane(VBox parent) {
        scroller = new ScrollPane();
        scroller.getStyleClass().clear();
        
        //scroller.setStyle("-fx-background-color: grey");
        scroller.prefWidthProperty().bind(parent.widthProperty());
        scroller.prefHeightProperty().bind(parent.heightProperty().multiply(0.93));
        scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
        content = new VBox();

        content.prefWidthProperty().bind(scroller.widthProperty());
        content.prefHeightProperty().bind(scroller.heightProperty());
        content.setAlignment(Pos.CENTER);
        for (int i = 0; i < 60; i++) {
             content.getChildren().add(createOneRow(content, i));
        }
        scroller.setContent(content);
       
        return scroller;
      }
      
      private HBox createOneRow(VBox parent, int moveNr) {
        HBox oneRow = new HBox();
        oneRow.prefWidthProperty().bind(parent.widthProperty());
        Label moveNrLabel = createNormalLabel(oneRow, moveNr);
        Label teamLabel  = createTeamLabel(oneRow, moveNr);
        Label moveLabel = createMoveClassificationLabel(oneRow, moveNr, "");
        oneRow.getChildren().addAll(moveNrLabel,teamLabel,moveLabel);
        rows[moveNr] = oneRow;
        return oneRow;
      }
      
     
      
      
      
      private void scrollToLabel(ScrollPane scrollPane, VBox vbox, javafx.scene.Node label) {
        Bounds viewportBounds = scrollPane.getViewportBounds();
        Bounds contentBounds = label.getBoundsInParent();

        double viewportHeight = viewportBounds.getHeight();
        double contentHeight = vbox.getHeight();

        double scrollOffset = contentBounds.getMinY() / (contentHeight - viewportHeight);
        scrollPane.setVvalue(scrollOffset);
    }
      
      /**
       * Creates a normal label to display the content in the table
       * @author Manuel Krakowski
       * @param text: String that is displayed by the label
       * @param h: parent used for relative resizing
       * @param i: number of the team the label belong to
       * @return: Label
       */
      private Label createNormalLabel(HBox h,int i) {
            Label l = new Label(String.valueOf(i));
            l.setAlignment(Pos.CENTER);
            if((i % 2) == 0) {
                 l.getStyleClass().add("lobby-normal-label");
            }else {
                l.getStyleClass().add("lobby-normal-label-2");
            }
           
            l.prefWidthProperty().bind(h.widthProperty().multiply(0.2));
           // l.setStyle("-fx-border-color:black");
            l.fontProperty().bind(moveTableContent);
            return l;
          }
      
      /**
       * Creates a normal label to display the content in the table
       * @author Manuel Krakowski
       * @param text: String that is displayed by the label
       * @param h: parent used for relative resizing
       * @param i: number of the team the label belong to
       * @return: Label
       */
      private Label createTeamLabel(HBox h,int i) {
            Label l = new Label("Team " + i);
            l.setAlignment(Pos.CENTER);
            if((i % 2) == 0) {
                 l.getStyleClass().add("lobby-normal-label");
            }else {
                l.getStyleClass().add("lobby-normal-label-2");
            }
            l.prefWidthProperty().bind(h.widthProperty().multiply(0.3));
            l.fontProperty().bind(moveTableContent);
            return l;
          }
      
      private Label createMoveClassificationLabel(HBox h,int i,String s) {
        Label l = new Label("SUPERBLUNDER");
        l.setAlignment(Pos.CENTER);
        if((i % 2) == 0) {
             l.getStyleClass().add("lobby-normal-label");
        }else {
            l.getStyleClass().add("lobby-normal-label-2");
        }
        l.prefWidthProperty().bind(h.widthProperty().multiply(0.5));
        l.fontProperty().bind(moveTableContent);
        return l;
      }
}

