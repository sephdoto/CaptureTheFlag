package configs;

import java.util.HashMap;
import java.util.Objects;



import javafx.scene.image.Image;

public class ImageLoader {
	
	private static Image defauImage;
	
	private static final String WORRIOR = "RealWarrior.png";
	private static final String DEFAULTIMAGE = "Gorilla.JPG";
	private static final String BASE = "xy.jpg";
	
	
	//public static final String BASE = ""
	private static HashMap<String, Image> images;

	/**
	 * Loads the images and initializes the HashMap with the image resources. This
	 * method should be called when a new Game is created or joined
	 */
	public static void loadImages() {
		images = new HashMap<>();
		Image worriorImage = initImage(WORRIOR);
		images.put("WarriorV1", worriorImage);
		defauImage = initImage(DEFAULTIMAGE);
		Image hexagon = initImage(BASE);
		images.put("base", hexagon);
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
