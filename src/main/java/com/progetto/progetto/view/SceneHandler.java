package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneHandler {

    private static final SceneHandler handler = new SceneHandler();

    public static SceneHandler getInstance() {
        return handler;
    }

    private Stage stage;
    private Scene scene;


    private SceneHandler(){}

    //with this we initialize a new stage
    public void init(Stage stage)
    {
        this.stage = stage;
        this.loadLoginScene();
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    private <T> T loadRootFromFXML(String resourceName) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(resourceName));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            createAlertMessage("Error","Something went wrong while loading a scene!", Alert.AlertType.ERROR);
        }
        return null;
    }

    public void createAlertMessage(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType,message);
        alert.setTitle(title);
        alert.setAlertType(alertType);
        alert.show();
    }

    //---------------------------SCENES------------------------------//

    public void loadLoginScene()
    {
        Parent root = loadRootFromFXML("MainView.fxml");
        if(root == null)
            return;
        if(this.scene == null)
            this.scene = new Scene(root);
        this.scene.setRoot(root);
        stage.setTitle("Login Scene");
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinWidth());
    }



}
