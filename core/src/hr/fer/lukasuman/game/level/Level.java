package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.blocks.*;

public class Level implements Disposable {
    public static final String TAG = Level.class.getName();

    private int width;
    private int height;

    private Pixmap levelPixmap;
    private AbstractBlock[][] blocks;
    private float blockSize;
    private float effectiveWidth;
    private float effectiveHeight;

    private GridPoint2 start;
    private GridPoint2 goal;

    private GridPoint2 currentPosition;
    private Direction currentDirection;

    private FileHandle file;
    private String levelName;
    private boolean isChangesPending;

    public Level (String fileName) {
        file = Gdx.files.internal(fileName);
        levelPixmap = new Pixmap(file);
        init();
    }

    public Level (FileHandle file) {
        this.file = file;
        levelPixmap = new Pixmap(file);
        init();
    }

    public Level (int levelWidth, int levelHeight) {
        levelPixmap = new Pixmap(levelWidth, levelHeight, Pixmap.Format.RGBA8888);
        levelPixmap.setColor(EmptyBlock.COLOR_IN_LEVEL);
        init();
    }

    private void init () {
        if (file != null) {
            levelName = file.nameWithoutExtension();
        } else {
            levelName = "new level";
        }
        width = levelPixmap.getWidth();
        height = levelPixmap.getHeight();
        blocks = new AbstractBlock[width][height];

        for (int pixelY = 0; pixelY < height; pixelY++) {
            for (int pixelX = 0; pixelX < width; pixelX++) {

                int posY = height - 1 - pixelY;
                int currentPixel = levelPixmap.getPixel(pixelX, pixelY);
                AbstractBlock newBlock = BlockFactory.getBlocByColor(currentPixel);

                if (newBlock == null) {
                    Gdx.app.error(TAG, "Invalid block type in level " + levelName
                            + " replaced with an empty block instead");
                    newBlock = new EmptyBlock();
                }

                if (newBlock.getClass().equals(StartBlock.class)) {
                    start = new GridPoint2(pixelX, posY);
                } else if (newBlock.getClass().equals(GoalBlock.class)) {
                    goal = new GridPoint2(pixelX, posY);
                }

                blocks[pixelX][posY] = newBlock;
            }
        }

        resetLevel();

        Gdx.app.debug(TAG, "level '" + levelName + "' loaded: " + width + "x" + height
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

    public AbstractBlock getBlockAt(GridPoint2 pos) {
        return getBlockAt(pos.x, pos.y);
    }

    public AbstractBlock getBlockAt(Vector2 pos) {
        return getBlockAt(getBlockPosition(pos));
    }

    public void setBlockAt(AbstractBlock newBlock, GridPoint2 pos) {
        if (newBlock == null || blocks[pos.x][pos.y].equals(newBlock)) {
            return;
        }

        AbstractBlock blockAtPos = blocks[pos.x][pos.y];
        if (BlockFactory.isStart(blockAtPos)) {
            if (start.equals(currentPosition)) {
                currentPosition = null;
            }
            start = null;
        } else if (BlockFactory.isGoal(blockAtPos)){
            if (goal.equals(currentPosition)) {
                currentPosition = null;
            }
            goal = null;
        }

        if (BlockFactory.isStart(newBlock)) {
            if (start != null) {
                blocks[start.x][start.y] = BlockFactory.getBlockByName(EmptyBlock.LABEL);
            }
            start = new GridPoint2(pos);
        } else if (BlockFactory.isGoal(newBlock)) {
            if (goal != null) {
                blocks[goal.x][goal.y] = BlockFactory.getBlockByName(EmptyBlock.LABEL);
            }
            goal = new GridPoint2(pos);
        }

        blocks[pos.x][pos.y] = newBlock;
        levelPixmap.drawPixel(pos.x, height - 1 - pos.y, newBlock.getColorInLevel());
        isChangesPending = true;
    }

    public GridPoint2 getBlockPosition(Vector2 pos) {
        GridPoint2 result = new GridPoint2();
        result.x = (int)((pos.x + effectiveWidth / 2.0f) / blockSize);
        result.y = (int)((pos.y + effectiveHeight / 2.0f) / blockSize);
        return result;
    }

    public boolean isPositionWithinLevel(GridPoint2 pos) {
        if (pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void dispose() {
        levelPixmap.dispose();
    }

    public Pixmap getLevelPixmap() {
        return levelPixmap;
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

    public GridPoint2 getStart() {
        return start;
    }

    public GridPoint2 getGoal() {
        return goal;
    }

    public float getBlockSize() {
        return blockSize;
    }

    public GridPoint2 getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(GridPoint2 currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    public FileHandle getFile() {
        return file;
    }

    public String getLevelName() {
        return levelName;
    }

    public boolean isChangesPending() {
        return isChangesPending;
    }

    public void setChangesPending(boolean changesPending) {
        isChangesPending = changesPending;
    }
}
