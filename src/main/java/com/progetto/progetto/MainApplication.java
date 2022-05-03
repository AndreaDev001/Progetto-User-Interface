package com.progetto.progetto;

import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        //CREATE SQL CONNECTION
        SQLGetter.getInstance().getMySQL().Connect();

        SceneHandler.getInstance().init(stage);

        //DISCONNECT WHEN APP IS CLOSED
        stage.setOnCloseRequest(event -> {
            if (!event.isConsumed())
                SQLGetter.getInstance().getMySQL().Disconnect();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
