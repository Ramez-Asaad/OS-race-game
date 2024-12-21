package com.example.demo;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class scoreRectangle extends AnchorPane {
    static int score = 0;
    static Label scoreLabel = new Label("Score: " + score);
    static ImageView heart = new ImageView("heart.png");
    static ArrayList<ImageView> hearts = new ArrayList<>();
    static Label weatherLabel = new Label("Weather: " + score);

    public scoreRectangle(int x, int y, int width, int height) {
        scoreLabel.setStyle("-fx-font-family: 'hooge 05_55';" +
                "-fx-font-size: 18px;" +
                "-fx-text-fill: #ffcc00;" +
                "-fx-effect: dropshadow(gaussian, #000000, 5, 0.5, 2, 2);" +
                "fx-border-color: #ffffff;");
        this.setPrefSize(width, height);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setBackground(Background.EMPTY);

        ImageView heart1 = new ImageView("heart.png");
        ImageView heart2 = new ImageView("heart.png");
        ImageView heart3 = new ImageView("heart.png");

        heart1.setFitHeight(20);
        heart2.setFitHeight(20);
        heart3.setFitHeight(20);

        heart1.setFitWidth(20);
        heart2.setFitWidth(20);
        heart3.setFitWidth(20);

        heart1.setX(280);
        heart2.setX(310);
        heart3.setX(340);
        hearts.add(heart1);
        hearts.add(heart2);
        hearts.add(heart3);
        this.getChildren().add(scoreLabel);
        this.getChildren().add(weatherLabel);

        for(ImageView heart:hearts) this.getChildren().add(heart);

    }

    static void resetScore(){
        score = 0;
        scoreLabel.setText("Score: " + score);
    }

    static synchronized public void incrementScore(int amount) {
        score = score + amount > 0 ? score + amount : score;
        scoreLabel.setText("Score: " + score);
    }

    public int getScore() {
        return score;
    }

    public static ArrayList<ImageView> getHearts() {
        return hearts;
    }

    synchronized public static void reduceHeart() {
        if(hearts.size()>1) {
            Platform.runLater(()->{hearts.removeLast();});
        }

        else return ;

    }

    public void setHearts(ArrayList<ImageView> hearts) {
        this.hearts = hearts;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateScore(){
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public String toString() {
        return "scoreRectangle{} " + super.toString();
    }
}
