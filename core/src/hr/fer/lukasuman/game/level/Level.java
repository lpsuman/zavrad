package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.blocks.*;

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

    public enum DIRECTION {
        NORTH(0),
        EAST(1),
        SOUTH(2),
        WEST(3);

        private int direction;

        DIRECTION(int direction) {
            this.direction = direction;
        }
    }

    private int width;
    private int height;

    private AbstractBlock[][] blocks;
    private float blockSize;
    private float effectiveWidth;
    private float effectiveHeight;

    private Position start;
    private Position goal;

    private int currentX;
    private int currentY;
    private DIRECTION currentDirection;

    public Level (String filename) {
        init(filename);
    }

    private void init (String filename) {
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        width = pixmap.getWidth();
        height = pixmap.getHeight();
        blocks = new AbstractBlock[width][height];

        effectiveWidth = (Constants.VIEWPORT_WIDTH / 2.0f) - Constants.RIGHT_VIEWPORT_BORDER;
        effectiveHeight = Constants.VIEWPORT_HEIGHT - Constants.RIGHT_VIEWPORT_BORDER;
        blockSize = Math.min(effectiveWidth / pixmap.getWidth(), effectiveHeight / pixmap.getHeight());

        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                AbstractBlock block = null;
                int posY = height - 1 - pixelY;

                Vector2 gamePos = calcPos(pixelX, posY);

                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    block = new EmptyBlock();
                } else if (BLOCK_TYPE.WALL.sameColor(currentPixel)) {
                    block = new WallBlock((Texture)Assets.getInstance().getAssetManager().get(Constants.WALL_TEXTURE),
                            gamePos, blockSize);
                } else if (BLOCK_TYPE.START.sameColor(currentPixel)) {
                    start = new Position(pixelX, posY);
                    block = new StartBlock((Texture)Assets.getInstance().getAssetManager().get(Constants.START_TEXTURE),
                            gamePos, blockSize);
                } else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
                    goal = new Position(pixelX, posY);
                    block = new GoalBlock((Texture)Assets.getInstance().getAssetManager().get(Constants.GOAL_TEXTURE),
                            gamePos, blockSize);
                }

                blocks[pixelX][posY] = block;
            }
        }
        pixmap.dispose();

        resetLevel();

        Gdx.app.debug(TAG, "level '" + filename + "' loaded: " + width + "x" + height
                + " start(" + start.x + ", " + start.y + ") end(" + goal.x + ", " + goal.y + ")");
    }

    public Vector2 calcPos(int x, int y) {
        return new Vector2((x + 0.5f) * blockSize - effectiveWidth / 2.0f,
                (y + 0.5f) * blockSize - effectiveHeight / 2.0f);
    }

    public void resetLevel() {
        currentX = start.x;
        currentY = start.y;
        currentDirection = DIRECTION.NORTH;
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

    public float getBlockSize() {
        return blockSize;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public DIRECTION getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(DIRECTION currentDirection) {
        this.currentDirection = currentDirection;
    }
}
