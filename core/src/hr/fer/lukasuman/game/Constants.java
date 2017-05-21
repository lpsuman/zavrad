package hr.fer.lukasuman.game;

import com.badlogic.gdx.graphics.Color;
import hr.fer.lukasuman.game.automata.AutomatonAction;
import hr.fer.lukasuman.game.level.blocks.EmptyBlock;

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
    public static final float GUI_BORDER_FACTOR = 0.01f;

    public static final float DIALOG_WIDTH_FACTOR = 0.33f;
    public static final float DIALOG_HEIGHT_FACTOR = 0.33f;

    public static final float STATE_SIZE = VIEWPORT_WIDTH / 15;
    public static final String DEFAULT_STATE_LABEL = "state";
    public static final float STATE_FONT_SCALE = STATE_SIZE * 15 / VIEWPORT_WIDTH;
    public static final int STATE_CIRCLES_PIXMAP_SIZE = 127;
    public static final float STATE_CIRCLES_LINE_WIDTH_RATIO = 0.1f;
    public static final float STATE_CIRCLES_SIZE = 1.2f * STATE_SIZE;

    public static final float TRANSITIONS_LINE_WIDTH = 3.0f;
    public static final Color TRANSITION_COLOR = new Color(0x000000ff);
    public static final Color SELECTED_TRANSITION_COLOR = new Color(0xdd0000ff);
    public static final float TRANSITION_FONT_SIZE = 20.0f;
    public static final float BEZIER_FIDELITY = 1.0f;

    public static final String PREFERENCES_FILE = "preferences";

//    private static final String SKIN_NAME = "glassy-ui/glassy-ui";
//    public static final String DEFAULT_FONT_NAME = "font";
    private static final String SKIN_NAME = "default/uiskin";
    public static final String DEFAULT_FONT_NAME = "default-font";
//    private static final String SKIN_NAME = "shade-ui/uiskin";
//    public static final String DEFAULT_FONT_NAME = "font-button";

    private static final String SKIN_FOLDER = "skins";
    public static final String TEXTURE_ATLAS_LIBGDX_UI = SKIN_FOLDER + "/" + SKIN_NAME + ".atlas";
    public static final String SKIN_LIBGDX_UI = SKIN_FOLDER + "/" + SKIN_NAME + ".json";

    public static final String IMAGE_FOLDER = "images";
    public static final String AUTOMATA_STATE_TEXTURE = IMAGE_FOLDER + "/state.png";
    public static final String AUTOMATA_SELECTED_STATE_TEXTURE = IMAGE_FOLDER + "/state_selected.png";
    public static final String AUTOMATA_RUNNING_STATE_TEXTURE = IMAGE_FOLDER + "/state_running.png";
    public static final String PLAYER_TEXTURE = IMAGE_FOLDER + "/box_arrow.png";
    public static final String MENU_BACKGROUND_TEXTURE = IMAGE_FOLDER + "/gear_background.jpg";

    private static final String LEVEL_FOLDER = "levels";
    public static final String LEVEL_PATH = LEVEL_FOLDER + "/level_02.png";
    public static final int MIN_LEVEL_WIDTH = 2;
    public static final int MIN_LEVEL_HEIGHT = 2;
    public static final int MAX_LEVEL_WIDTH = 100;
    public static final int MAX_LEVEL_HEIGHT = 100;

    public static final float SIMULATION_SPEED_FACTOR = 1.0f;

    public static final String START_SIM_BTN_TEXT = "start";
    public static final String STOP_SIM_BTN_TEXT = "stop";
    public static final String PAUSE_SIM_BTN_TEXT = "pause";
    public static final String RESUME_SIM_BTN_TEXT = "resume";

    public static final AutomatonAction DEFAULT_ACTION = AutomatonAction.MOVE_FORWARD;
    public static final String DEFAULT_TRANSITION_TRIGGER = EmptyBlock.LABEL;

    public static final String AUTOMATON_CONFIRM_MESSAGE = "Current automaton is unsaved. Would you like to save it?";
    public static final String LEVEL_CONFIRM_MESSAGE = "Current level is unsaved. Would you like to save it?";
    public static final String LEVEL_PASSED_FORMAT_MESSAGE = "Congratulations! You have passed the current level" +
            " with %d states. Would you like to load the next level?";
    public static final String NEW_LEVEL_CONFIRM_MESSAGE = "Create new level with the specified dimensions?";
}
