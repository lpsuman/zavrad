package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;

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
        return ((I18NBundle)Assets.getInstance().getAssetManager().get(Constants.BUNDLE)).get(value);
    }
}