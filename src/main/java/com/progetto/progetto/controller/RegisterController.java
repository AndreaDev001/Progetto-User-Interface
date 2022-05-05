package com.progetto.progetto.controller;

import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.view.PageEnum;
import com.progetto.progetto.view.SceneHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class RegisterController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private TextField usernameField;


    @FXML
    private void initialize()
    {
        //this disables the button if the passwords are not equal

        BooleanBinding disableProperty = passwordField.textProperty().isEqualTo(repeatPasswordField.textProperty()).and(this.passwordField.textProperty().isNotEmpty()).not();
        this.registerButton.disableProperty().bind(disableProperty);

        disableProperty.addListener((observable, oldValue, newValue) -> registerButton.setTooltip(newValue ? new Tooltip("eeee") : null));
    }

    @FXML
    void onRegisterPressed(ActionEvent event) {

        String encryptedPassword = BCrypt.hashpw(this.passwordField.getText(),BCrypt.gensalt(10));
        boolean successfulResult = ProfileHandler.getInstance().createUser(usernameField.getText(),encryptedPassword);
        if(successfulResult)
        {
            //close current stage
            this.registerButton.getScene().getWindow().hide();
        }
    }


}
