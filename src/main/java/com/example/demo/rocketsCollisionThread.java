package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.demo.scoreRectangle.*;

public class rocketsCollisionThread implements Runnable {
    Pane gamePane;
    rocketShip rocketShip;
    ArrayList<rocket> rockets;
    private volatile boolean running = true;

    rocketsCollisionThread(rocketShip rocketShip, ArrayList<rocket> rockets) {
        this.rocketShip = rocketShip;
        this.rockets = rockets;
        gamePane = rockets.getFirst().getGamePane();
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
                                try {
                                    rockets.get(finalI).getGamePane().getChildren().remove(rockets.get(finalI).getRocketNode());
                                    rockets.get(finalI).stopThread();
                                    rockets.remove(finalI);

                                }catch(IndexOutOfBoundsException e){

                                }

                                scoreRectangle.incrementScore(-50);
                                if(scoreRectangle.getHearts().size()>1) {
                                    reduceHeart();
                                    scoreRectangle r = (scoreRectangle) gamePane.getChildren().get(2);
                                    if(r.getChildren().size()>1) {
                                        r.getChildren().removeLast();
                                    }

                                }
                                else if(scoreRectangle.getHearts().size()==1){
                                    reduceHeart();
                                    running = false;
                                    try {
                                        gamePane.getChildren().clear();
                                        gamePane.getChildren().add(FXMLLoader.load(Objects.requireNonNull(HelloApplication.class.getResource("GameOver.fxml"))));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }


                            });

                        }
                        if (currentRocket.checkOutOfBound()){
                            int finalI1 = i;
                            Platform.runLater(() ->currentRocket.getGamePane().getChildren().remove(currentRocket.getRocketNode()));
                            currentRocket.stopThread();
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
        for (rocket rocket : rockets) {
            gamePane.getChildren().remove(rocket.getRocketNode());
            rocket.stopThread();
        }
        rocketShip.stop();
        resetScore();
        WeatherLogicThread.stopWeather();
    }
}
