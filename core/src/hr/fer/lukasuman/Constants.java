package hr.fer.lukasuman;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import hr.fer.lukasuman.automata.TransitionInput;

public class Constants {
    public static final float VIEWPORT_WIDTH = 1000;
    public static final float VIEWPORT_HEIGHT = 500;

    public static final float STATE_SIZE = 100;
    public static final int STATE_SPRITE_SIZE = 128;
    public static final String DEFAULT_STATE_LABEL = "state";

    public static final float STATE_BORDER_LINE_WIDTH = 2.0f;

    public static final Color STATE_TEXTURE_FILL_COLOR = new Color(0, 0, 1, 1);
    public static final Color STATE_TEXTURE_BORDER_COLOR = new Color(1, 0.5f, 0, 1);

    public static final Color SELECTED_STATE_FILL_COLOR = new Color(1, 1, 0, 1);
    public static final Color SELECTED_STATE_BORDER_COLOR = new Color(1, 0.5f, 0, 1);

    public static final float TRANSITIONS_LINE_WIDTH = 2.0f;
    public static final Color TRANSITION_COLOR = new Color(0, 0, 0, 1);
    public static final BitmapFont TRANSITION_FONT = new BitmapFont();
    public static final float TRANSITION_FONT_SIZE = 20.0f;

    public static final int EMPTY_KEY = Input.Keys.E;
    public static final char EMPTY_CHAR = '.';
    public static final String EMPTY_LABEL = "empty";

    public static final int WALL_KEY = Input.Keys.W;
    public static final char WALL_CHAR = '#';
    public static final String WALL_LABEL = "wall";
}
