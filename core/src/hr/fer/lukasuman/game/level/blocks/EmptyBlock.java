package hr.fer.lukasuman.game.level.blocks;

import hr.fer.lukasuman.game.level.Direction;

public class EmptyBlock extends AbstractBlock {

    public static final String TEXTURE = null;
    public static final String LABEL = "empty";
    public static final boolean IS_TRAVERSABLE = true;
    private static final boolean IS_DIRECTIONAL = false;
    public static final int COLOR_IN_LEVEL = 255 << 24 | 255 << 16 | 255 << 8 | 0xff; //WHITE

    public EmptyBlock(Direction direction) {
        super(LABEL, IS_TRAVERSABLE, IS_DIRECTIONAL, COLOR_IN_LEVEL, direction, loadTexture(TEXTURE));
    }
}
