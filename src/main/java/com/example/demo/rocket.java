package com.example.demo;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class rocket implements Runnable{
    private final ArrayList<ImageView> rocketNodes = new ArrayList<>();
    private final ImageView rocketNode;
    private boolean running = true;
    private final Pane gamePane;
    private HelloApplication.Lane lane;
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

    public HelloApplication.Lane getRandomLane(){
        HelloApplication.Lane myLane = null;
        int x = (int)(Math.random() * 300);
        if (x > 50 && x < 150) {
            myLane = HelloApplication.Lane.RIGHT_LANE;
        } else if (x >=150 && x<250) {
            myLane = HelloApplication.Lane.MIDDLE_LANE;
        } else if (x>=250 && x<350 ) {
            myLane = HelloApplication.Lane.LEFT_LANE;
        } else myLane = getRandomLane();
        return myLane;
    }
    public void moveRandom(HelloApplication.Lane l){
        int direction = Math.random() < 0.5 ? 1 : -1; // Decide direction (left or right)

        switch (l) {
            case RIGHT_LANE:
                this.lane = HelloApplication.Lane.MIDDLE_LANE;
                break;
            case MIDDLE_LANE:
                this.lane = direction == 1 ? HelloApplication.Lane.LEFT_LANE : HelloApplication.Lane.RIGHT_LANE;
                break;
            case LEFT_LANE:
                this.lane = HelloApplication.Lane.MIDDLE_LANE;
                break;
        }

        // Update the rocket's position on the screen
        Platform.runLater(() -> rocketNode.setX(this.lane.getValue()+15));

    }

    public rocket(int time,Pane gamePane) {
        rocketNodes.add(new ImageView("rocketlvl1.png"));
        rocketNodes.add(new ImageView("rocketlvl2.png"));
        rocketNodes.add(new ImageView("rocketlvl3.png"));
        this.lane = getRandomLane();
        this.positionY = -45;
        this.time = time;

       double randomRocket = Math.random();
       if (randomRocket < 0.3) {
           this.rocketNode = rocketNodes.get(0);
       }else if(randomRocket >0.3 && randomRocket< 0.7){
           this.rocketNode = rocketNodes.get(1);
       }else{
           this.rocketNode = rocketNodes.get(2);
        }

        rocketNode.setFitWidth(70);
        rocketNode.setFitHeight(70);
        rocketNode.setX(lane.getValue()+15);
        rocketNode.setY(positionY);

        this.gamePane = gamePane;


    }

    private int frameCounter = 0; // Counter to control movement frequency
    private final int moveInterval = 67; // Move every 100 frames

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        rocket rocket = (rocket) o;
        return running == rocket.running && getPositionY() == rocket.getPositionY() && time == rocket.time && frameCounter == rocket.frameCounter && moveInterval == rocket.moveInterval && Objects.equals(rocketNode, rocket.rocketNode) && Objects.equals(getGamePane(), rocket.getGamePane()) && lane == rocket.lane && Objects.equals(timer, rocket.timer);
    }

    public ImageView getRocketNode(){
        return rocketNode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rocketNode, running, getGamePane(), lane, getPositionY(), time, timer, frameCounter, moveInterval);
    }

    public void moveRocket() {
        Platform.runLater(() -> {
            positionY += 1;
            rocketNode.setY(positionY);
        });
    }
    public boolean checkCollision(rocketShip k){
        double range = 80.0; // The range within which a collision is detected

        // Check if the X-coordinate of `k` is within the range of `this`
        boolean xCollision = Math.abs(k.getX() - this.getX()) <= range;

        // Check if the Y-coordinate of `k` is within the range of `this`
        boolean yCollision = Math.abs((k.getY()) - (this.getY()-33)) <= range;

        // Return true if both X and Y are within the range
        return xCollision && yCollision;
    }
    public boolean checkOutOfBound(){
        double y = this.getY();
        if(y>gamePane.getHeight() || y< -50){
            return true;
        }
        else return false;
    }
    @Override
    public void run() {
        Platform.runLater(() -> gamePane.getChildren().add(rocketNode));


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    gamePane.getChildren().remove(rocketNode);
                });
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
