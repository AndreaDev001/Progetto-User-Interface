package com.progetto.progetto.controller;

import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.PageEnum;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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
        this.loginButton.visibleProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
        this.logoutButton.visibleProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNotNull());
        this.libraryButton.disableProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
        this.loginButton.managedProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNull());
        this.logoutButton.managedProperty().bind(ProfileHandler.getInstance().getLoggedUser().isNotNull());
        SceneHandler.getInstance().currentPageProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonSelection(homeButton, newValue, PageEnum.MAIN);
            updateButtonSelection(settingsButton, newValue, PageEnum.SETTINGS);
            updateButtonSelection(loginButton, newValue, PageEnum.LOGIN);
        });
    }
    @FXML
    void onHomePressed(ActionEvent event)
    {
        if(SceneHandler.getInstance().currentPageProperty().getValue() == PageEnum.MAIN)
        {
            if(!homeButton.getStyleClass().contains("highlight"))
                 homeButton.getStyleClass().add("highlight");
            libraryButton.getStyleClass().remove("highlight");
        }
        ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME,false,true,false);
        SceneHandler.getInstance().loadPage(PageEnum.MAIN);
    }
    @FXML
    void onLibraryPressed(ActionEvent event)
    {
        ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.LIBRARY,false,true,SceneHandler.getInstance().currentPageProperty().getValue() != PageEnum.MAIN);
        SceneHandler.getInstance().loadPage(PageEnum.MAIN);
        homeButton.getStyleClass().remove("highlight");
        if(!libraryButton.getStyleClass().contains("highlight"))
            libraryButton.getStyleClass().add("highlight");
    }
    @FXML
    void onLoginPressed(ActionEvent event) {
        libraryButton.getStyleClass().remove("highlight");
        SceneHandler.getInstance().loadPage(PageEnum.LOGIN);
    }

    @FXML
    void onSettingsPressed(ActionEvent event)
    {
        libraryButton.getStyleClass().remove("highlight");
        SceneHandler.getInstance().loadPage(PageEnum.SETTINGS);
    }

    @FXML
    void onLogoutPressed(ActionEvent event)
    {
        try {
            ProfileHandler.getInstance().logout();
        } catch (IOException | ConnectionException e) {
            e.printStackTrace();
        }
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
