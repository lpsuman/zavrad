package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Assets;

public abstract class AbstractBlock {
    protected Sprite sprite;
    protected boolean isTraversable;
    protected String label;

    protected AbstractBlock(String label, boolean isTraversable) {
        this.label = label;
        this.isTraversable = isTraversable;
    }

    protected AbstractBlock(String label, boolean isTraversable, Texture texture) {
        this(label, isTraversable);
        if (texture != null) {
            sprite = new Sprite(texture);
            sprite.setOriginCenter();
        }
    }

    protected AbstractBlock(String label, boolean isTraversable, Texture texture, Vector2 spritePos, float spriteSize) {
        this(label, isTraversable, texture);
        setSpriteSize(spriteSize);
        setSpritePos(spritePos);
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }

    protected static Texture loadTexture(String textureName) {
        if (textureName == null) {
            return null;
        } else {
            return (Texture) Assets.getInstance().getAssetManager().get(textureName);
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

    public boolean isTraversable() {
        return isTraversable;
    }

    public void setTraversable(boolean traversable) {
        isTraversable = traversable;
    }

    public String getLabel() {
        return label;
    }
}
