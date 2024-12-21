package com.example.demo;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.Random;

public class WeatherLogicThread extends Thread {
    private final String[] weatherConditions = {"Sun", "Meteors", "Supernova", "Solar Wind"};
    private String currentWeather = "Sun";
    private final Random random = new Random();
    private static volatile boolean running = true;
    private final Label weatherLabel;
    private final rocketShip rocketShip;
    private final Pane gamePane;

    public WeatherLogicThread(Label weatherLabel, rocketShip rocketShip, Pane gamePane) {
        this.weatherLabel = weatherLabel;
        this.rocketShip = rocketShip;
        this.gamePane = gamePane;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(3000 + random.nextInt(3000));

                currentWeather = weatherConditions[random.nextInt(weatherConditions.length)];

                Platform.runLater(() -> {
                    weatherLabel.setText("Current Weather: " + currentWeather);
                    rocketShip.updateRocketSpeed(currentWeather);
                    displayWeatherAnimation(currentWeather);
                });

            } catch (InterruptedException e) {
                System.out.println("Weather thread interrupted");
                running = false;
            }
        }
    }

    private void displayWeatherAnimation(String weather) {
        // Clear any previous weather effects
        gamePane.getChildren().removeIf(node -> node.getUserData() != null && node.getUserData().equals("weatherEffect"));

        switch (weather) {
            case "Sun":
                showSunAnimation();
                break;
            case "Meteors":
                showMeteorAnimation();
                break;
            case "Supernova":
                showSupernovaAnimation();
                break;
            case "Solar Wind":
                showSolarWindAnimation();
                break;
        }
    }

    private void showSunAnimation() {
        Circle sun = new Circle(50, Color.GOLD);
        sun.setLayoutX(400);
        sun.setLayoutY(100);
        sun.setUserData("weatherEffect");

        // Pulsing animation
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), sun);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);

        gamePane.getChildren().add(sun);
        pulse.play();
    }

    private void showMeteorAnimation() {
        for (int i = 0; i < 5; i++) {
            Line meteor = new Line(0, 0, 30, 30);
            meteor.setStroke(Color.ORANGE);
            meteor.setStrokeWidth(3);
            meteor.setLayoutX(100 + random.nextInt(600));
            meteor.setLayoutY(-50);
            meteor.setUserData("weatherEffect");

            // Falling animation
            TranslateTransition fall = new TranslateTransition(Duration.seconds(2 + random.nextDouble()), meteor);
            fall.setToY(400);
            fall.setToX(meteor.getLayoutX() + random.nextInt(200) - 100);
            fall.setOnFinished(event -> gamePane.getChildren().remove(meteor));

            gamePane.getChildren().add(meteor);
            fall.play();
        }
    }

    private void showSupernovaAnimation() {
        Circle supernova = new Circle(30, Color.RED);
        supernova.setLayoutX(400);
        supernova.setLayoutY(200);
        supernova.setUserData("weatherEffect");

        // Glowing and expanding animation
        ScaleTransition expand = new ScaleTransition(Duration.seconds(2), supernova);
        expand.setFromX(1);
        expand.setFromY(1);
        expand.setToX(3);
        expand.setToY(3);

        FadeTransition glow = new FadeTransition(Duration.seconds(2), supernova);
        glow.setFromValue(1.0);
        glow.setToValue(0.0);

        ParallelTransition supernovaEffect = new ParallelTransition(expand, glow);
        supernovaEffect.setOnFinished(event -> gamePane.getChildren().remove(supernova));

        gamePane.getChildren().add(supernova);
        supernovaEffect.play();
    }

    private void showSolarWindAnimation() {
        Line windLine = new Line(0, 0, 200, 0);
        windLine.setStroke(Color.LIGHTBLUE);
        windLine.setStrokeWidth(4);
        windLine.setLayoutX(-200);
        windLine.setLayoutY(150);
        windLine.setUserData("weatherEffect");

        // Wind blowing animation
        TranslateTransition wind = new TranslateTransition(Duration.seconds(3), windLine);
        wind.setToX(800);
        wind.setOnFinished(event -> gamePane.getChildren().remove(windLine));

        gamePane.getChildren().add(windLine);
        wind.play();
    }

    public static void stopWeather() {
        running = false;
    }
}