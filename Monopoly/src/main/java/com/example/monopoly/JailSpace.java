package com.example.monopoly;

public class JailSpace extends Space {

    public JailSpace(int dX, int dY) {
        super(dX, dY);
    }

    @Override
    void onPlayerEnter(Game currentGame, Player player) {
        player.jail();
        currentGame.changeControllerMessage(String.format("%s (Player %d) landed on the Go To Jail space and ended up in jail!", player.getPlayerName(), player.getPlayerNumber()));
        currentGame.promptPlayerChoice(false, false,0, 0);
    }

    @Override
    public String toString() {
        return "Jail";
    }
}
