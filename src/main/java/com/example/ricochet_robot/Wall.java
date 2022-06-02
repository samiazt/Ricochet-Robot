package com.example.ricochet_robot;

import java.util.ArrayList;

public class Wall {
    private int x, y;
    private boolean vertical;

    public Wall(int x, int y, boolean vertical) {
        this.x = x;
        this.y = y;
        this.vertical = vertical;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isVertical() {
        return vertical;
    }

    public static ArrayList<Wall> createWalls() {
        ArrayList<Wall> walls = new ArrayList<>();
        walls.add(new Wall(1, 0, true));
        walls.add(new Wall(11, 0, true));
        walls.add(new Wall(4, 0, false));
        walls.add(new Wall(1, 1, false));
        walls.add(new Wall(3, 1, true));
        walls.add(new Wall(1, 2, true));
        walls.add(new Wall(11, 2, true));
        walls.add(new Wall(11, 2, false));
        walls.add(new Wall(13, 2, false));
        walls.add(new Wall(6, 3, true));
        walls.add(new Wall(6, 3, false));
        walls.add(new Wall(13, 3, true));
        walls.add(new Wall(9, 4, true));
        walls.add(new Wall(10, 4, false));
        walls.add(new Wall(12, 4, false));
        walls.add(new Wall(0, 5, false));
        walls.add(new Wall(11, 5, true));
        walls.add(new Wall(15, 5, false));
        walls.add(new Wall(2, 6, true));
        walls.add(new Wall(3, 6, false));
        walls.add(new Wall(2, 7, false));
        walls.add(new Wall(2, 8, true));
        walls.add(new Wall(4, 9, true));
        walls.add(new Wall(5, 9, false));
        walls.add(new Wall(15, 9, false));
        walls.add(new Wall(1, 10, true));
        walls.add(new Wall(1, 10, false));
        walls.add(new Wall(8, 10, true));
        walls.add(new Wall(8, 10, false));
        walls.add(new Wall(13, 10, false));
        walls.add(new Wall(4, 11, false));
        walls.add(new Wall(12, 11, true));
        walls.add(new Wall(0, 12, false));
        walls.add(new Wall(4, 12, true));
        walls.add(new Wall(6, 13, false));
        walls.add(new Wall(8, 13, true));
        walls.add(new Wall(9, 13, false));
        walls.add(new Wall(14, 13, false));
        walls.add(new Wall(5, 14, true));
        walls.add(new Wall(14, 14, true));
        walls.add(new Wall(3, 15, true));
        walls.add(new Wall(11, 15, true));
        return walls;
    }
}
