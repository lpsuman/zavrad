package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.control.GameController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelController {
    private static final String TAG = LevelController.class.getName();

    private List<Level> levels;
    private Level currentLevel;

    private GameController gameController;

    public LevelController(GameController gameController) {
        this.gameController = gameController;
        init();
    }

    private void init() {
        levels = new ArrayList<>();
        try {
            currentLevel = new Level(Constants.LEVEL_PATH);
        } catch (Exception exc) {
            currentLevel = new Level(10, 10);
        }
        levels.add(currentLevel);
    }

    public boolean loadLevel(FileHandle file) {
        if (file == null) {
            return false;
        }
        try {
            if (!file.exists()) {
                return false;
            }
            Level newLevel = new Level(file);
            levels.add(newLevel);
            currentLevel = newLevel;
            levelChanged();
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
            currentLevel.setFile(file);
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

            String newFileName = calculateNewLevelName(fileName);
            if (newFileName == null) {
                Gdx.app.debug(TAG, "level number not found in level name: " + fileName);
                newFileName = findNameOfNextLevel(currentLevel.getFile().file());
                if (newFileName == null) {
                    String msg = "Couldn't find next level in the same directory: " + currentPath;
                    Gdx.app.debug(TAG, msg);
                    showMessage(msg);
                    return;
                }
            }
            newFile = new FileHandle(new File(currentPath + newFileName + ".png"));
            if (!loadLevel(newFile)) {
                showMessage("There are no more levels in the same directory: " + currentPath);
            }
        } catch (Exception exc) {
            Gdx.app.debug(TAG, "Couldn't load next level");
            exc.printStackTrace();
        }
    }

    private void showMessage(String message) {
        gameController.getGameRenderer().getStageManager().showInformation(message);
    }

    private static String calculateNewLevelName(String fileName) {
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
        String newFileName = null;
        try {
            String strNumber = fileName.substring(numberStart, numberEnd + 1);
            int levelNumber = Integer.parseInt(strNumber);
            String strLevelNumber = Integer.toString(levelNumber);
            String leadingZeros = strNumber.substring(0, strNumber.length() - strLevelNumber.length());
            int incrementedLevelNumber = levelNumber + 1;
            String strIncrementedNumber = Integer.toString(incrementedLevelNumber);
            leadingZeros = leadingZeros.substring(0,
                    leadingZeros.length() - (strIncrementedNumber.length() - strLevelNumber.length()));
            newFileName = fileName.substring(0, numberStart) + leadingZeros + incrementedLevelNumber;
        } catch (Exception exc) {
        }
        return newFileName;
    }

    private static String findNameOfNextLevel(File file) {
        File[] fileList = file.getParentFile().listFiles((dir, name) -> {
            return name.toLowerCase().endsWith(".png");
        });
        Arrays.sort(fileList);
        String result = null;
        boolean isNext = false;
        for (File sortedFile : fileList) {
            if (isNext) {
                result = sortedFile.getName();
                break;
            }
            if (sortedFile.equals(file)) {
                isNext = true;
            }
        }
        return result;
    }

    public boolean createNewLevel(int width, int height) {
        if (width < Constants.MIN_LEVEL_WIDTH || width > Constants.MAX_LEVEL_WIDTH
                || height < Constants.MIN_LEVEL_HEIGHT || height > Constants.MAX_LEVEL_HEIGHT) {
            gameController.getGameRenderer().getStageManager().showInformation("Invalid level dimensions! Width must be ["
                + Constants.MIN_LEVEL_WIDTH + "-" + Constants.MAX_LEVEL_WIDTH + "] and height must be ["
                + Constants.MIN_LEVEL_HEIGHT + "-" + Constants.MAX_LEVEL_HEIGHT + "].");
            return false;
        }
        try {
            Level newLevel = new Level(width, height);
            levels.add(newLevel);
            currentLevel = newLevel;
            levelChanged();
            return true;
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private void levelChanged() {
        gameController.stopSimulation();
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
