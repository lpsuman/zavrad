package hr.fer.lukasuman.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import hr.fer.lukasuman.game.automata.AutomataController;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.LevelController;
import hr.fer.lukasuman.game.screens.MenuScreen;

import java.util.HashMap;
import java.util.Map;

public class GameController extends InputAdapter {
    private static final String TAG = GameController.class.getName();

    private OrthographicCamera camera;
    private Game game;
    public AutomataController automataController;
    public Map<AutomatonState, Sprite> stateSprites;
    public LevelController levelController;
    public int score;
    private Texture stateTexture;

    private int stateID = 1;

    public GameController(Game game) {
        this.game = game;
        init();
    }

    private void init() {
        automataController = new AutomataController();
        stateSprites = new HashMap<AutomatonState, Sprite>();
        initLevel();
        initStateObjects();
    }

    private void initLevel() {
        score = 0;
        levelController = new LevelController();
    }

    private void initStateObjects() {
        loadStateTexture();
        for (AutomatonState state : automataController.getStates()) {
            addSpriteForState(state, stateTexture);
        }
    }

    private void loadStateTexture() {
        try {
            stateTexture = new Texture(Constants.AUTOMATA_STATE_TEXTURE);
        } catch (Exception exc) {
            exc.printStackTrace();
            Pixmap pixmap = createStatePixmap(Constants.STATE_SPRITE_SIZE,
                    Constants.STATE_TEXTURE_FILL_COLOR, Constants.STATE_TEXTURE_BORDER_COLOR);
            stateTexture = new Texture(pixmap);
        }

    }

    private void addSpriteForState(AutomatonState state, Texture texture) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(Constants.STATE_SIZE, Constants.STATE_SIZE);
        float halfWidth = sprite.getWidth() / 2.0f;
        float halfHeight = sprite.getHeight() / 2.0f;
        sprite.setOrigin(halfWidth, halfHeight);
        stateSprites.put(state, sprite);
    }

    public void update(float deltaTime) {
        updateStateObjects();
    }

    private void updateStateObjects() {
        for (Map.Entry<AutomatonState, Sprite> entry : stateSprites.entrySet()) {
            AutomatonState state = entry.getKey();
            Sprite sprite = entry.getValue();
            sprite.setPosition(state.getX() - sprite.getWidth() / 2.0f, state.getY() - sprite.getHeight() / 2.0f);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 posInGame = getPosInGame(screenX, screenY);

        AutomatonState closestState = automataController.getClosestState(posInGame.x, posInGame.y);
        float distance = AutomataController.pointDistance(closestState, posInGame.x, posInGame.y);

        if (closestState != null) {
            if (button == Input.Buttons.LEFT) {
                if (distance <= Constants.STATE_SIZE / 2) {
                    automataController.setCurrentState(closestState);
                } else {
                    automataController.setCurrentState(null);
                }
            }
        }

        if(button == Input.Buttons.RIGHT){
            if (distance > Constants.STATE_SIZE) {
                AutomatonState newState = new AutomatonState(posInGame.x, posInGame.y,
                        Constants.DEFAULT_STATE_LABEL + (stateID++), automataController.getCurrentAutomaton());
                automataController.addState(newState);
                addSpriteForState(newState, stateTexture);

            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 posInGame = getPosInGame(screenX, screenY);

        AutomatonState closestState = automataController.getClosestState(posInGame.x, posInGame.y);
        float distance = AutomataController.pointDistance(closestState, posInGame.x, posInGame.y);

        if (distance <= Constants.STATE_SIZE / 2) {
            closestState.setX(posInGame.x);
            closestState.setY(posInGame.y);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (automataController.getCurrentState() != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL)) {
                removeSelectedState();
            } else if (Gdx.input.isKeyPressed(Constants.EMPTY_KEY)) {
                addTransition(Constants.EMPTY_LABEL);
            } else if (Gdx.input.isKeyPressed(Constants.WALL_KEY)) {
                addTransition(Constants.WALL_LABEL);
            } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                game.setScreen(new MenuScreen(game));
            }
        }
        return true;
    }

    private void removeSelectedState() {
        automataController.removeState(automataController.getCurrentState());
    }

    private void addTransition(String label) {
        AutomatonState currentState = automataController.getCurrentState();
        Vector3 posInGame = getPosInGame(Gdx.input.getX(), Gdx.input.getY());
        AutomatonState closestState = automataController.getClosestState(posInGame.x, posInGame.y);

        if (currentState.equals(closestState)) {
            return;
        }
        float distance = AutomataController.pointDistance(closestState, posInGame.x, posInGame.y);

        if (distance <= Constants.STATE_SIZE / 2) {
            currentState.addTransition(label, closestState);
        }
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public Vector3 getPosInGame(int x, int y) {
        return camera.unproject(new Vector3(x, y, 0));
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
}
