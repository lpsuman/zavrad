package hr.fer.lukasuman.game.automata;

import hr.fer.lukasuman.game.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomatonState implements Serializable {

    private String label;
    private float x;
    private float y;
    private AutomatonAction action;
    private Automaton parent;

    private Map<String, AutomatonState> transitions;
    private Set<AutomatonState> incomingStates;

    public AutomatonState(String label, float x, float y, Automaton parent) {
        this(label, x, y, Constants.DEFAULT_ACTION, parent);
    }

    public AutomatonState(String label, float x, float y, AutomatonAction action, Automaton parent) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.action = action;
        this.parent = parent;
        transitions = new HashMap<>();
        incomingStates = new HashSet<>();
    }

    public AutomatonState(AutomatonState other) {
        this.label = other.label;
        this.x = other.x;
        this.y = other.y;
        this.action = other.action;
        this.parent = other.parent;
        transitions = new HashMap<>(other.transitions);
        incomingStates = new HashSet<>(other.incomingStates);
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

    public void update(Automaton newParent, Map<AutomatonState, AutomatonState> parentMap) {
        parent = newParent;
        transitions.replaceAll((k, v) -> parentMap.get(v));
        Set<AutomatonState> newIncomingStates = new HashSet<>();
        for (AutomatonState oldState : incomingStates) {
            newIncomingStates.add(parentMap.get(oldState));
        }
        incomingStates = newIncomingStates;
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

    public AutomatonAction getAction() {
        return action;
    }

    public void setAction(AutomatonAction action) {
        this.action = action;
    }

    public Automaton getParent() {
        return parent;
    }

    public void setParent(Automaton parent) {
        this.parent = parent;
    }

    public Map<String, AutomatonState> getTransitions() {
        return transitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatonState that = (AutomatonState) o;

        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        return "AutomatonState{" + label + " " + x + " " + y + "}";
    }
}
