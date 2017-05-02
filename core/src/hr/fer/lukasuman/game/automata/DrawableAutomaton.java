package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import hr.fer.lukasuman.game.Constants;

import java.util.HashMap;
import java.util.Map;

public class DrawableAutomaton extends Automaton {

    private Texture stateTexture;
    private Map<AutomatonState, Sprite> stateSprites;
    private AutomatonState currentState;
    private int stateID = 1;

    public DrawableAutomaton(Texture stateTexture) {
        super();
        this.stateTexture = stateTexture;
        stateSprites = new HashMap<AutomatonState, Sprite>();
        for (AutomatonState state : getStates()) {
            addSpriteForState(state);
        }
        currentState = getStartState();
    }

    private void addSpriteForState(AutomatonState state) {
        Sprite sprite = new Sprite(stateTexture);
        sprite.setSize(Constants.STATE_SIZE, Constants.STATE_SIZE);
        float halfWidth = sprite.getWidth() / 2.0f;
        float halfHeight = sprite.getHeight() / 2.0f;
        sprite.setOrigin(halfWidth, halfHeight);
        stateSprites.put(state, sprite);
    }

    public void createState(float posX, float posY) {
        AutomatonState newState = new AutomatonState(posX, posY,
                Constants.DEFAULT_STATE_LABEL + (stateID++), this);
        addState(newState);
    }

    @Override
    public void addState(AutomatonState state) {
        super.addState(state);
        addSpriteForState(state);
    }

    @Override
    public void removeState(AutomatonState state) {
        super.removeState(state);
        stateSprites.remove(state);
    }

    public Map<AutomatonState, Sprite> getStateSprites() {
        return stateSprites;
    }

    public AutomatonState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(AutomatonState currentState) {
        this.currentState = currentState;
    }

    public void setCurrentState(float x, float y) {
        AutomatonState closestState = getClosestState(x, y);
        if (pointDistance(x, y, closestState.getX(), closestState.getY()) <= Constants.STATE_SIZE) {
            currentState = closestState;
        }
    }

    public AutomatonState getClosestState(float x, float y) {
        float minDistance = Float.MAX_VALUE;
        AutomatonState closestState = null;

        for (AutomatonState state : getStates()) {
            float distance = pointDistance(x, y, state.getX(), state.getY());
            if (distance < minDistance) {
                minDistance = distance;
                closestState = state;
            }
        }
        return closestState;
    }

    public static float pointDistance(float x1, float y1, float x2, float y2) {
        float dx = Math.abs(x1 - x2);
        float dy = Math.abs(y1 - y2);
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float pointDistance(AutomatonState state, float x, float y) {
        if (state == null) {
            return Float.MAX_VALUE;
        }
        return pointDistance(state.getX(), state.getY(), x, y);
    }
}
