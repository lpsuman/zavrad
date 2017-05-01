package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractBlock {
    public abstract void render(int xPos, int yPos, SpriteBatch batch);
}
