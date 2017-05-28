package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.Direction;

public abstract class AbstractBlock {
    protected Sprite sprite;
    protected boolean isTraversable;
    protected boolean isDirectional;
    protected String label;
    protected int colorInLevel;
    protected Direction direction;
    //TODO add isDangerous flag

    //TODO add a few more block types

    protected AbstractBlock(String label, boolean isTraversable, boolean isDirectional, int colorInLevel,
                            Direction direction, Texture texture) {
        this.label = label;
        this.isTraversable = isTraversable;
        this.isDirectional = isDirectional;
        this.colorInLevel = colorInLevel;
        this.direction = direction;
        if (texture != null) {
            sprite = new Sprite(texture);
            sprite.setOriginCenter();
        }
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

    public int getColorInLevel() {
        return colorInLevel;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isDirectional() {
        return isDirectional;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public String toString() {
        return ((I18NBundle)Assets.getInstance().getAssetManager().get(Constants.BUNDLE)).get(label);
    }
}
