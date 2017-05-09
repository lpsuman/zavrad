package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class GoalBlock extends AbstractBlock {

    public static final String TEXTURE = "door_closed.png";
    public static final String LABEL = "goal";
    private static final boolean IS_TRAVERSABLE = true;

    public GoalBlock() {
        this(loadTexture(TEXTURE));
    }

    public GoalBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, texture);
    }

    public GoalBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public GoalBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, texture, spritePos, spriteSize);
    }
}
