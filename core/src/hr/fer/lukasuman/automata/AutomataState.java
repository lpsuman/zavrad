package hr.fer.lukasuman.automata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomataState implements Serializable {

    private float x;
    private float y;
    private String label;

    private Map<TransitionInput, AutomataState> transitions;
    private Set<AutomataState> incomingStates;

    public AutomataState(float x, float y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
        transitions = new HashMap<TransitionInput, AutomataState>();
        incomingStates = new HashSet<AutomataState>();
    }

    public void addTransition(TransitionInput input, AutomataState newState) {
        transitions.put(input, newState);
        newState.addIncomingState(this);
    }

    public AutomataState transition(TransitionInput input) {
        AutomataState nextState = transitions.get(input);
        if (nextState == null) {
            nextState = this;
        }
        return nextState;
    }

    public void addIncomingState(AutomataState state) {
        incomingStates.add(state);
    }

    public void removeTransition(AutomataState state) {
        while (transitions.values().remove(state));
    }

    public void removeIncomingTransitions() {
        for (AutomataState state : incomingStates) {
            state.removeTransition(this);
        }
        incomingStates.clear();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
