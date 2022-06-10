package com.progetto.progetto.model.handlers;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.enums.ErrorType;
import com.progetto.progetto.model.enums.StyleMode;
import com.progetto.progetto.view.SceneHandler;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//STYLE HANDLER CLASS written by Pierlugi, aka PierKnight
//FOR the moment the config file is saved in the home folder inside a directory named "film_app"
public class StyleHandler {

    private static final StyleHandler STYLE_HANDLER = new StyleHandler();

    public static StyleHandler getInstance() {
        return STYLE_HANDLER;
    }

    public StyleMode styleMode = StyleMode.DARK;
    public double customHue = 0;
    public boolean dyslexic = false;

    private Locale currentLanguage = Locale.ENGLISH;

    //immutable list of supported languages
    public final List<Locale> supportedLanguages = List.of(Locale.ENGLISH, Locale.ITALIAN,Locale.GERMAN,Locale.FRENCH,Locale.forLanguageTag("es"));

    private StyleHandler() {
        //loading necessary fonts
        Font.loadFont(MainApplication.class.getResourceAsStream("fonts/Roboto-Regular.ttf"),10);
        Font.loadFont(MainApplication.class.getResourceAsStream("fonts/Roboto-Bold.ttf"),10);
        Font.loadFont(MainApplication.class.getResourceAsStream("fonts/OpenDyslexic-Regular.otf"),10);
        Font.loadFont(MainApplication.class.getResourceAsStream("fonts/OpenDyslexic-Bold.otf"),10);
        Font.loadFont(MainApplication.class.getResourceAsStream("fonts/OpenDyslexic-Italic.otf"),10);
    }

    //read the config file and update the scene along with it
    public void init(Scene scene)
    {
        Path folderPath = Path.of(getFolderPath());

        try {
            Files.createDirectories(folderPath);

            Path filePath = Path.of(getFolderPath() + File.separator + "config.txt");
            Properties properties = new Properties();

            if (!Files.exists(filePath)) {
                //Use system language if it is supported by the app
                if (this.supportedLanguages.contains(Locale.getDefault()))
                    this.currentLanguage = Locale.getDefault();
                saveConfigurationOnFile(properties);
            }

            if (scene == null)
                return;

            FileReader fileReader = new FileReader(filePath.toFile());
            properties.load(fileReader);
            this.dyslexic = properties.getProperty("use_dyslexic_font", "false").equals("true");
            this.customHue = Double.parseDouble(properties.getProperty("custom_color", String.valueOf(this.customHue)));
            this.styleMode = StyleMode.values()[Integer.parseInt(properties.getProperty("style_file", "0"))];

            String localeTag = properties.getProperty("language");
            if(localeTag != null)
                this.setLanguage(Locale.forLanguageTag(localeTag));

            StyleHandler.getInstance().updateScene(scene);

        } catch (IOException e) {
            LoggerHandler.error("Exception while loading folder {} during initialisation",e,folderPath);
            SceneHandler.getInstance().createErrorMessage(ErrorType.FILE);
        }

    }
    private ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("com.progetto.progetto.lang.film",currentLanguage,MainApplication.class.getClassLoader());
    }

    public String getLocalizedString(String unlocalizedString)
    {
        try
        {
            return getResourceBundle().getString(unlocalizedString);
        }
        catch (Exception exception)
        {
            LoggerHandler.error("Error during localisation of the string {} to language {}",exception,unlocalizedString,currentLanguage);
            if(!unlocalizedString.equals(ErrorType.LOADING_PAGE.getUnlocalizedMessage())) //this is to avoid a potential loop
                SceneHandler.getInstance().createErrorMessage(ErrorType.LOADING_PAGE);
        }
        return null;
    }

    public void saveConfigurationOnFile(Properties properties) throws IOException {
        Path filePath = Path.of(getFolderPath() + File.separator + "config.txt");
        properties.setProperty("use_dyslexic_font", String.valueOf(this.dyslexic));
        properties.setProperty("style_file", String.valueOf(this.styleMode.ordinal()));
        properties.setProperty("custom_color", String.valueOf(this.customHue));
        properties.setProperty("language",this.getCurrentLanguage().toLanguageTag());
        properties.store(new FileWriter(filePath.toFile()), "FILM APP CONFIGURATION");
        saveCustomCSS();
    }

    public Locale getCurrentLanguage() {
        return currentLanguage;
    }

    public boolean setLanguage(Locale locale) {
        if (this.supportedLanguages.contains(locale)) {
            this.currentLanguage = locale;
            //this sets the interpreter language
            //with this even java libraries display elements in the correct language
            Locale.setDefault(locale);
            return true;
        }
        return false;
    }

    //this is called when we need to re-apply the style, in our cause this is called when a new scene is showed
    //or when we change the style configurations in runtime.
    public void updateScene(Scene scene) {
        if(scene == null)
            return;

        scene.getStylesheets().clear();
        //ADD NEW CSS PATH
        scene.getStylesheets().add(getCssPath(this.styleMode));

        scene.getStylesheets().add(String.valueOf(Objects.requireNonNull(MainApplication.class.getResource("css/base_style.css")).toExternalForm()));

        //--------dyslexic font----------
        String dyslexic_style = Objects.requireNonNull(MainApplication.class.getResource("css/dyslexic.css")).toExternalForm();
        if(this.dyslexic)
            scene.getStylesheets().add(dyslexic_style);
        else
            scene.getStylesheets().remove(dyslexic_style);

    }

    private void saveCustomCSS() throws IOException {
        URL cssCopy = Objects.requireNonNull(MainApplication.class.getResource("css/dark.css"));

        try {
            String cssString = Files.readString(Path.of(cssCopy.toURI()));
            cssString = cssString.replace("rgb(24,24,24)","hsb(" + this.customHue + ",50%,50%)");
            Files.writeString(Path.of(getFolderPath() + File.separator + "custom.css"),cssString);
        } catch (URISyntaxException e) {
            LoggerHandler.error("Malformed URI: {}",e,cssCopy);
            SceneHandler.getInstance().createErrorMessage(ErrorType.FILE);
        }

    }

    private String getCssPath(StyleMode styleMode) {
        if(styleMode != StyleMode.CUSTOM)
            return MainApplication.class.getResource("css/" + styleMode.getName() + ".css").toExternalForm();
        try {
            return Path.of(getFolderPath() + File.separator + "custom.css").toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            LoggerHandler.error("Malformed URL in cssPath",e);
            SceneHandler.getInstance().createErrorMessage(ErrorType.FILE);
        }
        return getCssPath(StyleMode.DARK);
    }
    public FontIcon createIcon(String value, int size)
    {
        FontIcon result = new FontIcon(value);
        result.setIconSize(size);
        return result;
    }
    private String getFolderPath()
    {
        return System.getProperty("user.home") + File.separator + ".film_app";
    }
}