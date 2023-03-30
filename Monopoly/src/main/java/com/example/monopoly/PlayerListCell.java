package com.example.monopoly;

import javafx.scene.control.TableCell;

// JavaFX does not support selectors for nth-child(), meaning if I want to color-code the player list I have to do this.
public class PlayerListCell extends TableCell<Player, Integer> {

    public PlayerListCell() {}

    @Override
    public void updateItem(Integer it, boolean empty) {
        super.updateItem(it, empty);

        // Depending on which of the four valid rows it is, set the color
        if (it != null) {
            switch (it) {
                case 1:
                    getStyleClass().add("player1");
                    break;
                case 2:
                    getStyleClass().add("player2");
                    break;
                case 3:
                    getStyleClass().add("player3");
                    break;
                case 4:
                    getStyleClass().add("player4");
                    break;
            }
        }
    }
}
