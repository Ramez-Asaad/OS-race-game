module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.graphics;
    requires annotations;

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}