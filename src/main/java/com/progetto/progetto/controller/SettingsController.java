package com.progetto.progetto.controller;

import com.progetto.progetto.model.Options;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.PageEnum;
import com.progetto.progetto.model.enums.StyleMode;
import com.progetto.progetto.model.handlers.LoggerHandler;
import com.progetto.progetto.model.handlers.StyleHandler;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.nodes.ColorBarPicker;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
    private ColorBarPicker colorPicker;

    @FXML
    private CheckBox dyslexicCheckBox;

    @FXML
    private ComboBox<Locale> languageBox;

    private ToggleGroup toggleGroup;

    private final StyleHandler styleHandler = StyleHandler.getInstance();

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
        this.colorPicker.disableProperty().bind(this.customModeToggle.selectedProperty().not());

        //setup possible languages in the combo box
        for(Locale locale : Options.SUPPORTED_LANGUAGES)
            this.languageBox.getItems().add(locale);

        //this is to represent the language name inside the language box
        this.languageBox.setConverter(localeConverter);

        //update control values according to the config file
        this.colorPicker.setHue(this.styleHandler.customHue);
        this.dyslexicCheckBox.setSelected(this.styleHandler.dyslexic);
        this.languageBox.setValue(styleHandler.getCurrentLanguage());


        this.toggleGroup.selectToggle(this.toggleGroup.getToggles().get(this.styleHandler.styleMode.ordinal()));

        //THIS PART OF CODE HANDLER THE UPDATE OF THE STYLE----------

        //the style mode
        toggleGroup.selectedToggleProperty().addListener((observableValue, toggle, selectedRadio) -> {saveConfigurationAndUpdate();});
        //dyslexic mode
        this.dyslexicCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());

        //colors
        this.colorPicker.hueProperty().addListener((observableValue, aBoolean, t1) -> saveConfigurationAndUpdate());

        //update language
        this.languageBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            this.saveConfigurationAndUpdate();
            SceneHandler.getInstance().reloadApplication(PageEnum.SETTINGS);
        });
    }


    //I preferred using this method since if We need to update via a button we can simply call this
    /** save current settings inside the config file and in the style handler*/
    private void saveConfigurationAndUpdate()
    {
        try
        {
            this.styleHandler.styleMode = StyleMode.values()[this.toggleGroup.getToggles().indexOf(this.toggleGroup.getSelectedToggle())];
            this.styleHandler.customHue = this.colorPicker.getHue();
            this.styleHandler.dyslexic = this.dyslexicCheckBox.isSelected();
            this.styleHandler.setLanguage(this.languageBox.getValue());
            this.styleHandler.updateScene(this.customModeToggle.getScene());
            this.styleHandler.saveConfigurationOnFile(new Properties());
        } catch (IOException e)
        {
            LoggerHandler.error("Error during configuration settings update!",e.fillInStackTrace());
            SceneHandler.getInstance().createErrorMessage(ErrorType.FILE);
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