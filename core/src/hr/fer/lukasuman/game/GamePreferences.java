package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
    public static final String TAG = GamePreferences.class.getName();

    private static final GamePreferences instance = new GamePreferences();

    public boolean showFpsCounter;
    private Preferences prefs;

    private GamePreferences () {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE);
    }

    public void load () {
        showFpsCounter = prefs.getBoolean("showFpsCounter", false);
    }

    public void save () {
        prefs.putBoolean("showFpsCounter", showFpsCounter);
        prefs.flush();
    }

    public static GamePreferences getInstance() {
        return instance;
    }
}