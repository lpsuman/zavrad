package hr.fer.lukasuman.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import hr.fer.lukasuman.game.AutomataGame;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.test.PathTest;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = Constants.DEFAULT_WINDOW_WIDTH;
		config.height = Constants.DEFAULT_WINDOW_HEIGHT;
		config.title = Constants.TITLE;
		new LwjglApplication(new AutomataGame(), config);
	}
}
