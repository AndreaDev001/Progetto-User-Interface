package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.view.CheckLabel;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private Button confirmButton;

    @FXML
    private Label emailError;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passConfirmationError;

    @FXML
    private PasswordField passwordConfirmation;

    @FXML
    private CheckLabel upperCaseTick;
    @FXML
    private CheckLabel lowerCaseTick;
    @FXML
    private CheckLabel lengthTick;
    @FXML
    private CheckLabel numberTick;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.]+@\\w+\\.\\w+");
    private static final Pattern LOWER_CASE_PATTERN = Pattern.compile("\\S*[a-z]\\S*");
    private static final Pattern UPPER_CASE_PATTERN = Pattern.compile("\\S*[A-Z]\\S*");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\S*\\d\\S*");


    @FXML
    private void initialize()
    {
        emailField.textProperty().addListener((observableValue, s, t1) ->
        {
            emailError.setVisible(!EMAIL_PATTERN.matcher(t1).matches());
        });

        passwordField.textProperty().addListener((observableValue, oldValue, newValue) ->
        {
            upperCaseTick.setChecked(UPPER_CASE_PATTERN.matcher(newValue).matches());
            lowerCaseTick.setChecked(LOWER_CASE_PATTERN.matcher(newValue).matches());
            numberTick.setChecked(NUMBER_PATTERN.matcher(newValue).matches());
            lengthTick.setChecked(newValue.length() >= 6);
        });

        passConfirmationError.visibleProperty().bind(passwordField.textProperty().isEqualTo(passwordConfirmation.textProperty()).not());
        confirmButton.disableProperty().bind(emailError.visibleProperty().not().and(upperCaseTick.checkedProperty()).and(lowerCaseTick.checkedProperty()).and(lengthTick.checkedProperty()).and(passConfirmationError.visibleProperty().not()).not());
    }
    @FXML
    private void onConfirmButton(ActionEvent event) {

        try
        {
            String user = Client.getInstance().register(emailField.getText(),passwordField.getText());
            if(user != null)
            {
                SceneHandler.getInstance().loadEmailConfirmation();
                emailField.getScene().getWindow().hide();
            }
            else
                SceneHandler.getInstance().createErrorMessage("registrationFail.name");
        } catch (Exception e)
        {
            LoggerHandler.error("Profile Registration failed",e.fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        }

    }

}

