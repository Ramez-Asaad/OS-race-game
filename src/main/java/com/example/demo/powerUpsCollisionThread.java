package com.example.demo;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class powerUpsCollisionThread implements Runnable {
    Pane gamePane;
    rocketShip rocketShip;
    ArrayList<powerUp> powerUps;
    String star = "star.png";
    String xPowerUp = "x-powerup.png";
    private volatile boolean running = true;
    powerUp starPower;
    powerUp xPowerUpPower;
    powerUp currentPowerUp;
    public powerUpsCollisionThread(rocketShip rocketShip, Pane gamePane) {
        this.rocketShip = rocketShip;
        this.gamePane = gamePane;
        xPowerUpPower = new powerUp(new ImageView(this.xPowerUp), gamePane );
        starPower = new powerUp(new ImageView(this.star),gamePane);
        currentPowerUp = starPower;
    }

    @Override
    public void run() {
        new Thread(currentPowerUp).start();
        while (running) {
                try {
                    Thread.sleep(16);
                    boolean collision = currentPowerUp.checkCollision(rocketShip);
                    if (collision) {
                        Platform.runLater(() -> {
                            scoreRectangle.incrementScore(100);
                            currentPowerUp.powerUpChange();
                        });
                    }else if(currentPowerUp.checkOutOfBound()) {
                        Platform.runLater(()->{
                            currentPowerUp.powerUpChange();
                        });
                    }
                    if(!rocketShip.isRunning()) running = false;


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

        }
    }

    public Pane getGamePane() {
        return gamePane;
    }
}
