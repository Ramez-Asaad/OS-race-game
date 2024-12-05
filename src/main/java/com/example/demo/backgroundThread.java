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
                    boolean collision = rockets.get(0).checkCollision(rocketShip);
                    if(collision){
                        System.out.println(collision);
                        rockets.get(0).getGamePane().getChildren().remove(rockets.get(0));
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
