package hr.fer.lukasuman.game.level;

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
        levels = new ArrayList<Level>();
        currentLevel = new Level(Constants.LEVEL_PATH);
        levels.add(currentLevel);
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}
