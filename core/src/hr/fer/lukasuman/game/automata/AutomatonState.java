package hr.fer.lukasuman.game.automata;

import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;

import java.io.Serializable;
import java.util.*;

public class AutomatonState implements Serializable {

    private String label;
    private float x;
    private float y;
    private AutomatonAction action;
    private Automaton parent;

    private boolean isValid;

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
            nextState = transitions.get(LocalizationKeys.REST);
            if (nextState == null) {
                nextState = this;
            }
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

    public void removeTransition(String trigger, AutomatonState state) {
        for(Iterator<Map.Entry<String, AutomatonState>> it = transitions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, AutomatonState> entry = it.next();
            if(entry.getKey().equals(trigger) && entry.getValue().equals(state)) {
                it.remove();
            }
        }
    }

    public void removeTransition(AutomatonState state) {
        for(Iterator<Map.Entry<String, AutomatonState>> it = transitions.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, AutomatonState> entry = it.next();
            if(entry.getValue().equals(state)) {
                it.remove();
            }
        }
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatonState that = (AutomatonState) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AutomatonState{" + label + " " + x + " " + y + "}";
    }
}
