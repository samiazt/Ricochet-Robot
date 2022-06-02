package com.example.ricochet_robot;

import java.util.ArrayList;

public class Objective {
    private int x, y;
    private int color;

    public Objective(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public static ArrayList<Objective> createPossibleObjectives() {
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new Objective(4, 1, 0));
        objectives.add(new Objective(1, 2, 3));
        objectives.add(new Objective(11, 2, 0));
        objectives.add(new Objective(6, 3, 2));
        objectives.add(new Objective(13, 3, 2));
        objectives.add(new Objective(10, 4, 3));
        objectives.add(new Objective(12, 5, 1));
        objectives.add(new Objective(3, 6, 1));
        objectives.add(new Objective(5, 9, 3));
        objectives.add(new Objective(1, 10, 1));
        objectives.add(new Objective(8, 10, 2));
        objectives.add(new Objective(13, 11, 1));
        objectives.add(new Objective(4, 12, 0));
        objectives.add(new Objective(9, 13, 3));
        objectives.add(new Objective(6, 14, 2));
        objectives.add(new Objective(14, 14, 0));
        return objectives;
    }
}
