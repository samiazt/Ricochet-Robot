package com.example.ricochet_robot;

import java.util.ArrayList;
import java.util.Arrays;

public class AI {
    public static final int AI_MAX_GENERATIONS = 8;

    private Game game;

    private ArrayList<RobotsPosition> robotsPositions;
    private Objective objective;

    public AI(Game game) {
        this.game = game;
    }

    // Renvoie le nombre de déplacements si une solution est trouvée, sinon renvoie -1
    public int solveRound() {
        System.out.println("IA lancee");
        // Création d'une liste de positions de robots au cours de la partie
        robotsPositions = new ArrayList<>();

        // On ajoute la position de départ depuis laquelle on va explorer de nouvelles positions
        RobotsPosition startPosition = new RobotsPosition(game.getOldRobots(), 0, -1, 0);
        robotsPositions.add(startPosition);

        // On récupère l'objectif
        objective = game.getObjective();

        int generations = 0, id = 0;
        // On arrête si generations == AI_MAX_GENERATIONS car les opérations deviennent trop longues
        while (generations < AI_MAX_GENERATIONS) {
            if (generations > 0) {
                System.out.println("Generation " + generations);
            }
            // On ajoute les nouvelles positions dans une autre liste pour éviter de modifier la liste que l'on parcourt
            ArrayList<RobotsPosition> generationRobotsPositions = new ArrayList<>();

            for (RobotsPosition robotsPosition : robotsPositions) {
                if (robotsPosition.getGeneration() == generations) {
                    Robot[] robots = robotsPosition.getRobots();
                    // Pour chaque robot, pour chacune des directions, on fait un déplacement
                    for (int robotId = 0; robotId < Game.ROBOT_NUMBER; robotId++) {
                        for (int direction = 0; direction < 4; direction++) {
                            if (canRobotMove(robots, robotId, direction)) {
                                // On crée une copie des positions des robots pour ne pas modifier l'original
                                Robot[] robotsMoved = {
                                        robots[0].clone(), robots[1].clone(), robots[2].clone(), robots[3].clone()
                                };
                                // Déplacement des robots
                                robotMove(robotsMoved, robotId, direction);

                                RobotsPosition newPosition = new RobotsPosition(robotsMoved, id + 1, robotsPosition.getId(), generations + 1);
                                // On vérifie que l'on n'ait pas déjà ces positions de robots
                                if (!robotsPositions.contains(newPosition) && !generationRobotsPositions.contains(newPosition)) {
                                    generationRobotsPositions.add(newPosition);
                                    id++;

                                    // On vérifie si l'objectif est trouvé
                                    if (objectiveFound(robotsMoved)) {
                                        System.out.println("L'IA a trouvé l'objectif en " + (generations + 1) + " mouvements !");
                                        robotsPositions.addAll(generationRobotsPositions);
                                        printSolution();
                                        return generations + 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            robotsPositions.addAll(generationRobotsPositions);
            generations++;
        }

        return -1;
    }

    // Affiche les mouvements faits par l'IA pour atteindre l'objectif
    private void printSolution() {
        int id = robotsPositions.size() - 1;
        ArrayList<RobotsPosition> robotsPositionsUsed = new ArrayList<>();
        // On récupère les robotsPositions utilisés et on les stocke dans une liste
        while (id >= 0) {
            RobotsPosition robotsPosition = robotsPositions.get(id);
            robotsPositionsUsed.add(robotsPosition);
            id = robotsPosition.getLastMoveId();
        }

        // On compare chaque étape pour retrouver quel robot a bougé et dans quelle direction
        System.out.println("Deplacements trouves par l'IA:");
        for (int i = robotsPositionsUsed.size() - 2; i >= 0; i--) {
            Robot[] currentRobots = robotsPositionsUsed.get(i).getRobots();
            Robot[] lastRobots = robotsPositionsUsed.get(i + 1).getRobots();
            for (int robotId = 0; robotId < 4; robotId++) {
                if (currentRobots[robotId].getX() != lastRobots[robotId].getX() || currentRobots[robotId].getY() != lastRobots[robotId].getY()) {
                    System.out.print("Robot " + colorIdToColor(robotId));

                    if (currentRobots[robotId].getY() > lastRobots[robotId].getY()) {
                        System.out.println(" vers le bas");
                    } else if (currentRobots[robotId].getY() < lastRobots[robotId].getY()) {
                        System.out.println(" vers le haut");
                    } else if (currentRobots[robotId].getX() > lastRobots[robotId].getX()) {
                        System.out.println(" vers la droite");
                    } else if (currentRobots[robotId].getX() < lastRobots[robotId].getX()) {
                        System.out.println(" vers la gauche");
                    }
                    break;
                }
            }
        }

    }

    // Donne le nom de l'id d'une couleur
    private String colorIdToColor(int colorId) {
        switch (colorId) {
            case 0:
                return "rouge";
            case 1:
                return "bleu";
            case 2:
                return "jaune";
            case 3:
                return "vert";
        }
        return "";
    }

    // --- Déplacements pour IA --
    // Les méthodes de déplacements de game sont légèrement modifiées ci-dessous pour accepter les positions des robots données

    // Fait un déplacement complet pour le robot sélectionné dans la direction donnée
    private void robotMove(Robot[] robots, int robotId, int direction) {
        while (canRobotMove(robots, robotId, direction)) {
            switch (direction) {
                case 0:  // Haut
                    robots[robotId].addY(-1);
                    break;
                case 1:  // Droite
                    robots[robotId].addX(1);
                    break;
                case 2:  // Bas
                    robots[robotId].addY(1);
                    break;
                case 3:  // Gauche
                    robots[robotId].addX(-1);
                    break;
            }
        }
        return;
    }

    // Renvoie true si le robot peut bouger dans la direction donnée
    private boolean canRobotMove(Robot[] robots, int robotId, int direction) {
        int x = robots[robotId].getX();
        int y = robots[robotId].getY();

        switch (direction) {
            case 0:  // Haut
                if (isTileFree(x, y - 1, robotId, robots) && !game.isWall(x, y - 1, false)) {
                    return true;
                }
                break;
            case 1:  // Droite
                if (isTileFree(x + 1, y, robotId, robots) && !game.isWall(x, y, true)) {
                    return true;
                }
                break;
            case 2:  // Bas
                if (isTileFree(x, y + 1, robotId, robots) && !game.isWall(x, y, false)) {
                    return true;
                }
                break;
            case 3:  // Gauche
                if (isTileFree(x - 1, y, robotId, robots) && !game.isWall(x - 1, y, true)) {
                    return true;
                }
                break;
        }

        // Mouvement impossible
        return false;
    }

    // Renvoie true s'il est possible de se déplacer suivant les coordonnées données
    private boolean isTileFree(int x, int y, int robotId, Robot[] robots) {
        // Cases bloquées au milieu du jeu
        if ((x == 7 || x == 8) && (y == 7 || y == 8)) {
            return false;
        }

        // Limites du jeu
        if (x < 0 || x >= Game.BOARD_SIZE || y < 0 || y >= Game.BOARD_SIZE) {
            return false;
        }

        // Robots qui bloquent
        for (int i = 0; i < Game.ROBOT_NUMBER; i++) {
            Robot robot = robots[i];
            if (i != robotId && x == robot.getX() && y == robot.getY()) {
                return false;
            }
        }

        return true;
    }

    private boolean objectiveFound(Robot[] robots) {
        for (Robot robot : robots) {
            if (robot.getColor() == objective.getColor() && robot.getX() == objective.getX() && robot.getY() == objective.getY()) {
                return true;
            }
        }
        return false;
    }
}
