package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int BACKGROUND_WIDTH = 400;
    private static final int BACKGROUND_HEIGHT = 1000;
    private static final int SCROLL_SPEED = 2; // Speed of scrolling in pixels per frame

    @Override
    public void start(Stage primaryStage) {
        //start the background scroll animation and set stage
        Scene scene = loadBg(primaryStage);

        //create the round robin scheduler for the bot rockets
        Scheduler scheduler = new Scheduler(50);
        rocketShip rocketShip = new rocketShip(Lane.MIDDLE_LANE, 300);
         rocket r1 = new rocket(20000, (Pane)scene.getRoot());
         rocket r2 = new rocket(20000, (Pane)scene.getRoot());

         scheduler.addRocket(r1);

         new Thread(r1).start();

         scheduler.start();

         //function to create and start the player's rocket thread
        loadRocket(rocketShip, scene);
        ArrayList<rocket> rockets = new ArrayList<>();
        rockets.add(r1);

        new Thread(new backgroundThread(rocketShip,rockets)).start();
    }

    public static void main(String[] args) {
        launch();
    }

    public Scene loadBg(Stage primaryStage) {
        Pane root = new Pane();

        // Load background image
        Image backgroundImage = new Image("demo-bg.png"); // Replace with your image path

        // Create two ImageView instances for scrolling
        ImageView background1 = new ImageView(backgroundImage);
        ImageView background2 = new ImageView(backgroundImage);

        // position the second image below the first one
        background1.setFitWidth(BACKGROUND_WIDTH);
        background1.setFitHeight(BACKGROUND_HEIGHT);

        background2.setFitWidth(BACKGROUND_WIDTH);
        background2.setFitHeight(BACKGROUND_HEIGHT);
        background2.setLayoutY(-BACKGROUND_HEIGHT);

        // Add images to the pane
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

    public void loadRocket(rocketShip rocketShip, Scene scene) {
        //get root pane to put the rocket on
        Pane pane = (Pane) scene.getRoot();
        rocketShip rocket = rocketShip;
        pane.getChildren().add(rocket.rocketNode);


        //create a thread for the rocket
        Thread t1 = new Thread(rocket);
        t1.start();

        //set what happens when the payer clicks on left and right arrows
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> rocket.moveLeft();
                case RIGHT -> rocket.moveRight();
            }
        });
    }

    public static class rocketShip implements Runnable {

        private final ImageView rocketNode;
        private volatile boolean running = true; // flag to control the thread
        public static Lane lane = Lane.MIDDLE_LANE;

        public rocketShip(Lane lane, int startY) {
            rocketShip.lane = lane;
            rocketNode = new ImageView(new Image("img.png"));
            rocketNode.setFitWidth(100);
            rocketNode.setFitHeight(100);
            rocketNode.setX(lane.getValue());
            rocketNode.setY(startY);
            rocketNode.setVisible(true);
        }
        public double getX(){
            return rocketNode.getX();
        }
        public double getY(){
            return rocketNode.getY();
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
                    Thread.sleep(16); // ~60 FPS update rate
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }

        public void stop() {
            running = false;
        }
    }

    public static class Scheduler { //round-robin schedular
        private final Queue<rocket> rocketQueue = new LinkedList<>();
        private final int timeQuantum; // Time slice in milliseconds
        private boolean isRunning = true;

        public Scheduler(int timeQuantum) {
            this.timeQuantum = timeQuantum;
        }

        public void addRocket(rocket rocket) {
            rocketQueue.add(rocket);
        }
        public void addRockets(ArrayList<rocket> rocketList) {
            for (rocket rocket : rocketList) {
                addRocket(rocket);
            }
        }

        public void start() {
            new Thread(() -> {
                while (isRunning) {
                    if (!rocketQueue.isEmpty()) {
                        rocket rocket = rocketQueue.poll(); // Get the next rocket
                        synchronized (rocket) {
                            rocket.notify(); // Activate the rocket thread
                        }
                        try {
                            Thread.sleep(timeQuantum); // allow the car to rocket for the time slice
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        rocketQueue.offer(rocket); // Re-add the rocket to the end of the queue
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

            public double getX(){
                return rocketNode.getX();
            }
            public double getY(){
                return rocketNode.getY();
            }
            public Pane getGamePane(){
                return gamePane;
            }

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
                        this.lane = Lane.MIDDLE_LANE;
                        break;
                    case MIDDLE_LANE:
                        this.lane = direction == 1 ? Lane.LEFT_LANE : Lane.RIGHT_LANE;
                        break;
                    case LEFT_LANE:
                        this.lane = Lane.MIDDLE_LANE;
                        break;
                }

                // Update the rocket's position on the screen
                Platform.runLater(() -> rocketNode.setX(this.lane.getValue()));

            }

            public rocket(int time,Pane gamePane) {
                this.lane = getRandomLane();
                this.positionY = 0;
                this.time = time;

                this.rocketNode = new ImageView(new Image("img.png"));
                rocketNode.setFitWidth(70);
                rocketNode.setFitHeight(70);
                rocketNode.setX(lane.getValue());
                rocketNode.setY(positionY);

                this.gamePane = gamePane;

            }

        private int frameCounter = 0; // Counter to control movement frequency
        private final int moveInterval = 67; // Move every 100 frames


        public void moveRocket() {
                frameCounter++;

                // Only move randomly after the specified interval
                if (frameCounter >= moveInterval) {
                    frameCounter = 0; // Reset the counter
                    moveRandom(this.lane);
                }
                Platform.runLater(() -> {
                    positionY += 1;
                    rocketNode.setY(positionY);
                });
            }
        public boolean checkCollision(rocketShip k){
            return (k.getX()==this.getX()) && (k.getY()==this.getY());
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
                       //place to check

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

    private int playerLives = 3; // Initial lives of the player

    public void checkCollision(Node playerRocket, Node botRocket) {
        // Get the bounding boxes of the player and bot rockets
        Bounds playerBounds = playerRocket.getBoundsInParent();
        Bounds botBounds = botRocket.getBoundsInParent();

        // Check if the bounding boxes intersect
        if (playerBounds.intersects(botBounds)) {
            handleCollision();
        }
    }

    private void handleCollision() {
        playerLives--; // Decrease the player's lives
        System.out.println("Collision detected! Lives remaining: " + playerLives);

        if (playerLives <= 0) {
            endGame(); // Call the game-over logic
        }
    }

    private void endGame() {
        System.out.println("Game Over!");
        // Add logic to stop the game or show a game-over screen
    }
}
