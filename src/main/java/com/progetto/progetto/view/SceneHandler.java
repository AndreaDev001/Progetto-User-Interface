package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.Options;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.PageEnum;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class SceneHandler
{

    private static final SceneHandler handler = new SceneHandler();

    public static SceneHandler getInstance() {
        return handler;
    }

    private Stage stage;
    private Stage filmStage;
    private Scene scene;

    /**
        this pane contains the selected page
        which is picked from {@link PageEnum}
     */
    private StackPane menuPane;
    private final ReadOnlyObjectWrapper<PageEnum> currentPageProperty = new ReadOnlyObjectWrapper<>(null);
    private Image appIcon;

    private SceneHandler(){}

    //with this we initialize a new stage
    public void init(Stage stage)
    {
        this.appIcon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("images/filmsharp_32.png")));

        this.filmStage = new Stage(StageStyle.DECORATED);
        this.filmStage.initModality(Modality.APPLICATION_MODAL);
        this.stage = stage;
        this.loadApplicationScene();
        this.stage.setScene(this.scene);
        this.stage.show();
        this.stage.setOnCloseRequest((event) -> {
            this.filmStage.close();
            try
            {
                Alert alert = createQuitMessage("leaveApp.name");
                Optional<ButtonType> buttonType = alert.showAndWait();
                if(buttonType.isEmpty())
                    return;
                if(buttonType.get() == ButtonType.CANCEL)
                {
                    event.consume();
                    return;
                }
                Client.getInstance().close();
            } catch (Exception exception) {
                LoggerHandler.error("Error during application close request",exception);
                createErrorMessage(ErrorType.CONNECTION);
            }
        });
        this.stage.getIcons().add(appIcon);
        this.filmStage.getIcons().add(appIcon);

    }

    /**
     * This method creates a new parent from a fxml file
     * be aware the returned node will have the css ID as resourceName
     * this way we can automatically load a unique optional css to that page later, check {@link StyleHandler#updateScene}
     * @param resourceName the fxml file to load
     * @return the root node
     */
    private <T extends Parent> T loadRootFromFXML(String resourceName) {
        //in the docs it is stated that the resource bundle is cached,it is perfectly fine to call this method many times as needed
        ResourceBundle resourceBundle = ResourceBundle.getBundle("com.progetto.progetto.lang.film", StyleHandler.getInstance().getCurrentLanguage(),MainApplication.class.getClassLoader());
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(resourceName),resourceBundle);
        try {
            T node = fxmlLoader.load();
            node.setId(resourceName.split("\\.")[0]);
            return node;
        } catch (Exception e) {
            LoggerHandler.error("Couldn't load fxml page: {}",e,resourceName);
            createErrorMessage(ErrorType.LOADING_PAGE);
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

        if(newPage == null)
            return false;

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
    public Alert createErrorMessage(String unlocalizedMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR,StyleHandler.getInstance().getLocalizedString(unlocalizedMessage));
        alert.setTitle("Error");
        alert.getDialogPane().getStylesheets().addAll(this.scene.getStylesheets());
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
        alert.showAndWait();
        return alert;
    }
    public Alert createErrorMessage(ErrorType errorType) {
        return createErrorMessage(errorType.getUnlocalizedMessage());
    }
    public Alert createQuitMessage(String unlocalizedMessage)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,StyleHandler.getInstance().getLocalizedString(unlocalizedMessage));
        alert.setTitle(StyleHandler.getInstance().getLocalizedString("exit.name"));
        alert.getDialogPane().getStylesheets().addAll(this.scene.getStylesheets());
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
        ((Button)alert.getDialogPane().lookupButton(ButtonType.OK)).setText(StyleHandler.getInstance().getLocalizedString("confirmation.name"));
        return alert;
    }
    //---------------------------SCENES------------------------------//
    private void loadApplicationScene()
    {

        StyleHandler.getInstance().init(null);
        Parent root = loadRootFromFXML("MenuView.fxml");
        if(root == null)
            return;
        if(this.scene == null)
            this.scene = new Scene(root);
        StyleHandler.getInstance().updateScene(this.scene);
        this.scene.setRoot(root);
        stage.setMinWidth(Options.MAIN_WINDOW_WIDTH);
        stage.setMinHeight(Options.MAIN_WINDOW_HEIGHT);
        stage.setResizable(true);
        stage.setTitle(StyleHandler.getInstance().getLocalizedString("mainView.name"));
        centerStage(stage,1280,720);
        stage.setWidth(1280);
        stage.setHeight(720);

        this.menuPane = (StackPane) scene.lookup("#stackPane");
        this.loadPage(PageEnum.MAIN);
    }

    //this is used to reload all resources like the language resource bundle when locale is changed.
    public void reloadApplication(PageEnum pageEnum)
    {
        this.currentPageProperty.set(null);
        Parent root = loadRootFromFXML("MenuView.fxml");
        this.scene.setRoot(root);
        this.menuPane = (StackPane) scene.lookup("#stackPane");
        this.loadPage(pageEnum);
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
        stage.setMinWidth(700);
        stage.setMinHeight(700);
        stage.setTitle(StyleHandler.getInstance().getLocalizedString("registryView.name"));
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(appIcon);
        stage.show();
    }

    public void loadEmailConfirmation()
    {
        Parent root = loadRootFromFXML("EmailConfirmation.fxml");
        if(root == null)
            return;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {if(keyEvent.getCode() == KeyCode.ESCAPE) stage.close();});
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        StyleHandler.getInstance().updateScene(scene);
        scene.setRoot(root);
        stage.setMinWidth(650);
        stage.setMinHeight(300);
        stage.setTitle(StyleHandler.getInstance().getLocalizedString("emailConfirmationView.name"));
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(appIcon);
        stage.show();
    }

    public void loadFilmScene()
    {
        this.filmStage.hide();
        Parent root = loadRootFromFXML("FilmView.fxml");
        if(root == null)
            return;
        Scene filmScene = new Scene(root);
        filmScene.getStylesheets().clear();
        StyleHandler.getInstance().updateScene(filmScene);
        filmScene.setRoot(root);
        filmStage.setTitle(StyleHandler.getInstance().getLocalizedString("filmView.name"));
        filmStage.setResizable(false);
        filmStage.setScene(filmScene);
        filmStage.setWidth(640);
        filmStage.setIconified(false);
        centerStage(filmStage,filmStage.getWidth(),filmStage.getHeight());
        this.filmStage.show();
    }
    public void centerStage(Stage stage,double width,double height)
    {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - width) / 2);
        stage.setY((screenBounds.getHeight() - height) / 2);
    }
    public final Stage getFilmStage() {return filmStage;}

}