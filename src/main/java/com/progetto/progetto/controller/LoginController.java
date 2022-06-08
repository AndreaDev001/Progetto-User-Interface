package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.view.PageEnum;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.TogglePasswordField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController
{
    @FXML
    private TextField usernameField;
    @FXML
    private TogglePasswordField togglePassword;
    @FXML
    private Label errorMessage;

    private FadeTransition errorFadeTransition;

    @FXML
    void initialize()
    {
        errorFadeTransition = new FadeTransition(Duration.millis(400));
        errorFadeTransition.setNode(errorMessage);
        errorMessage.setOpacity(0.0D);
        errorMessage.setDisable(true);
        HBox.setHgrow(togglePassword.getPasswordField(), Priority.ALWAYS);
     }

    @FXML
    void onLoginPressed(ActionEvent event) {

        try
        {
            String result = !Client.getInstance().isLogged().get() ? Client.getInstance().login(usernameField.getText(),togglePassword.getPasswordField().getText()) : "logged";
            if(result != null)
            {
                if(!Client.getInstance().isEmailVerified())
                    SceneHandler.getInstance().loadEmailConfirmation();
                else
                    SceneHandler.getInstance().loadPage(PageEnum.MAIN);
            }
            else{
                errorFadeTransition.setToValue(1.0D);
                errorFadeTransition.play();
                errorMessage.setDisable(false);
            }
        } catch (IOException | ConnectionException e)
        {
            LoggerHandler.error("Login Check Failed ",e.fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        }
    }

    @FXML
    void onAccountPressed(ActionEvent event) {
        SceneHandler.getInstance().loadRegisterScene();
    }

    @FXML
    void onCloseMessage(ActionEvent event) {
        errorMessage.setDisable(true);
        errorFadeTransition.setToValue(0.0D);
        errorFadeTransition.play();
    }
}