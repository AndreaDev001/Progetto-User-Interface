package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
            SceneHandler.getInstance().loadMainScene();
    }

    @FXML
    void onAccountPressed(ActionEvent event) {
        SceneHandler.getInstance().loadRegisterScene();
    }

    @FXML
    void onExitPressed(ActionEvent event) {
        SceneHandler.getInstance().loadMainScene();
    }
}