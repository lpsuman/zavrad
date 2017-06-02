package hr.fer.lukasuman.game.automata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Automaton implements Serializable {

    protected List<AutomatonState> states;
    private AutomatonState startState;
    private String name;
    private int uniqueID;

    public Automaton(List<AutomatonState> states, AutomatonState startState, String name, int uniqueID) {
        this.states = states;
        this.startState = startState;
        this.name = name;
        this.uniqueID = uniqueID;
    }

    public Automaton(Automaton other) {
        Map<AutomatonState, AutomatonState> parentMap = new HashMap<>();
        this.states = new ArrayList<>();

        for (AutomatonState otherState : other.states) {
            System.out.println(otherState);

            AutomatonState newState = new AutomatonState(otherState);
            parentMap.put(otherState, newState);
            this.states.add(newState);
        }

        for (AutomatonState state : this.states) {
            state.update(this, parentMap);
        }

        if (other.startState != null) {
            this.startState = parentMap.get(other.startState);
        }
        name = other.name;
        this.uniqueID = other.uniqueID;
    }

    public Automaton(String name, int uniqueID) {
        this(new ArrayList<>(), null, name, uniqueID);
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
        if (startState.equals(state)) {
            if (states.isEmpty()) {
                startState = null;
            } else {
                startState = states.get(0);
            }
        }
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

    public int getNumberOfState() {
        return states.size();
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Automaton automaton = (Automaton) o;

        return uniqueID == automaton.uniqueID;
    }

    @Override
    public int hashCode() {
        return uniqueID;
    }
}
