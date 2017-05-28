package hr.fer.lukasuman.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.level.blocks.BlockFactory;
import hr.fer.lukasuman.game.level.blocks.WallBlock;
import hr.fer.lukasuman.game.render.StageManager;
import hr.fer.lukasuman.game.screens.DirectedGame;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.level.LevelController;

import java.util.Map;

public class GameController {
    private static final String TAG = GameController.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private GameRenderer gameRenderer;
    private StageManager stageManager;

    private DirectedGame game;
    private AutomataController automataController;
    private LevelController levelController;

    private AutomatonState selectedState;
    private AutomatonTransition selectedTransition;

    private boolean isSimulationStarted;
    private boolean isSimulationRunning;
    private boolean isCustomPlay;
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
        levelController = new LevelController(this);
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
        if (!automataController.getCurrentAutomaton().checkIfAutomatonValid()) {
            gameRenderer.getStageManager().showInformation(getBundle().get(LocalizationKeys.AUTOMATON_INVALID_MESSAGE));
            return;
        }
        if (!automataController.getCurrentAutomaton().getStates().isEmpty() && isSimulationStarted == false) {
            fullReset();
            isSimulationStarted = true;
            isSimulationRunning = true;
            stageManager.getStartSimulationButton().setText(getBundle().get(LocalizationKeys.STOP_SIM_BTN_TEXT));
        }
    }

    public void stopSimulation() {
        if (isSimulationStarted == true) {
            fullReset();
            isSimulationStarted = false;
            isSimulationRunning = false;
            automataController.getCurrentAutomaton().setCurrentState(null);
            stageManager.getStartSimulationButton().setText(getBundle().get(LocalizationKeys.START_SIM_BTN_TEXT));
            stageManager.getPauseSimulationButton().setText(getBundle().get(LocalizationKeys.PAUSE_SIM_BTN_TEXT));
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
            stageManager.getPauseSimulationButton().setText(getBundle().get(LocalizationKeys.RESUME_SIM_BTN_TEXT));
        }
    }

    private void resumeSimulation() {
        if (isSimulationStarted) {
            isSimulationRunning = true;
            stageManager.getPauseSimulationButton().setText(getBundle().get(LocalizationKeys.PAUSE_SIM_BTN_TEXT));
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
        stageManager.getUpperLeftStage().act(deltaTime);
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
            String stateMsg = null;
            if (getNumberOfStates() == 1) {
                stateMsg = getBundle().get(LocalizationKeys.STATE);
            } else {
                stateMsg = getBundle().get(LocalizationKeys.STATES);
            }
            stageManager.showConfirmationDialog(levelController::loadNextLevel, null,
                    getBundle().format(LocalizationKeys.LEVEL_PASSED_FORMAT_MESSAGE, getNumberOfStates(), stateMsg));
            return;
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
                    pauseSimulation();
                    stageManager.showInformation(getBundle().get(LocalizationKeys.ILLEGAL_MOVE));
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
            stageManager.getActionSelectBox().setSelected(this.selectedState.getAction());
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
        return automataController.getCurrentAutomaton().getNumberOfState();
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public void setGameRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.stageManager = gameRenderer.getStageManager();
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

    public boolean isSimulationStarted() {
        return isSimulationStarted;
    }

    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }
}
