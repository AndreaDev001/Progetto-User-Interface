package com.progetto.progetto.view.nodes;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class TogglePasswordField extends HBox {

    private static final String TOGGLED_EYE = "far-eye";
    private static final String UNTOGGLED_EYE = "far-eye-slash";
    private final TextField passwordField;

    public TogglePasswordField()
    {
        ToggleButton toggleEye = new ToggleButton();
        toggleEye.getStyleClass().add("toggle-eye");

        passwordField = new TextField()
        {
            @Override
            public void copy() {
                if(toggleEye.isSelected()) super.copy();
            }
        };
        passwordField.getStyleClass().add("toggle-password");

        FontIcon eyeIcon = new FontIcon(TOGGLED_EYE);
        eyeIcon.getStyleClass().add("eye-icon");
        toggleEye.selectedProperty().addListener((observable, oldValue, newValue) -> eyeIcon.setIconLiteral(newValue ? UNTOGGLED_EYE : TOGGLED_EYE));
        toggleEye.setGraphic(eyeIcon);
        passwordField.setSkin(new TogglePasswordSkin(passwordField,toggleEye.selectedProperty()));

        this.getChildren().add(passwordField);
        this.getChildren().add(toggleEye);

    }

    public TextField getPasswordField()
    {
        return passwordField;
    }

    private static class TogglePasswordSkin extends TextFieldSkin
    {
        private final BooleanProperty property;
        public TogglePasswordSkin(TextField control, BooleanProperty property) {
            super(control);
            this.property = property;
            //this updates the text when the toggle button is pressed
            this.property.addListener((observable, oldValue, newValue) -> control.setText(control.getText()));

        }

        @Override
        protected String maskText(String txt) {
            if(this.property != null && this.property.get())
                return txt;
            return "●".repeat(txt.length());
        }
    }
}
