package com.progetto.progetto.controller;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.PageEnum;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.view.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class MailConfirmationController
{
    @FXML
    private Label emailText;

    @FXML
    private VBox emailResult;

    @FXML
    private VBox checkResult;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private void initialize()
    {
        emailText.setText(Client.getInstance().getEmail());
        this.sendEmail(null);

    }

    @FXML
    private void checkEmail(ActionEvent event) {

        emailResult.setVisible(false);
        checkResult.setVisible(false);
        progress.setVisible(true);
        Client.getInstance().callAsyncMethod(() -> Client.getInstance().isEmailVerified(), workerStateEvent ->
        {
            if((Boolean)workerStateEvent.getSource().getValue())
            {
                checkResult.getScene().getWindow().hide();
                SceneHandler.getInstance().loadPage(PageEnum.MAIN);
            }
            else
            {
                checkResult.setVisible(true);
                progress.setVisible(false);
            }
        }, workerStateEvent ->
        {
            checkResult.setVisible(true);
            progress.setVisible(false);
            LoggerHandler.error("Check Email Failed in controller",workerStateEvent.getSource().getException().fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        });

    }

    @FXML
    private void sendEmail(ActionEvent event) {

        emailResult.setVisible(false);
        checkResult.setVisible(false);
        progress.setVisible(true);
        Client.getInstance().callAsyncMethod(() -> Client.getInstance().sendEmailVerification(), workerStateEvent ->
        {
            emailResult.setVisible(true);
            progress.setVisible(false);
        }, workerStateEvent ->
        {
            emailResult.setVisible(true);
            progress.setVisible(false);
            LoggerHandler.error("Send Email Failed in controller",workerStateEvent.getSource().getException().fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.CONNECTION);
        });

    }

    @FXML
    private void onExitPressed(ActionEvent event)
    {
        this.checkResult.getScene().getWindow().hide();
    }
}
