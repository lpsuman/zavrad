package hr.fer.lukasuman.game.control;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.viewport.Viewport;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.automata.AutomatonTransition;
import hr.fer.lukasuman.game.automata.DrawableAutomaton;
import hr.fer.lukasuman.game.level.Direction;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;
import hr.fer.lukasuman.game.level.blocks.BlockFactory;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.screens.MenuScreen;
import hr.fer.lukasuman.game.screens.ScreenTransition;
import hr.fer.lukasuman.game.screens.ScreenTransitionSlide;

public class InputController extends InputAdapter {

    private static final String TAG = GameController.class.getName();

    private GameController gameController;
    private GameRenderer gameRenderer;

    private boolean wasStateMoved;
    private boolean touchedDown;

    private AutomatonState transitionStartState;

    private MenuScreen menuScreen;

    public InputController(GameController gameController, GameRenderer gameRenderer, MenuScreen menuScreen) {
        this.gameController = gameController;
        this.gameRenderer = gameRenderer;
        this.menuScreen = menuScreen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameController.isIgnoreNextClick()) {
            gameController.setIgnoreNextClick(false);
            return false;
        }
        touchedDown = true;
        Vector2 posInGame = getPosInGame(screenX, screenY, gameRenderer.getLeftViewport());
        if (gameController.checkInside(posInGame)) {
            automataTouchDown(posInGame, button);
        } else {
            posInGame = getPosInGame(screenX, screenY, gameRenderer.getRightViewport());
            if (gameController.checkInside(posInGame)) {
                levelTouchDown(posInGame, button);
            }
        }
        return false;
    }

    private void automataTouchDown(Vector2 posInGame, int button) {
//        Gdx.app.debug(TAG, "click on automata screen");
        if (button == Input.Buttons.LEFT) {
            DrawableAutomaton automaton = gameController.getAutomataController().getCurrentAutomaton();
            AutomatonState closestState = automaton.getClosestState(posInGame.x, posInGame.y);
            float distance = DrawableAutomaton.pointDistance(closestState, posInGame.x, posInGame.y);

            Button checkedButton = gameRenderer.getAutomatonButtonGroup().getChecked();
            if (checkedButton.equals(gameRenderer.getSelectionButton())) {
                if (closestState != null) {
                    if (distance <= Constants.STATE_SIZE / 2) {
                        gameController.setSelectedState(closestState);
                    } else {
                        gameController.setSelectedState(null);
                        gameController.setSelectedTransition(
                                automaton.getClosestTransition(posInGame, Constants.STATE_SIZE / 2.0f));
                    }
                }
            } else if (checkedButton.equals(gameRenderer.getCreateStateButton())) {
                if (closestState == null || distance > Constants.STATE_SIZE) {
                    gameController.setSelectedState(automaton.createState(
                            posInGame.x, posInGame.y, gameRenderer.getActionSelectBox().getSelected()));
                }
            } else if (checkedButton.equals(gameRenderer.getDeleteStateButton())) {
                if (closestState != null) {
                    if (distance <= Constants.STATE_SIZE / 2) {
                        if (closestState.equals(gameController.getSelectedState())) {
                            gameController.setSelectedState(null);
                        }
                        automaton.removeState(closestState);
                    }
                }
            } else if (checkedButton.equals(gameRenderer.getCreateTransitionButton())) {
                if (closestState != null) {
                    if (distance <= Constants.STATE_SIZE / 2) {
                        transitionStartState = closestState;
                        gameRenderer.setTempTransition(new AutomatonTransition(
                                gameRenderer.getTransitionSelectBox().getSelected(), closestState, posInGame));
                    }
                }
            } else if (checkedButton.equals(gameRenderer.getDeleteTransitionButton())) {
                AutomatonTransition closestTransition = automaton.getClosestTransition(posInGame, Constants.STATE_SIZE / 2.0f);
                if (closestTransition != null) {
                    if (closestTransition.equals(gameController.getSelectedTransition())) {
                        gameController.setSelectedTransition(null);
                    }
                    automaton.removeTransition(closestTransition);
                }
            } else if (checkedButton.equals(gameRenderer.getSetStartStateButton())) {
                if (closestState != null) {
                    automaton.setStartState(closestState);
                }
            }
        } else if (button == Input.Buttons.RIGHT) {
            //TODO right click on automata
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
        if (gameController.checkInside(posInGame)) {
            automataTouchDragged(posInGame);
        } else {
            posInGame = getPosInGame(screenX, screenY, gameRenderer.getRightViewport());
            if (gameController.checkInside(posInGame)) {
                levelTouchDragged(posInGame);
            }
        }
        return false;
    }

    private void automataTouchDragged(Vector2 posInGame) {
        AutomatonState selectedState = gameController.getSelectedState();
        Button checkedButton = gameRenderer.getAutomatonButtonGroup().getChecked();
        if (checkedButton.equals(gameRenderer.getSelectionButton())) {
            if (selectedState != null) {
                wasStateMoved = true;
                selectedState.setX(posInGame.x);
                selectedState.setY(posInGame.y);

                //might be cpu intensive
                gameController.getAutomataController().getCurrentAutomaton().recalculateTransitions();
            }
        } else if (checkedButton.equals(gameRenderer.getCreateTransitionButton())) {
            gameRenderer.getTempTransition().setEndPoint(posInGame);
            gameRenderer.getTempTransition().recalculate();
        }
    }

    private void levelTouchDragged(Vector2 posInGame) {
        levelTouch(posInGame);
    }

    private void levelTouch(Vector2 posInGame) {
        Button checkedButton = gameRenderer.getLevelButtonGroup().getChecked();
        SelectBox<String> blockTypeSelectBox = gameRenderer.getBlockTypeSelectBox();
        SelectBox<Direction> blockDirectionSelectBox = gameRenderer.getBlockDirectionSelectBox();
        Level currentLevel = gameController.getLevelController().getCurrentLevel();
        GridPoint2 levelPos = currentLevel.getBlockPosition(posInGame);

        if (checkedButton.equals(gameRenderer.getSelectBlockButton())) {
            AbstractBlock selectedBlock = currentLevel.getBlockAt(levelPos);
            blockTypeSelectBox.setSelected(selectedBlock.getLabel());
            if (selectedBlock.isDirectional()) {
                blockDirectionSelectBox.setSelected(selectedBlock.getDirection());
            }
        } else if (checkedButton.equals(gameRenderer.getPaintBlockButton())) {
            AbstractBlock newBlock = BlockFactory.getBlockByName(blockTypeSelectBox.getSelected());
            if (newBlock == null) {
                newBlock = BlockFactory.getBlockByName(blockTypeSelectBox.getSelected()
                        + " " + blockDirectionSelectBox.getSelected().toString());
            }
            currentLevel.setBlockAt(newBlock, levelPos);
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchedDown = false;
        Vector2 posInGame = getPosInGame(screenX, screenY, gameRenderer.getLeftViewport());
        if (!gameController.checkInside(posInGame)) {
            return false;
        }
        DrawableAutomaton automaton = gameController.getAutomataController().getCurrentAutomaton();
        Button checkedButton = gameRenderer.getAutomatonButtonGroup().getChecked();
        if (checkedButton.equals(gameRenderer.getSelectionButton())) {
            if (wasStateMoved) {
                wasStateMoved = false;
                automaton.recalculateTransitions();
            }
        } else if (checkedButton.equals(gameRenderer.getCreateTransitionButton()) && (transitionStartState != null)) {
            AutomatonState endState = automaton.getClosestState(posInGame.x, posInGame.y);
            float distance = DrawableAutomaton.pointDistance(endState, posInGame.x, posInGame.y);

            if (distance <= Constants.STATE_SIZE / 2) {
                String newLabel = gameRenderer.getTransitionSelectBox().getSelected();
                if (transitionStartState.getTransitions().containsKey(newLabel)) {
                    automaton.removeTransition(transitionStartState, newLabel);
                }
                AutomatonTransition existingTransition = automaton.getTransition(transitionStartState, endState);
                if (existingTransition == null) {
                    //TODO maybe reuse temp transition
                    AutomatonTransition newTransition = automaton.addTransition(newLabel, transitionStartState, endState);
                    gameController.setSelectedTransition(newTransition);
                } else {
                    existingTransition.addLabel(newLabel);
                }
            }

            gameRenderer.setTempTransition(null);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (gameController.getAutomataController().getCurrentAutomaton().getCurrentState() != null) {
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

    public Vector2 getPosInGame(int x, int y, Viewport viewPort) {
        return viewPort.unproject(new Vector2(x, y));
    }
}
