package hr.fer.lukasuman.game.level.blocks;

import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;

import java.util.Arrays;
import java.util.Optional;

public enum BlockLabel {
    EMPTY(LocalizationKeys.EMPTY),
    WALL(LocalizationKeys.WALL),
    START(LocalizationKeys.START),
    GOAL(LocalizationKeys.GOAL),
    REST(LocalizationKeys.REST);

    private String label;

    BlockLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BlockLabel getByLabel(String label) {
        Optional<BlockLabel> car = Arrays.stream(BlockLabel.values())
                .filter(c -> c.label.equals(label))
                .findFirst();
        if (car.isPresent()) {
            return car.get();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return ((I18NBundle) Assets.getInstance().getAssetManager().get(Constants.BUNDLE)).get(label);
    }
}
