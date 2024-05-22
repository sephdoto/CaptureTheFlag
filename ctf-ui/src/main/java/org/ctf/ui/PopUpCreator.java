package org.ctf.ui;
import java.util.ArrayList;
import java.util.HashMap;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Descriptions;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.constants.Enums.AIConfigs;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.creators.InfoPaneCreator;
import org.ctf.ui.customobjects.ButtonPane;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import org.ctf.ui.remoteGame.JoinScene;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *  Creates the PopUpPanes to choose an AI an and custom it if possible
 *  @author Manuel Krakowski
 */
public class PopUpCreator {
	private StackPane root;
	private PopUpPane aiLevelPopUpPane;
	private PopUpPane aiorHumanpopup;
	private Scene scene;
	private PopUpPane aiconfigPopUp;
	private AIConfig defaultConfig;
	private AI aitype;
	PopUpPane saveConfig;
	private HomeSceneController hsc;
	private String defaultAiName;
	TextField enterConfigNamefield;
	private boolean remote = false;
	private ArrayList<ButtonPane> buttonPanes = new ArrayList<ButtonPane>();
	private HashMap<AIConfigs, Integer> multipliers = new HashMap<AIConfigs, Integer>();
	private SpinnerValueFactory<Integer> values;
	private SpinnerValueFactory<Double> values2;
private ObjectProperty<Font> popUpLabel;
	private ObjectProperty<Font> leaveButtonText;
	private ObjectProperty<Font> aiPowerText;
	private ObjectProperty<Font> aiConfigHeader;
	private ObjectProperty<Font> configButtonText; 
	private ObjectProperty<Font> configDescriptionLabel;
	private ObjectProperty<Font> spinnerLabel;
	private ObjectProperty<Font> enterNameButtonText; 
	private ObjectProperty<Font> saveConfigLavel; 
	public PopUpCreator(Scene scene, StackPane root, HomeSceneController hsc) {
		this.hsc = hsc;
		this.scene = scene;
		this.root = root;
		manageFontSizes();
	}

