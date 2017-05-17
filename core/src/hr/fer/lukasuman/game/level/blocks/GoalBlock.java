package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Constants;

public class GoalBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/door_closed.png";
    public static final String LABEL = "goal";
    private static final boolean IS_TRAVERSABLE = true;
    public static final int COLOR_IN_LEVEL = 0 << 24 | 0 << 16 | 255 << 8 | 0xff; //BLUE

    public GoalBlock() {
        this(loadTexture(TEXTURE));
    }

    public GoalBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture);
    }

    public GoalBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public GoalBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture, spritePos, spriteSize);
    }
}
