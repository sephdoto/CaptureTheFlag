package org.ctf.UI.customObjects;

	/**
	 * @author mkrakows
	 * this class represents a region that for now just contains a white rectangle that has exactly the size of the region
	 */
	import javafx.scene.Node;
	import javafx.scene.control.Button;
	import javafx.scene.layout.Region;
	import javafx.scene.layout.StackPane;
	import javafx.scene.paint.Color;
	import javafx.scene.shape.Rectangle;

	public class BackgroundRepV1 extends Region {
		StackPane parenPane;
		private Rectangle rc;

		public BackgroundRepV1(StackPane sp) {

			parenPane = sp;
			rc = new Rectangle();
			rc.setFill(Color.WHITE);
			rc.setStroke(Color.BLACK);
			rc.setStrokeWidth(6);
			getChildren().add(rc);
			bindToPArent();
		}

		public void bindToPArent() {
			this.prefHeightProperty().bind(parenPane.heightProperty());
			this.prefWidthProperty().bind(parenPane.widthProperty());
		}

		protected void layoutChildren() {
			super.layoutChildren();
			rc.setWidth(this.getWidth());
			rc.setHeight(this.getHeight());

		}
		/**
		 * @author mkrakows
		 * This method makes it possible to add Nodes to the region from the outside
		 * @param Node
		 */
		public void addNode(Node node) {
			this.getChildren().add(node);
		}
	}


