package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractBlock {
    protected Sprite sprite;

    protected AbstractBlock() {}

    protected AbstractBlock(Texture texture) {
        sprite = new Sprite(texture);
        sprite.setOriginCenter();
    }

    protected AbstractBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        this(texture);
        setSpriteSize(spriteSize);
        setSpritePos(spritePos);
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }

    public void setSpriteSize(float spriteSize) {
        if (sprite != null) {
            sprite.setSize(spriteSize, spriteSize);
        }
    }

    public void setSpritePos(Vector2 spritePos) {
        if (sprite != null) {
            sprite.setPosition(spritePos.x - sprite.getWidth() / 2.0f, spritePos.y - sprite.getHeight() / 2.0f);
        }
    }
}
