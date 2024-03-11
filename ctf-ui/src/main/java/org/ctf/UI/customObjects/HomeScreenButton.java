package customObjects;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FillTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author mkrakows
 * This class represents a custom button which is showing an animation when hove
 */
public class HomeScreenButton extends StackPane {
	Runnable action;
	public HomeScreenButton(String name, Runnable action) {
		this.action = action;
		List<Stop> st = new ArrayList<Stop>();
		st.add(new Stop(0.1, Color.web("black", 0.8)));
		st.add(new Stop(1.0, Color.web("black", 0.6)));
		LinearGradient g = new LinearGradient(0.0, 0.5, 1.0, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
				st);
		Rectangle r = new Rectangle(300, 60, g);
		Rectangle r2 = new Rectangle(300, 60, Color.web("black", 0.2));
		FillTransition transition = new FillTransition(javafx.util.Duration.seconds(1), r2, Color.web("black", 0.2),
				Color.web("white", 0.2));
		transition.setAutoReverse(true);
		transition.setCycleCount(Integer.MAX_VALUE);
		hoverProperty().addListener((ob, old, isHovering) -> {
			if (isHovering) {
				transition.playFromStart();
			} else {
				transition.stop();
				r2.setFill(Color.web("black", 0.2));
			}

		});
		Rectangle line = new Rectangle(10, 60);
		line.fillProperty()
				.bind(Bindings.when(hoverProperty()).then(Color.RED).otherwise(Color.web("lightblue", 0.7)));
		line.widthProperty().bind(Bindings.when(hoverProperty()).then(12).otherwise(7));
		Text text = new Text(name);
		text.setFont(Font.font(30.0));
		text.fillProperty()
				.bind(Bindings.when(hoverProperty()).then(Color.WHITE).otherwise(Color.web("lightblue", 0.7)));

		setOnMouseClicked(e -> action.run());
		setOnMousePressed(e -> r.setFill(Color.LIGHTBLUE));
		setOnMouseReleased(e -> r.setFill(g));

		HBox hBox = new HBox(5, line, text);
		hBox.setAlignment(Pos.CENTER_LEFT);
		getChildren().addAll(r, r2, hBox);
	}
}
