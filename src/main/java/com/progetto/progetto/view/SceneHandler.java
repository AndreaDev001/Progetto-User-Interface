package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class SceneHandler {

    private static final SceneHandler handler = new SceneHandler();

    public static SceneHandler getInstance() {
        return handler;
    }

    private Stage stage;
    private Scene scene;

    /**
        this pane contains the selected page
        which is picked from {@link PageEnum}
     */

    private StackPane menuPane;
    private final ReadOnlyObjectWrapper<PageEnum> currentPageProperty = new ReadOnlyObjectWrapper<>(null);

    private SceneHandler(){}

    //with this we initialize a new stage
    public void init(Stage stage)
    {
        this.stage = stage;
        this.loadApplicationScene();
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    private <T> T loadRootFromFXML(String resourceName) {
        //in the docs it is stated that the resource bundle is cached,it is perfectly fine to call this method many times as needed
        ResourceBundle resourceBundle = ResourceBundle.getBundle("com.progetto.progetto.lang.film", StyleHandler.getInstance().getCurrentLanguage(),MainApplication.class.getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(resourceName),resourceBundle);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            createAlertMessage("Error","Something went wrong while loading a scene!", Alert.AlertType.ERROR);
        }
        return null;
    }

    /**
     * this method loads a new page from fxml with a sliding animation using a stack pane and interpolating
     * between the two pages.
     * @return true means the page loaded successfully;
     */
    public boolean loadPage(PageEnum page)
    {
        if(stage == null)
            return false;

        PageEnum currentPage = this.currentPageProperty.get();

        if(page.equals(currentPage))
            return false;

        Parent newPage = loadRootFromFXML(page.getFxml());
        newPage.requestFocus();
        menuPane.getChildren().add(newPage);

        if(currentPage == null) {
            this.currentPageProperty.set(page);
            return true;
        }
        Node currentNode = menuPane.getChildren().get(0);
        int slideRightVal = page.ordinal() > currentPage.ordinal() ? -1 : 1;

        newPage.setTranslateX(stage.getWidth() * -slideRightVal);
        KeyValue newPageKeyValue = new KeyValue(newPage.translateXProperty(),0, Interpolator.EASE_IN);
        KeyFrame newPageKeyFrame = new KeyFrame(Duration.millis(450),newPageKeyValue);
        KeyValue oldPageKeyValue = new KeyValue(currentNode.translateXProperty(),stage.getWidth() * slideRightVal, Interpolator.EASE_IN);
        KeyFrame oldPageKeyFrame = new KeyFrame(Duration.millis(450),oldPageKeyValue);

        Timeline timeline = new Timeline(newPageKeyFrame,oldPageKeyFrame);
        timeline.setOnFinished(event -> menuPane.getChildren().remove(0));
        timeline.play();
        currentPageProperty.set(page);
        return true;
    }

    public ReadOnlyObjectProperty<PageEnum> currentPageProperty() {
        return currentPageProperty.getReadOnlyProperty();
    }


    /**
     * generic alert message it uses the same stylesheet
     */
    public Alert createAlertMessage(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType,message);
        alert.setTitle(title);
        alert.setAlertType(alertType);
        alert.getDialogPane().getStylesheets().addAll(this.scene.getStylesheets());
        alert.showAndWait();
        return alert;
    }

    //---------------------------SCENES------------------------------//
    private void loadApplicationScene()
    {
        Parent root = loadRootFromFXML("MenuView.fxml");
        if(root == null)
            return;
        if(this.scene == null) {
            this.scene = new Scene(root);
            StyleHandler.getInstance().init(this.scene);
        }
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

        this.menuPane = (StackPane) scene.lookup("#stackPane");
        this.loadPage(PageEnum.MAIN);
    }

    //this is used to reload all resources like the language resource bundle when locale is changed.
    public void reloadApplication()
    {
        this.currentPageProperty.set(null);
        Parent root = loadRootFromFXML("MenuView.fxml");
        this.scene.setRoot(root);
        this.menuPane = (StackPane) scene.lookup("#stackPane");
        this.loadPage(PageEnum.SETTINGS);
    }


    public void loadRegisterScene()
    {
        Parent root = loadRootFromFXML("RegistryView.fxml");
        if(root == null)
            return;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(root);
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