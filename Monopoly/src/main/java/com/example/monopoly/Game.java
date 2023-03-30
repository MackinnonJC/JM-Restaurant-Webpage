package com.example.monopoly;

class Game {
    private int gameID;
    // Connect to the controller to call associated UI events
    private GameController controller;
    // -- Connect to the controller to call associated UI events


    // Define the board
    private Space[] board;
    // -- Define the board


    // Define variables pertaining to the players
    private Player[] players;
    private int currentTurn = 1;
    private TurnState currentTurnState = TurnState.STARTING;
    // -- Define variables pertaining to the players


    // Constructor to setup gameID and players, controller will be passed in later in the process
    public Game(int gameID, Player[] players, int currentTurn) {
        this.gameID = gameID;
        this.players = players;
        this.currentTurn = currentTurn;
    }

    // Initialize the board
    void initializeBoard(Space[] board) {
        if (this.board == null) this.board = board;
    }

    // Called by the controller to initialize the game
    void initializeGame(GameController controller) {
        this.controller = controller;
        this.controller.receiveGameValues(players);
        controller.changeCurrentPlayer(players[currentTurn - 1]);
    }

    // Called to end a player's turn
    private void endTurn() {
        if (currentTurnState == TurnState.ENDED) {
            // Reset the current turn state and change the player
            currentTurnState = TurnState.STARTING;
            currentTurn++;
            if (currentTurn > players.length) currentTurn = 1;

            // Disable the previous options and update the UI
            controller.disableChoiceButtons();
            controller.changeCurrentPlayer(players[currentTurn - 1]);
        }
    }

    // Process a dice roll
    void attemptRollDice() {
        if (currentTurnState == TurnState.STARTING) {
            currentTurnState = TurnState.ROLLING;

            // Roll some dice
            int dice1 = (int) (Math.random() * 6) + 1;
            int dice2 = (int) (Math.random() * 6) + 1;
            controller.rollDice(dice1, dice2);

            // Store the previous jailed state
            boolean oldJailedState = players[currentTurn-1].isJailed();

            // Move the player and begin prompt for their next action
            // If they didn't move, don't play the animation but proceed the gameplay loop
            int spacesTraveled = players[currentTurn-1].rolledDice(dice1, dice2);
            if (spacesTraveled > 0) {
                controller.updatePlayer(players[currentTurn-1], board, true);
            } else {
                // Jail state changed in 0 spaces, meaning the player got out of jail
                if (players[currentTurn-1].isJailed() != oldJailedState) {
                    controller.forceMessageLabel(String.format("%s (Player %d) got out of jail!",
                            players[currentTurn-1].getPlayerName(), players[currentTurn-1].getPlayerNumber()));
                } else {
                    controller.forceMessageLabel(String.format("%s (Player %d) is still in jail - they need to roll a doubles to get out.",
                            players[currentTurn-1].getPlayerName(), players[currentTurn-1].getPlayerNumber()));
                }
                finishedMovement();
                controller.allowPlayerChoice(false, false, 0, true, 0);
            }
        }
    }

    // Called once the player's position is finished being updated to progress the turn
    void finishedMovement() {
        if (currentTurnState == TurnState.ROLLING) {
            currentTurnState = TurnState.LANDED;
            int currentSpace = players[currentTurn-1].getCurrentSpace();

            // Run the space enter method - save the current space before the method incase it involves a displacement
            Space spaceObj = board[currentSpace];
            int oldSpace = players[currentTurn-1].getCurrentSpace();
            spaceObj.onPlayerEnter(this, players[currentTurn-1]);

            // The player may have been displaced, so update them again
            if (oldSpace != players[currentTurn-1].getCurrentSpace()) {
                controller.updatePlayer(players[currentTurn-1], board, true);
            }
        }
    }

    // Change the turn state
    void changeTurnState(TurnState newState) {
        currentTurnState = newState;
    }

    // Prompt the player for their next action
    void promptPlayerChoice(boolean canBuy, boolean canSell, int cost, int sellPrice) {
        controller.allowPlayerChoice(canBuy, canSell, cost, false, sellPrice);
    }

    // Change the Controller's currently displayed message
    void changeControllerMessage(String newContent) {
        controller.changeMessageLabel(newContent);
    }

    // Called by the Controller when the Buy Property is pressed
    void attemptBuyProperty() {
        if (currentTurnState == TurnState.LANDED) {
            int pos = players[currentTurn - 1].getCurrentSpace();
            Space space = board[pos];

            // Check if it's a property space and if the player can buy it
            if (space instanceof PropertySpace) {
                if (((PropertySpace) space).canPurchase(players[currentTurn-1])) {
                    // They can buy it, so process the transaction
                    ((PropertySpace) space).promptPurchase(players[currentTurn-1]);

                    currentTurnState = TurnState.ENDED;
                    endTurn();
                }
            }
        }
    }

    // Called by the Controller when the Sell Property is pressed
    void attemptSellProperty() {
        if (currentTurnState == TurnState.LANDED) {
            int pos = players[currentTurn - 1].getCurrentSpace();
            Space space = board[pos];

            // Check if it's a property space and if the player can sell it
            if (space instanceof PropertySpace) {
                if (((PropertySpace) space).playerOwns(players[currentTurn-1])) {
                    // They can sell it, so process the transaction
                    ((PropertySpace) space).promptSell(players[currentTurn-1]);

                    currentTurnState = TurnState.ENDED;
                    endTurn();
                }
            }
        }
    }

    // Called by the Controller when the End Turn is pressed
    void attemptEndTurn() {
        if (currentTurnState == TurnState.LANDED) {
            currentTurnState = TurnState.ENDED;
            endTurn();
        }
    }


    public int getGameID() { return gameID; }
    public int getCurrentTurn() {
        // This is called by the database to be saved.
        // If the player hasn't rolled, save this turn
        // But if they've already rolled, save next turn and treat it like they ended instead of letting them
        // roll a second time.
        if (currentTurnState == TurnState.ROLLING || currentTurnState == TurnState.STARTING) {
            return currentTurn;
        } else {
            int turnHolder = currentTurn;
            turnHolder++;
            if (turnHolder > players.length) turnHolder = 1;
            return turnHolder;
        }
    }

}
