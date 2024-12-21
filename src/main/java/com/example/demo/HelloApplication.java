package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class HelloApplication extends Application {
    public enum Lane {
        LEFT_LANE(50),
        MIDDLE_LANE(150),
        RIGHT_LANE(250);

        private final int value;

        Lane(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    private static Stage primaryStage;
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int BACKGROUND_WIDTH = 400;
    private static final int BACKGROUND_HEIGHT = 1000;

    static Scheduler scheduler = new Scheduler(50);
    static rocketShip rocketShip = new rocketShip(Lane.MIDDLE_LANE, 280);
    static ArrayList<rocket> rockets = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        HelloApplication.primaryStage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("HomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        primaryStage.setTitle("Space Game");
        primaryStage.setScene(scene);
        primaryStage.show();




    }


    public static void main(String[] args) {
        launch();
    }

    public static Pane loadBg(Stage primaryStage) {
        Pane root = new Pane();
        Image backgroundImage = new Image("03d76512-ee4f-473e-a8d7-e85eec101786.jpg"); // Replace with your image path

        // Create two ImageView instances for scrolling
        ImageView background1 = new ImageView(backgroundImage);
        ImageView background2 = new ImageView(backgroundImage);

        // position the second image below the first one
        background1.setFitWidth(BACKGROUND_WIDTH);
        background1.setFitHeight(BACKGROUND_HEIGHT);

        background2.setFitWidth(BACKGROUND_WIDTH);
        background2.setFitHeight(BACKGROUND_HEIGHT);
        background2.setLayoutY(-BACKGROUND_HEIGHT);

        root.getChildren().addAll(background1, background2);


        Timeline timeline = getTimeline(background1, background2);
        timeline.play();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scoreRectangle score = new scoreRectangle(20,20,100,20);
        root.getChildren().add(score);

        primaryStage.setTitle("Space Racing Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        return root;
    }

    private static @NotNull Timeline getTimeline(ImageView background1, ImageView background2) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), _ -> {
            // Move both images down
            background1.setLayoutY(background1.getLayoutY() + com.example.demo.rocketShip.speed);
            background2.setLayoutY(background2.getLayoutY() + com.example.demo.rocketShip.speed);


            // Reset position when an image scrolls out of view
            if (background1.getLayoutY() >= WINDOW_HEIGHT) {
                background1.setLayoutY(background2.getLayoutY() - BACKGROUND_HEIGHT);
            }
            if (background2.getLayoutY() >= WINDOW_HEIGHT) {
                background2.setLayoutY(background1.getLayoutY() - BACKGROUND_HEIGHT);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public static void startGame(){
        primaryStage.close();
        Pane scene = loadBg(primaryStage);


        //create the round-robin scheduler for the bot rockets
        rocket r1 = new rocket(200000, scene);
        rocket r2 = new rocket(200000, scene);

        scheduler.addRocket(r1);
        scheduler.addRocket(r2);

        new Thread(r1).start();
        new Thread(r2).start();

        scheduler.start();

        //function to create and start the player's rocket thread
        loadRocket(rocketShip, scene.getScene());
        rockets.add(r1);
        rockets.add(r2);

        new Thread(new rocketsCollisionThread(rocketShip,rockets)).start();
        new Thread(new powerUpsCollisionThread(rocketShip,scene)).start();
        new Thread(new WeatherLogicThread(new Label(),rocketShip,scene)).start();
    }

    public static void loadRocket(rocketShip rocketShip, Scene scene) {
        //get root pane to put the rocket on
        Pane pane = (Pane) scene.getRoot();
        pane.getChildren().add(rocketShip.getNode());


        //create a thread for the rocket
        Thread t1 = new Thread(rocketShip);
        t1.start();

        //set what happens when the payer clicks on left and right arrows
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> rocketShip.moveLeft();
                case RIGHT -> rocketShip.moveRight();
                case ESCAPE -> System.exit(0);
            }
        });
    }
}
