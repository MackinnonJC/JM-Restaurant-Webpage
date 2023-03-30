package com.example.monopoly;

public class PropertySpace extends Space {

    private Property heldProperty;

    public PropertySpace(int dX, int dY, Property toHold) {
        super(dX, dY);
        heldProperty = toHold;
    }

    private static String PAY_TO_FORMAT = "%s (Player %d) landed on %s (Player %d)'s %s and must pay $%d.";
    @Override
    void onPlayerEnter(Game currentGame, Player player) {
        // first determine if the property is owned by this player
        if (heldProperty.getOwner() == player || heldProperty.getOwner() == null) {
            // property is owned by this player, prompt an option to upgrade
            if (heldProperty.getOwner() == null) {
                currentGame.promptPlayerChoice(true, false, heldProperty.getPrice(), 0);
            } else {
                currentGame.promptPlayerChoice(true, true, heldProperty.getUpgradeCost(), heldProperty.getSellPrice());
            }
        } else {
            // property is not owned by this player, charge rent
            player.payTo(heldProperty.getOwner(), heldProperty.getRentalCharge());
            currentGame.changeControllerMessage(String.format(PAY_TO_FORMAT,
                    player.getPlayerName(), player.getPlayerNumber(),
                    heldProperty.getOwner().getPlayerName(), heldProperty.getOwner().getPlayerNumber(),
                    heldProperty.getPropertyName(), heldProperty.getRentalCharge()));
            currentGame.promptPlayerChoice(false, false, heldProperty.getRentalCharge(), 0);
        }
    }

    private final String PROPERTY_LABEL_FORMAT = "%s (Tier %d)";
    @Override
    public String toString() {
        if (heldProperty.getCurrentTier() > 0) {
            return String.format(PROPERTY_LABEL_FORMAT, heldProperty.getPropertyName(), heldProperty.getCurrentTier());
        } else {
            return heldProperty.getPropertyName();
        }
    }

    private final String styleFormat = "textColorCategory%d";
    @Override
    public String getStyle() {
        return String.format(styleFormat, heldProperty.getColorCategory());
    }

    boolean canPurchase(Player player) {
        boolean allow = false;
        if (heldProperty.getOwner() == player) {
            // Same player, meaning prompt for an upgrade
            allow = player.getPlayerBalance() >= heldProperty.getUpgradeCost();
        } else if (heldProperty.getOwner() == null) {
            // Not occupied, prompt for purchase
            allow = player.getPlayerBalance() >= heldProperty.getPrice();
        }
        return allow;
    }

    void promptPurchase(Player player) {
        if (heldProperty.getOwner() == player) {
            // Same player, so upgrade
            int upgradePrice = heldProperty.getUpgradeCost();
            player.deductBalance(upgradePrice);
            heldProperty.upgradeTier();
        } else if (heldProperty.getOwner() == null) {
            // Not occupied, prompt for purchase
            int buyPrice = heldProperty.getPrice();
            player.deductBalance(buyPrice);
            player.addProperty(heldProperty);
            heldProperty.changeOwner(player);
        }
    }
    void promptSell(Player player) {
        if (heldProperty.getOwner() == player) {
            player.sellProperty(heldProperty);
        }
    }
    boolean playerOwns(Player player) {
        return heldProperty.getOwner() == player;
    }
}
