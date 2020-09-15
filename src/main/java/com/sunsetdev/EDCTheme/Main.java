package main.java.com.sunsetdev.EDCTheme;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static Stage STAGE;

    @Override
    public void start(Stage stage) {
        STAGE = stage;
        try {
            String resource = "resources/view/Main.fxml";
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(resource)));
            stage.setScene(new Scene(root));
            stage.setTitle("EDCTheme V1.1");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("resources/images/edctimg.png"))));
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> System.exit(0));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


}
