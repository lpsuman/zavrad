package hr.fer.lukasuman.game.automata;

public class AutomatonTransition {

    private String label;
    private AutomatonState startState;
    private AutomatonState endState;

    public AutomatonTransition(String label, AutomatonState startState, AutomatonState endState) {
        this.label = label;
        this.startState = startState;
        this.endState = endState;
    }
}
