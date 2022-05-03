package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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


    private SceneHandler(){}

    //with this we initialize a new stage
    public void init(Stage stage)
    {
        this.stage = stage;
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

    private String currentPage;

    public void loadPage(StackPane stackPane,String pageToLoad)
    {
        Parent newPage = loadRootFromFXML(pageToLoad);
        stackPane.getChildren().add(newPage);
        Node currentNode = stackPane.getChildren().get(0);

        if(currentPage == null || currentPage.equals(pageToLoad))
        {
            currentPage = pageToLoad;
            return;
        }
        currentPage = pageToLoad;

        newPage.setTranslateX(stage.getWidth());


        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(newPage.translateXProperty(),0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(450),keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(event -> stackPane.getChildren().remove(currentNode));
        timeline.play();



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
        stage.hide();
        Parent root = loadRootFromFXML("MenuView.fxml");
        if(root == null)
            return;
        if(this.scene == null) {
            this.scene = new Scene(root);
            StyleHandler.getInstance().init(this.scene);
        }
        this.scene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(this.scene);
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
        Stage filmStage = new Stage(StageStyle.DECORATED);
        Parent root = loadRootFromFXML("FilmView.fxml");
        if(root == null)
            return;
        Scene filmScene = new Scene(root);
        filmScene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(filmScene);
        filmScene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("css" + "/" + "FilmView.css")).toExternalForm());
        filmScene.setRoot(root);
        filmStage.setTitle("Film View");
        filmStage.setResizable(false);
        filmStage.setScene(filmScene);
        filmStage.setWidth(640);
        filmStage.setHeight(480);
        centerStage(filmStage,filmStage.getWidth(),filmStage.getHeight());
        filmStage.show();
    }
    public void centerStage(Stage stage,double width,double height)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
}