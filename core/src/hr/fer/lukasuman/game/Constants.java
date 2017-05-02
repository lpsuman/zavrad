package hr.fer.lukasuman.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Constants {
    public static final String TITLE = "Automata Game";
    public static final int DEFAULT_WINDOW_WIDTH = 1000;
    public static final int DEFAULT_WINDOW_HEIGHT = 500;

    public static final float VIEWPORT_WIDTH = 1000;
    public static final float VIEWPORT_HEIGHT = 500;
    public static final float LEFT_VIEWPORT_BORDER = 50;
    public static final float RIGHT_VIEWPORT_BORDER = 50;

    public static final float VIEWPORT_GUI_WIDTH = 1000;
    public static final float VIEWPORT_GUI_HEIGHT = 500;

    public static final float STATE_SIZE = VIEWPORT_HEIGHT / 10;
    public static final String STATE_TEXTURE = "box_arrow.png";
    public static final int STATE_SPRITE_SIZE = 128;
    public static final String DEFAULT_STATE_LABEL = "state";

    public static final float TRANSITIONS_LINE_WIDTH = 2.0f;
    public static final Color TRANSITION_COLOR = new Color(0, 0, 0, 1);
    public static final BitmapFont TRANSITION_FONT = new BitmapFont();
    public static final float TRANSITION_FONT_SIZE = 20.0f;

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
