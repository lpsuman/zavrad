package hr.fer.lukasuman.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.screens.DirectedGame;
import hr.fer.lukasuman.game.screens.MenuScreen;

public class AutomataGame extends DirectedGame {

    public static void updateTitle() {
        Gdx.graphics.setTitle(((I18NBundle) Assets.getInstance().getAssetManager().get(Constants.BUNDLE))
                .get(LocalizationKeys.TITLE));
    }
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		GamePreferences.getInstance().load();
		Assets.getInstance().init(new AssetManager());
		updateTitle();
		setScreen(new MenuScreen(this));
	}
}
