package com.example.demo;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class collisionThread implements Runnable {
    Pane gamePane;
    rocketShip rocketShip;
    ArrayList<rocket> rockets;
    private volatile boolean running = true;
    scoreRectangle score = new scoreRectangle(20,20,100,20);

    collisionThread(rocketShip rocketShip, ArrayList<rocket> rockets) {
        this.rocketShip = rocketShip;
        this.rockets = rockets;
        gamePane = rockets.getFirst().getGamePane();
        gamePane.getChildren().add(score);// flag to control the thread
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(16);
                    for (int i = 0; i < rockets.size(); i++) {
                        rocket currentRocket = rockets.get(i);
                        boolean collision = currentRocket.checkCollision(rocketShip);
                        if (collision) {
                            int finalI = i;
                            Platform.runLater(() -> {
                                rockets.get(finalI).getGamePane().getChildren().remove(rockets.get(finalI).getRocketNode());
                                rockets.remove(finalI);
                                score.incrementScore(50);

                            });

                        }
                        if (currentRocket.checkOutOfBound()){
                            int finalI1 = i;
                            Platform.runLater(() ->currentRocket.getGamePane().getChildren().remove(currentRocket.getRocketNode()));
                            rockets.remove(currentRocket);
                        }
                        if(rockets.size() <= 1){
                            rocket newRocket = new rocket(20000, gamePane);
                            rockets.add(newRocket);
                            new Thread(newRocket).start();
                        }
                    }
                }// ~60 FPS update rate
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
