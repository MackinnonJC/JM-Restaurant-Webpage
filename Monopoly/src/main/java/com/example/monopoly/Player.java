package com.example.monopoly;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class Player {

    // Static method to find a player ID in a list of players
    static Player getPlayerFromID(Player[] plrs, int ID) {
        Player result = null;
        for (int i = 0; i < plrs.length; i++) {
            if (plrs[i] != null && plrs[i].getPlayerID() == ID) {
                result = plrs[i];
                break;
            }
        }
        return result;
    }
    private int playerID;
    private int playerNumber;
    private String playerName;
    private int playerBalance = 1000;
    private int currentSpace = 0;
    private boolean isJailed = false;
    private ObservableList<Property> ownedProperties;

    // Constructors - one is for a new player while the other is for a player with existing data.
    public Player(int playerID, int playerNumber, String playerName) {
        this.playerID = playerID;
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        ownedProperties = FXCollections.observableArrayList();
    }
    public Player(int playerID, int playerNumber, String playerName, int playerBalance, int currentSpace, boolean isJailed) {
        this.playerID = playerID;
        this.playerNumber = playerNumber;
        this.playerName = playerName;
        this.playerBalance = playerBalance;
        this.currentSpace = currentSpace;
        this.isJailed = isJailed;
        ownedProperties = FXCollections.observableArrayList();
    }

    // Based on a dice roll, either adjust the space or become unjailed. Returns how many spaces were moved.
    int rolledDice(int dice1, int dice2) {
        int spacesTraveled = 0;
        if (isJailed) {
            // Player is jailed, meaning for now their only option is to try and roll a doubles.
            if (dice1 == dice2) {
                isJailed = false;
            }
        } else {
            // Player is not jailed - they can move forward normally
            currentSpace += dice1 + dice2;
            spacesTraveled = dice1 + dice2;

            // Ensure the current space position does not exceed 39
            if (currentSpace > 39) currentSpace -= 39;
        }
        return spacesTraveled;
    }
    // Add a property to the player's owned properties
    void addProperty(Property property) {
        ownedProperties.add(property);
        property.changeOwner(this);
    }

    // Remove a property from the player's owned properties
    void removeProperty(Property property) {
        ownedProperties.remove(property);
        property.changeOwner(null);
    }

    // Sell a property
    void sellProperty(Property property) {
        int sellPrice = property.getSellPrice();
        removeProperty(property);
        System.out.println(sellPrice);
        changeBalance(sellPrice);
    }

    // Given a ListView, populates it with the player's currently owned properties.
    // This keeps the actual list of properties slightly more secure.
    void displayProperties(ListView<Property> target) {
        target.setItems(ownedProperties);
    }

    // Private method associated with changing money
    private void changeBalance(int deltaBalance) {
        playerBalance += deltaBalance;
        if (playerBalance < 0) playerBalance = 0;
    }

    // Method for paying another player
    void payTo(Player other, int amount) {
        other.changeBalance(amount);
        changeBalance(-amount);
    }

    // Reduce the balance, can be called from elsewhere
    void deductBalance(int amount) {
        if (amount > 0) {
            changeBalance(-amount);
        }
    }

    // Jail the player
    void jail() {
        if (!isJailed) {
            isJailed = true;
            currentSpace = 10;
        }
    }
    // Appropiate getter methods
    public int getPlayerID() { return playerID; }
    public int getPlayerNumber() { return playerNumber; }
    public String getPlayerName() { return playerName; }
    public int getPlayerBalance() { return playerBalance; }
    public String getPlayerMoney() { return String.format("$%d", playerBalance); }
    public int getCurrentSpace() { return currentSpace; }
    public int getNumOfProperties() { return ownedProperties.size(); }
    public boolean isJailed() { return isJailed; }
    String parseForDB() {
        return String.format("%d-%d-%d-%d",
                getPlayerID(), getPlayerBalance(), getCurrentSpace(), isJailed ? 1 : 0);
    }
    String parsePropertiesForDB() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPlayerID());
        for (int i = 0; i < ownedProperties.size(); i++) {
            builder.append("-");
            Property property = ownedProperties.get(i);
            builder.append(property.getPropertyID());
            builder.append(",");
            builder.append(property.getCurrentTier());
        }
        return builder.toString();
    }
}
