package com.progetto.progetto.controller;

import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.StyleMode;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Properties;

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

        //this is to disable the color configuration if custom radio button is not selected
        this.background_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());
        this.foreground_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());

        //THIS PART OF CODE IS USED TO BIND THE CURRENT STYLE CONFIGURATION WITH THE SETTINGS

        //update the color pickers with the current style
        this.foreground_color.valueProperty().set(StyleHandler.getInstance().getPrimaryColor().get());
        this.background_color.valueProperty().set(StyleHandler.getInstance().getSecondaryColor().get());
        this.dyslexicCheckBox.selectedProperty().set(StyleHandler.getInstance().getDyslexicFont().get());

        StyleHandler.getInstance().getPrimaryColor().bind(this.foreground_color.valueProperty());
        StyleHandler.getInstance().getSecondaryColor().bind(this.background_color.valueProperty());
        StyleHandler.getInstance().getDyslexicFont().bind(this.dyslexicCheckBox.selectedProperty());

        this.lightModeToggle.setSelected(StyleHandler.getInstance().getCurrentStyle().get() == StyleMode.LIGHT);
        this.darkModeToggle.setSelected(StyleHandler.getInstance().getCurrentStyle().get() == StyleMode.DARK);
        this.customModeToggle.setSelected(StyleHandler.getInstance().getCurrentStyle().get() == StyleMode.CUSTOM);

        //THIS PART OF CODE HANDLEL THE UPDATE OF THE STYLE----------

        //the style mode
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, selectedRadio) ->
        {
            StyleMode selectedMode = selectedRadio == lightModeToggle ? StyleMode.LIGHT : (selectedRadio == darkModeToggle ? StyleMode.DARK : StyleMode.CUSTOM);
            StyleHandler.getInstance().getCurrentStyle().set(selectedMode);
            saveConfigurationAndUpdate(background_color.getScene());
        });
        //dyslexic mode
        this.dyslexicCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate(background_color.getScene()));

        //colors
        this.foreground_color.valueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate(background_color.getScene()));
        this.background_color.valueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate(background_color.getScene()));



    }


    private void saveConfigurationAndUpdate(Scene scene)
    {
        StyleHandler.getInstance().updateScene(scene);
        try
        {
            StyleHandler.getInstance().saveConfigurationOnFile(new Properties());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
