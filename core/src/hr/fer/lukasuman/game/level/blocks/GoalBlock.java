package hr.fer.lukasuman.game.level.blocks;

import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.Direction;

public class GoalBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/door_closed.png";
    public static final String LABEL = "goal";
    private static final boolean IS_TRAVERSABLE = true;
    private static final boolean IS_DIRECTIONAL = false;
    public static final int COLOR_IN_LEVEL = 0 << 24 | 0 << 16 | 255 << 8 | 0xff; //BLUE

    public GoalBlock(Direction direction) {
        super(LABEL, IS_TRAVERSABLE, IS_DIRECTIONAL, COLOR_IN_LEVEL, direction, loadTexture(TEXTURE));
    }
}
