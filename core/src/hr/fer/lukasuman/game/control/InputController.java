package hr.fer.lukasuman.game.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.viewport.Viewport;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.automata.AutomatonTransition;
import hr.fer.lukasuman.game.automata.DrawableAutomaton;
import hr.fer.lukasuman.game.level.Direction;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.level.blocks.BlockFactory;
import hr.fer.lukasuman.game.level.blocks.BlockLabel;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.render.StageManager;
import hr.fer.lukasuman.game.screens.MenuScreen;
import hr.fer.lukasuman.game.screens.ScreenTransition;
import hr.fer.lukasuman.game.screens.ScreenTransitionSlide;

public class InputController extends InputAdapter {

    private static final String TAG = GameController.class.getName();

    private GameController gameController;
    private GameRenderer gameRenderer;
    private StageManager stageManager;

    private boolean wasStateMoved;
    private boolean touchedDown;
    private boolean selectionStartedOnState;
    private boolean selectionStartedOnTransition;

    private AutomatonState transitionStartState;

    private MenuScreen menuScreen;

    public InputController(GameController gameController, GameRenderer gameRenderer, MenuScreen menuScreen) {
        this.gameController = gameController;
        this.gameRenderer = gameRenderer;
        this.menuScreen = menuScreen;
        this.stageManager = gameRenderer.getStageManager();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameController.isIgnoreNextClick()) {
            gameController.setIgnoreNextClick(false);
            return false;
        }
        if (gameController.isSimulationStarted()) {
            return false;
        }
        touchedDown = true;
        Vector2 posInGame = getPosInGame(screenX, screenY, gameRenderer.getLeftViewport());
        if (posInGame != null) {
            automataTouchDown(posInGame, button);
        } else {
            posInGame = getPosInGame(screenX, screenY, gameRenderer.getRightViewport());
            if (posInGame != null) {
                levelTouchDown(posInGame, button);
            }
        }
        return false;
    }

    private void automataTouchDown(Vector2 posInGame, int button) {
        if (button != Input.Buttons.LEFT) {
            return;
        }
        DrawableAutomaton automaton = gameController.getAutomataController().getCurrentAutomaton();
        AutomatonState closestState = automaton.getClosestState(posInGame.x, posInGame.y);
        float distance = DrawableAutomaton.pointDistance(closestState, posInGame.x, posInGame.y);

        Button checkedButton = stageManager.getAutomatonButtonGroup().getChecked();
        if (checkedButton.equals(stageManager.getSelectionButton())) {
            if (closestState == null) {
                return;
            }
            if (distance <= Constants.STATE_SIZE / 2.0f) {
                gameController.setSelectedState(closestState);
                selectionStartedOnState = true;
            } else {
                selectionStartedOnState = false;
                AutomatonTransition closestTransition = automaton.getClosestTransition(
                        posInGame, Constants.STATE_SIZE / 2.0f);
                gameController.setSelectedTransition(closestTransition);
                if (closestTransition != null) {
                    selectionStartedOnTransition = true;
                } else {
                    gameController.setSelectedState(null);
                    selectionStartedOnTransition = false;
                }
            }
        } else if (checkedButton.equals(stageManager.getCreateStateButton())) {
            if (closestState == null || distance > Constants.STATE_SIZE) {
                gameController.setSelectedState(automaton.createState(
                        posInGame.x, posInGame.y, stageManager.getActionSelectBox().getSelected()));
            }
        } else if (checkedButton.equals(stageManager.getDeleteStateButton())) {
            if (closestState != null) {
                if (distance <= Constants.STATE_SIZE / 2) {
                    if (closestState.equals(gameController.getSelectedState())) {
                        gameController.setSelectedState(null);
                    }
                    automaton.removeState(closestState);
                }
            }
        } else if (checkedButton.equals(stageManager.getCreateTransitionButton())) {
            if (closestState != null) {
                if (distance <= Constants.STATE_SIZE / 2) {
                    transitionStartState = closestState;
                    gameRenderer.setTempTransition(new AutomatonTransition(
                            stageManager.getTransitionSelectBox().getSelected().getLabel(), closestState, posInGame));
                }
            }
        } else if (checkedButton.equals(stageManager.getDeleteTransitionButton())) {
            AutomatonTransition closestTransition = automaton.getClosestTransition(posInGame, Constants.STATE_SIZE / 2.0f);
            if (closestTransition != null) {
                if (closestTransition.equals(gameController.getSelectedTransition())) {
                    gameController.setSelectedTransition(null);
                }
                automaton.removeTransition(closestTransition);
            }
        } else if (checkedButton.equals(stageManager.getSetStartStateButton())) {
            if (closestState != null) {
                automaton.setStartState(closestState);
            }
        }
    }

    private void levelTouchDown(Vector2 posInGame, int button) {
        if (button == Input.Buttons.LEFT) {
            levelTouch(posInGame);
        } else {
            //TODO right click on level
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!touchedDown) {
            return false;
        }
        Vector2 posInGame = getPosInGame(screenX, screenY, gameRenderer.getLeftViewport());
        if (posInGame != null) {
            automataTouchDragged(posInGame);
        } else {
            posInGame = getPosInGame(screenX, screenY, gameRenderer.getRightViewport());
            if (posInGame != null) {
                levelTouchDragged(posInGame);
            }
        }
        return false;
    }

    private void automataTouchDragged(Vector2 posInGame) {
        AutomatonState selectedState = gameController.getSelectedState();
        Button checkedButton = stageManager.getAutomatonButtonGroup().getChecked();
        if (checkedButton.equals(stageManager.getSelectionButton())) {
            if (selectedState != null && selectionStartedOnState) {
                wasStateMoved = true;
                selectedState.setX(posInGame.x);
                selectedState.setY(posInGame.y);
                gameController.getAutomataController().getCurrentAutomaton().recalculateTransitions();
                return;
            }
            AutomatonTransition selectedTransition = gameController.getSelectedTransition();
            if (selectedTransition != null && selectionStartedOnTransition
                    && selectedTransition.getStartState().equals(selectedTransition.getEndState())) {
                selectedTransition.setManualLoopPositon(posInGame);
                selectedTransition.recalculate();
            }
        } else if (checkedButton.equals(stageManager.getCreateTransitionButton())) {
            AutomatonTransition tempTransition = gameRenderer.getTempTransition();
            if (tempTransition != null) {
                tempTransition.setEndPoint(posInGame);
                tempTransition.recalculate();
            }
        }
    }

    private void levelTouchDragged(Vector2 posInGame) {
        levelTouch(posInGame);
    }

    private void levelTouch(Vector2 posInGame) {
        Button checkedButton = stageManager.getLevelButtonGroup().getChecked();
        SelectBox<BlockLabel> blockTypeSelectBox = stageManager.getBlockTypeSelectBox();
        SelectBox<Direction> blockDirectionSelectBox = stageManager.getBlockDirectionSelectBox();
        Level currentLevel = gameController.getLevelController().getCurrentLevel();
        GridPoint2 levelPos = currentLevel.getBlockPosition(posInGame);
        if (levelPos == null) {
            return;
        }

        if (checkedButton.equals(stageManager.getSelectBlockButton())) {
            AbstractBlock selectedBlock = currentLevel.getBlockAt(levelPos);
            blockTypeSelectBox.setSelected(BlockLabel.getByLabel(selectedBlock.getLabel()));
            if (selectedBlock.isDirectional()) {
                blockDirectionSelectBox.setSelected(selectedBlock.getDirection());
            }
        } else if (checkedButton.equals(stageManager.getPaintBlockButton())) {
            AbstractBlock newBlock = BlockFactory.getBlockByName(blockTypeSelectBox.getSelected().getLabel());
            if (newBlock == null) {
                newBlock = BlockFactory.getBlockByName(blockTypeSelectBox.getSelected().getLabel()
                        + " " + blockDirectionSelectBox.getSelected().getName());
            }
            currentLevel.setBlockAt(newBlock, levelPos);
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchedDown = false;
        if (gameController.isSimulationStarted()) {
            return false;
        }
        Vector2 posInGame = getPosInGame(screenX, screenY, gameRenderer.getLeftViewport());
        if (posInGame == null) {
            return false;
        }
        DrawableAutomaton automaton = gameController.getAutomataController().getCurrentAutomaton();
        Button checkedButton = stageManager.getAutomatonButtonGroup().getChecked();
        if (checkedButton.equals(stageManager.getSelectionButton())) {
            if (wasStateMoved) {
                wasStateMoved = false;
                automaton.recalculateTransitions();
            }
        } else if (checkedButton.equals(stageManager.getCreateTransitionButton()) && (transitionStartState != null)) {
            AutomatonState endState = automaton.getClosestState(posInGame.x, posInGame.y);
            float distance = DrawableAutomaton.pointDistance(endState, posInGame.x, posInGame.y);

            if (distance <= Constants.STATE_SIZE / 2) {
                String newLabel = stageManager.getTransitionSelectBox().getSelected().getLabel();
                if (transitionStartState.getTransitions().containsKey(newLabel)) {
                    automaton.removeTransition(transitionStartState, newLabel);
                }
                automaton.addTransition(newLabel, transitionStartState, endState);
            }
            if (GamePreferences.getInstance().debug) {
                gameRenderer.getTempTransition().debug();
            }
            gameRenderer.setTempTransition(null);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (gameController.getAutomataController().getCurrentAutomaton().getCurrentState() != null
                && !gameController.isSimulationStarted()) {
            if (keycode == Input.Keys.FORWARD_DEL) {
                gameController.removeSelectedState();
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            backToMenu();
        }
        return true;
    }

    public void backToMenu() {
        gameController.stopSimulation();
        ScreenTransition transition = ScreenTransitionSlide.init(0.75f,
                ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);
        gameController.getGame().setScreen(menuScreen, transition);
    }

    public Vector2 getPosInGame(int x, int y, Viewport viewport) {
        float zoom = ((OrthographicCamera) viewport.getCamera()).zoom;
        Vector2 pos = viewport.unproject(new Vector2(x, y));
        if ((Math.abs(pos.x) < viewport.getWorldWidth() * zoom / 2.0f)
                && (Math.abs(pos.y) < viewport.getWorldHeight() * zoom / 2.0f)) {
            return pos;
        } else {
            return null;
        }
    }
}
