package com.progetto.progetto.controller;

import com.progetto.progetto.view.ErrorTextField;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.TogglePassword;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LoginController
{
    @FXML
    private ErrorTextField usernameField;
    @FXML
    private TogglePassword togglePassword;

    @FXML
    void initialize()
    {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            usernameField.showErrorProperty().set(newValue.equals("sos"));
        });

    }

    @FXML
    void onLoginPressed(ActionEvent event) {
        //boolean result = ProfileHandler.getInstance().login(usernameField.getText(),passwordField.getText(),false);
        //if(result)
        //    SceneHandler.getInstance().loadPage(PageEnum.MAIN);
    }

    @FXML
    void onAccountPressed(ActionEvent event) {
        SceneHandler.getInstance().loadRegisterScene();
    }
}