package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class WallBlock extends AbstractBlock {

    public WallBlock(Texture texture) {
        super(texture);
    }

    public WallBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(texture, spritePos, spriteSize);
    }
}
