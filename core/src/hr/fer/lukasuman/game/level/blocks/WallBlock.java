package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Constants;

public class WallBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/wall_tile.png";
    public static final String LABEL = "wall";
    private static final boolean IS_TRAVERSABLE = false;
    public static final int COLOR_IN_LEVEL = 255 << 24 | 0 << 16 | 0 << 8 | 0xff; //RED

    public WallBlock() {
        this(loadTexture(TEXTURE));
    }

    public WallBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture);
    }

    public WallBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public WallBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture, spritePos, spriteSize);
    }
}
