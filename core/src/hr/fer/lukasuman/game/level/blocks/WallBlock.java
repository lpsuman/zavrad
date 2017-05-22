package hr.fer.lukasuman.game.level.blocks;

import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.Direction;

public class WallBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/wall_tile.png";
    public static final String LABEL = "wall";
    private static final boolean IS_TRAVERSABLE = false;
    private static final boolean IS_DIRECTIONAL = false;
    public static final int COLOR_IN_LEVEL = 0 << 24 | 0 << 16 | 0 << 8 | 0xff; //BLACK

    public WallBlock(Direction direction) {
        super(LABEL, IS_TRAVERSABLE, IS_DIRECTIONAL, COLOR_IN_LEVEL, direction, loadTexture(TEXTURE));
    }
}
