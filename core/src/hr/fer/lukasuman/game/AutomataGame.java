package hr.fer.lukasuman.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import hr.fer.lukasuman.game.screens.MenuScreen;

public class AutomataGame extends Game {

	private static final String TAG = AutomataGame.class.getName();
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Assets.getInstance().init(new AssetManager());
		setScreen(new MenuScreen(this));
	}
}
