package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.PageEnum;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController
{
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    void onLoginPressed(ActionEvent event) {
        boolean result = ProfileHandler.getInstance().login(usernameField.getText(),passwordField.getText(),false);
        if(result)
            SceneHandler.getInstance().loadPage(PageEnum.MAIN);
        else
        {
            usernameField.clear();
            passwordField.clear();
        }
    }

    @FXML
    void onAccountPressed(ActionEvent event) {
        SceneHandler.getInstance().loadRegisterScene();
    }
}