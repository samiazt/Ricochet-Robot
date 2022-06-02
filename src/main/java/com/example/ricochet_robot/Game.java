package com.example.ricochet_robot;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    @FXML
    private GridPane gridPane;
    @FXML
    private Label infoLabel, timerLabel;
    @FXML
    private Label player1Label, player2Label, player3Label, player4Label;
    private Label[] playersLabel;
    @FXML
    private TextField player1TextField, player2TextField, player3TextField, player4TextField;
    private TextField[] playersTextFields;
    @FXML
    private Button aiButton, helpButton;

    private final static int TILE_SIZE = 45;  // Taille en pixels
    public final static int ROBOT_NUMBER = 4, PLAYER_NUMBER = 4, BOARD_SIZE = 16;

    private Robot[] robots, oldRobots;
    private ImageView[] robotsImageView;
    private Image[] robotsImage, robotsSelectedImage;
    private int robotSelectedId = 0;

    private int[] playersGuesses, playersScore = {0, 0, 0, 0};
    private int bestGuess = -1, bestGuessPlayer = -1, moves = 0;

    private ArrayList<Wall> walls;

    private ArrayList<Objective> possibleObjectives;
    private Objective objective;
    private ImageView objectiveImageView;

    private Timeline timeLine;
    private static final int TIMER_START_TIME = 30;  // Temps du compte à rebours : 30 sec
    private int timeLeft = TIMER_START_TIME;

    public final static String[] COLORS = new String[]{"red", "blue", "yellow", "green"};

    private GameState state;
    private AI ai;

    public Game() {
        // Création de murs
        walls = Wall.createWalls();

        // Création de la liste d'objectifs
        possibleObjectives = Objective.createPossibleObjectives();

        // Création des robots
        Random random = new Random();
        robots = new Robot[ROBOT_NUMBER];
        oldRobots = new Robot[ROBOT_NUMBER];
        playersGuesses = new int[ROBOT_NUMBER];
        for (int i = 0; i < ROBOT_NUMBER; i++) {
            int x = random.nextInt(BOARD_SIZE);
            int y = random.nextInt(BOARD_SIZE);
            // On recommence jusqu'à avoir des coordonnées libres
            while (!isTileFree(x, y, i)) {
                x = random.nextInt(BOARD_SIZE);
                y = random.nextInt(BOARD_SIZE);
            }
            robots[i] = new Robot(x, y, i);
            oldRobots[i] = new Robot(x, y, i);
            playersGuesses[i] = 0;
        }

        // On choisit un objectif
        chooseObjective();

        // TESTS
        robots[0] = new Robot(4, 5, 0);
        oldRobots[0] = new Robot(4, 5, 0);
        objective = possibleObjectives.get(12);

        ai = new AI(this);
        state = GameState.WAITING_FOR_GUESS;
    }

    // Crée un objectif aléatoirement
    private void chooseObjective() {
        Random random = new Random();
        // Choix aléatoire d'un objectif
        int randomObjectiveId = random.nextInt(possibleObjectives.size());
        int x = possibleObjectives.get(randomObjectiveId).getX();
        int y = possibleObjectives.get(randomObjectiveId).getY();
        // On recommence jusqu'à avoir un objectif libre
        while (!isTileFree(x, y, -1)) {  // On met -1 pour comparer avec les robots de toutes les couleurs
            randomObjectiveId = random.nextInt(possibleObjectives.size());
            x = possibleObjectives.get(randomObjectiveId).getX();
            y = possibleObjectives.get(randomObjectiveId).getY();
        }
        objective = possibleObjectives.get(randomObjectiveId);
    }

    @FXML
    public void initialize() {
        // Création des tuiles de la grille
        Image tileImage = new Image("tile.png", TILE_SIZE, TILE_SIZE, false, false);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                ImageView tileImageView = new ImageView(tileImage);
                gridPane.add(tileImageView, x, y);
            }
        }

        // Création des murs
        Image horizontalWallImage = new Image("horizontalWall.png", TILE_SIZE, TILE_SIZE, false, false);
        Image verticalWallImage = new Image("verticalWall.png", TILE_SIZE, TILE_SIZE, false, false);
        for (Wall wall : walls) {
            ImageView wallImageView;
            if (wall.isVertical()) {  // Vertical
                wallImageView = new ImageView(verticalWallImage);
            } else {  // Horizontal
                wallImageView = new ImageView(horizontalWallImage);
            }
            gridPane.add(wallImageView, wall.getX(), wall.getY());
        }

        // Ajout des cases du milieu
        Image blockedTileImage = new Image("blockedTile.png", TILE_SIZE, TILE_SIZE, false, true);
        for (int x = 7; x <= 8; x++) {
            for (int y = 7; y <= 8; y++) {
                ImageView blockedTileImageView = new ImageView(blockedTileImage);
                gridPane.add(blockedTileImageView, x, y);
            }
        }

        // Ajout des robots
        robotsImage = new Image[ROBOT_NUMBER];
        robotsSelectedImage = new Image[ROBOT_NUMBER];
        robotsImageView = new ImageView[ROBOT_NUMBER];
        for (int i = 0; i < ROBOT_NUMBER; i++) {
            robotsImage[i] = new Image(COLORS[i] + "Robot.png", TILE_SIZE, TILE_SIZE, false, false);
            robotsSelectedImage[i] = new Image(COLORS[i] + "RobotSelected.png", TILE_SIZE, TILE_SIZE, false, false);
            robotsImageView[i] = new ImageView(robotsImage[i]);
            gridPane.add(robotsImageView[i], robots[i].getX(), robots[i].getY());
        }
        // On met l'image de robot sélectionnée au robot sélectionné
        robotsImageView[robotSelectedId].setImage(robotsSelectedImage[robotSelectedId]);

        // Ajout dde l'objectif
        Image objectiveImage = new Image(COLORS[objective.getColor()] + "Objective.png", TILE_SIZE, TILE_SIZE, false, false);
        objectiveImageView = new ImageView(objectiveImage);
        gridPane.add(objectiveImageView, objective.getX(), objective.getY());

        gridPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (state == GameState.PLAYER_MOVING) {
                    switch (event.getCode()) {
                        case UP:  // Déplacements
                            tryRobotSelectedMove(0);
                            break;
                        case RIGHT:
                            tryRobotSelectedMove(1);
                            break;
                        case DOWN:
                            tryRobotSelectedMove(2);
                            break;
                        case LEFT:
                            tryRobotSelectedMove(3);
                            break;
                        case SPACE:  // Changement de robot sélectionné
                            robotSelectedId = (robotSelectedId + 1) % 4;
                            // On met l'image par défaut de chaque robot et on met l'image sélectionnée du robot sélectionné
                            for (int i = 0; i < ROBOT_NUMBER; i++) {
                                robotsImageView[i].setImage(robotsImage[i]);
                            }
                            robotsImageView[robotSelectedId].setImage(robotsSelectedImage[robotSelectedId]);
                            break;
                        case R:  // Reset
                            for (int i = 0; i < ROBOT_NUMBER; i++) {
                                robots[i] = oldRobots[i].clone();
                            }
                            moves = 0;
                            // On actualise le nombre de mouvements affiché
                            infoLabel.setText("Joueur " + (bestGuessPlayer + 1) + ",\ndéplacez les robots et\natteignez l'objectif en\n"
                                    + bestGuess + " déplacements\n" + moves + " déplacements faits");
                            break;
                        case H:  // Abandon
                            setState(GameState.WAITING_FOR_GUESS);
                            break;
                    }
                    updatePanePlayerPosition(robotSelectedId);
                }
            }
        });

        // On ajoute un actionListener aux champs des joueurs pour intercepter un nombre de coups entré
        playersTextFields = new TextField[]{player1TextField, player2TextField, player3TextField, player4TextField};
        for (int i = 0; i < PLAYER_NUMBER; i++) {
            int playerId = i;  // On donne l'id du joueur (0 à 3) à chaque champ
            playersTextFields[i].setOnAction(actionEvent -> onPlayerGuess(playerId));
        }

        // On crée une liste avec les labels de nom et score de chaque joueur
        playersLabel = new Label[]{player1Label, player2Label, player3Label, player4Label};

        // On crée un compte à rebours qu'on utilisera lorsque la première solution sera entrée par un joueur
        timeLine = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> countDownOneSecond()));
        timeLine.setCycleCount(Animation.INDEFINITE);
    }

    @FXML
    public void showHelp() {
        String helpString = "Chaque joueur écrit la longueur d'un chemin pour aller jusqu'à l'objectif.\n" +
                "Au bout de 30 secondes après que le premier nombre ait été entré, le joueur avec le chemin\n" +
                "le plus court peut déplacer les robots.\n" +
                "(flèches: déplacements, espace: changer de robot, r: recommencer déplacements, h: abandonner)";

        Alert helpMessage = new Alert(Alert.AlertType.INFORMATION, helpString);
        helpMessage.setHeaderText(null);
        helpMessage.setGraphic(null);
        helpMessage.showAndWait();
    }

    @FXML
    public void startAI() {
        int minimumMoves = ai.solveRound();
        String aiString = "";
        if (minimumMoves > 0) {  // Solution trouvée à afficher
            aiString = "L'IA a trouvée une solution en " + minimumMoves + " déplacements\n(voir console)";
        } else {  // Pas de solution trouvée
            aiString = "L'IA n'a pas trouvée de solution avec " + AI.AI_MAX_GENERATIONS + " mouvements";
        }

        Alert helpMessage = new Alert(Alert.AlertType.INFORMATION, aiString);
        helpMessage.setHeaderText(null);
        helpMessage.setGraphic(null);
        helpMessage.showAndWait();
    }

    // Un joueur vient d'entrer un nouveau nombre de coups pour atteindre l'objectif
    @FXML
    public void onPlayerGuess(int playerId) {
        // On sort si l'état n'est pas waiting_for_guess ou timer
        if (!(state == GameState.WAITING_FOR_GUESS || state == GameState.TIMER)) {
            return;
        }

        // On récupère le texte
        String text = playersTextFields[playerId].getText();
        if (text == null || text.equals("")) {
            return;
        }
        text = text.trim();

        // On essaie de convertir le nombre de coups en entier
        try {
            int guess = Integer.parseInt(text);
            if (guess > 0) {  // Nombre correct entré
                playersGuesses[playerId] = guess;

                // On vérifie si le nombre entier est le meilleur nombre de coups entré
                if (bestGuess < 0 || guess < bestGuess) {
                    bestGuess = guess;
                    bestGuessPlayer = playerId;

                    // Si c'est le premier nombre de coups trouvé, on lance le compte à rebours de 30 sec
                    if (state == GameState.WAITING_FOR_GUESS) {
                        setState(GameState.TIMER);
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    private void startCountDown() {
        timeLeft = TIMER_START_TIME;  // 30 sec
        timerLabel.setText(timeLeft + " secondes restantes");
        timeLine.play();
    }

    public void countDownOneSecond() {
        timeLeft--;
        timerLabel.setText(timeLeft + " secondes restantes");

        // Si le compte à rebours est terminé, on laisse le meilleur joueur jouer
        if (timeLeft <= 0) {
            timerLabel.setText("");
            timeLine.stop();

            setState(GameState.PLAYER_MOVING);
        }
    }

    // --- Déplacements standard ---

    // Déplace le robot sur la gridPane du jeu
    private void updatePanePlayerPosition(int robotId) {
        int x = robots[robotId].getX();
        int y = robots[robotId].getY();
        GridPane.setConstraints(robotsImageView[robotId], x, y);
    }

    // Commence le déplacement d'un robot si possible
    private void tryRobotSelectedMove(int direction) {
        if (canRobotSelectedMove(direction)) {
            // On actualise le nombre de mouvements affiché
            moves++;
            infoLabel.setText("Joueur " + (bestGuessPlayer + 1) + ",\ndéplacez les robots et\natteignez l'objectif en\n"
                    + bestGuess + " déplacements\ndéplacements: " + moves);

            // On se déplace
            robotSelectedMove(direction);
            while (canRobotSelectedMove(direction)) {
                robotSelectedMove(direction);
            }
            checkObjectiveReached();
        }
    }

    // Vérifie si l'objectif est atteint
    private void checkObjectiveReached() {
        Robot robotSel = robots[robotSelectedId];
        if (robotSel.getX() == objective.getX() && robotSel.getY() == objective.getY() && robots[robotSelectedId].getColor() == objective.getColor()) {
            if (moves == bestGuess) {
                playersScore[bestGuessPlayer]++;
                playersLabel[bestGuessPlayer].setText("Joueur " + (bestGuessPlayer + 1) + "   score: " + playersScore[bestGuessPlayer]);
            } else {
                System.out.println("Tu n'as pas fait le bon nombre de coups");
            }
            setState(GameState.WAITING_FOR_GUESS);
        }
    }

    // Fait un déplacement pour le robot sélectionné dans la direction donnée
    private void robotSelectedMove(int direction) {
        switch (direction) {
            case 0:  // Haut
                robots[robotSelectedId].addY(-1);
                break;
            case 1:  // Droite
                robots[robotSelectedId].addX(1);
                break;
            case 2:  // Bas
                robots[robotSelectedId].addY(1);
                break;
            case 3:  // Gauche
                robots[robotSelectedId].addX(-1);
                break;
        }
    }

    // Renvoie true si le robot peut bouger dans la direction donnée
    private boolean canRobotSelectedMove(int direction) {
        int x = robots[robotSelectedId].getX();
        int y = robots[robotSelectedId].getY();

        switch (direction) {
            case 0:  // Haut
                if (isTileFree(x, y - 1, robotSelectedId) && !isWall(x, y - 1, false)) {
                    return true;
                }
                break;
            case 1:  // Droite
                if (isTileFree(x + 1, y, robotSelectedId) && !isWall(x, y, true)) {
                    return true;
                }
                break;
            case 2:  // Bas
                if (isTileFree(x, y + 1, robotSelectedId) && !isWall(x, y, false)) {
                    return true;
                }
                break;
            case 3:  // Gauche
                if (isTileFree(x - 1, y, robotSelectedId) && !isWall(x - 1, y, true)) {
                    return true;
                }
                break;
        }

        // Mouvement impossible
        return false;
    }

    // Renvoie true s'il est possible de se déplacer suivant les coordonnées données
    private boolean isTileFree(int x, int y, int robotSelectedId) {
        // Cases bloquées au milieu du jeu
        if ((x == 7 || x == 8) && (y == 7 || y == 8)) {
            return false;
        }

        // Limites du jeu
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return false;
        }

        // Robots qui bloquent
        for (int i = 0; i < ROBOT_NUMBER; i++) {
            if (robots[i] != null) {
                Robot robot = robots[i];
                if (i != robotSelectedId && x == robot.getX() && y == robot.getY()) {
                    return false;
                }
            }
        }

        return true;
    }

    // Renvoie true s'il existe un mur suivant les informations données
    public boolean isWall(int x, int y, boolean vertical) {
        for (Wall wall : walls) {
            if (wall.isVertical() == vertical && wall.getX() == x && wall.getY() == y) {
                return true;
            }
        }
        return false;
    }

    // -- Fin Déplacements standard ---


    private void setState(GameState newState) {
        if (newState == GameState.WAITING_FOR_GUESS) {  // Les joueurs peuvent chercher une solution
            // On choisit un nouvel objectif et on l'affiche
            chooseObjective();
            objectiveImageView.setImage(new Image(COLORS[objective.getColor()] + "Objective.png", TILE_SIZE, TILE_SIZE, false, false));
            GridPane.setConstraints(objectiveImageView, objective.getX(), objective.getY());

            bestGuessPlayer = -1;
            bestGuess = -1;
            for (int i = 0; i < PLAYER_NUMBER; i++) {
                playersGuesses[i] = 0;

                // On active les champs pour que les joueurs mettent leur réponse
                playersTextFields[i].setText("");
                playersTextFields[i].setDisable(false);
            }

            // On sauvegarde la position initiale des robots
            oldRobots = new Robot[ROBOT_NUMBER];
            for (int i = 0; i < ROBOT_NUMBER; i++) {
                oldRobots[i] = robots[i].clone();
            }

            // On active le bouton IA et aide
            aiButton.setDisable(false);
            helpButton.setDisable(false);
        } else if (newState == GameState.TIMER) {
            startCountDown();
        } else {  // GameState.PLAYER_MOVING
            // On désactive les champs des joueurs pour éviter les modifications
            for (int i = 0; i < PLAYER_NUMBER; i++) {
                playersTextFields[i].setDisable(true);
            }

            // On sauvegarde la position initiale des robots
            oldRobots = new Robot[ROBOT_NUMBER];
            for (int i = 0; i < ROBOT_NUMBER; i++) {
                oldRobots[i] = robots[i].clone();
            }
            moves = 0;

            // On désactive le bouton IA et aide
            aiButton.setDisable(true);
            helpButton.setDisable(true);
        }

        // On actualise la variable state
        state = newState;
        infoLabel.setText(state.getDescription());

        if (state == GameState.PLAYER_MOVING) {
            infoLabel.setText("Joueur " + (bestGuessPlayer + 1) + ",\ndéplacez les robots et\natteignez l'objectif en\n"
                    + bestGuess + " déplacements\ndéplacements: 0");
        }
    }

    // Renvoie une copie des robots avant qu'ils n'aient été déplacés
    public Robot[] getOldRobots() {
        return new Robot[]{oldRobots[0].clone(), oldRobots[1].clone(), oldRobots[2].clone(), oldRobots[3].clone()};
    }

    // Renvoie l'objectif
    public Objective getObjective() {
        return objective;
    }
}