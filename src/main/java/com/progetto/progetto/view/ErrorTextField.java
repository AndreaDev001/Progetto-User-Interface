package com.progetto.progetto.view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ErrorTextField extends TextField {


    private static final Duration popUpAnimation = Duration.millis(300);
    private StringProperty errorText;
    private BooleanProperty showError;

    public ErrorTextField()
    {
        this.getStyleClass().add("error-text-field");

        Label label = new Label();
        label.textProperty().bind(errorTextProperty());
        label.getStyleClass().add("text-field-error");

        label.translateYProperty().bind(heightProperty());

        label.setOpacity(getShowError() ? 1.0 : 0.0);


        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(popUpAnimation);
        fadeTransition.setNode(label);


        showErrorProperty().addListener((observable, oldValue, newValue) -> {

            fadeTransition.stop();
            int val = newValue ? 0 : 1;
            fadeTransition.setToValue(1 - val);
            fadeTransition.play();
        });
        this.getChildren().add(label);

    }

    public StringProperty errorTextProperty() { if(this.errorText == null) errorText = new SimpleStringProperty("Empty Error"); return this.errorText; }
    public final String getErrorText() { return this.errorTextProperty().get(); }
    public final void setErrorText(String text) { this.errorTextProperty().setValue(text);}

    public BooleanProperty showErrorProperty() { if(this.showError == null) showError = new SimpleBooleanProperty(false); return this.showError; }
    public final boolean getShowError() { return this.showErrorProperty().get(); }
    public final void setShowError(boolean flag) { this.showErrorProperty().setValue(flag);}
}
