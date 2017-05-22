package hr.fer.lukasuman.game.level.blocks;

import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.Direction;

import java.util.ArrayList;
import java.util.List;

public class StartBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/door_open.png";
    public static final String LABEL = "start";
    private static final boolean IS_TRAVERSABLE = true;
    private static final boolean IS_DIRECTIONAL = true;

    private static final int COLOR_IN_LEVEL_NORTH = 255 << 24 | 0 << 16 | 0 << 8 | 0xff; //RED
    private static final int COLOR_IN_LEVEL_EAST = 255 << 24 | 128 << 16 | 0 << 8 | 0xff; //ORANGE
    private static final int COLOR_IN_LEVEL_SOUTH = 0 << 24 | 255 << 16 | 0 << 8 | 0xff; //GREEN
    private static final int COLOR_IN_LEVEL_WEST = 255 << 24 | 255 << 16 | 0 << 8 | 0xff; //YELLOW

    public static List<Integer> COLORS_IN_LEVEL;
    static {
        COLORS_IN_LEVEL = new ArrayList<>();
        COLORS_IN_LEVEL.add(COLOR_IN_LEVEL_NORTH);
        COLORS_IN_LEVEL.add(COLOR_IN_LEVEL_EAST);
        COLORS_IN_LEVEL.add(COLOR_IN_LEVEL_SOUTH);
        COLORS_IN_LEVEL.add(COLOR_IN_LEVEL_WEST);
    }

    public StartBlock(Direction direction) {
        super(LABEL, IS_TRAVERSABLE, IS_DIRECTIONAL, COLORS_IN_LEVEL.get(direction.getDegrees()), direction, loadTexture(TEXTURE));
    }

    @Override
    public int getColorInLevel() {
        return COLORS_IN_LEVEL.get(direction.getDegrees());
    }
}
