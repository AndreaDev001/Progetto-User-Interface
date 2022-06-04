package com.progetto.progetto;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.view.SceneHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneHandler.getInstance().init(stage);
        try {
            Client.getInstance().register("pier.altimari@libero.it","ciaooo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
