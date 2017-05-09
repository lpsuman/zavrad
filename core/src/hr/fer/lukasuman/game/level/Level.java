package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.blocks.*;

public class Level implements Disposable {
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

    private Pixmap levelPixmap;
    private AbstractBlock[][] blocks;
    private float blockSize;
    private float effectiveWidth;
    private float effectiveHeight;

    private Position start;
    private Position goal;

    private Position currentPosition;
    private Direction currentDirection;

    public Level (String filename) {
        init(filename);
    }

    private void init (String filename) {
        levelPixmap = new Pixmap(Gdx.files.internal(filename));
        width = levelPixmap.getWidth();
        height = levelPixmap.getHeight();
        blocks = new AbstractBlock[width][height];

        for (int pixelY = 0; pixelY < height; pixelY++) {
            for (int pixelX = 0; pixelX < width; pixelX++) {

                int posY = height - 1 - pixelY;
                AbstractBlock block;
                int currentPixel = levelPixmap.getPixel(pixelX, pixelY);

                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    block = new EmptyBlock();
                } else if (BLOCK_TYPE.WALL.sameColor(currentPixel)) {
                    block = new WallBlock();
                } else if (BLOCK_TYPE.START.sameColor(currentPixel)) {
                    start = new Position(pixelX, posY);
                    block = new StartBlock();
                } else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
                    goal = new Position(pixelX, posY);
                    block = new GoalBlock();
                } else {
                    Gdx.app.error(TAG, "Invalid block type in level " + filename
                            + " replaced with an empty block instead");
                    block = new EmptyBlock();
                }
                blocks[pixelX][posY] = block;
            }
        }

        resetLevel();

        Gdx.app.debug(TAG, "level '" + filename + "' loaded: " + width + "x" + height
                + " start(" + start.x + ", " + start.y + ") end(" + goal.x + ", " + goal.y + ")");
    }

    public void updateSprites(Camera camera) {
        effectiveWidth = camera.viewportWidth;
        effectiveHeight = camera.viewportHeight;
        blockSize = Math.min(effectiveWidth / levelPixmap.getWidth(), effectiveHeight / levelPixmap.getHeight());

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                Vector2 gamePos = calcPos(i, j);

                blocks[i][j].setSpriteSize(blockSize);
                blocks[i][j].setSpritePos(gamePos);
            }
        }
    }

    public Vector2 calcPos(int x, int y) {
        return new Vector2((x + 0.5f) * blockSize - effectiveWidth / 2.0f,
                (y + 0.5f) * blockSize - effectiveHeight / 2.0f);
    }

    public void resetLevel() {
        currentPosition = start;
        currentDirection = Direction.NORTH;
    }

    public AbstractBlock getBlockAt(int x, int y) {
        return blocks[x][y];
    }

    public AbstractBlock getBlockAt(Position pos) {
        return getBlockAt(pos.x, pos.y);
    }

    @Override
    public void dispose() {
        levelPixmap.dispose();
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

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
}
