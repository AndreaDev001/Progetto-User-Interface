package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button confirmButton;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private void initialize()
    {
        emailField.textProperty().addListener((observableValue, s, t1) -> emailError.setVisible(!Client.getInstance().emailPattern.matcher(t1).matches()));
        passwordField.textProperty().addListener((observableValue, s, t1) -> passwordError.setVisible(!Client.getInstance().passwordPattern.matcher(t1).matches()));
        confirmButton.disableProperty().bind(emailError.visibleProperty().or(passwordError.visibleProperty()));
    }

    @FXML
    void onConfirmButton(ActionEvent event) {

        try
        {
            String user = Client.getInstance().register(emailField.getText(),passwordField.getText());
            if(user != null)
            {
                SceneHandler.getInstance().loadEmailConfirmation();
                emailField.getScene().getWindow().hide();
            }
            else
                SceneHandler.getInstance().createErrorMessage("Invalid or Already Used Email!");
        } catch (Exception e)
        {
            LoggerHandler.error("Profile Registration failed",e.fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        }

    }

}

