package hr.fer.lukasuman.game;

import com.badlogic.gdx.graphics.Color;
import hr.fer.lukasuman.game.automata.AutomatonAction;
import hr.fer.lukasuman.game.level.blocks.EmptyBlock;

public class Constants {

    //TODO find other options for this class, too big and too diverse

    public static final String TITLE = "Automata Game";

    public static final float MENU_BUTTON_PADDING = 4.0f;

    public static final int BASE_SIZE = 700;
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

    public static final float DIALOG_WIDTH_FACTOR = 0.4f;
    public static final float DIALOG_HEIGHT_FACTOR = 0.4f;

    public static final float STATE_SIZE = VIEWPORT_WIDTH / 10;
    public static final String DEFAULT_STATE_LABEL = "S";
    public static final float STATE_FONT_SCALE = STATE_SIZE * 12 / VIEWPORT_WIDTH;
    public static final float STATE_LABEL_PADDING = 3.0f;
    public static final int STATE_CIRCLES_PIXMAP_SIZE = 127;
    public static final float START_STATE_CIRCLE_LINE_WIDTH_RATIO = 0.1f;
    public static final float STATE_BORDER_LINE_WIDTH_RATIO = 0.1f;
    public static final Color SELECTED_STATE_BORDER_COLOR = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static final float START_STATE_CIRCLE_SIZE = 1.2f * STATE_SIZE;

    public static final float TRANSITIONS_LINE_WIDTH = 3.0f;
    public static final Color TRANSITION_COLOR = new Color(0x000000ff);
    public static final Color SELECTED_TRANSITION_COLOR = new Color(0xffff00ff);
    public static final float BEZIER_FIDELITY = 1.0f;

    public static final String PREFERENCES_FILE = "preferences";

//    private static final String SKIN_NAME = "glassy-ui/glassy-ui";
//    public static final String DEFAULT_FONT_NAME = "font";
    private static final String SKIN_NAME = "default/uiskin";
    public static final String DEFAULT_FONT_NAME = "default-font";
//    private static final String SKIN_NAME = "shade-ui/uiskin";
//    public static final String DEFAULT_FONT_NAME = "font-button";
    public static final float FONT_RESOLUTION_FACTOR = 2.0f;

    private static final String SKIN_FOLDER = "skins";
    public static final String TEXTURE_ATLAS_LIBGDX_UI = SKIN_FOLDER + "/" + SKIN_NAME + ".atlas";
    public static final String SKIN_LIBGDX_UI = SKIN_FOLDER + "/" + SKIN_NAME + ".json";

    public static final String IMAGE_FOLDER = "images";
    public static final String AUTOMATA_STATE_TEXTURE = IMAGE_FOLDER + "/state.png";
//    public static final String AUTOMATA_SELECTED_STATE_TEXTURE = IMAGE_FOLDER + "/state_selected.png";
    public static final String AUTOMATA_RUNNING_STATE_TEXTURE = IMAGE_FOLDER + "/state_running.png";
    public static final String PLAYER_TEXTURE = IMAGE_FOLDER + "/box_arrow.png";
    public static final String MENU_BACKGROUND_TEXTURE = IMAGE_FOLDER + "/gear_background.jpg";

    private static final String LEVEL_FOLDER = "levels";
    public static final String LEVEL_PATH = LEVEL_FOLDER + "/level_01.png";
    public static final int MIN_LEVEL_WIDTH = 3;
    public static final int MIN_LEVEL_HEIGHT = 3;
    public static final int MAX_LEVEL_WIDTH = 100;
    public static final int MAX_LEVEL_HEIGHT = 100;

    public static final float SIMULATION_SPEED_FACTOR = 1.0f;

    public static final AutomatonAction DEFAULT_ACTION = AutomatonAction.MOVE_FORWARD;
    public static final String DEFAULT_TRANSITION_TRIGGER = EmptyBlock.LABEL;
    public static final String REMAINING_TRANSITIONS_LABEL = "*";

    private static final String LOCALIZATION_FOLDER = "i18n";
    private static final String BASE_BUNDLE_NAME = "translations";
    public static final String BUNDLE = LOCALIZATION_FOLDER + "/" + BASE_BUNDLE_NAME;

    public static final int LEVEL_NAME_NUM_OF_CHAR_DISPLAYED = 14;
    public static final float GUI_PADDING = 3.0f;
}
