package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class EmptyBlock extends AbstractBlock {

    public EmptyBlock() {
        super();
    }

    public EmptyBlock(Texture texture) {
        super(texture);
    }

    public EmptyBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(texture, spritePos, spriteSize);
    }
}
