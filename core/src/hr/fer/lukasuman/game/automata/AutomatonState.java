package hr.fer.lukasuman.game.automata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomatonState implements Serializable {

    private float x;
    private float y;
    private String label;
    private Automaton parent;

    private Map<String, AutomatonState> transitions;
    private Set<AutomatonState> incomingStates;

    public AutomatonState(float x, float y, String label, Automaton parent) {
        this.x = x;
        this.y = y;
        this.label = label;
        this.parent = parent;
        transitions = new HashMap<String, AutomatonState>();
        incomingStates = new HashSet<AutomatonState>();
    }

    public void addTransition(String input, AutomatonState newState) {
        transitions.put(input, newState);
        newState.addIncomingState(this);
    }

    public AutomatonState transition(String input) {
        AutomatonState nextState = transitions.get(input);
        if (nextState == null) {
            nextState = this;
        }
        return nextState;
    }

    public void addIncomingState(AutomatonState state) {
        incomingStates.add(state);
    }

    public void removeTransition(AutomatonState state) {
        while (transitions.values().remove(state));
    }

    public void removeIncomingTransitions() {
        for (AutomatonState state : incomingStates) {
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

    public Automaton getParent() {
        return parent;
    }

    public Map<String, AutomatonState> getTransitions() {
        return transitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatonState that = (AutomatonState) o;

        if (!label.equals(that.label)) return false;
        return parent.equals(that.parent);
    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + parent.hashCode();
        return result;
    }
}
