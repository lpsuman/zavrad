package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.automata.AutomatonState;

import java.util.ArrayList;
import java.util.List;

public class LevelController {

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
            return true;
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
