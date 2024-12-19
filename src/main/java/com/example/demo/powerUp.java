package com.example.demo;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class powerUp implements Runnable {


    private ImageView powerUpNode;
    private final Pane gamePane;
    private HelloApplication.Lane lane;
    private int positionY;
    private int positionX;

    public double getX(){ return powerUpNode.getX(); }
    public double getY(){ return powerUpNode.getY(); }

    public void setLane(HelloApplication.Lane lane) {
        setPositionX(lane.getValue() + 30);
    }
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
    public void setNode(String name) {
        this.powerUpNode = new ImageView(name);
    }

    public HelloApplication.Lane getLane() {
        return lane;
    }

    public powerUp(ImageView powerUpNode, Pane gamePane) {
        final HelloApplication.Lane[] newlane = {rocket.getRandomLane()};
        for (rocket rocket : HelloApplication.rockets) {
            if(rocket.getX() == newlane[0].getValue()) newlane[0] = com.example.demo.rocket.getRandomLane();
            else break;
        }
        lane = newlane[0];
        this.powerUpNode = powerUpNode;
        this.gamePane = gamePane;
        this.positionY = -45;
        powerUpNode.setFitWidth(50);
        powerUpNode.setFitHeight(50);
        powerUpNode.setX(lane.getValue()+35);
        powerUpNode.setY(positionY);
    }

    public ImageView getPowerUpNode() {
        return powerUpNode;
    }

    public void movePowerUP(){
        Platform.runLater(()->{
            positionY += 2;
            powerUpNode.setY(positionY);
        });
    }
    public boolean checkCollision(rocketShip k){
        double range = 50.0; // The range within which a collision is detected

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
        Platform.runLater(() -> gamePane.getChildren().add(powerUpNode));

        while(true){
            try{
                Thread.sleep(16);
                movePowerUP();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

    }
    public void powerUpChange(){

        Platform.runLater(()->{
            setLane(rocket.getRandomLane());
            setPositionY(-30);
            gamePane.getChildren().remove(this.powerUpNode);
            if(powerUpNode.getImage().getUrl().contains("star")) {
                setNode("x-powerup.png");
                powerUpNode.setX(getPositionX());
                powerUpNode.setFitWidth(50);
                powerUpNode.setFitHeight(50);
            }
            else {
                setNode("star.png");
                powerUpNode.setX(getPositionX());
                powerUpNode.setFitWidth(50);
                powerUpNode.setFitHeight(50);
            }
            gamePane.getChildren().add(this.powerUpNode);
        });

    }
}
