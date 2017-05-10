package hr.fer.lukasuman.game.automata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Automaton implements Serializable {

    private List<AutomatonState> states;
    private AutomatonState startState;
    private String name;

    public Automaton() {
        states = new ArrayList<AutomatonState>();
    }

    public List<AutomatonState> getStates() {
        return states;
    }

    public void addState(AutomatonState state) {
        if (states.isEmpty()) {
            startState = state;
        }
        states.add(state);
    }

    public void removeState(AutomatonState state) {
        state.removeIncomingTransitions();
        states.remove(state);
    }

    public AutomatonState getStartState() {
        return startState;
    }

    public void setStartState(AutomatonState state) {
        startState = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
