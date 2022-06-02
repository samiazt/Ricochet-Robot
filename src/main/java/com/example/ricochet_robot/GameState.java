package com.example.ricochet_robot;

public enum GameState {
    WAITING_FOR_GUESS("En combien de déplacements\npouvez-vous atteindre\nl'objectif ?"),
    TIMER("En combien de déplacements\npouvez-vous atteindre\nl'objectif ?\nLe temps presse..."),
    PLAYER_MOVING("Déplacez le robot\navec le moins de coups\npossibles");

    private String description;

    GameState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
