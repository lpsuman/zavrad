package hr.fer.lukasuman.automata;

import hr.fer.lukasuman.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Automata implements Serializable {

    private List<AutomataState> states;
    private AutomataState currentState;

    public Automata() {
        states = new ArrayList<AutomataState>();
    }

    public void addState(AutomataState state) {
        states.add(state);
    }

    public List<AutomataState> getStates() {
        return states;
    }

    public AutomataState getCurrentState() {
        return currentState;
    }

    public AutomataState getClosestState(float x, float y) {
        float minDistance = Float.MAX_VALUE;
        AutomataState closestState = null;

        for (AutomataState state : states) {
            float distance = pointDistance(x, y, state.getX(), state.getY());
            if (distance < minDistance) {
                minDistance = distance;
                closestState = state;
            }
        }
        return closestState;
    }

    public void setCurrentState(float x, float y) {
        AutomataState closestState = getClosestState(x, y);
        if (pointDistance(x, y, closestState.getX(), closestState.getY()) <= Constants.STATE_SIZE) {
            currentState = closestState;
        }
    }

    public void setCurrentState(AutomataState state) {
        currentState = state;
    }

    public void removeState(AutomataState state) {
        state.removeIncomingTransitions();
        states.remove(state);
    }

    public static float pointDistance(float x1, float y1, float x2, float y2) {
        float dx = Math.abs(x1 - x2);
        float dy = Math.abs(y1 - y2);
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static float pointDistance(AutomataState state, float x, float y) {
        if (state == null) {
            return Float.MAX_VALUE;
        }
        return pointDistance(state.getX(), state.getY(), x, y);
    }
}
