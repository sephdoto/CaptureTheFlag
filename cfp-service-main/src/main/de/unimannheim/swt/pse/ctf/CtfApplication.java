package de.unimannheim.swt.pse.ctf;

import de.unimannheim.swt.pse.ctf.game.DummyGame;
import de.unimannheim.swt.pse.ctf.game.Game;
import de.unimannheim.swt.pse.ctf.game.GameEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main class (entry point) of your webservice.
 */
@SpringBootApplication
public class CtfApplication {

	public static void main(String[] args) {
		SpringApplication.run(CtfApplication.class, args);
	}

	/**
	 * FIXME You need to return your game engine implementation here. Return an instance here.
	 *
	 * @return your {@link Game} engine.
	 */
	public static Game createGameEngine() {
		// FIXME change object
		return new GameEngine();
	}

}
