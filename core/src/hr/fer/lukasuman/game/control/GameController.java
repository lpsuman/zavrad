package hr.fer.lukasuman.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.Position;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.screens.DirectedGame;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.level.LevelController;

import java.util.Map;

public class GameController {
    private static final String TAG = GameController.class.getName();

    private GameRenderer gameRenderer;
    private OrthographicCamera fullCamera;
    private OrthographicCamera automataCamera;
    private OrthographicCamera levelCamera;

    private DirectedGame game;
    private AutomataController automataController;
    private LevelController levelController;
    private int score;

    private AutomatonState selectedState;
    private boolean isSimulationRunning;
    private float simulationSpeed;
    private float timeUntilNextMove;

    public GameController(DirectedGame game) {
        this.game = game;
        init();
    }

    private void init() {
        automataController = new AutomataController();
        levelController = new LevelController();
        score = 0;
        isSimulationRunning = false;
        simulationSpeed = 1.0f;
        resetTimeUntilNextMove();
    }

    public void startSimulation() {
        if (isSimulationRunning == false) {
            isSimulationRunning = true;
        } else {
            isSimulationRunning = false;
            levelController.getCurrentLevel().resetLevel();
            resetTimeUntilNextMove();
        }
    }

    public void pauseSimulation() {
        if (isSimulationRunning == false) {
            isSimulationRunning = true;
        } else {
            isSimulationRunning = false;
        }
    }

    private void resetTimeUntilNextMove() {
        timeUntilNextMove = 1.0f;
    }

    private void incrementTimeUntilNextMove() {
        timeUntilNextMove += 1.0f;
    }

    public void update(float deltaTime) {
        gameRenderer.getStage().act(deltaTime);
        if (isSimulationRunning) {
            timeUntilNextMove -= deltaTime * simulationSpeed * Constants.SIMULATION_SPEED_FACTOR;
            if (timeUntilNextMove <= 0) {
                incrementTimeUntilNextMove();
                makeMove();
            }
        }
        updateStateObjects();
    }

    private void makeMove() {
        Level level = levelController.getCurrentLevel();
        Position currentPosition = level.getCurrentPosition();
        Position newPosition = level.getCurrentDirection().incrementPosition(currentPosition);
        AbstractBlock blockInFront = level.getBlockAt(newPosition);

        DrawableAutomaton automaton = automataController.getCurrentAutomaton();
        automaton.setCurrentState(automaton.getCurrentState().transition(blockInFront.getLabel()));
        AutomatonAction action = automaton.getCurrentState().getAction();

        switch (action) {
            case MOVE_FORWARD:

                if (level.getBlockAt(newPosition).isTraversable()) {
                    level.setCurrentPosition(newPosition);
                } else {
                    //TODO illegal move forward
                }
                break;
            case ROTATE_LEFT:
                level.setCurrentDirection(level.getCurrentDirection().changeDirection(-1));
                break;
            case ROTATE_RIGHT:
                level.setCurrentDirection(level.getCurrentDirection().changeDirection(1));
                break;
            default:
                Gdx.app.error(TAG, "unsupported action");
        }

    }

    private void updateStateObjects() {
        for (Map.Entry<AutomatonState, Sprite> entry :
                automataController.getCurrentAutomaton().getStateSprites().entrySet()) {
            AutomatonState state = entry.getKey();
            Sprite sprite = entry.getValue();
            sprite.setPosition(state.getX() - sprite.getWidth() / 2.0f, state.getY() - sprite.getHeight() / 2.0f);
        }
    }

    public boolean checkInside(Vector3 pos, Camera camera) {
        return ((Math.abs(pos.x) < camera.viewportWidth / 2)
                && (Math.abs(pos.y) < camera.viewportHeight / 2));
    }

    public void removeSelectedState() {
        automataController.getCurrentAutomaton().removeState(automataController.getCurrentAutomaton().getCurrentState());
    }

    public OrthographicCamera getFullCamera() {
        return fullCamera;
    }

    public OrthographicCamera getAutomataCamera() {
        return automataCamera;
    }

    public OrthographicCamera getLevelCamera() {
        return levelCamera;
    }

    public AutomatonState getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(AutomatonState selectedState) {
        this.selectedState = selectedState;
    }

    public void setSimulationSpeed(float simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }

    public void setFullCamera(OrthographicCamera fullCamera) {
        this.fullCamera = fullCamera;
    }

    public void setAutomataCamera(OrthographicCamera automataCamera) {
        this.automataCamera = automataCamera;
    }

    public void setLevelCamera(OrthographicCamera levelCamera) {
        this.levelCamera = levelCamera;
    }

    public DirectedGame getGame() {
        return game;
    }

    public AutomataController getAutomataController() {
        return automataController;
    }

    public LevelController getLevelController() {
        return levelController;
    }

    public int getScore() {
        return score;
    }

    public void setGameRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }
}
