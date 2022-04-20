package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

//STYLE HANDLER CLASS written by Pierlugi, aka PierKnight
//FOR the moment the config file is saved in the home folder inside a directory named "film_app"
public class StyleHandler {
    private static final StyleHandler STYLE_HANDLER = new StyleHandler();

    public static StyleHandler getInstance() {
        return STYLE_HANDLER;
    }

    private final String folder_path = System.getProperty("user.home") + File.separator + ".film_app";

    private final ObjectProperty<StyleMode> currentStyle = new SimpleObjectProperty<>(StyleMode.DARK);
    private final ObjectProperty<Color> primaryColor = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> secondaryColor = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> textColor = new SimpleObjectProperty<>(Color.BLACK);
    private final BooleanProperty dyslexicFont = new SimpleBooleanProperty();


    private StyleHandler() {
    }

    //read the config file and update the scene along with it
    public void init(Scene scene) {
        try {
            Path folderPath = Path.of(folder_path);
            Files.createDirectories(folderPath);

            Path filePath = Path.of(folder_path + File.separator + "config.txt");
            Properties properties = new Properties();

            if (!Files.exists(filePath))
                saveConfigurationOnFile(properties);

            FileReader fileReader = new FileReader(filePath.toFile());
            properties.load(fileReader);
            this.dyslexicFont.set(properties.getProperty("use_dyslexic_font", "false").equals("true"));
            this.primaryColor.set(Color.web(properties.getProperty("primary_color", "0XFFFFFFFF")));
            this.secondaryColor.set(Color.web(properties.getProperty("secondary_color", "0XFFFFFFFF")));
            this.textColor.set(Color.web(properties.getProperty("text_color","0X00000000")));
            this.currentStyle.set(StyleMode.values()[Integer.parseInt(properties.getProperty("style_file", "0"))]);

            StyleHandler.getInstance().updateScene(scene);

        } catch (IOException e) {
        }

    }


    //this is called when we need to re-apply the style, in our cause this is called when a new scene is showed
    //or when we change the style configurations in runtime.
    public void updateScene(Scene scene) {
        //--------dyslexic font----------
        String dyslexic_style = Objects.requireNonNull(MainApplication.class.getResource("css/dyslexic.css")).toExternalForm();
        if(this.dyslexicFont.get())
            scene.getStylesheets().add(dyslexic_style);
        else
            scene.getStylesheets().remove(dyslexic_style);

        //DELETE (IF EXIST) THE OLD STYLES
        scene.getStylesheets().removeIf(styleString -> Arrays.stream(StyleMode.values()).anyMatch(styleMode -> styleString.endsWith(styleMode.getName() + ".css")));
        //ADD NEW CSS PATH
        scene.getStylesheets().add(getCssPath(this.currentStyle.get()));

    }

    public void saveConfigurationOnFile(Properties properties) throws IOException {
        Path filePath = Path.of(folder_path + File.separator + "config.txt");
        properties.setProperty("use_dyslexic_font", String.valueOf(this.dyslexicFont.get()));
        properties.setProperty("style_file", String.valueOf(this.currentStyle.get().ordinal()));
        properties.setProperty("primary_color", primaryColor.get().toString());
        properties.setProperty("secondary_color", secondaryColor.get().toString());
        properties.setProperty("text_color", textColor.get().toString());
        properties.store(new FileWriter(filePath.toFile()), "FILM APP CONFIGURATION");

        saveCustomCSS();

    }

    public void saveCustomCSS() throws IOException {
        URL cssCopy = Objects.requireNonNull(MainApplication.class.getResource("css/dark.css"));

        try {
            String cssString = Files.readString(Path.of(cssCopy.toURI()));
            //DANNATAMENTE INEFFICENTE TODO: da fixare il substitute nel custom css
            cssString = cssString.replace("rgb(24,24,24)","#" + getPrimaryColor().get().toString().substring(2));
            cssString = cssString.replace("rgb(60,60,60)","#" + getSecondaryColor().get().toString().substring(2));
            cssString = cssString.replace("white","#" + getTextColor().get().toString().substring(2));
            Files.writeString(Path.of(folder_path + File.separator + "custom.css"),cssString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public ObjectProperty<StyleMode> getCurrentStyle() {
        return currentStyle;
    }

    public ObjectProperty<Color> getPrimaryColor() {
        return primaryColor;
    }

    public ObjectProperty<Color> getSecondaryColor() {
        return secondaryColor;
    }

    public ObjectProperty<Color> getTextColor() {
        return textColor;
    }

    public BooleanProperty getDyslexicFont() {
        return this.dyslexicFont;
    }




    private String getCssPath(StyleMode styleMode) {
        if(styleMode != StyleMode.CUSTOM)
            return MainApplication.class.getResource("css/" + styleMode.getName() + ".css").toExternalForm();
        try {
            return Path.of(folder_path + File.separator + "custom.css").toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getCssPath(StyleMode.DARK);
    }
}