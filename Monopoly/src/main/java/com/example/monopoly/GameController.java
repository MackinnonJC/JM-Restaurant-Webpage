package com.example.monopoly;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class GameController extends ControllerBase {

    // Components dealing with the board, such as dice rolls
    @FXML
    private Button diceRollButton;
    @FXML
    private ImageView leftDiceImageView, rightDiceImageView;
    // -- Components dealing with the board


    // Components for the space information (roll amount, location, price, property option buttons)
    @FXML
    private Button buyPropertyButton, sellPropertyButton, endTurnButton;
    @FXML
    private Label diceRollLabel, currentLocationLabel, propertyCostLabel, messageLabel;
    // -- Components for the space information (roll amount, location, price, property option buttons)


    // Components pertaining to the current turn information
    @FXML
    private Label playerNameLabel, playerBalanceLabel;
    // -- Components pertaining to the current turn information


    // Components pertaining to the Held Properties and Game Summary sections
    @FXML
    private ListView<Property> heldPropertiesListView;
    @FXML
    private TableView<Player> gameSummaryTableView;
    @FXML
    private TableColumn<Player, String> playerNameColumn, playerBalanceColumn;
    @FXML
    private TableColumn<Player, Integer> playerPropertiesOwnedColumn, playerNumberColumn;
    // -- Components pertaining to the Game Summary section


    // Components pertaining to Player indicators and colors
    @FXML
    private Pane playerHolderPane;
    private Circle[] playerIndicators;
    private ObservableList<Player> playersList;
    private Color[] playerColors = new Color[]{
            Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE
    };

    // -- Components pertaining to Player indicators and colors


    // Components pertaining to saving and quitting
    @FXML
    private Button saveButton, quitButton;
    // -- Components pertaining to saving and quitting


    // misc formats
    private int currentCost;
    private int sellBack;
    private final String moneyFormat = "$%d";
    private final String sellBackFormat = "$%d\n(+$%d sell-back)";
    private int buttonState = 0b000;
    // -- misc formats

    private String messageContent;

    // Initialize the game
    @Override
    protected void initializeWithGame() {
        getGame().initializeGame(this);
    }

    // Stylize the game listing
    private void stylizePlayerList() {
        gameSummaryTableView.getChildrenUnmodifiable();
        System.out.println(gameSummaryTableView.getChildrenUnmodifiable());
    }
    // When the game is done initializing all of its' values, it will update the Controller with all the players
    void receiveGameValues(Player[] players) {

        // For every player, initialize an associated Circle
        playerIndicators = new Circle[players.length];
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            System.out.println(currentPlayer.getCurrentSpace());
            // Create the circle and set its' default values
            Circle newCircle = new Circle();
            newCircle.setRadius(20);
            newCircle.setCenterX(BoardPositionReference.getDX(currentPlayer.getCurrentSpace()));
            newCircle.setCenterY(BoardPositionReference.getDY(currentPlayer.getCurrentSpace()));
            newCircle.setFill(playerColors[i]);

            // Add it to the pane and array
            playerHolderPane.getChildren().add(newCircle);
            playerIndicators[i] = newCircle;
        }

        // Initialize the Game Summary section
        playersList = FXCollections.observableArrayList(players);
        playerNumberColumn.setCellFactory(col -> new PlayerListCell());
        playerNumberColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("playerNumber"));
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("playerName"));
        playerBalanceColumn.setCellValueFactory(new PropertyValueFactory<Player, String>("playerMoney"));
        playerPropertiesOwnedColumn.setCellValueFactory(new PropertyValueFactory<Player, Integer>("numOfProperties"));
        gameSummaryTableView.setItems(playersList);
    }

    // Process the dice roll button being pressed - the Game class will determine if an action is to be made
    @FXML
    private void onDiceRollButtonPressed(ActionEvent e) {
        getGame().attemptRollDice();
    }

    // Process a property purchase
    @FXML
    private void onBuyPropertyButtonPressed(ActionEvent e) {
        getGame().attemptBuyProperty();
    }

    // Process a property sell
    @FXML
    private void onSellPropertyButtonPressed(ActionEvent e) {
        getGame().attemptSellProperty();
    }

    // Process an end turn
    @FXML
    private void onEndTurnButtonPressed(ActionEvent e) {
        getGame().attemptEndTurn();
    }

    // When the save button is pressed, send the information to the database.
    long lastSaved = 0;
    @FXML
    private void onSaveButtonPressed(ActionEvent event) {
         if (System.currentTimeMillis() - lastSaved > 10000) {
             lastSaved = System.currentTimeMillis();
             if (DBUtils.saveGame(getGame(), playersList)) {
                 messageLabel.setText("Game has successfully been saved! To continue later, enter the following ID:\n" + getGame().getGameID());
             }
         }
    }

    // When the quit button is pressed, ask for confirmation then return to home screen
    @FXML
    private void onQuitButtonPressed(ActionEvent event) {

        switchScene("startPage.fxml", event);
    }

    // Update a player's position
    private String lastStyle;
    private void updateLocationLabel(Player target, Space space) {
        currentLocationLabel.setText(space.toString());

        // Change location label color
        String style = space.getStyle();
        currentLocationLabel.getStyleClass().add(style);
        currentLocationLabel.getStyleClass().remove(lastStyle);
        lastStyle = style;
    }
    private void updateLocation(Player target, Space space, boolean animate) {

        // Update indicator position
        Circle indicator = playerIndicators[target.getPlayerNumber() - 1];
        int startX = (int) indicator.getCenterX();
        int startY = (int) indicator.getCenterY();
        if (animate) {
            currentLocationLabel.setText("???");
            PlayerIconTransition transition = new PlayerIconTransition(indicator, startX, startY, space.getDeltaX(), space.getDeltaY(), ()->{
                updateLocationLabel(target, space);
                System.out.println("Sellback");
                System.out.println(sellBack);
                if (currentCost > 0 && sellBack > 0) {
                    propertyCostLabel.setText(String.format(sellBackFormat, currentCost, sellBack));
                } else if (currentCost > 0) {
                    propertyCostLabel.setText(String.format(moneyFormat, currentCost));
                }
                buyPropertyButton.setVisible((buttonState & 0b100) == 0b100);
                sellPropertyButton.setVisible((buttonState & 0b010) == 0b010);
                endTurnButton.setVisible(true);
                if (messageContent != null) {
                    messageLabel.setText(messageContent);
                    messageContent = null;
                }
            });
            transition.play();
        } else {
            updateLocationLabel(target, space);
            indicator.setCenterX(space.getDeltaX());
            indicator.setCenterY(space.getDeltaY());
        }
    }
    void updatePlayer(Player target, Space[] board, boolean animate) {
        int pos = target.getCurrentSpace();
        Space space = board[pos];

        // Update location
        updateLocation(target, space, animate);

        // Notify the game that movement is finished, if this was called BY finishedmovement it won't do anything as turn state changed
        getGame().finishedMovement();
    }

    // Called by the Game to switch the current player in the UI
    void changeCurrentPlayer(Player newPlayer) {
        gameSummaryTableView.refresh();
        currentCost = 0;
        sellBack = 0;
        playerNameLabel.setText(newPlayer.getPlayerName());
        playerBalanceLabel.setText(String.format(moneyFormat, newPlayer.getPlayerBalance()));

        newPlayer.displayProperties(heldPropertiesListView);

        // Update output label to indicate who is next
        messageLabel.setText(String.format("%s (Player %d)'s turn!", newPlayer.getPlayerName(), newPlayer.getPlayerNumber()));

        // Reset the dice
        diceRollButton.setVisible(true);
    }

    // Change the message label after the movement animation finishes, called by the Game
    void changeMessageLabel(String newContent) {
        messageContent = newContent;
    }

    // Change the message label without waiting for the movement animation, called by the Game
    void forceMessageLabel(String newContent) {
        messageContent = null;
        messageLabel.setText(newContent);
    }

    // Enables the Buy / Sell / End turn buttons while updating the cost if needed
    void allowPlayerChoice(boolean canBuy, boolean canSell, int cost, boolean forceDisplay, int sellPrice) {
        if (cost > 0) currentCost = cost;
        sellBack = sellPrice;

        // If the values need to be initialized, they cannot wait for the animation
        if (forceDisplay) {
            propertyCostLabel.setText(String.format(moneyFormat, cost));
            buyPropertyButton.setVisible(canBuy);
            sellPropertyButton.setVisible(canSell);
            endTurnButton.setVisible(true);
        }
        // Update the binary states
        buttonState = 0b001 + (canBuy ? 0b100 : 0b000) + (canSell ? 0b010 : 0b000);
    }
    // Disables the Buy / Sell / End turn buttons
    void disableChoiceButtons() {
        buyPropertyButton.setVisible(false);
        sellPropertyButton.setVisible(false);
        endTurnButton.setVisible(false);
        buttonState = 0;
        propertyCostLabel.setText("N/A");
    }

    private final String diceFormat = "Player rolled a %02d";
    private final String emptyDiceFormat = "Player rolled a ##";
    private Timeline diceAnimation;
    private ArrayList<Image> diceImages;
    void rollDice(int dice1, int dice2) {
        // Play the animation to cycle through the six images
        diceAnimation.play();
        diceRollButton.setVisible(false);
        diceRollLabel.setText(emptyDiceFormat);

        // Set the Finished event to load the images
        diceAnimation.setOnFinished(event -> {
            // Load the image
            Image finalImage1 = diceImages.get(dice1-1);
            Image finalImage2 = diceImages.get(dice2-1);

            leftDiceImageView.setImage(finalImage1);
            rightDiceImageView.setImage(finalImage2);

            diceRollLabel.setText(String.format(diceFormat, dice1 + dice2));
            event.consume();
        });
    }

    public void initialize() throws URISyntaxException {
        // Hide all the option buttons at first
        disableChoiceButtons();

        // Initialize the dice roll animation
        diceAnimation = new Timeline();
        diceImages = new ArrayList<>();

        // Initialize all the images
        for (int i = 1; i <= 6; i++) {
            // Called by the game, plays an animation to roll the two dice.
            String imagePathFormat = "images/Dice%d.png";
            String path = String.format(imagePathFormat, i);
            System.out.println(path);
            Image newImage = new Image(GameController.class.getResource(path).toURI().toString());
            diceImages.add(newImage);
        }

        // Set up the animation keyframes for both views
        // This utilizes KeyFrames which are placed at specific points in the timeline
        for (int i = 0; i < diceImages.size(); i++) {
            diceAnimation.getKeyFrames().add(
                new KeyFrame(
                    Duration.seconds(i * 0.1),
                    new KeyValue(leftDiceImageView.imageProperty(), diceImages.get(i))
                )
            );
            diceAnimation.getKeyFrames().add(
                new KeyFrame(
                    Duration.seconds(i * 0.1),
                    new KeyValue(rightDiceImageView.imageProperty(), diceImages.get(i))
                )
            );
        }

        // Ensure animation only plays three times
        diceAnimation.setCycleCount(3);
    }
}