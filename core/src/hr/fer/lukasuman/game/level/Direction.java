package hr.fer.lukasuman.game.level;

public enum Direction {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    private int xIncrement;
    private int yIncrement;

    Direction(int xIncrement, int yIncrement) {
        this.xIncrement = xIncrement;
        this.yIncrement = yIncrement;
    }

    public Position incrementPosition(Position oldPos) {
        return new Position(oldPos.x + xIncrement, oldPos.y + yIncrement);
    }

    public Direction changeDirection(int rotateClockwise) {
        int index = (this.ordinal() + rotateClockwise) % 4;
        if (index < 0) index += 4;
        return Direction.values()[index];
    }
}
