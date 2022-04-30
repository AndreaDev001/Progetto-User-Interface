package com.progetto.progetto.controller;

import com.progetto.progetto.model.records.StyleConfiguration;
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

    private ToggleGroup toggleGroup;

    private final StyleHandler styleHandler = StyleHandler.getInstance();
    private final StyleConfiguration styleConfig = styleHandler.getStyleConfiguration();



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
        this.toggleGroup = new ToggleGroup();
        this.lightModeToggle.setToggleGroup(toggleGroup);
        this.darkModeToggle.setToggleGroup(toggleGroup);
        this.customModeToggle.setToggleGroup(toggleGroup);

        //this is to disable the color configuration if custom radio button is not selected
        this.background_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());
        this.foreground_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());
        this.text_color.disableProperty().bind(this.customModeToggle.selectedProperty().not());

        this.foreground_color.setValue(this.styleConfig.foregroundColor);
        this.background_color.setValue(this.styleConfig.backgroundColor);
        this.text_color.setValue(this.styleConfig.textColor);
        this.dyslexicCheckBox.setSelected(this.styleConfig.dyslexic);


        this.lightModeToggle.setSelected(styleConfig.styleMode == StyleMode.LIGHT);
        this.darkModeToggle.setSelected(styleConfig.styleMode == StyleMode.DARK);
        this.customModeToggle.setSelected(styleConfig.styleMode == StyleMode.CUSTOM);

        //THIS PART OF CODE HANDLEL THE UPDATE OF THE STYLE----------

        //the style mode
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, selectedRadio) ->
        {
            styleConfig.styleMode = selectedRadio == lightModeToggle ? StyleMode.LIGHT : (selectedRadio == darkModeToggle ? StyleMode.DARK : StyleMode.CUSTOM);
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
        try
        {
            Toggle selectedRadio = toggleGroup.getSelectedToggle();
            styleConfig.styleMode = selectedRadio == lightModeToggle ? StyleMode.LIGHT : (selectedRadio == darkModeToggle ? StyleMode.DARK : StyleMode.CUSTOM);
            this.styleConfig.foregroundColor = this.foreground_color.getValue();
            this.styleConfig.backgroundColor = this.background_color.getValue();
            this.styleConfig.textColor = this.text_color.getValue();
            this.styleConfig.dyslexic = this.dyslexicCheckBox.isSelected();
            styleHandler.saveConfigurationOnFile(new Properties());
            styleHandler.updateScene(this.background_color.getScene());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}