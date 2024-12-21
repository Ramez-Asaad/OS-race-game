package com.example.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;
public class HomePage {
    public AnchorPane mainPane;
    @FXML
    private Button PlayButton;
    @FXML
    private Button QuitButton;
    @FXML
    private Button SettingsButton;
    @FXML
    private void HyperLinkAction(ActionEvent event) {
        try {

            String url = "https://github.com/Ramez-Asaad/OS-race-game";

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        setupButtonHover(PlayButton);
        setupButtonHover(QuitButton);
        setupButtonHover(SettingsButton);
       QuitButton.setOnAction(this::quitApplication);

       PlayButton.setOnAction((ActionEvent event) -> {
            HelloApplication.startGame();
       });
    }
    private void setupButtonHover(Button button) {
        Text arrow = new Text("→");
        arrow.setStyle("-fx-fill: #ffcc00; -fx-font-size: 20px;");
        arrow.setVisible(false);
        arrow.setTranslateY(-5);
        button.setGraphic(arrow);
        button.setStyle("-fx-alignment: center-left;");
        button.setOnMouseEntered(event -> arrow.setVisible(true));
        button.setOnMouseExited(event -> arrow.setVisible(false));    }
    private void quitApplication(ActionEvent event) {
        System.exit(0);
    }
}
