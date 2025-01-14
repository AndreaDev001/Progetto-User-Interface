package com.progetto.progetto.model;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Options
{
    public static final String APP_VERSION = "1.0.0";
    //the folder where everything persistent will be stored
    public static final String APP_FOLDER_LOCATION = System.getProperty("user.home") + File.separator + ".filmsharp";
    //the settings file
    public static final String CONFIG_NAME = "config.properties";
    //immutable list of supported languages
    public static final List<Locale> SUPPORTED_LANGUAGES = List.of(Locale.ENGLISH, Locale.ITALIAN,Locale.GERMAN,Locale.FRENCH,Locale.forLanguageTag("es"));

    public static final int MAIN_WINDOW_WIDTH = 640;
    public static final int MAIN_WINDOW_HEIGHT = 480;
}
