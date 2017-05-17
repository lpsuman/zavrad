package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Constants;

public class StartBlock extends AbstractBlock {

    public static final String TEXTURE = Constants.IMAGE_FOLDER + "/door_open.png";
    public static final String LABEL = "start";
    private static final boolean IS_TRAVERSABLE = true;
    public static final int COLOR_IN_LEVEL = 255 << 24 | 255 << 16 | 0 << 8 | 0xff; //YELLOW

    public StartBlock() {
        this(loadTexture(TEXTURE));
    }

    public StartBlock(Texture texture) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture);
    }

    public StartBlock(Vector2 spritePos, float spriteSize) {
        this(loadTexture(TEXTURE), spritePos, spriteSize);
    }

    public StartBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        super(LABEL, IS_TRAVERSABLE, COLOR_IN_LEVEL, texture, spritePos, spriteSize);
    }
}
