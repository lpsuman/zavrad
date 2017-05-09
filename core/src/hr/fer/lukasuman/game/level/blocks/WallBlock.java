package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class WallBlock extends AbstractBlock {

    public static final String TEXTURE = "wall_tile.png";
    public static final String LABEL = "wall";
    private static final boolean IS_TRAVERSABLE = false;

    public WallBlock() {
        this(loadTexture(TEXTURE));
    }

    public WallBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, texture);
    }

    public WallBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public WallBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, texture, spritePos, spriteSize);
    }
}
