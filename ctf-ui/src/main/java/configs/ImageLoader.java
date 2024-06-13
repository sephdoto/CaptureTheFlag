package configs;

import java.util.HashMap;
import java.util.Objects;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import javafx.scene.image.Image;

public class ImageLoader {
	
	private static Image defauImage;
	
	private static final String ROOK = "R2D2.png";
	private static final String QUEEN = "Yoda.png";
	private static final String BISHOP = "LukeSkywalker.png";
	private static final String Knight = "BobaFett.png";
	private static final String DEFAULTIMAGE = "RealWarrior.png";
	
	
	
	//public static final String BASE = ""
	private static HashMap<String, Image> images;

	/**
	 * Loads the images and initializes the HashMap with the image resources. This
	 * method should be called when a new Game is created or joined
	 */
	public static void loadImages() {
		images = new HashMap<>();
//		Image r2d2 = initImage(ROOK);
		images.put("Rook", ImageController.loadThemedImage(ImageType.PIECE, "Rook"));
//		Image yoda = initImage(QUEEN);
		images.put("Queen", ImageController.loadThemedImage(ImageType.PIECE, "Queen"));
//		Image luke = initImage(BISHOP);
		images.put("Bishop", ImageController.loadThemedImage(ImageType.PIECE, "Bishop"));
		defauImage = ImageController.loadThemedImage(ImageType.PIECE, "Default");
//		Image boba = initImage(Knight);
		images.put("Knight", ImageController.loadThemedImage(ImageType.PIECE, "Knight"));
	}

	/**
	 * Returns the image with the given name. If the image is not found, null is
	 * returned. Could be done by using an enum later
	 *
	 * @param imageName The name of the image.
	 * @return The image with the given name.
	 */
	public static Image getImageByName(String imageName) {
		return images.get(imageName);
	}
	public static Image getDefaultImage() {
		return defauImage;
	}

	/**
	 * Initializes an image with the given path. If the path is null, null is
	 * returned.
	 *
	 * @param path The path of the image.
	 * @return The image with the given path.
	 */
	public static Image initImage(String path) {
		return new Image(Objects.requireNonNull(ImageLoader.class.getResource(path)).toString()); //
	}
}
