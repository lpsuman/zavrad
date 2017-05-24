package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;

import java.util.*;

public class DrawableAutomaton extends Automaton {

    private Texture stateTexture;
    private Map<AutomatonState, Sprite> stateSprites;
    private AutomatonState currentState;
    private int stateID = 1;
    private Set<AutomatonTransition> transitionSet;

    private boolean changesPending;

    public DrawableAutomaton(String name) {
        this(Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_STATE_TEXTURE), name);
    }

    public DrawableAutomaton(Texture stateTexture, String name) {
        super(name);
        this.stateTexture = stateTexture;
        stateSprites = new HashMap<>();
        transitionSet = new HashSet<>();
        reset();
    }

    public DrawableAutomaton(Automaton automaton) {
        super(automaton);
        this.stateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_STATE_TEXTURE);
        stateSprites = new HashMap<>();
        for (AutomatonState state : getStates()) {
            addSpriteForState(state);
        }
        currentState = getStartState();
        transitionSet = new HashSet<>();
        for (AutomatonState startState : getStates()) {
            for (Map.Entry<String, AutomatonState> entry : startState.getTransitions().entrySet()) {
                addTransition(entry.getKey(), startState, entry.getValue());
            }
        }
        reset();
    }

    public AutomatonTransition addTransition(String trigger, AutomatonState startState, AutomatonState endState) {
        if (startState.equals(endState)) {
            return null;
        }
        AutomatonTransition newTransition = new AutomatonTransition(trigger, startState, endState);
        if (transitionSet.contains(newTransition)) {
            return null;
        }
        startState.addTransition(trigger, endState);
        transitionSet.add(newTransition);
        changesPending = true;
        return newTransition;
    }

    public void removeTransition(AutomatonTransition transition) {
        if (transitionSet.remove(transition)) {
            for (String label : transition.getTransitionLabels()) {
                transition.getStartState().getTransitions().remove(label);
            }
            changesPending = true;
        }
    }

    public void removeTransition(AutomatonState state, String label) {
        Iterator<AutomatonTransition> it = transitionSet.iterator();
        while (it.hasNext()) {
            AutomatonTransition transition = it.next();
            if (transition.getStartState().equals(state) && transition.getTransitionLabels().contains(label)) {
                transition.removeLabel(label);
                if (transition.getTransitionLabels().isEmpty()) {
                    transitionSet.remove(transition);
                }
                return;
            }
        }
    }

    public AutomatonTransition getClosestTransition(Vector2 position, float maxDistance) {
        float minDistance = Float.MAX_VALUE;
        AutomatonTransition closestTransition = null;

        for (AutomatonTransition transition : transitionSet) {
            float distance = pointDistance(position, transition.getMiddlePoint());
            if (distance < minDistance) {
                minDistance = distance;
                closestTransition = transition;
            }
        }
        if (minDistance > maxDistance) {
            return null;
        } else {
            return closestTransition;
        }
    }

    public AutomatonTransition getTransition(AutomatonState startState, AutomatonState endState) {
        for (AutomatonTransition transition : transitionSet) {
            if (transition.getStartState().equals(startState) && transition.getEndState().equals(endState)) {
                return transition;
            }
        }
        return null;
    }

    public void recalculateTransitions() {
        for (AutomatonTransition transition : transitionSet) {
            transition.recalculate();
        }
    }

    public void drawTransitions(ShapeRenderer transitionRenderer) {
        for (AutomatonTransition transition : transitionSet) {
            transition.drawLines(transitionRenderer);
        }
    }

    private void addSpriteForState(AutomatonState state) {
        Sprite sprite = new Sprite(stateTexture);
        sprite.setSize(Constants.STATE_SIZE, Constants.STATE_SIZE);
        float halfWidth = sprite.getWidth() / 2.0f;
        float halfHeight = sprite.getHeight() / 2.0f;
        sprite.setOrigin(halfWidth, halfHeight);
        stateSprites.put(state, sprite);
    }

    public AutomatonState createState(float posX, float posY, AutomatonAction action) {
        AutomatonState newState = new AutomatonState(Constants.DEFAULT_STATE_LABEL + (stateID++), posX, posY, this);
        newState.setAction(action);
        addState(newState);
        return newState;
    }

    @Override
    public void addState(AutomatonState state) {
        super.addState(state);
        addSpriteForState(state);
        changesPending = true;
    }

    @Override
    public void removeState(AutomatonState state) {
        if (state == null || !getStates().contains(state)) {
            return;
        }
        Iterator<AutomatonTransition> iterator = transitionSet.iterator();
        while (iterator.hasNext()) {
            AutomatonTransition transition = iterator.next();
            if (transition.getStartState().equals(state) || transition.getEndState().equals(state)) {
                iterator.remove();
            }
        }
        if (state.equals(currentState)) {
            currentState = null;
        }
        super.removeState(state);
        stateSprites.remove(state);
        changesPending = true;
    }

    public Automaton getSerializable() {
        return new Automaton(this);
    }

    public void reset() {
        currentState = getStartState();
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

    public static float pointDistance(Vector2 pos1, Vector2 pos2) {
        return pointDistance(pos1.x, pos1.y, pos2.x, pos2.y);
    }

    public static float pointDistance(AutomatonState state, float x, float y) {
        if (state == null) {
            return Float.MAX_VALUE;
        }
        return pointDistance(state.getX(), state.getY(), x, y);
    }

    public Set<AutomatonTransition> getTransitionSet() {
        return transitionSet;
    }

    public boolean isChangesPending() {
        return changesPending;
    }

    public void setChangesPending(boolean changesPending) {
        this.changesPending = changesPending;
    }
}
