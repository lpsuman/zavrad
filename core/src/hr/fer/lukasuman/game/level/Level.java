package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;
import hr.fer.lukasuman.game.level.blocks.*;

public class Level implements Disposable {
    public static final String TAG = Level.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private int width;
    private int height;

    private Pixmap levelPixmap;
    private AbstractBlock[][] blocks;
    private float blockSize;
    private float effectiveWidth;
    private float effectiveHeight;

    private Direction startDirection;
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
        checkPixmapSize(levelPixmap);
        init();
    }

    public Level (FileHandle file) {
        this.file = file;
        levelPixmap = new Pixmap(file);
        checkPixmapSize(levelPixmap);
        init();
    }

    public Level (int levelWidth, int levelHeight) {
        levelPixmap = new Pixmap(levelWidth, levelHeight, Pixmap.Format.RGBA8888);
        checkPixmapSize(levelPixmap);
        levelPixmap.setColor(EmptyBlock.COLOR_IN_LEVEL);
        levelPixmap.fill();
        width = levelPixmap.getWidth();
        height = levelPixmap.getHeight();
        drawLevelBorders();
        init();
    }

    private void checkPixmapSize(Pixmap pixmap) {
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        if (width < Constants.MIN_LEVEL_WIDTH || width > Constants.MAX_LEVEL_WIDTH
                || height < Constants.MIN_LEVEL_HEIGHT || height > Constants.MAX_LEVEL_HEIGHT) {
            throw new IllegalArgumentException("Level pixmap is too big!");
        }
    }

    private void drawLevelBorders() {
        levelPixmap.setColor(WallBlock.COLOR_IN_LEVEL);
        levelPixmap.drawLine(0, 0, width - 1, 0);
        levelPixmap.drawLine(width - 1, 0, width - 1, height - 1);
        levelPixmap.drawLine(width - 1, height - 1, 0, height - 1);
        levelPixmap.drawLine(0, height - 1, 0, 0);
    }

    private void init () {
        if (file != null) {
            levelName = file.nameWithoutExtension();
        } else {
            levelName = getBundle().get(LocalizationKeys.NEW_LEVEL_NAME);
        }
        width = levelPixmap.getWidth();
        height = levelPixmap.getHeight();
        blocks = new AbstractBlock[width][height];

        boolean multipleStarts = false;
        boolean multipleGoals = false;

        for (int pixelY = 0; pixelY < height; pixelY++) {
            for (int pixelX = 0; pixelX < width; pixelX++) {

                int posY = height - 1 - pixelY;
                int currentPixel = levelPixmap.getPixel(pixelX, pixelY);
                AbstractBlock newBlock = BlockFactory.getBlocByColor(currentPixel);

                if (newBlock == null) {
                    Gdx.app.error(TAG, "Invalid block type in level " + levelName
                            + " replaced with an empty block instead");
                    newBlock = BlockFactory.getBlockByName(EmptyBlock.LABEL);
                }

                if (newBlock.getClass().equals(StartBlock.class)) {
                    if (start != null) {
                        drawBlock(start, BlockFactory.getBlockByName(EmptyBlock.LABEL));
                        multipleStarts = true;
                    }
                    startDirection = newBlock.getDirection();
                    start = new GridPoint2(pixelX, posY);
                } else if (newBlock.getClass().equals(GoalBlock.class)) {
                    if (goal != null) {
                        drawBlock(goal, BlockFactory.getBlockByName(EmptyBlock.LABEL));
                        multipleGoals = true;
                    }
                    goal = new GridPoint2(pixelX, posY);
                }

                blocks[pixelX][posY] = newBlock;
            }
        }

        resetLevel();

        Gdx.app.debug(TAG, "level '" + levelName + "' loaded: " + width + "x" + height);
        String errMsg = "";
        if (multipleStarts) {
            errMsg += "Multiple starts found in level! All but last have been replaced with empty blocks.";
        }
        if (multipleGoals) {
            errMsg += "Multiple goals found in level! All but last have been replaced with emtpy blocks.";
        }
        if (!errMsg.isEmpty()) {
            Gdx.app.debug(TAG, errMsg);
        }
    }

    public void updateSprites(Camera camera) {
        effectiveWidth = camera.viewportWidth;
        effectiveHeight = camera.viewportHeight;
        blockSize = Math.min(effectiveWidth / levelPixmap.getWidth(), effectiveHeight / levelPixmap.getHeight());

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                AbstractBlock block = blocks[i][j];
                Vector2 gamePos = calcPos(i, j);
                block.setSpriteSize(blockSize);
                if (block.isDirectional()) {
                    block.getSprite().setOriginCenter();
                    block.getSprite().setRotation(block.getDirection().getDegrees() * -90.0f);
                }
                blocks[i][j].setSpritePos(gamePos);
            }
        }
    }

    public Vector2 calcPos(int x, int y) {
        int difference = Math.abs(width - height) / 2;
        if (width < height) {
            x += difference;
        } else {
            y += difference;
        }
        return new Vector2((x + 0.5f) * blockSize - effectiveWidth / 2.0f,
                (y + 0.5f) * blockSize - effectiveHeight / 2.0f);
    }

    public void resetLevel() {
        currentPosition = start;
        currentDirection = startDirection;
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
        if (newBlock == null || blocks[pos.x][pos.y].equals(newBlock)
                || pos.x <= 0 || pos.x >= width - 1 || pos.y <= 0 || pos.y >= height - 1) {
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
                drawBlock(start, BlockFactory.getBlockByName(EmptyBlock.LABEL));
            }
            startDirection = newBlock.getDirection();
            start = new GridPoint2(pos);
        } else if (BlockFactory.isGoal(newBlock)) {
            if (goal != null) {
                drawBlock(goal, BlockFactory.getBlockByName(EmptyBlock.LABEL));
            }
            goal = new GridPoint2(pos);
        }
        drawBlock(pos, newBlock);
        isChangesPending = true;
    }

    private void drawBlock(GridPoint2 pos, AbstractBlock newBlock) {
        blocks[pos.x][pos.y] = newBlock;
        levelPixmap.drawPixel(pos.x, height - 1 - pos.y, newBlock.getColorInLevel());
    }

    public GridPoint2 getBlockPosition(Vector2 pos) {
        GridPoint2 result = new GridPoint2();
        result.x = (int)((pos.x + effectiveWidth / 2.0f) / blockSize);
        result.y = (int)((pos.y + effectiveHeight / 2.0f) / blockSize);
        int difference = Math.abs(width - height) / 2;
        if (width < height) {
            result.x -= difference;
        } else {
            result.y -= difference;
        }
        if (result.x < 0 || result.x >= width || result.y < 0 || result.y >= height) {
            return null;
        }
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

    public void setFile(FileHandle file) {
        this.file = file;
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
