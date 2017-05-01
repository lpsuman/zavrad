package hr.fer.lukasuman.game.automata;

import hr.fer.lukasuman.game.Constants;

import java.util.ArrayList;
import java.util.List;

public class AutomataController {

    private List<Automaton> automata;
    private Automaton currentAutomaton;
    private AutomatonState currentState;

    public AutomataController() {
        init();
    }

    public void init() {
        automata = new ArrayList<Automaton>();
        currentAutomaton = new Automaton();
        currentState = currentAutomaton.getStartState();
    }

    public Automaton getCurrentAutomaton() {
        return currentAutomaton;
    }

    public List<AutomatonState> getStates() {
        return currentAutomaton.getStates();
    }

    public void addState(AutomatonState state) {
        if (state != null) {
            currentAutomaton.addState(state);
        }
    }

    public void removeState(AutomatonState state) {
        if (state != null) {
            state.removeIncomingTransitions();
            currentAutomaton.removeState(state);
        }
    }

    public AutomatonState getClosestState(float x, float y) {
        float minDistance = Float.MAX_VALUE;
        AutomatonState closestState = null;

        for (AutomatonState state : currentAutomaton.getStates()) {
            float distance = pointDistance(x, y, state.getX(), state.getY());
            if (distance < minDistance) {
                minDistance = distance;
                closestState = state;
            }
        }
        return closestState;
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
