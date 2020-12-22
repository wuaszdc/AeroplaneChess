package model;

public class Square {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private final int x;
    private final int y;

    public int getDirection() {
        return direction;
    }

    private final int direction;


    public Square(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}