package com.example.monopoly;

class DBStartGameResult {
    private int gameID;
    private int[] playerIDs;

    public DBStartGameResult(int ID, int[] pIDs) {
        gameID = ID;
        playerIDs = pIDs;
    }

    public int getGameID() {
        return gameID;
    }

    public int[] getPlayerIDs() {
        return playerIDs;
    }
}
