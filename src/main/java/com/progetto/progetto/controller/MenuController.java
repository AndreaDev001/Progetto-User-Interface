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
import javafx.scene.control.ProgressIndicator;
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
    private ProgressIndicator progressIndicator;

    @FXML
    private void initialize()
    {
        if(Client.getInstance().isLogged().get() && !FilmHandler.getInstance().IsLibraryAvailable())
        {
            progressIndicator.setVisible(true);
            libraryButton.setDisable(true);
        }
        else if(Client.getInstance().isLogged().get())
        {
            progressIndicator.setVisible(false);
            libraryButton.setDisable(false);
        }
        this.loginButton.visibleProperty().bind(Client.getInstance().isLogged().not());
        this.logoutButton.visibleProperty().bind(Client.getInstance().isLogged());
        this.loginButton.managedProperty().bind(Client.getInstance().isLogged().not());
        this.logoutButton.managedProperty().bind(Client.getInstance().isLogged());
        progressIndicator.managedProperty().bind(progressIndicator.visibleProperty());
        Client.getInstance().isLogged().addListener((obs,oldValue,newValue) -> {
            if(!Client.getInstance().isLogged().get())
            {
                this.progressIndicator.setVisible(false);
                this.libraryButton.setDisable(true);
            }
            else
            {
                this.progressIndicator.setVisible(true);
                this.libraryButton.setDisable(true);
            }
        });
        FilmHandler.getInstance().addLibraryListener((obs,oldValue,newValue) -> {
            if(Client.getInstance().isLogged().get() && !FilmHandler.getInstance().IsLibraryAvailable())
            {
                this.progressIndicator.setVisible(true);
                this.libraryButton.setDisable(true);
            }
            if(Client.getInstance().isLogged().get() && FilmHandler.getInstance().IsLibraryAvailable())
            {
                this.progressIndicator.setVisible(false);
                this.libraryButton.setDisable(false);
            }
        },this.getClass().getSimpleName());
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
