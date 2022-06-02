package com.example.ricochet_robot;

import java.util.Arrays;

public class RobotsPosition {
    private Robot[] robots;
    private int id, lastMoveId, generation;

    public RobotsPosition(Robot[] robots, int id, int lastMoveId, int generation) {
        this.robots = robots;
        this.id = id;
        this.lastMoveId = lastMoveId;
        this.generation = generation;
    }

    // Permet de comparer deux objets RobotsPosition pour voir s'ils sont égaux
    @Override
    public boolean equals(Object o) {
        if (o instanceof RobotsPosition robotsPosition) {
            return Arrays.equals(robotsPosition.getRobots(), robots);
        }
        return false;
    }

    public Robot[] getRobots() {
        return robots;
    }

    public int getId() {
        return id;
    }

    public int getLastMoveId() {
        return lastMoveId;
    }

    public int getGeneration() {
        return generation;
    }
}
