package com.example.monopoly;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

class ControllerBase {

    private Game currentGame;
    private void setGame(Game newGame) {
        if (currentGame == null) {
            currentGame = newGame;
        }
    }
    Game getGame() { return currentGame; }

    // Switch the scene of the program
    protected void switchScene(String newScene, ActionEvent event, Game newGame) {
        try {
            // Load the new scene and fetch needed components to display it
            FXMLLoader loader = new FXMLLoader(ControllerBase.class.getResource(newScene));
            Parent root = loader.load();
            ControllerBase controller = loader.getController();
            controller.setGame(newGame);
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the stage
            Scene scene = new Scene(root, 1250, 800);
            stage.setScene(scene);
            stage.setTitle("Monopoly");

            // Show the stage
            stage.show();

            // configure controller
            controller.initializeWithGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Switch to the target scene without passing a game - used for game screen >> home screen.
    protected void switchScene(String newScene, ActionEvent event) {
        try {
            // Load the new scene and fetch needed components to display it
            FXMLLoader loader = new FXMLLoader(ControllerBase.class.getResource(newScene));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the stage
            Scene scene = new Scene(root, 1250, 800);
            stage.setScene(scene);
            stage.setTitle("Monopoly");

            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void initializeWithGame() {
        // Intended to be overwritten
    }

}
