package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
            e.printStackTrace();
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
    public void loadMainScene()
    {
        stage.hide();
        Parent root = loadRootFromFXML("MainView.fxml");
        if(root == null)
            return;
        if(this.scene == null)
            this.scene = new Scene(root);
        this.scene.getStylesheets().clear();
        this.scene.setRoot(root);
        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setTitle("Main Scene");
        centerStage(stage.getWidth(),stage.getHeight());
        stage.show();
    }
    public void loadLoginScene()
    {
        Parent root = loadRootFromFXML("LoginView.fxml");
        if(root == null)
            return;
        if(this.scene == null)
            this.scene = new Scene(root);
        this.scene.getStylesheets().clear();
        this.scene.setRoot(root);
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.setTitle("Login View");
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setResizable(false);
        centerStage(stage.getWidth(),stage.getHeight());
    }
    public void centerStage(double width,double height)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
}
