package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class AbstractBlock {
    protected Sprite sprite;

    protected AbstractBlock() {}

    protected AbstractBlock(Texture texture, Vector2 spritePos, float spriteSize) {
        sprite = new Sprite(texture);
        sprite.setSize(spriteSize, spriteSize);
        float halfWidth = sprite.getWidth() / 2.0f;
        float halfHeight = sprite.getHeight() / 2.0f;
        sprite.setOrigin(halfWidth, halfHeight);
        sprite.setPosition(spritePos.x - sprite.getWidth() / 2.0f, spritePos.y - sprite.getHeight() / 2.0f);
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }
}
