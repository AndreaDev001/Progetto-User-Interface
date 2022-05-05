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
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Locale;
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

    @FXML
    private ComboBox<Locale> languageBox;

    private ToggleGroup toggleGroup;

    private final StyleHandler styleHandler = StyleHandler.getInstance();
    private final StyleConfiguration styleConfig = styleHandler.getStyleConfiguration();

    //used to represent the language combo box correctly
    private final LocaleConverter localeConverter = new LocaleConverter();


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

        //setup possible languages in the combo box
        for(Locale locale : styleHandler.supportedLanguages)
            this.languageBox.getItems().add(locale);

        //this is to represent the language name inside the language box
        this.languageBox.setConverter(localeConverter);

        //update control values according to the config file
        this.foreground_color.setValue(this.styleConfig.foregroundColor);
        this.background_color.setValue(this.styleConfig.backgroundColor);
        this.text_color.setValue(this.styleConfig.textColor);
        this.dyslexicCheckBox.setSelected(this.styleConfig.dyslexic);
        this.languageBox.setValue(styleHandler.getCurrentLanguage());


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

        //update language
        this.languageBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            this.saveConfigurationAndUpdate();
            SceneHandler.getInstance().reloadApplication();
        });



    }


    //I preferred using this method since if We need to update via a button we can simply call this
    /** save current settings inside the config file and in the style handler*/
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
            this.styleHandler.setLanguage(this.languageBox.getValue());
            this.styleHandler.updateScene(this.background_color.getScene());
            this.styleHandler.saveConfigurationOnFile(new Properties());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class LocaleConverter extends StringConverter<Locale> {

        @Override
        public String toString(Locale object) {
            return object.getDisplayName(object);
        }

        @Override
        public Locale fromString(String string) {
            return languageBox.getItems().stream().filter(loc -> loc.getDisplayName(loc).equals(string)).findAny().orElse(null);
        }
    }
}