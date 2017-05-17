package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class EmptyBlock extends AbstractBlock {

    public static final String TEXTURE = null;
    public static final String LABEL = "empty";
    public static final boolean IS_TRAVERSABLE = true;
    public static final int COLOR_IN_LEVEL = 255 << 24 | 255 << 16 | 255 << 8 | 0xff; //WHITE

    public EmptyBlock() {
        this(loadTexture(TEXTURE));
    }

    public EmptyBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture);
    }

    public EmptyBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public EmptyBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture, spritePos, spriteSize);
    }
}
