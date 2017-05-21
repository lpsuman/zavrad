package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import hr.fer.lukasuman.game.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LevelController {
    private static final String TAG = LevelController.class.getName();

    private List<Level> levels;
    private Level currentLevel;

    public LevelController() {
        init();
    }

    private void init() {
        levels = new ArrayList<>();
        currentLevel = new Level(Constants.LEVEL_PATH);
        levels.add(currentLevel);
    }

    public boolean loadLevel(FileHandle file) {
        if (file == null) {
            return false;
        }
        try {
            Level newLevel = new Level(file);
            levels.add(newLevel);
            currentLevel = newLevel;
            return true;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    public boolean saveLevel(FileHandle file) {
        if (file == null || currentLevel == null) {
            return false;
        } else {
            PixmapIO.writePNG(file, currentLevel.getLevelPixmap());
            currentLevel.setChangesPending(false);
            return true;
        }
    }

    public void loadNextLevel() {
        FileHandle newFile = null;
        try {
            String currentPath = currentLevel.getFile().pathWithoutExtension();
            String fileName = currentLevel.getFile().nameWithoutExtension();
            currentPath = currentPath.substring(0, currentPath.length() - fileName.length());

            int numberStart = -1, numberEnd = -1;
            boolean isNumberFound = false;
            for (int i = fileName.length() - 1; i >= 0; i--) {
                char c = fileName.charAt(i);
                if (Character.isDigit(c)) {
                    if (!isNumberFound) {
                        isNumberFound = true;
                        numberEnd = i;
                    }
                } else {
                    if (isNumberFound) {
                        numberStart = i + 1;
                        break;
                    }
                }
            }
            int levelNumber = Integer.parseInt(fileName.substring(numberStart, numberEnd + 1));
            String newFileName = fileName.substring(0, numberStart) + (levelNumber + 1);
            newFile = new FileHandle(new File(currentPath + newFileName + ".png"));

            loadLevel(newFile);
        } catch (Exception exc) {
            Gdx.app.debug(TAG, "Couldn't load next level: " + newFile == null ? "null" : newFile.path());

        }
    }

    public boolean createNewLevel(int width, int height) {
        if (width < Constants.MIN_LEVEL_WIDTH && width > Constants.MAX_LEVEL_WIDTH
                && height < Constants.MIN_LEVEL_HEIGHT && height > Constants.MAX_LEVEL_HEIGHT) {
            return false;
        }
        try {
            Level newLevel = new Level(width, height);
            levels.add(newLevel);
            currentLevel = newLevel;
            return true;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
