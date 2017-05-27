package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
    public static final String TAG = GamePreferences.class.getName();

    private static final GamePreferences instance = new GamePreferences();

    private static final String DEBUG_KEY = "debug";
    private static final String FPS_KEY = "showFpsCounter";
    private static final String LANG_KEY = "language";

    public boolean debug;
    public boolean showFpsCounter;
    public String language;
    //TODO add more options (state size, various colors, choose skin, allow level editing)

    private Preferences prefs;

    private GamePreferences () {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES_FILE);
    }

    public void load() {
        debug = prefs.getBoolean(DEBUG_KEY, false);
        showFpsCounter = prefs.getBoolean(FPS_KEY, false);
        language = prefs.getString(LANG_KEY, "hr");
    }

    public void save() {
        prefs.putBoolean(DEBUG_KEY, debug);
        prefs.putBoolean(FPS_KEY, showFpsCounter);
        prefs.putString(LANG_KEY, language);
        prefs.flush();
    }

    public static GamePreferences getInstance() {
        return instance;
    }
}
