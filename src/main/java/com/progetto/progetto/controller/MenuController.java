package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.PageEnum;
import com.progetto.progetto.view.SceneHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MenuController {

    @FXML
    private StackPane stackPane;

    @FXML
    private Button homeButton;

    @FXML
    private Button libraryButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button loginButton;


    @FXML
    private Button logoutButton;


    @FXML
    private void initialize()
    {
        this.libraryButton.disableProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());

        this.loginButton.visibleProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
        this.logoutButton.visibleProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNotNull());

        this.loginButton.managedProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
        this.logoutButton.managedProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNotNull());

        //bold selected button page
        SceneHandler.getInstance().currentPageProperty().addListener((observable, oldValue, newValue) -> {

            updateButtonSelection(homeButton, newValue, PageEnum.MAIN);
            updateButtonSelection(libraryButton, newValue, PageEnum.LIBRARY);
            updateButtonSelection(settingsButton, newValue, PageEnum.SETTINGS);
            updateButtonSelection(loginButton, newValue, PageEnum.LOGIN);
        });

    }

    @FXML
    void onHomePressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(PageEnum.MAIN);
    }

    @FXML
    void onLibraryPressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(PageEnum.LIBRARY);
    }

    @FXML
    void onLoginPressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(PageEnum.LOGIN);
    }

    @FXML
    void onSettingsPressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(PageEnum.SETTINGS);
    }

    @FXML
    void onLogoutPressed(ActionEvent event) {
        ProfileHandler.getInstance().logout();
        SceneHandler.getInstance().loadPage(PageEnum.MAIN);
    }

    private void updateButtonSelection(Button button,PageEnum newPage,PageEnum pageEnum)
    {
        if(pageEnum.equals(newPage))
            button.getStyleClass().add("highlight");
        else
            button.getStyleClass().remove("highlight");
    }


}
