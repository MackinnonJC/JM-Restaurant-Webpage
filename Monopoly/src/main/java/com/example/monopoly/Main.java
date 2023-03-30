package com.example.monopoly;

/*
    Name: Jacob Mackinnon
    Assignment: Monopoly 1
    Description: Design the UI for an application that will eventually serve as a method of p\aying Monopoly.
    Includes buttons to roll dice, buttons to buy and sell property and a button to end a turn.
    Utilizes a ListView and a TableView for player data, though no data is set yet.
*/
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("startPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1250, 800);
        stage.setTitle("Monopoly");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}