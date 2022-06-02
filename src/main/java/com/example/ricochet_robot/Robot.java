package com.example.ricochet_robot;

public class Robot {
    private int x, y;
    private int color;  // couleur de 0 à 3, définie dans Game

    public Robot(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void addX(int value) {
        x += value;
    }

    public void addY(int value) {
        y += value;
    }

    @Override
    public Robot clone() {
        return new Robot(x, y, color);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Robot) {
            Robot robot = (Robot) obj;
            if (x == robot.getX() && y == robot.getY() && color == robot.getColor()) {
                return true;
            }
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }
}
