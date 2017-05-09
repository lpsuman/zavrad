package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class StartBlock extends AbstractBlock {

    public static final String TEXTURE = "door_open.png";
    public static final String LABEL = "start";
    private static final boolean IS_TRAVERSABLE = true;

    public StartBlock() {
        this(loadTexture(TEXTURE));
    }

    public StartBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, texture);
    }

    public StartBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public StartBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, texture, spritePos, spriteSize);
    }
}
