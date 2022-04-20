package com.progetto.progetto.controller;

import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.StyleMode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

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
    private ColorPicker text_color;

    @FXML
    private CheckBox dyslexicCheckBox;


    private final StyleHandler styleHandler = StyleHandler.getInstance();


    @FXML
    void onHomeClicked(MouseEvent event) {
        SceneHandler.getInstance().loadMainScene();
    }

    @FXML
    void onLibraryClicked(MouseEvent event) {
        SceneHandler.getInstance().loadMainScene();
    }

    @FXML
    void onQuitClicked(MouseEvent event) {
        SceneHandler.getInstance().loadLoginScene();
    }

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
        this.text_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());

        //THIS PART OF CODE IS USED TO BIND THE CURRENT STYLE CONFIGURATION WITH THE SETTINGS

        //update the color pickers with the current style
        this.foreground_color.valueProperty().set(styleHandler.getPrimaryColor().get());
        this.background_color.valueProperty().set(styleHandler.getSecondaryColor().get());
        this.text_color.valueProperty().set(styleHandler.getTextColor().get());
        this.dyslexicCheckBox.selectedProperty().set(styleHandler.getDyslexicFont().get());

        styleHandler.getPrimaryColor().bind(this.foreground_color.valueProperty());
        styleHandler.getSecondaryColor().bind(this.background_color.valueProperty());
        styleHandler.getTextColor().bind(this.text_color.valueProperty());
        styleHandler.getDyslexicFont().bind(this.dyslexicCheckBox.selectedProperty());

        this.lightModeToggle.setSelected(styleHandler.getCurrentStyle().get() == StyleMode.LIGHT);
        this.darkModeToggle.setSelected(styleHandler.getCurrentStyle().get() == StyleMode.DARK);
        this.customModeToggle.setSelected(styleHandler.getCurrentStyle().get() == StyleMode.CUSTOM);

        //THIS PART OF CODE HANDLEL THE UPDATE OF THE STYLE----------

        //the style mode
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, selectedRadio) ->
        {
            StyleMode selectedMode = selectedRadio == lightModeToggle ? StyleMode.LIGHT : (selectedRadio == darkModeToggle ? StyleMode.DARK : StyleMode.CUSTOM);
            styleHandler.getCurrentStyle().set(selectedMode);
            saveConfigurationAndUpdate();
        });
        //dyslexic mode
        this.dyslexicCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());

        //colors
        this.foreground_color.valueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());
        this.background_color.valueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());
        this.text_color.valueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());


    }


    private void saveConfigurationAndUpdate()
    {
        styleHandler.updateScene(this.background_color.getScene());
        try
        {
            styleHandler.saveConfigurationOnFile(new Properties());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}