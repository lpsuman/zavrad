package hr.fer.lukasuman.game.level;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;

public enum Direction {
    NORTH(0, 1, 0, "north"),
    EAST(1, 0, 1, "east"),
    SOUTH(0, -1, 2, "south"),
    WEST(-1, 0, 3, "west");

    private int xIncrement;
    private int yIncrement;
    private int degrees;
    private String name;

    Direction(int xIncrement, int yIncrement, int degrees, String name) {
        this.xIncrement = xIncrement;
        this.yIncrement = yIncrement;
        this.degrees = degrees;
        this.name = name;
    }

    public GridPoint2 incrementPosition(GridPoint2 oldPos) {
        return new GridPoint2(oldPos.x + xIncrement, oldPos.y + yIncrement);
    }

    public Direction changeDirection(int rotateClockwise) {
        int index = (this.ordinal() + rotateClockwise) % 4;
        if (index < 0) index += 4;
        return Direction.values()[index];
    }

    public int getDegrees() {
        return degrees;
    }

    public static Direction getByIndex(int index) {
        switch (index) {
            case 0:
                return NORTH;
            case 1:
                return EAST;
            case 2:
                return SOUTH;
            case 3:
                return WEST;
            default:
                return null;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ((I18NBundle)Assets.getInstance().getAssetManager().get(Constants.BUNDLE)).get(name);
    }
}
