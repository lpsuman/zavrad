package hr.fer.lukasuman.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import hr.fer.lukasuman.game.screens.DirectedGame;
import hr.fer.lukasuman.game.screens.MenuScreen;

public class AutomataGame extends DirectedGame {
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Assets.getInstance().init(new AssetManager());
		setScreen(new MenuScreen(this));
	}
}
