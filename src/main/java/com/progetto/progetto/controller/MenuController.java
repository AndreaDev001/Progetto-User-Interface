package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MenuController {

    @FXML
    private StackPane stackPane;

    @FXML
    private Button libraryButton;

    @FXML
    private void initialize()
    {
        SceneHandler.getInstance().loadPage(stackPane,"MainView.fxml");
        libraryButton.disableProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
    }

    @FXML
    void onHomePressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(stackPane,"MainView.fxml");
    }

    @FXML
    void onLibraryPressed(ActionEvent event) {

    }

    @FXML
    void onLoginPressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(stackPane,"LoginView.fxml");
    }

    @FXML
    void onSettingsPressed(ActionEvent event) {
        SceneHandler.getInstance().loadPage(stackPane,"SettingsView.fxml");
    }

}
