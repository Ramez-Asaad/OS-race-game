package com.example.demo;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.awt.*;

public class scoreRectangle extends AnchorPane {
    int score = 0;
    Label scoreLabel = new Label("Score: " + score);

    public scoreRectangle(int x, int y, int width, int height) {
        this.setPrefSize(width, height);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setBackground(Background.fill(Color.AZURE));
        this.getChildren().add(scoreLabel);
    }

    synchronized public void incrementScore(int amount) {
        score += amount;
        scoreLabel.setText("Score: " + score);

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
