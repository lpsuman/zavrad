package hr.fer.lukasuman.game.level;

import hr.fer.lukasuman.game.Constants;

import java.util.ArrayList;
import java.util.List;

public class LevelController {

    private List<Level> levels;
    private Level currentLevel;
    private Position currentPosition;

    public LevelController() {
        init();
    }

    private void init() {
        levels = new ArrayList<Level>();
        currentLevel = new Level(Constants.LEVEL_PATH);
        levels.add(currentLevel);
        currentPosition = currentLevel.getStart();
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }
}
