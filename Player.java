package model;

public class Player implements Comparable<Player>{
    private int eatNumber = 0;
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public int getEatNumber() {
        return eatNumber;
    }

    public void setEatNumber(int eatNumber) {
        this.eatNumber = eatNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Player o) {
        return o.eatNumber - this.eatNumber;
    }

}
