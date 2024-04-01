package configs;

import java.util.HashMap;
import java.util.Objects;



import javafx.scene.image.Image;

public class ImageLoader {

	private static final String WORRIOR = "WarriorV1.JPG";
	private static HashMap<String, Image> images;

	/**
	 * Loads the images and initializes the HashMap with the image resources. THis
	 * method should be called when a new Game is created or joined
	 */
	public static void loadImages() {
		images = new HashMap<>();
		Image logInImage = initImage(WORRIOR);
		images.put("WarriorV1", logInImage);
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
