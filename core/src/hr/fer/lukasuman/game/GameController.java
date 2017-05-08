package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import hr.fer.lukasuman.game.automata.AutomataController;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.automata.DrawableAutomaton;
import hr.fer.lukasuman.game.level.LevelController;
import hr.fer.lukasuman.game.screens.*;

import java.util.Map;

public class GameController extends InputAdapter {
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

    public GameController(DirectedGame game) {
        this.game = game;
        init();
    }

    private void init() {
        automataController = new AutomataController();
        score = 0;
        levelController = new LevelController();
    }

    public void update(float deltaTime) {
        gameRenderer.getStage().act(deltaTime);

        updateStateObjects();
    }

    private void updateStateObjects() {
        for (Map.Entry<AutomatonState, Sprite> entry :
                automataController.getCurrentAutomaton().getStateSprites().entrySet()) {
            AutomatonState state = entry.getKey();
            Sprite sprite = entry.getValue();
            sprite.setPosition(state.getX() - sprite.getWidth() / 2.0f, state.getY() - sprite.getHeight() / 2.0f);
        }
    }

    private boolean checkInside(Vector3 pos, Camera camera) {
        return ((Math.abs(pos.x) < camera.viewportWidth / 2)
                && (Math.abs(pos.y) < camera.viewportHeight / 2));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 posInGame = getPosInGame(screenX, screenY, automataCamera);
        if (checkInside(posInGame, automataCamera)) {
            automataTouchDown(posInGame, button);
        } else {
            posInGame = getPosInGame(screenX, screenY, levelCamera);
            if (checkInside(posInGame, levelCamera)) {
                levelTouchDown(posInGame, button);
            }
        }
        posInGame = getPosInGame(screenX, screenY, fullCamera);
        touchDownGUI(posInGame, button);
        return false;
    }

    private void touchDownGUI(Vector3 posInGame, int button) {
        Gdx.app.debug(TAG, "click on GUI");
    }

    private void automataTouchDown(Vector3 posInGame, int button) {
        Gdx.app.debug(TAG, "click on automata screen");
        if (button == Input.Buttons.LEFT) {
            AutomatonState closestState = automataController.getCurrentAutomaton().getClosestState(posInGame.x, posInGame.y);
            float distance = DrawableAutomaton.pointDistance(closestState, posInGame.x, posInGame.y);

            Button checkedButton = gameRenderer.getButtonGroup().getChecked();
            if (checkedButton.equals(gameRenderer.getSelectionButton())) {
                if (closestState != null) {
                    if (distance <= Constants.STATE_SIZE / 2) {
                        selectedState = closestState;
                    } else {
                        selectedState = null;
                    }
                }
            } else if (checkedButton.equals(gameRenderer.getCreateStateButton())) {
                if (closestState == null || distance > Constants.STATE_SIZE) {
                    automataController.getCurrentAutomaton().createState(posInGame.x, posInGame.y);
                }
            } else if (checkedButton.equals(gameRenderer.getDeleteStateButton())) {
                if (closestState != null) {
                    if (distance <= Constants.STATE_SIZE / 2) {
                        automataController.getCurrentAutomaton().removeState(closestState);
                    }
                }
            }

        } else if (button == Input.Buttons.RIGHT) {
            //TODO left click on automata
        }
    }

    private void levelTouchDown(Vector3 posInGame, int button) {
        Gdx.app.debug(TAG, "click on level screen");
        //TODO click on level
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 posInGame = getPosInGame(screenX, screenY, automataCamera);
        if (checkInside(posInGame, automataCamera)) {
            if (selectedState != null) {
                selectedState.setX(posInGame.x);
                selectedState.setY(posInGame.y);
            }
        }

        //TODO drag on level
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 posInGame = getPosInGame(screenX, screenY, automataCamera);
        if (checkInside(posInGame, automataCamera)) {
            selectedState = null;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (automataController.getCurrentAutomaton().getCurrentState() != null) {
            if (keycode == Input.Keys.FORWARD_DEL) {
                removeSelectedState();
            }
        }
        if (keycode == Input.Keys.ESCAPE) {
            backToMenu();
        }
        return true;
    }

    private void backToMenu() {
        ScreenTransition transition = ScreenTransitionSlide.init(0.75f,
                ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);
        game.setScreen(new MenuScreen(game), transition);
    }

    private void removeSelectedState() {
        automataController.getCurrentAutomaton().removeState(automataController.getCurrentAutomaton().getCurrentState());
    }

    private void addTransition(String label) {
        AutomatonState currentState = automataController.getCurrentAutomaton().getCurrentState();
        Vector3 posInGame = getPosInGame(Gdx.input.getX(), Gdx.input.getY(), automataCamera);
        AutomatonState closestState = automataController.getCurrentAutomaton().getClosestState(posInGame.x, posInGame.y);

        if (currentState.equals(closestState)) {
            return;
        }
        float distance = DrawableAutomaton.pointDistance(closestState, posInGame.x, posInGame.y);

        if (distance <= Constants.STATE_SIZE / 2) {
            currentState.addTransition(label, closestState);
        }
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

    public Vector3 getPosInGame(int x, int y, Camera camera) {
//        Gdx.app.debug(TAG, "input pos (" + x + " " + y + ")");
        if (camera.equals(automataCamera)) {
            x *= 2;
        } else if (camera.equals(levelCamera)) {
            x -= Gdx.graphics.getWidth() / 2;
            x *= 2;
        }
        Vector3 result = camera.unproject(new Vector3(x, y, 0));
//        Gdx.app.debug(TAG, "output pos (" + result.x + " " + result.y + ") with " + x);
        return result;
    }

    private Pixmap createStatePixmap(int size, Color fillColor, Color borderColor) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);

        pixmap.setColor(fillColor);
        pixmap.fillCircle(size / 2, size / 2, size / 2);

        pixmap.setColor(borderColor);
        pixmap.drawCircle(size / 2, size / 2, size / 2);
        return pixmap;
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
