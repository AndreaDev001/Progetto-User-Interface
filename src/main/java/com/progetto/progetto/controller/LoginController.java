package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController
{
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize()
    {
        loginButton.addEventHandler(MouseEvent.MOUSE_CLICKED,(e) -> {
            boolean result = ProfileHandler.getInstance().login(usernameField.getText(),passwordField.getText(),true);
            if(result)
                SceneHandler.getInstance().loadMainScene();
        });

    }
    public final Button getLoginButton() {return loginButton;}
    public final TextField getUsernameLabel() {return usernameField;}
    public final PasswordField getPasswordField() {return passwordField;}
}