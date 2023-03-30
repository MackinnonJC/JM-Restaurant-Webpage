package com.example.monopoly;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class StartPageController extends ControllerBase {

    // Components for the player name section
    @FXML
    private TextField player1TextField, player2TextField, player3TextField, player4TextField;
    // -- Components for the player name section


    // Components for the load existing game section
    @FXML
    private TextField gameIDTextField;
    // -- Components for the load existing game section


    // Components for handling game start
    private boolean canStart = true;
    @FXML
    private Label startGameOutputLabel;
    // -- Components for handling game start


    // Display an output message in the event something wrong occurs.
    private void changeOutput(String output) {
        startGameOutputLabel.setText(output);
    }

    // Add a label's contents to a String array list if it is not blank
    private void addToListIfNotBlank(TextField field, ArrayList<String> list) {
        if (!field.getText().isBlank()) {
            list.add(field.getText());
        }
    }
    @FXML
    private void onStartGameButtonPressed(ActionEvent event) {
        // Debounce value to prevent duplicate requests
        if (canStart) {
            canStart = false;

            // First, determine if the user is attempting to start a new game or load an existing one
            // Define values to eventually be passed to the game
            Player[] players = null;
            int gameID = -1;
            int currentTurn = 1;
            if (!gameIDTextField.getText().isBlank()) {
                // Reload a game
                // First step, parse the game ID
                try {
                    gameID = Integer.parseInt(gameIDTextField.getText());
                }
                catch (NumberFormatException ignored) {}
                finally {
                    // Next step, If the game ID is valid, attempt to load the players
                    if (gameID > -1) {
                        players = DBUtils.loadPlayers(gameID);
                        currentTurn = DBUtils.loadNextTurn(gameID);
                    } else {
                        changeOutput("An invalid game ID was provided, please only use numbers when specifying an ID.");
                    }
                }

                // If steps 1 and 2 succeed, players array will not be null and the game will start after this if statement.
                // But if there are no registered players, tell the user here. This is as the non-load game option
                // has different error messages that would be overridden if I put the changeOutput in an else block.
                if (players == null) {
                    changeOutput("No players were found with that game ID. Please try again.");
                }
            } else {
                // Start a new game
                // Collect a list of all the player names
                ArrayList<String> playerNames = new ArrayList<>();
                addToListIfNotBlank(player1TextField, playerNames);
                addToListIfNotBlank(player2TextField, playerNames);
                addToListIfNotBlank(player3TextField, playerNames);
                addToListIfNotBlank(player4TextField, playerNames);

                // Ensure the list is not empty
                if (playerNames.size() > 0) {
                    // Submit the request to the database
                    changeOutput("Starting game...");
                    DBStartGameResult result = DBUtils.attemptStartGame(playerNames);
                    if (result != null) {
                        gameID = result.getGameID();
                        int[] playerIDs = result.getPlayerIDs();

                        // Create the list of players
                        players = new Player[playerIDs.length];
                        for (int i = 0; i < playerIDs.length; i++) {
                            Player newPlayer = new Player(playerIDs[i], i + 1, playerNames.get(i));
                            players[i] = newPlayer;
                        }
                    } else {
                        changeOutput("There was a problem starting the game, please try again later.");
                    }
                } else {
                    changeOutput("No valid players were entered, please provide some player names in order to start.");
                }
            }

            if (players != null) {
                // Next, load the board
                Space[] board = DBUtils.loadBoard(gameID, players);

                // Create the game - and ensure the current turn is greater than zero
                System.out.println(currentTurn);
                Game newGame = new Game(gameID, players, Math.max(currentTurn, 1));
                newGame.initializeBoard(board);
                switchScene("maingame.fxml", event, newGame);
            }
        }
    }
}
