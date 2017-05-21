package hr.fer.lukasuman.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.level.blocks.BlockFactory;
import hr.fer.lukasuman.game.level.blocks.WallBlock;
import hr.fer.lukasuman.game.screens.DirectedGame;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.level.LevelController;

import java.util.Map;

public class GameController {
    private static final String TAG = GameController.class.getName();

    private GameRenderer gameRenderer;

    private DirectedGame game;
    private AutomataController automataController;
    private LevelController levelController;
    private int numberOfStates;

    private AutomatonState selectedState;
    private AutomatonTransition selectedTransition;

    private boolean isSimulationStarted;
    private boolean isSimulationRunning;
    private float simulationSpeed;
    private float timeUntilNextMove;

    private boolean ignoreNextClick;

    private FileProcessor fileProcessor;

    public GameController(DirectedGame game) {
        this.game = game;
        init();
    }

    private void init() {
        automataController = new AutomataController();
        levelController = new LevelController();
        numberOfStates = 0;
        isSimulationStarted = false;
        isSimulationRunning = false;
        simulationSpeed = 1.0f;
        resetTimeUntilNextMove();
    }

    private void fullReset() {
        automataController.getCurrentAutomaton().reset();
        levelController.getCurrentLevel().resetLevel();
        resetTimeUntilNextMove();
    }

    public void toggleSimulationStarted() {
        if (isSimulationStarted == false) {
            startSimulation();
        } else {
            stopSimulation();
        }
    }

    private void startSimulation() {
        if (!automataController.getCurrentAutomaton().getStates().isEmpty() && isSimulationStarted == false) {
            fullReset();
            isSimulationStarted = true;
            isSimulationRunning = true;
            gameRenderer.getStartSimulationButton().setText(Constants.STOP_SIM_BTN_TEXT);
        }
    }

    public void stopSimulation() {
        if (isSimulationStarted == true) {
            fullReset();
            isSimulationStarted = false;
            isSimulationRunning = false;
            automataController.getCurrentAutomaton().setCurrentState(null);
            gameRenderer.getStartSimulationButton().setText(Constants.START_SIM_BTN_TEXT);
        }
    }

    public void toggleSimulationPaused() {
        if (isSimulationStarted) {
            if (isSimulationRunning == true) {
                pauseSimulation();
            } else {
                resumeSimulation();
            }
        }
    }

    private void pauseSimulation() {
        if (isSimulationStarted) {
            isSimulationRunning = false;
            gameRenderer.getPauseSimulationButton().setText(Constants.RESUME_SIM_BTN_TEXT);
        }
    }

    private void resumeSimulation() {
        if (isSimulationStarted) {
            isSimulationRunning = true;
            gameRenderer.getPauseSimulationButton().setText(Constants.PAUSE_SIM_BTN_TEXT);
        }
    }

    private void resetTimeUntilNextMove() {
        timeUntilNextMove = 1.0f;
    }

    private void incrementTimeUntilNextMove() {
        timeUntilNextMove += 1.0f;
    }

    public void update(float deltaTime) {
        ignoreNextClick = false;
        gameRenderer.getUpperLeftStage().act(deltaTime);
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
        GridPoint2 currentPosition = level.getCurrentPosition();
        if (currentPosition == null) {
            stopSimulation();
            return;
        }
        GridPoint2 newPosition = level.getCurrentDirection().incrementPosition(currentPosition);

        AbstractBlock currentBlock = level.getBlockAt(currentPosition);
        if (currentBlock != null && BlockFactory.isGoal(currentBlock)) {
            pauseSimulation();
            gameRenderer.showConfirmationDialog(levelController::loadNextLevel, GameController.this::resumeSimulation,
                    String.format(Constants.LEVEL_PASSED_FORMAT_MESSAGE, numberOfStates));
        }

        AbstractBlock blockInFront = null;
        boolean isBorderInFront = false;
        if (!level.isPositionWithinLevel(newPosition)) {
            isBorderInFront = true;
        } else {
            blockInFront = level.getBlockAt(newPosition);
        }

        DrawableAutomaton automaton = automataController.getCurrentAutomaton();
        if (automaton == null || automaton.getCurrentState() == null) {
            toggleSimulationStarted();
            return;
        }

        if (blockInFront != null) {
            automaton.setCurrentState(automaton.getCurrentState().transition(blockInFront.getLabel()));
        } else {
            automaton.setCurrentState(automaton.getCurrentState().transition(WallBlock.LABEL));
        }
        AutomatonAction action = automaton.getCurrentState().getAction();

        switch (action) {
            case MOVE_FORWARD:
                if (!isBorderInFront && blockInFront.isTraversable()) {
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

    public boolean checkInside(Vector2 pos) {
        return ((Math.abs(pos.x) < Constants.VIEWPORT_WIDTH / 2)
                && (Math.abs(pos.y) < Constants.VIEWPORT_WIDTH / 2));
    }

    public void removeSelectedState() {
        automataController.getCurrentAutomaton().removeState(automataController.getCurrentAutomaton().getCurrentState());
    }

    public AutomatonState getSelectedState() {
        return selectedState;
    }

    public void setSelectedState(AutomatonState selectedState) {
        this.selectedState = selectedState;
        if (this.selectedState != null) {
            gameRenderer.getActionSelectBox().setSelected(this.selectedState.getAction());
        }
    }

    public AutomatonTransition getSelectedTransition() {
        return selectedTransition;
    }

    public void setSelectedTransition(AutomatonTransition selectedTransition) {
        this.selectedTransition = selectedTransition;
    }

    public void setSimulationSpeed(float simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
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

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void setGameRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }

    public boolean isIgnoreNextClick() {
        return ignoreNextClick;
    }

    public void setIgnoreNextClick(boolean ignoreNextClick) {
        this.ignoreNextClick = ignoreNextClick;
    }

    public FileProcessor getFileProcessor() {
        return fileProcessor;
    }

    public void setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }
}
