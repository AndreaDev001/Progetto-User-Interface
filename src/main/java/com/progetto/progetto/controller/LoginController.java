package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.PageEnum;
import com.progetto.progetto.model.handlers.FilmHandler;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.TogglePasswordField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.json.JSONObject;

import java.io.IOException;

public class LoginController
{
    @FXML
    private TextField usernameField;
    @FXML
    private TogglePasswordField togglePassword;

    @FXML
    void initialize()
    {
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
                Client.getInstance().get("films",success -> {
                    JSONObject jsonObject = (JSONObject) success.getSource().getValue();
                    FilmHandler.getInstance().loadMovies(jsonObject.getJSONArray("films"));
                },error -> System.out.print("Client get function failed"));
            }
            else{
                SceneHandler.getInstance().createErrorMessage("loginFail.name");
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
    }
}