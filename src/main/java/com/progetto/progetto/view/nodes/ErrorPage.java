package com.progetto.progetto.view.nodes;

import com.progetto.progetto.model.handlers.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ErrorPage extends VBox
{
    private final Label errorText;
    private final Button errorButton;

    public ErrorPage(String errorMessage,String buttonText,boolean useResources)
    {
        this.errorText = new Label(useResources ? StyleHandler.getInstance().getLocalizedString(errorMessage) : errorMessage);
        this.errorButton = new Button(useResources ? StyleHandler.getInstance().getLocalizedString(buttonText) : buttonText);
        this.init();
    }
    private void init()
    {
        this.getStyleClass().add("background");
        this.setAlignment(Pos.CENTER);
        this.errorText.setStyle("-fx-font-size: 30px;-fx-font-weight: bold");
        errorButton.setStyle("-fx-padding: 10px 10px");
        this.getChildren().add(errorText);
        this.getChildren().add(errorButton);
    }
    public final Label getErrorText() {return errorText;}
    public final Button getErrorButton() {return errorButton;}
}
