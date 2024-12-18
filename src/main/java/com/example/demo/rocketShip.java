package com.example.demo;

import com.example.demo.HelloApplication;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class rocketShip implements Runnable {

    private final ImageView rocketNode;
    private volatile boolean running = true; // flag to control the thread
    public static HelloApplication.Lane lane = HelloApplication.Lane.MIDDLE_LANE;

    public rocketShip(HelloApplication.Lane lane, int startY) {
        rocketShip.lane = lane;
        rocketNode = new ImageView(new Image("rocketship.png"));
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
            if (lane == HelloApplication.Lane.MIDDLE_LANE) {
                lane = HelloApplication.Lane.LEFT_LANE;
                rocketNode.setX(HelloApplication.Lane.LEFT_LANE.getValue());
            } else if (lane == HelloApplication.Lane.RIGHT_LANE) {
                lane = HelloApplication.Lane.MIDDLE_LANE;
                rocketNode.setX(HelloApplication.Lane.MIDDLE_LANE.getValue());
            }
        });
    }

    public void moveRight() {
        Platform.runLater(() -> {
            if (lane == HelloApplication.Lane.LEFT_LANE) {
                lane = HelloApplication.Lane.MIDDLE_LANE;
                rocketNode.setX(HelloApplication.Lane.MIDDLE_LANE.getValue());
            } else if (lane == HelloApplication.Lane.MIDDLE_LANE) {
                lane = HelloApplication.Lane.RIGHT_LANE;
                rocketNode.setX(HelloApplication.Lane.RIGHT_LANE.getValue());
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