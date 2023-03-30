package com.example.monopoly;

class Space {

    // Store the offset from the center for player positioning
    private int deltaX;
    private int deltaY;

    public Space(int dX, int dY) {
        deltaX = dX;
        deltaY = dY;
    }

    // Method that is called whenever a player stops on this space, intended to be overwritten
    void onPlayerEnter(Game currentGame, Player player) {
        currentGame.promptPlayerChoice(false, false,0, 0);
    }

    @Override
    public String toString() {
        return "N/A";
    }
    public String getStyle() {
        return null;
    }
    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }
}
