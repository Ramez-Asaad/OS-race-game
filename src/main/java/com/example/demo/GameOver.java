package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameOver implements Initializable {
    public AnchorPane mainPane;
    public Button quitBtn;
    @FXML
    Button playAgainBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playAgainBtn.setOnAction(e -> {
            mainPane = null;
            HelloApplication.startGame();
        });
        quitBtn.setOnAction(e -> {
            System.exit(0);
        });

    }
}
