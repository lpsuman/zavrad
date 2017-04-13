package hr.fer.lukasuman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import hr.fer.lukasuman.automata.Automata;
import hr.fer.lukasuman.automata.AutomataState;
import hr.fer.lukasuman.automata.TransitionInput;

import java.util.HashMap;
import java.util.Map;

public class GameController extends InputAdapter {
    private static final String TAG = GameController.class.getName();

    private OrthographicCamera camera;

    public Automata automata;
    public Map<AutomataState, Sprite> stateSprites;
    private Texture stateTexture;
    private Texture selectedStateTexture;

    private int stateID = 1;

    public GameController() {
        init();
    }

    private void init() {
        automata = new Automata();
        stateSprites = new HashMap<AutomataState, Sprite>();
        initStateObjects();
    }

    private void initStateObjects() {
        Pixmap pixmap = createStatePixmap(Constants.STATE_SPRITE_SIZE,
                Constants.STATE_TEXTURE_FILL_COLOR, Constants.STATE_TEXTURE_BORDER_COLOR);
        stateTexture = new Texture(pixmap);
        pixmap = createStatePixmap(Constants.STATE_SPRITE_SIZE,
                Constants.SELECTED_STATE_FILL_COLOR, Constants.SELECTED_STATE_BORDER_COLOR);
        selectedStateTexture = new Texture(pixmap);

        for (AutomataState state : automata.getStates()) {
            addSpriteForState(state, stateTexture);
        }
    }

    private void addSpriteForState(AutomataState state, Texture texture) {
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
        for (Map.Entry<AutomataState, Sprite> entry : stateSprites.entrySet()) {
            AutomataState state = entry.getKey();
            Sprite sprite = entry.getValue();
            sprite.setPosition(state.getX() - sprite.getWidth() / 2.0f, state.getY() - sprite.getHeight() / 2.0f);
        }
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 posInGame = getPosInGame(screenX, screenY);

        AutomataState closestState = automata.getClosestState(posInGame.x, posInGame.y);
        float distance = Automata.pointDistance(closestState, posInGame.x, posInGame.y);

        if (closestState != null) {
            if (button == Input.Buttons.LEFT) {
                Sprite selectedSprite = stateSprites.get(automata.getCurrentState());
                if (selectedSprite != null) {
                    selectedSprite.setTexture(stateTexture);
                }
                if (distance <= Constants.STATE_SIZE / 2) {
                    automata.setCurrentState(closestState);
                    selectedSprite = stateSprites.get(automata.getCurrentState());
                    selectedSprite.setTexture(selectedStateTexture);
                } else {
                    automata.setCurrentState(null);
                }
            }
        }

        if(button == Input.Buttons.RIGHT){
            if (distance > Constants.STATE_SIZE) {
                AutomataState newState = new AutomataState(posInGame.x, posInGame.y, Constants.DEFAULT_STATE_LABEL + (stateID++));
                automata.addState(newState);
                addSpriteForState(newState, stateTexture);

            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 posInGame = getPosInGame(screenX, screenY);

        AutomataState closestState = automata.getClosestState(posInGame.x, posInGame.y);
        float distance = Automata.pointDistance(closestState, posInGame.x, posInGame.y);

        if (distance <= Constants.STATE_SIZE / 2) {
            closestState.setX(posInGame.x);
            closestState.setY(posInGame.y);
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (automata.getCurrentState() != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.FORWARD_DEL)) {
                removeSelectedState();
            }
            if (Gdx.input.isKeyPressed(Constants.EMPTY_KEY)) {
                addTransition(Constants.EMPTY_LABEL);
            }
            if (Gdx.input.isKeyPressed(Constants.WALL_KEY)) {
                addTransition(Constants.WALL_LABEL);
            }
        }
        return true;
    }

    private void removeSelectedState() {
        AutomataState selectedState = automata.getCurrentState();
        if (selectedState != null) {
            stateSprites.remove(selectedState);
            automata.removeState(selectedState);
        }
    }

    private void addTransition(String label) {
        AutomataState currentState = automata.getCurrentState();
        Vector3 posInGame = getPosInGame(Gdx.input.getX(), Gdx.input.getY());
        AutomataState closestState = automata.getClosestState(posInGame.x, posInGame.y);

        if (currentState.equals(closestState)) {
            return;
        }
        float distance = Automata.pointDistance(closestState, posInGame.x, posInGame.y);

        if (distance <= Constants.STATE_SIZE / 2) {
            currentState.addTransition(new TransitionInput(label), closestState);
        }
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public Vector3 getPosInGame(int x, int y) {
        return camera.unproject(new Vector3(x, y, 0));
    }
}
