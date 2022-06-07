package com.progetto.progetto.view.nodes;

import com.progetto.progetto.view.StyleHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

//Component used to handle errors
public class ErrorPage extends VBox
{
    private final Label errorText;
    private final Button errorButton;

    /**
     * Constructor of the ErrorPage class
     * @param errorMessage The errorMessage to show
     * @param buttonText The Text of The error Button
     * @param useResources If we need to use international strings or not
     */
    public ErrorPage(String errorMessage,String buttonText,boolean useResources)
    {
        this.errorText = new Label(useResources ? StyleHandler.getInstance().getResourceBundle().getString(errorMessage) : errorMessage);
        this.errorButton = new Button(useResources ? StyleHandler.getInstance().getResourceBundle().getString(buttonText) : buttonText);
        this.init();
    }

    /**
     * Method used to initialize the component
     */
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
