package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class HelloApplication extends Application {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int BACKGROUND_WIDTH = 400;
    private static final int BACKGROUND_HEIGHT = 1000;
    private static final int SCROLL_SPEED = 2; // Speed of scrolling in pixels per frame

    @Override
    public void start(Stage primaryStage) {
        Scene scene = loadBg(primaryStage);

        Scheduler scheduler = new Scheduler(50);
         rocket r1 = new rocket(20000, (Pane)scene.getRoot());
         rocket r2 = new rocket(2000, (Pane)scene.getRoot());
         rocket r3 = new rocket(1000, (Pane)scene.getRoot());

         scheduler.addRocket(r1);
         scheduler.addRocket(r2);
         scheduler.addRocket(r3);

         new Thread(r1).start();
         new Thread(r2).start();
         new Thread(r3).start();

         scheduler.start();

        loadRocket(scene, 300, 100);


    }

    public static void main(String[] args) {
        launch();
    }

    public Scene loadBg(Stage primaryStage) {
        Pane root = new Pane();

        // Load the background image
        Image backgroundImage = new Image("demo-bg.png"); // Replace with your image path

        // Create two ImageView instances for seamless scrolling
        ImageView background1 = new ImageView(backgroundImage);
        ImageView background2 = new ImageView(backgroundImage);

        // Position the second image below the first one
        background1.setFitWidth(BACKGROUND_WIDTH);
        background1.setFitHeight(BACKGROUND_HEIGHT);

        background2.setFitWidth(BACKGROUND_WIDTH);
        background2.setFitHeight(BACKGROUND_HEIGHT);
        background2.setLayoutY(-BACKGROUND_HEIGHT); // Place it above the first image

        // Add the images to the pane
        root.getChildren().addAll(background1, background2);

        // Create the scrolling animation
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), event -> {
            // Move both images down
            background1.setLayoutY(background1.getLayoutY() + SCROLL_SPEED);
            background2.setLayoutY(background2.getLayoutY() + SCROLL_SPEED);

            // Reset position when an image scrolls out of view
            if (background1.getLayoutY() >= WINDOW_HEIGHT) {
                background1.setLayoutY(background2.getLayoutY() - BACKGROUND_HEIGHT);
            }
            if (background2.getLayoutY() >= WINDOW_HEIGHT) {
                background2.setLayoutY(background1.getLayoutY() - BACKGROUND_HEIGHT);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Infinite Scrolling Background");
        primaryStage.setScene(scene);
        primaryStage.show();
        return scene;
    }

    public void loadRocket(Scene scene, int y, int speed) {
        Pane pane = (Pane) scene.getRoot();
        rocketShip rocket = new rocketShip(Lane.MIDDLE_LANE, y, speed);
        pane.getChildren().add(rocket.rocketNode);
        Thread t1 = new Thread(rocket);
        t1.start();
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> rocket.moveLeft();
                case RIGHT -> rocket.moveRight();
            }
        });
    }

    public static class rocketShip implements Runnable {

        private final ImageView rocketNode;
        private final int speed;
        private volatile boolean running = true; // Flag to control the thread
        private final int windowWidth = 400; // Width of the window
        public static Lane lane = Lane.MIDDLE_LANE;

        public rocketShip(Lane lane, int startY, int speed) {
            rocketShip.lane = lane;
            this.speed = speed;
            rocketNode = new ImageView(new Image("img.png")); // Replace with your image path
            rocketNode.setFitWidth(100); // Adjust size
            rocketNode.setFitHeight(100);
            rocketNode.setX(lane.getValue());
            rocketNode.setY(startY);
            rocketNode.setVisible(true);
        }

        public void moveLeft() {
            Platform.runLater(() -> {
                if (lane == Lane.MIDDLE_LANE) {
                    lane = Lane.LEFT_LANE;
                    rocketNode.setX(Lane.LEFT_LANE.getValue());
                } else if (lane == Lane.RIGHT_LANE) {
                    lane = Lane.MIDDLE_LANE;
                    rocketNode.setX(Lane.MIDDLE_LANE.getValue());
                }
            });
        }

        public void moveRight() {
            Platform.runLater(() -> {
                if (lane == Lane.LEFT_LANE) {
                    lane = Lane.MIDDLE_LANE;
                    rocketNode.setX(Lane.MIDDLE_LANE.getValue());
                } else if (lane == Lane.MIDDLE_LANE) {
                    lane = Lane.RIGHT_LANE;
                    rocketNode.setX(Lane.RIGHT_LANE.getValue());
                }
            });
        }

        public ImageView getNode() {
            return rocketNode;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    // Placeholder for continuous updates (e.g., animations or AI logic)
                    Thread.sleep(16); // ~60 FPS update rate
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                // Any real-time updates can be done here
            }
        }

        public void stop() {
            running = false;
        }


    }

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

    public static class Scheduler {
        private final Queue<rocket> rocketQueue = new LinkedList<>();
        private final int timeQuantum; // Time slice in milliseconds
        private boolean isRunning = true;

        public Scheduler(int timeQuantum) {
            this.timeQuantum = timeQuantum;
        }

        public void addRocket(rocket rocket) {
            rocketQueue.add(rocket);

        }

        public void start() {
            new Thread(() -> {
                while (isRunning) {
                    if (!rocketQueue.isEmpty()) {
                        rocket rocket = rocketQueue.poll(); // Get the next car
                        synchronized (rocket) {
                            rocket.notify(); // Activate the car thread
                        }
                        try {
                            Thread.sleep(timeQuantum); // Allow the car to run for the time slice
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        rocketQueue.offer(rocket); // Re-add the car to the end of the queue
                    }
                }
            }).start();
        }

    }
    public static class rocket implements Runnable{
            private final ImageView rocketNode;
            private boolean running = true;
            private final Pane gamePane;
            private Lane lane;
            private int positionY; // Car's position
            int time;
            Timer timer;

            public Lane getRandomLane(){
                Lane myLane = null;
                int x = (int)(Math.random() * 300);
                if (x > 50 && x < 150) {
                    myLane = Lane.RIGHT_LANE;
                } else if (x >=150 && x<250) {
                    myLane = Lane.MIDDLE_LANE;
                } else if (x>=250 && x<350 ) {
                    myLane = Lane.LEFT_LANE;
                } else myLane = getRandomLane();
                return myLane;
            }
            public void moveRandom(Lane l){
                int direction = Math.random() < 0.5 ? 1 : -1; // Decide direction (left or right)

                switch (l) {
                    case RIGHT_LANE:
                        this.lane = Lane.MIDDLE_LANE; // Right can only move to Middle
                        break;
                    case MIDDLE_LANE:
                        this.lane = direction == 1 ? Lane.LEFT_LANE : Lane.RIGHT_LANE; // Middle can move to Left or Right
                        break;
                    case LEFT_LANE:
                        this.lane = Lane.MIDDLE_LANE; // Left can only move to Middle
                        break;
                }

                // Update the rocket's position on the screen
                Platform.runLater(() -> rocketNode.setX(this.lane.getValue()));

            }

            public rocket(int time,Pane gamePane) {
                this.lane = getRandomLane();
                this.positionY = 100 + (int)(Math.random()*300);
                this.time = time;

                this.rocketNode = new ImageView(new Image("img.png"));
                rocketNode.setFitWidth(70);
                rocketNode.setFitHeight(70);
                rocketNode.setX(lane.getValue());
                rocketNode.setY(positionY);

                this.gamePane = gamePane;
            }

            public void moveRocket() {
                // Define the car's movement logic
                if (Math.random() < 0.01) { // 1% chance per frame to change lanes
                    moveRandom(this.lane);
                }
                // Move the rocket downward
            }

            @Override
            public void run() {
               Platform.runLater(() -> gamePane.getChildren().add(rocketNode));

               timer = new Timer();
               timer.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       Platform.runLater(() -> gamePane.getChildren().remove(rocketNode));
                       running = false;
                       timer.cancel();
                   }
               },time);

               while (running) {
                   try {
                       Thread.sleep(16);
                      moveRocket();
               }catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                   }
                }
            }

            public int getPositionY() {
                return positionY;
            }
        }
}