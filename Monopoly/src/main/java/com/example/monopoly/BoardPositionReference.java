package com.example.monopoly;

class BoardPositionReference {
    // Save a list of all space positions relative to the top left corner. The first space is the bottom right GO space.
    private static int[] deltaXList = new int[]{
        750, 663, 597, 532, 465,   400, 334, 269, 203, 137,
            50, 50, 50, 50, 50,   50, 50, 50, 50, 50,
            50, 137, 203, 269, 334,   400, 465, 532, 597, 663,
            750, 750, 750, 750, 750,  750, 750, 750, 750, 750,
    };
    private static int[] deltaYList = new int[]{
        750, 750, 750, 750, 750,   750, 750, 750, 750, 750, 750,
            663, 597, 532, 465, 400,  334, 269, 203, 137, 50,
            50, 50, 50, 50, 50,    50, 50, 50, 50, 50,
            137, 203, 269, 334, 400,    465, 532, 597, 663
    };

    static int getDX(int index) {
        int dX = 0;
        if (index >= 0 && index < deltaXList.length) {
            dX = deltaXList[index];
        }
        return dX;
    }

    static int getDY(int index) {
        int dY = 0;
        if (index >= 0 && index < deltaYList.length) {
            dY = deltaYList[index];
        }
        return dY;
    }
}