	/**
	 *  fits the size of all text that is displayed to to the size of the screen
	 *  @author Manuel Krakowski
	 */
	private void manageFontSizes() {
		 popUpLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/50));
		 leaveButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/80));
		 aiPowerText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/50));
		 aiConfigHeader = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/40));
		 configButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/60));
		 configDescriptionLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/70));
		 spinnerLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/50));
		 enterNameButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/60));
		 saveConfigLavel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/35));
		root.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiPowerText.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiConfigHeader.set(Font.font(newWidth.doubleValue() / 40));
				configButtonText.set(Font.font(newWidth.doubleValue() / 60));
				configDescriptionLabel.set(Font.font(newWidth.doubleValue() / 50));
				spinnerLabel.set(Font.font(newWidth.doubleValue() / 70));
				saveConfigLavel.set(Font.font(newWidth.doubleValue() / 35));
				enterNameButtonText.set(Font.font(newWidth.doubleValue() / 60));
			}
		});
	}

	/**
	 * Creates a PopupPane in which the user can choose one of 4 different AIs
	 * @author Manuel Krakowski
	 * @param aiOrHuman:    PopUpPane that is shown before this one, can be used to
	 *                      go back to it
	 * @param portText:     text which should be disabled in case of the create game
	 *                      screen
	 * @param serverIPText: text which should be disabled in case of the create game
	 *                      screen
	 * @return PopUpPane that should be shown
	 */
	public PopUpPane createAiLevelPopUp(PopUpPane aiOrHuman, TextField portText, TextField serverIPText) {
		if(aiOrHuman!= null) {
			aiorHumanpopup = aiOrHuman;
		root.getChildren().remove(aiorHumanpopup);
		}
		if(portText != null && serverIPText != null) {
		portText.setDisable(true);
		serverIPText.setDisable(true);
		}
		aiLevelPopUpPane = new PopUpPane(scene, 0.6, 0.4);
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			top.setSpacing(spacing);
		});
		Label l = new Label("Choose AI");
		l.prefWidthProperty().bind(aiLevelPopUpPane.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		aiLevelPopUpPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			double padding = newValue.doubleValue() * 0.03;
			buttonBox.setSpacing(newSpacing);
			buttonBox.setPadding(new Insets(0, padding, 0, padding));
		});
		buttonBox.getChildren().addAll(createRandomButton(),
				createAIPowerButton(AIConfigs.MCTS, 1));
		HBox buttonBox2 = new HBox();
		buttonBox2.setAlignment(Pos.CENTER);
		aiLevelPopUpPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			double padding = newValue.doubleValue() * 0.03;
			buttonBox2.setSpacing(newSpacing);
			buttonBox2.setPadding(new Insets(0, padding, 0, padding));
		});
		buttonBox2.getChildren().addAll(createAIPowerButton(AIConfigs.IMPROVED, 2),
				createAIPowerButton(AIConfigs.EXPERIMENTAL, 2));
		top.getChildren().addAll(buttonBox, buttonBox2);
		HBox centerLeaveButton = new HBox();
		centerLeaveButton.prefHeightProperty().bind(aiLevelPopUpPane.heightProperty().multiply(0.2));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().add(createBackButton());
		top.getChildren().add(centerLeaveButton);
		aiLevelPopUpPane.setContent(top);
		root.getChildren().add(aiLevelPopUpPane);
		return aiLevelPopUpPane;
	}
	
	private StackPane createRandomButton() {
	  StackPane stack = new StackPane();
	  stack.getStyleClass().add("ai-button-easy");
      Text text = new Text(AIConfigs.RANDOM.toString());
      text.fontProperty().bind(Bindings.createObjectBinding(
          () -> Font.font("Century Gothic", stack.getHeight() * 0.3), stack.heightProperty()));
      StackPane.setAlignment(text, Pos.CENTER_LEFT);
      stack.widthProperty().addListener((obs, old, newV) -> {
        double padding = newV.doubleValue() * 0.05;
        stack.setPadding(new Insets(padding));
      });
      text.setFill(Color.WHITE);
      text.setMouseTransparent(true);
      stack.getChildren().add(text);
      Image mp = ImageController.loadThemedImage(ImageType.MISC, "i1");
      ImageView vw = new ImageView(mp);
      StackPane.setAlignment(vw, Pos.CENTER_RIGHT);
      vw.fitHeightProperty().bind(stack.heightProperty().multiply(0.5));
      vw.setPreserveRatio(true);
      stack.getChildren().add(vw);
      InfoPaneCreator.addInfoPane(vw, App.getStage(), Descriptions.describe(AIConfigs.RANDOM), InfoPaneCreator.TOP);
      stack.prefWidthProperty().bind(root.widthProperty().multiply(0.22));
      stack.prefHeightProperty().bind(stack.widthProperty().multiply(0.45));
      stack.maxWidthProperty().bind(root.widthProperty().multiply(0.22));
      stack.maxHeightProperty().bind(stack.widthProperty().multiply(0.45));
      stack.setOnMouseClicked(event -> {
        aitype = AI.RANDOM;
        if (remote) {
          root.getChildren().remove(aiLevelPopUpPane);
          JoinScene joinscene = (JoinScene) scene;
          joinscene.createJoinWindowAI(joinscene.getId(), joinscene.getIp(), joinscene.getPort(),
              AI.RANDOM, null);
          return;
        }
        if (aiorHumanpopup == null) {
          PopUpCreatorEnterTeamName teamNamePopup =
              new PopUpCreatorEnterTeamName(scene, root, aiLevelPopUpPane, hsc, false, true);
          teamNamePopup.setAitype(aitype);
          teamNamePopup.setConfig(defaultConfig);
          teamNamePopup.createEnterNamePopUp();
        } else {
          PopUpCreatorEnterTeamName teamNamePopup =
              new PopUpCreatorEnterTeamName(scene, root, aiLevelPopUpPane, hsc, true, true);
          teamNamePopup.setAitype(aitype);
          teamNamePopup.setConfig(defaultConfig);
          teamNamePopup.createEnterNamePopUp();
        }

      }
      );
      return stack;
	}

	/**
	 * Creates a back button, which can be used to go back to the PopUpPane which was shown before the CHooseAi PopupPane
	 * @author Manuel Krakowski
	 */
	private Button createBackButton() {
		Button exit = new Button("back");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
		  if(remote==true) {
		    root.getChildren().remove(aiLevelPopUpPane);
		    return;
		  }
			if(aiorHumanpopup != null) {
			root.getChildren().remove(aiLevelPopUpPane);
			root.getChildren().add(aiorHumanpopup);
			System.out.println("Peter");
			} else {
				root.getChildren().remove(aiLevelPopUpPane);
			}
		});
		return exit;
	}

	/**
	 * Creates a {@link ButtonPane} for choosing an AI that can be customized by editing or
	 * loading AI configs. The Container provides the name of the AI and am Icon
	 * which provides additonal info when hovered over. When clicking the container
	 * two new button appear that allow to edit or load AI configs.
	 * 
	 * @author Aaron Niemesch
	 * @param aiName - AIConfigs value 
	 * @param InfoPanePosition - Constant that determines positioning of info popup
	 * @return {@link ButtonPane}
	 */
	private StackPane createAIPowerButton(AIConfigs aiName, int InfoPanePosition) {
		ButtonPane pane = new ButtonPane(aiName, hsc.getStage(), InfoPanePosition,buttonPanes);
		buttonPanes.add(pane);
		pane.prefWidthProperty().bind(root.widthProperty().multiply(0.22));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.45));
		pane.maxWidthProperty().bind(root.widthProperty().multiply(0.22));
		pane.maxHeightProperty().bind(pane.widthProperty().multiply(0.45));
		pane.getEditButton().setOnAction(e -> {		
		  this.aitype =  AI.valueOf(aiName.name()); 
		  root.getChildren().add(createConfigPane(1, 1,null));
			pane.reset();
		});
		pane.getLoadButton().setOnAction(e -> {
			this.aitype = AI.valueOf(aiName.name());
		  if(aiorHumanpopup!= null) {
			root.getChildren().remove(aiorHumanpopup);
			}
			root.getChildren().remove(aiLevelPopUpPane);
			root.getChildren().add(ComponentCreator.createAIWindow(this));
			pane.reset();
		});
		return pane;
	}

  public StackPane getRoot() {
		return root;
	}

	public PopUpPane getAiLevelPopUpPane() {
		return aiLevelPopUpPane;
	}

	/**
	 *Creates the pane in that a user can customize an AI with all its components
	 *@author Manuel Krakowski
	 * @param widht relative with in relation to the scene
	 * @param hight relative height in relation to the scene
	 * @return Popupane to custom Ai
	 */
	public PopUpPane createConfigPane(double widht, double hight, AIConfig costumConfig) {
		root.getChildren().remove(aiLevelPopUpPane);
        if (costumConfig != null) {
          defaultConfig = costumConfig;
        } else {
          defaultConfig = new AIConfig();
        }
		createConfigMaps();
		aiconfigPopUp = new PopUpPane(scene, widht, hight);
		StackPane configRoot = new StackPane();
		configRoot.getChildren().add(createBackgroundImage(configRoot));
		configRoot.getStyleClass().add("join-root");
		VBox mainBox = createMainBox(configRoot);
		mainBox.getChildren().add(createHeader());
		HBox sep = createMiddleHBox();
		VBox leftBoss = createLeftVBox(sep);
		leftBoss.getChildren().add(createHeader(leftBoss, "Heuristic Multipliers"));
		VBox rightBoss = createRightVBox(sep);
		rightBoss.getChildren().add(createHeader(rightBoss, "Hyperparameters"));
		StackPane left = createContentStackPane(0.3, 0.67);
		HBox topBox = createTopHbox(left);
		VBox leftColumn = createColumnVbox(topBox, 0.45);
		VBox rightColmn = createColumnVbox(topBox, 0.45);
		StackPane right = createContentStackPane(0.45, 0.67);
		aiconfigPopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newPadding = newValue.doubleValue() * 0.02;
			left.setPadding(new Insets(newPadding));
			right.setPadding(new Insets(newPadding));
		});
		HBox topBox2 = createTopHbox(right);
		VBox onlyColumn = createColumnVbox(topBox2, 0.9);
		fillColumns(leftColumn, rightColmn, onlyColumn);
		topBox.getChildren().addAll(leftColumn, rightColmn);
		topBox2.getChildren().add(onlyColumn);
		left.getChildren().add(topBox);
		right.getChildren().add(topBox2);
		leftBoss.getChildren().add(left);
		rightBoss.getChildren().add(right);
		sep.getChildren().add(leftBoss);
		sep.getChildren().add(rightBoss);
		mainBox.getChildren().add(sep);
		HBox buttomBox = createButtomHBox();
		
		mainBox.getChildren().add(buttomBox);
		configRoot.getChildren().add(mainBox);
		aiconfigPopUp.setContent(configRoot);
		return aiconfigPopUp;
	}

	/**
	 * Creates a transparent Background Image for the whole scene
	 * @author Manuel Krakowski
	 * @param configRoot Stackpane on that the background-image should be placed
	 * @return background-image
	 */
	private ImageView createBackgroundImage(StackPane configRoot) {
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, "tuning1");

		ImageView mpv = new ImageView(mp);
		mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1.2));
		mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1.2));
		mpv.setPreserveRatio(true);
		mpv.setOpacity(0.2);
		return mpv;
	}

	/**
	 * Creates Box which contains all the components of the scene.
	 * Divided into 3 parts: Header on top, Boxes with all Multipliers and hyperparams in the middle, Buttons to submit and leave in the bottom
	 * @author Manuel Krakowski
	 * @param parent StackPane in that the box is placed
	 * @return top-Vbox
	 */
	private VBox createMainBox(StackPane parent) {
		VBox mainBox = new VBox();
		mainBox.prefHeightProperty().bind(parent.heightProperty());
		mainBox.prefWidthProperty().bind(parent.widthProperty());
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.setSpacing(30);
		mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			mainBox.setSpacing(newSpacing);
		});
		return mainBox;
	}

	/**
	 * Creates the Header of the whole scene with the name AI-Generator
	 * @author Manuel Krakowski
	 * @return header-image
	 */
	private ImageView createHeader() {
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, "aiGenerator");
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(aiconfigPopUp.widthProperty().multiply(0.6));
		mpv.setPreserveRatio(true);
		mpv.fitHeightProperty().bind(aiconfigPopUp.heightProperty().multiply(0.08));
		return mpv;
	}

	/**
	 * Creates the HBox which is the top-Container of the middle part of the Screen
	 * @author Manuel Krakowski
	 * @return middle-seperator-Hbox
	 */
	private HBox createMiddleHBox() {
		
		HBox sep = new HBox();
		sep.prefHeightProperty().bind(aiconfigPopUp.heightProperty().multiply(0.65));
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		sep.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			sep.setSpacing(newSpacing);
		});
		return sep;
	}

	/**
	 * Creates Left Side of the Screen in which the multipliers will be placed
	 * @author Manuel Krakowski
	 * @param parent used for relative resizing
	 * @return Box in that multipliers will be placed
	 */
	private VBox createLeftVBox(HBox parent) {
		VBox leftBox = new VBox();
		// leftBox.setStyle("-fx-background-color: blue");
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.55));
		leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		return leftBox;
	}

	/**
	 *  Creates Right Side of the Screen in which the hyperparams will be placed
	 *  @author Manuel Krakowski
	 * @param parent used for relative resizing
	 * @return Box in that multipliers will be placed
	 */
	private VBox createRightVBox(HBox parent) {
		VBox rightBox = new VBox();
		// rightBox.setStyle("-fx-background-color: blue");
		rightBox.setAlignment(Pos.TOP_CENTER);
		rightBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.35));
		rightBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		return rightBox;
	}

	/**
	 * Creates the header-Text which can be used for the multiplier and the hyperparm-box
	 * @author Manuel Krakowski
	 * @param parent: used for relative resizing
	 * @param text:Text that the label should display
	 * @return
	 */
	private Label createHeader(VBox parent, String text) {
		Label l = new Label(text);
		l.getStyleClass().add("aiConfig-label");
		l.setAlignment(Pos.CENTER);
		l.fontProperty().bind(aiConfigHeader);
		l.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		return l;
	}

	/**
	 * Creates the box with border in which the parameters to modify the AI will be placed
	 * @author Manuel Krakowski
	 * @param relWidth  with in Relation two the whole screen
	 * @param relHeight height in relation of the whole scene
	 * @return:  paramter-box
	 */
	public StackPane createContentStackPane(double relWidth, double relHeight) {
		StackPane pane = new StackPane();
		pane.setAlignment(Pos.CENTER);
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(aiconfigPopUp.widthProperty().multiply(relWidth));
		pane.prefHeightProperty().bind(aiconfigPopUp.heightProperty().multiply(relHeight)); 
																						
		return pane;
	}

	/**
	 * Creates a HBox which is especially important in the left Box two divide it into two Columns
	 * @author Manuel Krakowski
	 * @param StackPane Box with border
	 * @return colum-Hbox
	 */
	private HBox createTopHbox(StackPane parent) {
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		topBox.prefHeightProperty().bind(parent.heightProperty());
		topBox.prefWidthProperty().bind(parent.widthProperty());
		topBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			topBox.setSpacing(newSpacing);
		});
		return topBox;
	}

	/**
	 *creates a Column which will contain the content 
	 * @author Manuel Krakowski
	 * @param parent  Box in which columns will be placed
	 * @param relWidth how much with of he Parent the column should fill relativly
	 * @return column-Vbox
	 */
	private VBox createColumnVbox(HBox parent, double relWidth) {
		VBox column = new VBox();
		column.prefWidthProperty().bind(parent.widthProperty().multiply(relWidth));
		column.prefHeightProperty().bind(parent.heightProperty());
		column.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.12;
			column.setSpacing(newSpacing);
		});
		return column;
	}

	/**
	 * creates one row containig a short description-label, info icon and spinner
	 * @author Manuel Krakowski
	 * @param parent Column in which the row should be placed
	 * @param text  Description Text for param
	 * @param min min value for param
	 * @param max max value for param
	 * @param current default value for param
	 * @param isDouble if param is double value
	 * @return Hbox: one row
	 */
	private HBox createOneRowHBox(VBox parent, AIConfigs text, int min, int max, int current, boolean isDouble) {
		HBox oneRow = new HBox();
		// oneRow.setStyle("-fx-background-color: yellow");
		oneRow.prefHeightProperty().bind(parent.heightProperty().divide(3));
		oneRow.prefWidthProperty().bind(parent.widthProperty());
		VBox divideRow = new VBox();
		divideRow.prefWidthProperty().bind(oneRow.widthProperty());
		divideRow.prefHeightProperty().bind(oneRow.heightProperty());
		HBox upperpart = createUpperPartOfRow(text, divideRow);
		HBox lowerPart = new HBox();
		lowerPart.setAlignment(Pos.CENTER);
		lowerPart.prefHeightProperty().bind(divideRow.heightProperty().divide(2));
		if (isDouble) {
			Spinner<Double> spinner = createConfigSpinnerDouble(0, Double.MAX_VALUE, defaultConfig.C, lowerPart);
			createDoubleSpinnerListener(spinner, text);
			lowerPart.getChildren().add(spinner);
		} else {
			Spinner<Integer> spinner = createConfigSpinner(min, max, current, lowerPart);
			createIntegerSpinnerListener(spinner, text);
			lowerPart.getChildren().add(spinner);
		}
		divideRow.getChildren().addAll(upperpart, lowerPart);
		oneRow.getChildren().add(divideRow);
		return oneRow;
	}

	/**
	 * creates the upper Part of the row containing a short
	 * description label and a info icon which shows information when hovering
	 * @author Manuel Krakowski 
	 * @param text:      Text of the Label
	 * @param oneRow:    Row in which it is placed
	 * @param divideRow: Vbox in which the whole row is placed
	 * @return HBox: upper part of the row
	 */
	private HBox createUpperPartOfRow(AIConfigs text, VBox divideRow) {
		HBox upperpart = new HBox();
		upperpart.setAlignment(Pos.CENTER);
		// upperpart.prefHeightProperty().bind(divideRow.heightProperty().divide(2));
		upperpart.prefWidthProperty().bind(divideRow.widthProperty().multiply(0.8));
		Label l = new Label(text.toString());
		l.getStyleClass().add("spinner-des-label");
		l.fontProperty().bind(configDescriptionLabel);
		l.setAlignment(Pos.CENTER);
		HBox upperLeft = new HBox();
		upperLeft.getChildren().add(l);
		upperLeft.prefWidthProperty().bind(upperpart.widthProperty().multiply(0.7));
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, "i1");

		ImageView vw = new ImageView(mp);
		vw.fitHeightProperty().bind(upperpart.heightProperty().multiply(0.7));
		vw.fitWidthProperty().bind(upperpart.widthProperty().multiply(0.2));
		vw.setPreserveRatio(true);
		InfoPaneCreator.addInfoPane(vw, App.getStage(), Descriptions.describe(text), InfoPaneCreator.BOTTOM);
		HBox upperRight = new HBox();
		upperRight.prefWidthProperty().bind(upperpart.widthProperty().multiply(0.1));
		upperRight.setAlignment(Pos.CENTER_RIGHT);
		upperRight.getChildren().add(vw);
		upperpart.getChildren().addAll(upperLeft, upperRight);
		return upperpart;
	}

	/**
	 *  creates an Integer-Spinn
	 * @author Manuel Krakowski
	 * @param min:    min value of param
	 * @param max;    max vallue of param
	 * @param cur:    default value of param
	 * @param parent: Hbox in which Spinner will be placed
	 * @return integer-spinner for one param
	 */
	private Spinner<Integer> createConfigSpinner(int min, int max, int cur, HBox parent) {
		this.values = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, cur);
		Spinner<Integer> spinner = new Spinner<>(values);
		spinner.getStyleClass().add("spinner");
		TextField spinnerText = spinner.getEditor();
		spinnerText.fontProperty().bind(spinnerLabel);
		spinner.setEditable(true);
		spinner.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
		spinner.prefHeightProperty().bind(parent.heightProperty().multiply(1));
		return spinner;
	}

	/**
	 *  creates an Double-Spinner which can be used to modify a param
	 * @author Manuel Krakowski
	 * @param min min value of param
	 * @param max  max vallue of param
	 * @param cur default value of param
	 * @param parent Hbox in which Spinner will be placed
	 * @return Double-spinner for one param
	 */
	private Spinner<Double> createConfigSpinnerDouble(double min, double max, double cur, HBox parent) {
		this.values2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, cur, 0.1);
		Spinner<Double> spinner = new Spinner<>(values2);
		spinner.getStyleClass().add("spinner");

		TextField spinnerText = spinner.getEditor();
		spinnerText.fontProperty().bind(spinnerLabel);
		spinner.setEditable(true);
		spinner.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
		spinner.prefHeightProperty().bind(parent.heightProperty().multiply(1));
		return spinner;
	}

	/**
	 * Adds a Listener to a Integer-Spinner which changes the respective value
	 * @author Manuel Krakowski 
	 * @param spinner: Spinner to which Listener is added
	 * @param valueToModify: Value that should be modified by the spinner
	 */
	private void createIntegerSpinnerListener(Spinner<Integer> spinner, AIConfigs valueToModify) {
		spinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			changeConfigIntValue(valueToModify, newValue);
		});
	}

	/**
	 *  Adds a Listener to a Double-Spinner which changes the respective value
	 * @author Manuel Krakowski
	 * @param spinner Spinner to which Listener is added
	 * @param valueToModify Value that should be modified by the spinner
	 */
	private void createDoubleSpinnerListener(Spinner<Double> spinner, AIConfigs valueToModify) {
		spinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			defaultConfig.C = newValue;
		});
	}

	/**
	 * Helper method for Spinner Listener to change the respective value of the
	 * config
	 * @author Manuel Krakoski
	 * @param config:   Config that should be changed
	 * @param newValue: new value of the config
	 */
	private void changeConfigIntValue(AIConfigs config, int newValue) {
		switch (config) {
		case ATTACK_POWER_MUL:
			defaultConfig.attackPowerMultiplier = newValue;
			System.out.println("Attack Power: " + defaultConfig.attackPowerMultiplier);
			break;
		case PIECE_MUL:
			defaultConfig.pieceMultiplier = newValue;
			System.out.println("Pice: " + newValue);
			break;
		case BASE_DISTANCE_MUL:
			defaultConfig.distanceBaseMultiplier = newValue;
			System.out.println("Base : " + newValue);
			break;
		case DIRECTION_MUL:
			defaultConfig.directionMultiplier = newValue;
			System.out.println("Distance: " + newValue);
			break;
		case FLAG_MUL:
			defaultConfig.flagMultiplier = newValue;
			System.out.println("Flags: " + newValue);
			break;
		case SHAPE_REACH_MUL:
			defaultConfig.shapeReachMultiplier = newValue;
			System.out.println("Shape Reach: " + newValue);
			break;
		case NUM_THREADS:
			defaultConfig.numThreads = newValue;
			break;
		case MAX_STEPS:
			defaultConfig.MAX_STEPS = newValue;
		default:
			break;
		}
	}

	/**
	 *  Initializes a Map with the names and default values
	 *  of all ai-multiplier values to fill the containers faster
	 *  @author Manuel Krakowski
	 */
	private void createConfigMaps() {
		multipliers.put(AIConfigs.ATTACK_POWER_MUL, defaultConfig.attackPowerMultiplier);
		multipliers.put(AIConfigs.PIECE_MUL, defaultConfig.pieceMultiplier);
		multipliers.put(AIConfigs.BASE_DISTANCE_MUL, defaultConfig.distanceBaseMultiplier);
		multipliers.put(AIConfigs.DIRECTION_MUL, defaultConfig.directionMultiplier);
		multipliers.put(AIConfigs.FLAG_MUL, defaultConfig.flagMultiplier);
		multipliers.put(AIConfigs.SHAPE_REACH_MUL, defaultConfig.shapeReachMultiplier);
	}

	/**
	 *  Finaly creates all the Multipliers and hyperparams with their spinners and sets their names and default values
	 * @author Manuel Krakowski
	 * @param multiplyerLeft Left Column of the multiplier-Box
	 * @param multiplyerRight Right Column of the multiplier-Box
	 * @param hyperparam Only Column of the hyperparam-box
	 */
	private void fillColumns(VBox multiplyerLeft, VBox multiplyerRight, VBox hyperparam) {
		int i = 0;
		for (AIConfigs multilier : multipliers.keySet()) {
			if (i < 3) {
				HBox oneRow = createOneRowHBox(multiplyerLeft, multilier, 0, Integer.MAX_VALUE,
						multipliers.get(multilier), false);
				multiplyerLeft.getChildren().add(oneRow);
			} else {
				HBox oneRow = createOneRowHBox(multiplyerRight, multilier, 0, Integer.MAX_VALUE,
						multipliers.get(multilier), false);
				multiplyerRight.getChildren().add(oneRow);
			}
			i++;
		}
		HBox row1 = createOneRowHBox(hyperparam, AIConfigs.C, -1, -1, -1, true);
		hyperparam.getChildren().add(row1);
		HBox row2 = createOneRowHBox(hyperparam, AIConfigs.MAX_STEPS, 0, Integer.MAX_VALUE, defaultConfig.MAX_STEPS,
				false);
		hyperparam.getChildren().add(row2);
		HBox row3 = createOneRowHBox(hyperparam, AIConfigs.NUM_THREADS, 1, Integer.MAX_VALUE, defaultConfig.numThreads,
				false);
		hyperparam.getChildren().add(row3);
	}

	/**
	 *  Creates the Hbox in the bottom of the screen which will contain 3 buttons
	 *  @author Manuel Krakowski
	 * @return Hbox
	 */
	private HBox createButtomHBox() {
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.TOP_CENTER);
		buttonBox.setSpacing(50);
		aiconfigPopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			buttonBox.setSpacing(newSpacing);
		});
		buttonBox.getChildren().addAll(createConfigButton("Save"), createConfigButton("Play"),
				createConfigButton("Back"));
		return buttonBox;
	}

	/**
	 * performs the action when the leave button is clicked
	 * @author Manuel Krakowski
	 * @param b: leave-button
	 */
	private void performLeave(Button b) {
		b.setOnAction(e -> {
			root.getChildren().remove(aiconfigPopUp);
			root.getChildren().add(aiLevelPopUpPane);
		});
	}

	/**
	 * Perfroms the action when the save-button is clicked
	 * @author Manuel Krakowski
	 * @param b: save-button
	 */
	private void performSave(Button b) {
		b.setOnAction(e -> {
			SoundController.playSound("Button", SoundType.MISC);
			createSaveConfigPopUp();
		});
	}
	
	/**
	 * Performs the action when the play-button is clicked
	 * @author Manuel Krakowski
	 * @param b play-button
	 */
	private void performPlay(Button b) {
		b.setOnAction(e -> {
			SoundController.playSound("Button", SoundType.MISC);
			if(remote) {
			  root.getChildren().remove(aiconfigPopUp);
			  JoinScene joinscene = (JoinScene) scene;
			  joinscene.createJoinWindowAI(joinscene.getId(), joinscene.getIp(), joinscene.getPort(), aitype, defaultConfig);
			  return;
			}
		  
		  if(aiorHumanpopup == null) {
				PopUpCreatorEnterTeamName teamNamePopup = new PopUpCreatorEnterTeamName(scene, root, aiconfigPopUp, hsc, false, true);
				teamNamePopup.setAitype(aitype);
				teamNamePopup.setConfig(defaultConfig);
				teamNamePopup.createEnterNamePopUp();
				

			}else {
				PopUpCreatorEnterTeamName teamNamePopup = new PopUpCreatorEnterTeamName(scene, root, aiconfigPopUp, hsc, true, true);
				teamNamePopup.setAitype(aitype);
				teamNamePopup.setConfig(defaultConfig);
				teamNamePopup.createEnterNamePopUp();
			}
		});
	}
	
	

	/**
	 * creates a button to either save/play, play or leave with always the same design
	 * @author Manuel Krakowski
	 * @param text
	 * @return
	 */
	private Button createConfigButton(String text) {
		Button configButton = new Button(text);
		configButton.fontProperty().bind(configButtonText);
		configButton.getStyleClass().add("leave-button");
		configButton.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		configButton.prefHeightProperty().bind(configButton.widthProperty().multiply(0.25));
		switch (text) {
		case "Back":
			performLeave(configButton);
			break;
		case "Save":
			performSave(configButton);
			break;
		case "Play":
			performPlay(configButton);
		default:
			break;
		}
		return configButton;
	}
	
	
	/**
	 * Creates the popup which is shown when the user wants to save a config
	 * @author Manuel Krakowski
	 */
	private void createSaveConfigPopUp() {
		saveConfig = new PopUpPane(scene, 0.55, 0.4);
		root.getChildren().remove(aiconfigPopUp);
		VBox topBox = new VBox();
		topBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			topBox.setSpacing(spacing);
		});
		Label l = new Label("Save Config as:");
		l.prefWidthProperty().bind(saveConfig.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(saveConfigLavel);
		topBox.getChildren().add(l);
		HBox enterNameBox = new HBox();
		enterNameBox.setAlignment(Pos.CENTER);
		enterConfigNamefield = CreateGameScreenV2.createTextfield("Enter the Config Name", 0.4);
		enterConfigNamefield.prefWidthProperty().bind(enterNameBox.widthProperty().multiply(0.8));
		enterNameBox.getChildren().add(enterConfigNamefield);
		topBox.getChildren().add(enterNameBox);
		HBox centerLeaveButton = new HBox();
		saveConfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			centerLeaveButton.setSpacing(newSpacing);
		});
		centerLeaveButton.prefHeightProperty().bind(saveConfig.heightProperty().multiply(0.4));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().addAll(createSavePopUpButton("Save"), createSavePopUpButton("Save and Play"), createSavePopUpButton("Back"));
		topBox.getChildren().add(centerLeaveButton);
		saveConfig.setContent(topBox);
		root.getChildren().add(saveConfig);
	}
	
	/**
	 * performs action when the user clicks back in the save-popup
	 * @author Manuel Krakowski
	 * @param b back button
	 */
	private void performSavePopUpBack(Button b) {
		b.setOnAction(e -> {
			root.getChildren().remove(saveConfig);
			root.getChildren().add(aiconfigPopUp);
		});
	}
	
	/**
	 * performs action when the save and play button is clicked
	 * @author Manuel Krakowski
	 * @param b save-and-play button
	 */
	private void performSavePopUpSaveAndPlay(Button b) {
		b.setOnAction(e -> {
			defaultConfig.saveConfigAs(enterConfigNamefield.getText());
			if(aiorHumanpopup == null) {
				PopUpCreatorEnterTeamName teamNamePopup = new PopUpCreatorEnterTeamName(scene, root, saveConfig, hsc, false, true);
				teamNamePopup.setConfig(defaultConfig);
				teamNamePopup.setAitype(aitype);
				teamNamePopup.createEnterNamePopUp();

			}else {
				PopUpCreatorEnterTeamName teamNamePopup = new PopUpCreatorEnterTeamName(scene, root, saveConfig, hsc, true, true);
				teamNamePopup.setConfig(defaultConfig);
				teamNamePopup.setAitype(aitype);
				teamNamePopup.createEnterNamePopUp();
			}
		});
	}
	
	/**
	 * performs the action when the save button is clicked on the save popup
	 * @author Manuel Krakowski
	 * @param b
	 */
	private void perfromSavePopUpSave(Button b) {
		b.setOnAction(e -> {
			defaultConfig.saveConfigAs(enterConfigNamefield.getText());
			root.getChildren().remove(saveConfig);
			root.getChildren().add(aiconfigPopUp);
		});
		
	}
	
	/**
	 * Creates a button-type which is used for the save-popup
	 * @author Manuel Krakowski
	 * @param text button-text
	 * @return default-button
	 */
	private Button createSavePopUpButton(String text) {
		Button namePopButton = new Button(text);
		namePopButton.fontProperty().bind(enterNameButtonText);
		namePopButton.getStyleClass().add("leave-button");
		namePopButton.prefWidthProperty().bind(root.widthProperty().multiply(0.13));
		namePopButton.prefHeightProperty().bind(namePopButton.widthProperty().multiply(0.25));
		switch (text) {
		case "Save":
			perfromSavePopUpSave(namePopButton);
			break;
		case "Save and Play":
			performSavePopUpSaveAndPlay(namePopButton);
			break;
		case "Back" :
			performSavePopUpBack(namePopButton);
		default:
			break;
		}
		return namePopButton;
	}
	
	public AIConfig getDefaultConfig() {
	    return defaultConfig;
	  }

	  public AI getAitype() {
	    return aitype;
	  }

    public void setRemote(boolean remote) {
      this.remote = remote;
    }
	  

}
