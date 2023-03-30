package com.example.monopoly;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class PlayerIconTransition extends Transition {
    {
        setCycleDuration(Duration.millis(3500));
    }
    protected Circle target;
    protected int startX, startY, endX, endY;
    protected void interpolate(double frac) {
        // Circle will only move during the last quarter of the animation.
        if (frac > 0.75) {
            int diffX = endX - startX;
            int diffY = endY - startY;

            double truePercent = (frac - 0.75) * 4;
            double newX = startX + (diffX * truePercent);
            double newY = startY + (diffY * truePercent);

            target.setCenterX(newX);
            target.setCenterY(newY);
        }
    }

    public PlayerIconTransition(Circle target, int startX, int startY, int endX, int endY, OnFinishedAttachment lambda) {
        this.target = target;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        this.setOnFinished(event -> {
            target.setCenterX(endX);
            target.setCenterY(endY);
            lambda.run();
        });
        this.setInterpolator(Interpolator.EASE_OUT);
    }
}
