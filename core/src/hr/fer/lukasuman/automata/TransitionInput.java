package hr.fer.lukasuman.automata;

public class TransitionInput {

    String inputKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransitionInput that = (TransitionInput) o;

        return inputKey.equals(that.inputKey);
    }

    @Override
    public int hashCode() {
        return inputKey.hashCode();
    }
}
