package hr.fer.lukasuman.game.automata;

public enum AutomatonAction {
    MOVE_FORWARD("F"),
    ROTATE_LEFT("L"),
    ROTATE_RIGHT("R");

    //TODO maybe add B(backward) action to turn 180 degrees

    private String value;

    AutomatonAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}