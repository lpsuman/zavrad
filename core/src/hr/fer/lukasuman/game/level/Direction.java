package hr.fer.lukasuman.game.level;

public enum Direction {
    NORTH(0, 1, 0),
    EAST(1, 0, 1),
    SOUTH(0, -1, 2),
    WEST(-1, 0, 3);

    private int xIncrement;
    private int yIncrement;
    private int degrees;

    Direction(int xIncrement, int yIncrement, int degrees) {
        this.xIncrement = xIncrement;
        this.yIncrement = yIncrement;
        this.degrees = degrees;
    }

    public Position incrementPosition(Position oldPos) {
        return new Position(oldPos.x + xIncrement, oldPos.y + yIncrement);
    }

    public Direction changeDirection(int rotateClockwise) {
        int index = (this.ordinal() + rotateClockwise) % 4;
        if (index < 0) index += 4;
        return Direction.values()[index];
    }

    public int getDegrees() {
        return degrees;
    }
}
