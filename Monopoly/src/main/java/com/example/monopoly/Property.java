package com.example.monopoly;

public class Property {

    private int propertyID;
    private String propertyName;
    private int basePrice;
    private int baseUpgradeCost;
    private int baseRentalCharge;
    private int currentTier;
    private int colorCategory;
    private Player owner;
    private int occupyingSpace;

    public Property(int ID, String name, int price, int rental, int upgradeCost, int tier, int color, int occupyingSpace) {
        propertyID = ID;
        propertyName = name;
        basePrice = price;
        baseUpgradeCost = upgradeCost;
        baseRentalCharge = rental;
        currentTier = tier;
        colorCategory = color;
        this.occupyingSpace = occupyingSpace;
    }

    // Called by the Player to officially possess the property
    void changeOwner(Player newOwner) {
        owner = newOwner;
    }

    // Upgrade the property
    void upgradeTier() {
        currentTier += 1;
    }

    // Appropriate getters
    public int getPropertyID() { return propertyID; }
    public String getPropertyName() { return propertyName; }
    public int getPrice() { return basePrice * (currentTier + 1); }
    public int getSellPrice() { return Math.max(basePrice, basePrice + baseUpgradeCost * (currentTier)); }
    public int getUpgradeCost() { return baseUpgradeCost * (currentTier + 1); }
    public int getRentalCharge() {
        if (currentTier >= 1) {
            return baseRentalCharge * currentTier;
        } else {
            return baseRentalCharge / 4;
        }
    }
    public int getCurrentTier() { return currentTier; }
    public int getColorCategory() { return colorCategory; }
    public int getOccupyingSpace() { return occupyingSpace; }
    Player getOwner() { return owner; }

    public String toString() {
        return propertyName;
    }
}
