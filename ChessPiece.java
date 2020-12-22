package model;

import javax.swing.*;


public class ChessPiece extends JLabel {
    public int getPlayer() {
        return player;
    }

    public int getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private final int player;
    private int color;
    private int index;

    public int getInitialColor() {
        return initialColor;
    }

    public int getInitialIndex() {
        return initialIndex;
    }

    private final int initialColor;
    private final int initialIndex;

    public ChessPiece(int player, String address,int initialColor,int initialIndex) {
        setIcon(new ImageIcon(address));
        this.player = player;
        this.initialColor = initialColor;
        this.initialIndex = initialIndex;

    }

    public boolean checkEndPoints(int index) {
        return index == 23;
    }
}