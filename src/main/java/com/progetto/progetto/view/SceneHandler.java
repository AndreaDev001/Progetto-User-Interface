package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Objects;

public class SceneHandler {

    private static final SceneHandler handler = new SceneHandler();

    public static SceneHandler getInstance() {
        return handler;
    }

    private Stage stage;
    private Scene scene;
    private Stage filmStage;
    private Scene filmScene;


    private SceneHandler(){}

    //with this we initialize a new stage
    public void init(Stage stage)
    {
        this.stage = stage;
        this.filmStage = new Stage(StageStyle.DECORATED);
        this.loadMainScene();
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

    public Alert createAlertMessage(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType,message);
        alert.setTitle(title);
        alert.setAlertType(alertType);
        alert.showAndWait();
        return alert;
    }

    //---------------------------SCENES------------------------------//
    public void loadMainScene()
    {
        this.filmStage.hide();
        stage.hide();
        Parent root = loadRootFromFXML("MainView.fxml");
        if(root == null)
            return;
        if(this.scene == null) {
            this.scene = new Scene(root);
            StyleHandler.getInstance().init(this.scene);
        }
        this.scene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(this.scene);
        this.scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css" + "/" + "MainView.css")).toExternalForm());
        this.scene.setRoot(root);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setTitle("Main Scene");
        centerStage(stage,1280,720);
        stage.show();
        stage.setWidth(1280);
        stage.setHeight(720);
    }

    public void loadSettingsScene()
    {
        this.filmStage.hide();
        stage.hide();
        Parent root = loadRootFromFXML("SettingsView.fxml");
        if(root == null)
            return;
        if(this.scene == null)
            this.scene = new Scene(root);

        this.scene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(this.scene);
        this.scene.setRoot(root);
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setResizable(true);
        stage.setTitle("Settings");
        centerStage(stage,stage.getWidth(),stage.getHeight());
        stage.show();
    }

    public void loadLoginScene()
    {
        this.filmStage.hide();
        Parent root = loadRootFromFXML("LoginView.fxml");
        if(root == null)
            return;
        StyleHandler.getInstance().updateScene(scene);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css" + "/" + "LoginView.css")).toExternalForm());
        scene.setRoot(root);
        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.setTitle("Login View");
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setResizable(false);
        centerStage(stage,stage.getWidth(),stage.getHeight());
        stage.setScene(scene);
        stage.show();
    }

    public void loadRegisterScene()
    {
        Parent root = loadRootFromFXML("RegistryView.fxml");
        if(root == null)
            return;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(scene);
        scene.setRoot(root);

        stage.setMinWidth(500);
        stage.setMinHeight(300);
        stage.setTitle("Registry View");
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void loadFilmScene()
    {
        Parent root = loadRootFromFXML("FilmView.fxml");
        if(root == null)
            return;
        if(this.filmScene == null)
            this.filmScene = new Scene(root);
        this.filmScene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(this.filmScene);
        this.filmScene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css" + "/" + "FilmView.css")).toExternalForm());
        this.filmScene.setRoot(root);
        this.filmStage.setTitle("Film View");
        this.filmStage.setResizable(false);
        this.filmStage.setScene(this.filmScene);
        this.filmStage.setWidth(640);
        this.filmStage.setHeight(480);
        centerStage(this.filmStage,this.filmStage.getWidth(),this.filmStage.getHeight());
        this.filmStage.show();
    }
    public void centerStage(Stage stage,double width,double height)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
}