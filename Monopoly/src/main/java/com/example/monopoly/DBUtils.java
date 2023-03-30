package com.example.monopoly;

import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

class DBUtils {

    // Serialize a string list of names into a list of players to send the database
    private static String serializeNameList(ArrayList<String> players) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            builder.append(players.get(i));

            // Ensure that the last entry does not add a delimiter
            if (i < players.size() - 1) {
                builder.append("|");
            }
        }
        // Return the built string
        return builder.toString();
    }
    // Given a serialized ID string, deserialize it
    private static int[] deserializeIDList(String serialized) {
        String[] split = serialized.split("\\|");
        int[] resultTable = new int[split.length];

        // Cycle through every section and attempt to parse it as an integer
        for (int i = 0; i < split.length; i++) {
            int numeric = -1;
            try {
                numeric = Integer.parseInt(split[i]);
            } catch (NumberFormatException ignored) {}
            resultTable[i] = numeric;
        }

        // Return the completed array
        return resultTable;
    }

    // Create a new game and create new players in the database with default values
    static DBStartGameResult attemptStartGame(ArrayList<String> players) {

        // Define all the SQL connection objects
        Connection conn = null;
        CallableStatement callStatement = null;

        // Values to be inserted into via SQL
        int gameID = -1;
        int[] playerIDs = null;
        try {
            // Establish a database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "password");

            // Prepare the procedure call statement
            callStatement = conn.prepareCall("{ CALL Start_Game(?, ?, ?)}");
            callStatement.setString(1, serializeNameList(players));
            callStatement.registerOutParameter(2, Types.INTEGER);
            callStatement.registerOutParameter(3, Types.VARCHAR);

            // Call the statement and retrieve values
            callStatement.execute();
            gameID = callStatement.getObject(2, Integer.class);
            String playerIDString = callStatement.getObject(3, String.class);
            playerIDs = deserializeIDList(playerIDString);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (callStatement != null) try { callStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Create a result and return it - if one or more did not work it will return null.
        DBStartGameResult result = null;
        if (gameID != -1 && playerIDs != null) {
            result = new DBStartGameResult(gameID, playerIDs);
        }
        return result;
    }

    // Load the players from a given game
    static Player[] loadPlayers(int gameID) {
        // Define all the SQL connection objects
        Connection conn = null;
        CallableStatement callStatement = null;
        ResultSet playerSet = null;

        // Values to be inserted into via SQL
        Player[] players = null;
        try {
            // Establish a database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "password");

            // Prepare the procedure call statement
            callStatement = conn.prepareCall("{ CALL Get_Players(?, ?)}");
            callStatement.setInt(1, gameID);
            callStatement.registerOutParameter(2, Types.INTEGER);

            // Call the statement and retrieve values
            playerSet = callStatement.executeQuery();
            int amountOfPlayers = callStatement.getObject(2, Integer.class);

            // Ensure there is a valid amount of players
            if (amountOfPlayers > 0) {
                players = new Player[amountOfPlayers];
                while (playerSet.next()) {
                    // Get the player's values
                    int playerID = playerSet.getInt(1);
                    int playerNumber = playerSet.getInt(2);
                    // index 3 is game id so we do not need to get it
                    String playerName = playerSet.getString(4);
                    int currentBalance = playerSet.getInt(5);
                    boolean isJailed = playerSet.getInt(6) == 1; // Jailed is stored as a binary digit
                    int currentSpace = playerSet.getInt(7);

                    // Create the Player object
                    Player newPlayer = new Player(playerID, playerNumber, playerName, currentBalance, currentSpace, isJailed);

                    // Add it to the list of players
                    players[playerNumber - 1] = newPlayer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (playerSet != null) try { playerSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (callStatement != null) try { callStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Return the players
        return players;
    }

    // Load the current turn
    static int loadNextTurn(int gameID) {
        // Define all the SQL connection objects
        Connection conn = null;
        CallableStatement callStatement = null;

        // Values to be inserted into via SQL
        int currentTurn = -1;
        try {
            // Establish a database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "password");

            // Prepare the procedure call statement
            callStatement = conn.prepareCall("{ CALL Get_Next_Turn(?, ?)}");
            callStatement.setInt(1, gameID);
            callStatement.registerOutParameter(2, Types.INTEGER);

            // Call the statement and retrieve values
            callStatement.executeQuery();
            currentTurn = callStatement.getObject(2, Integer.class);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (callStatement != null) try { callStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Return the current turn
        return currentTurn;
    }

    // Load the board from the database, including currently possessed properties
    static Space[] loadBoard(int gameID, Player[] players) {
        // Define all the SQL connection objects
        Connection conn = null;
        CallableStatement callStatement = null;
        ResultSet propertySet = null;

        // Values to be inserted into via SQL
        Space[] board = null;
        try {
            // Establish a database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "password");

            // Prepare the procedure call statement
            callStatement = conn.prepareCall("{ CALL Get_Properties(?)}");
            callStatement.setInt(1, gameID);

            // Call the statement and retrieve values
            propertySet = callStatement.executeQuery();
            board = new Space[40];
            while (propertySet.next()) {
                // Get the property's values
                int propertyID = propertySet.getInt(1);
                String propertyName = propertySet.getString(2);
                int occupyingSpace = propertySet.getInt(3);
                int basePrice = propertySet.getInt(4);
                int baseUpgradeCost = propertySet.getInt(5);
                int baseRentalCharge = propertySet.getInt(6);
                int colorCategory = propertySet.getInt(7);
                int playerID = propertySet.getInt(8);
                int propertyTier = propertySet.getInt(9);

                // Create the property and set its' player if it's already owned
                Property newProperty = new Property(propertyID, propertyName, basePrice, baseRentalCharge, baseUpgradeCost, propertyTier, colorCategory, occupyingSpace);
                if (playerID != 0) Player.getPlayerFromID(players, playerID).addProperty(newProperty);

                // Lastly, create the space and set it
                PropertySpace space = new PropertySpace(BoardPositionReference.getDX(occupyingSpace), BoardPositionReference.getDY(occupyingSpace), newProperty);
                board[occupyingSpace] = space;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (propertySet != null) try { propertySet.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (callStatement != null) try { callStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        // Ensure the board was initialized, so that we can return null if it wasn't
        if (board != null) {
            // Lastly, add some special spaces to the board
            board[30] = new JailSpace(BoardPositionReference.getDX(30), BoardPositionReference.getDY(30));

            // Rest are generic spaces
            for (int i = 0; i < board.length; i++) {
                if (board[i] == null) {
                    Space space = new Space(BoardPositionReference.getDX(i), BoardPositionReference.getDY(i));
                    board[i] = space;
                }
            }
        }
        // Return the board
        return board;
    }

    // Attempt to save the game into the database.
    private static String serializePlayerListForSaving(ObservableList<Player> players) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            builder.append(players.get(i).parseForDB());
            if (i < players.size() - 1) {
                builder.append("|");
            }
        }
        System.out.println(builder);
        return builder.toString();
    }
    private static String serializePlayerPropertiesForSaving(ObservableList<Player> players) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            builder.append(players.get(i).parsePropertiesForDB());
            if (i < players.size() - 1) {
                builder.append("|");
            }
        }
        System.out.println(builder);
        return builder.toString();
    }
    static boolean saveGame(Game game, ObservableList<Player> players) {

        // Define all the SQL connection objects
        Connection conn = null;
        CallableStatement callStatement = null;
        CallableStatement savePropsStatement = null;
        CallableStatement saveTurnStatement = null;

        int gameID = game.getGameID();
        int currentTurn = game.getCurrentTurn();
        try {
            // Establish a database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/monopolydb", "root", "password");

            // Prepare the save players procedure call statement
            callStatement = conn.prepareCall("{ CALL Save_Players(?, ?, ?)}");
            callStatement.setString(1, serializePlayerListForSaving(players));
            callStatement.setInt(2, gameID);
            callStatement.registerOutParameter(3, Types.INTEGER);

            // Call the statement and retrieve values
            callStatement.executeUpdate();
            int status = callStatement.getObject(3, Integer.class);
            System.out.println(status);

            // Prepare the save PROPERTIES procedure call statement
            savePropsStatement = conn.prepareCall("{ CALL Save_Properties(?, ?, ?, ?)}");
            savePropsStatement.setString(1, serializePlayerPropertiesForSaving(players));
            savePropsStatement.setInt(2, gameID);
            savePropsStatement.registerOutParameter(3, Types.INTEGER);
            savePropsStatement.registerOutParameter(4, Types.VARCHAR);

            // Call the statement and retrieve values
            savePropsStatement.executeUpdate();
            int status2 = savePropsStatement.getObject(3, Integer.class);
            System.out.println(status2);

            // Prepare the statement to save the current turn
            saveTurnStatement = conn.prepareCall("{ CALL Save_Next_Turn(?, ?)}");
            saveTurnStatement.setInt(1, gameID);
            saveTurnStatement.setInt(2, currentTurn);
            saveTurnStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (saveTurnStatement != null) try { saveTurnStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (savePropsStatement != null) try { savePropsStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (callStatement != null) try { callStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return true;
    }
}
