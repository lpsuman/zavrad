package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.level.blocks.EmptyBlock;
import hr.fer.lukasuman.game.level.blocks.WallBlock;

public class Level {
    public static final String TAG = Level.class.getName();

    public enum BLOCK_TYPE {
        START(255, 255, 0), // yellow
        EMPTY(255, 255, 255), // white
        WALL(255, 0, 0), // red
        GOAL(0, 0, 255); // blue

        private int color;

        BLOCK_TYPE(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor (int color) {
            return this.color == color;
        }
    }

    private int width;
    private int height;
    private AbstractBlock[][] blocks;
    private Position start;
    private Position goal;

    public Level (String filename) {
        init(filename);
    }

    private void init (String filename) {
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        blocks = new AbstractBlock[width][height];

        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                AbstractBlock block = null;
                int posY = height - 1 - pixelY;
                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    block = new EmptyBlock();
                } else if (BLOCK_TYPE.WALL.sameColor(currentPixel)) {
                    block = new WallBlock();
                } else if (BLOCK_TYPE.START.sameColor(currentPixel)) {
                    start = new Position(pixelX, posY);
                    block = new EmptyBlock();
                } else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
                    goal = new Position(pixelX, posY);
                    block = new EmptyBlock();
                }

                blocks[pixelX][posY] = block;
            }
        }

        pixmap.dispose();
        Gdx.app.debug(TAG, "level '" + filename + "' loaded: " + width + "x" + height
                + " start(" + start.x + ", " + start.y + ") end(" + goal.x + ", " + goal.y + ")");
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public AbstractBlock[][] getBlocks() {
        return blocks;
    }

    public Position getStart() {
        return start;
    }

    public Position getGoal() {
        return goal;
    }
}
