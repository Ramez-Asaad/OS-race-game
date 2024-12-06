package com.example.demo;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class backgroundThread implements Runnable {
    Pane gamePane;
    HelloApplication.rocketShip rocketShip;
    ArrayList<HelloApplication.rocket> rockets;
    private volatile boolean running = true; // flag to control the thread
    backgroundThread(HelloApplication.rocketShip rocketShip, ArrayList<HelloApplication.rocket> rockets) {
        this.rocketShip = rocketShip;
        this.rockets = rockets;
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(16);
                if(rockets.size() == 1){
                    boolean collision = rockets.getFirst().checkCollision(rocketShip);
                    if(collision){
                        System.out.println(collision);
                        Platform.runLater(() -> rockets.getFirst().getGamePane().getChildren().remove(rockets.getFirst().getRocketNode()));
                        break;
                    }
                } else if (rockets.size() > 1) {
                    for (HelloApplication.rocket rocket : rockets) {
                        boolean collision = rocket.checkCollision(rocketShip);
                        if (collision) {
                            rocket.getGamePane().getChildren().remove(rocket);
                        }
                    }
                }// ~60 FPS update rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
