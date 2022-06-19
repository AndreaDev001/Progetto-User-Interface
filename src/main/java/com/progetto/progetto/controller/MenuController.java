package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.enums.MovieViewMode;
import com.progetto.progetto.model.enums.PageEnum;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.ResearchHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MenuController
{

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
        this.loginButton.visibleProperty().bind(Client.getInstance().isLogged().not());
        this.logoutButton.visibleProperty().bind(Client.getInstance().isLogged());
        this.libraryButton.disableProperty().bind(FilmHandler.getInstance().IsLibraryAvailable().not());
        this.loginButton.managedProperty().bind(Client.getInstance().isLogged().not());
        this.logoutButton.managedProperty().bind(Client.getInstance().isLogged());
        SceneHandler.getInstance().currentPageProperty().addListener((observable, oldValue, newValue) -> {
            updateButtonSelection(homeButton, newValue, PageEnum.MAIN);
            updateButtonSelection(settingsButton, newValue, PageEnum.SETTINGS);
            updateButtonSelection(loginButton, newValue, PageEnum.LOGIN);
        });
        ResearchHandler.getInstance().addViewListener((obs,oldValue,newValue) -> {
            if(SceneHandler.getInstance().currentPageProperty().getValue() != PageEnum.MAIN)
                return;
            if(ResearchHandler.getInstance().getCurrentViewMode() == MovieViewMode.HOME)
            {
                if(!homeButton.getStyleClass().contains("highlight"))
                    homeButton.getStyleClass().add("highlight");
                libraryButton.getStyleClass().remove("highlight");
            }
            else
            {
                if(!libraryButton.getStyleClass().contains("highlight"))
                    libraryButton.getStyleClass().add("highlight");
                homeButton.getStyleClass().remove("highlight");
            }
        },this.getClass().getSimpleName());
    }
    @FXML
    void onHomePressed(ActionEvent event)
    {
        ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME,false,true,false);
        SceneHandler.getInstance().loadPage(PageEnum.MAIN);
        libraryButton.getStyleClass().remove("highlight");
        if(!homeButton.getStyleClass().contains("highlight"))
            homeButton.getStyleClass().add("highlight");
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
        homeButton.getStyleClass().remove("highlight");
        libraryButton.getStyleClass().remove("highlight");
        SceneHandler.getInstance().loadPage(PageEnum.LOGIN);
    }
    @FXML
    void onSettingsPressed(ActionEvent event)
    {
        homeButton.getStyleClass().remove("highlight");
        libraryButton.getStyleClass().remove("highlight");
        SceneHandler.getInstance().loadPage(PageEnum.SETTINGS);
    }

    @FXML
    void onLogoutPressed(ActionEvent event)
    {
        try {
            Client.getInstance().logout();
            ResearchHandler.getInstance().setCurrentViewMode(MovieViewMode.HOME,false,true,false);
        } catch (IOException | ConnectionException e) {
            e.printStackTrace();
        }
    }
    private void updateButtonSelection(Button button,PageEnum newPage,PageEnum pageEnum)
    {
        if(pageEnum.equals(newPage))
            button.getStyleClass().add("highlight");
        else
        {
            button.getStyleClass().remove("highlight");
        }
    }
}
