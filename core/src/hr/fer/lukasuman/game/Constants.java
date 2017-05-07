package hr.fer.lukasuman.game;

import com.badlogic.gdx.graphics.Color;

public class Constants {
    public static final String TITLE = "Automata Game";

    private static final int BASE_SIZE = 500;
    public static final float UPPER_BORDER_RATIO = 0.1f;
    public static final float LOWER_BORDER_RATIO = 0.1f;
    public static final int DEFAULT_WINDOW_WIDTH = BASE_SIZE * 2;
    public static final int DEFAULT_WINDOW_HEIGHT = (int)(BASE_SIZE * (UPPER_BORDER_RATIO + 1.0f + LOWER_BORDER_RATIO));

    public static final float VIEWPORT_WIDTH = 500;
    public static final float VIEWPORT_HEIGHT = VIEWPORT_WIDTH;

    public static final float VIEWPORT_GUI_WIDTH = 2.0f * VIEWPORT_WIDTH;
    public static final float UPPER_BORDER = VIEWPORT_GUI_WIDTH / 2.0f * UPPER_BORDER_RATIO;
    public static final float LOWER_BORDER = VIEWPORT_GUI_WIDTH / 2.0f * LOWER_BORDER_RATIO;
    public static final float VIEWPORT_GUI_HEIGHT = UPPER_BORDER + VIEWPORT_GUI_WIDTH / 2.0f + LOWER_BORDER;

    public static final float STATE_SIZE = VIEWPORT_WIDTH / 10;
    public static final String DEFAULT_STATE_LABEL = "state";

    public static final float TRANSITIONS_LINE_WIDTH = 2.0f;
    public static final Color TRANSITION_COLOR = new Color(0, 0, 0, 1);
    public static final float TRANSITION_FONT_SIZE = 20.0f;

    public static final String PREFERENCES_FILE = "preferences";

    public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
    public static final String SKIN_LIBGDX_UI = "images/uiskin.json";

    public static final String AUTOMATA_STATE_TEXTURE = "state.png";
    public static final String TEXTURE_ATLAS_OBJECTS = "images/automata.pack";
    public static final String MENU_BACKGROUND_TEXTURE = "gear_background.jpg";
    public static final String LEVEL_PATH = "levels/level_01.png";

    public static final String PLAYER_TEXTURE = "box_arrow.png";

    public static final String EMPTY_TEXTURE = null;
    public static final String EMPTY_LABEL = "empty";
    public static final String WALL_TEXTURE = "wall_tile.png";
    public static final String WALL_LABEL = "wall";
    public static final String START_TEXTURE = "door_open.png";
    public static final String START_LABEL = "start";
    public static final String GOAL_TEXTURE = "door_closed.png";
    public static final String GOAL_LABEL = "goal";
}
