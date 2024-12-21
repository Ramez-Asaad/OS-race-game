package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Application.launch;

public class GameOver implements Initializable {
    public AnchorPane mainPane;
    public Button quitBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        quitBtn.setOnAction(e -> {
            System.exit(0);
        });

    }
}
