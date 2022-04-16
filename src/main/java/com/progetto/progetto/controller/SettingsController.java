package com.progetto.progetto.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsController {

    @FXML
    private RadioButton customModeToggle;

    @FXML
    private RadioButton darkModeToggle;

    @FXML
    private RadioButton lightModeToggle;


    @FXML
    private ColorPicker background_color;

    @FXML
    private ColorPicker foreground_color;

    @FXML
    private CheckBox dyslexicCheckBox;

    @FXML
    private Button filmsButton;

    @FXML
    private Button libraryButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button settingsButton;

    @FXML
    public void initialize()
    {
        ToggleGroup toggleGroup = new ToggleGroup();
        this.lightModeToggle.setToggleGroup(toggleGroup);
        this.darkModeToggle.setToggleGroup(toggleGroup);
        this.customModeToggle.setToggleGroup(toggleGroup);

        this.background_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());
        this.foreground_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());

        this.lightModeToggle.setSelected(true);

    }

}
