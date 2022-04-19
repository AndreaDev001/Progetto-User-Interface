package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

//STYLE HANDLER CLASS written by Pierlugi, aka PierKnight
//FOR the moment the config file is saved in the home folder inside a directory named "film_app"
public class StyleHandler
{
    private static final StyleHandler STYLE_HANDLER = new StyleHandler();

    public static StyleHandler getInstance()
    {
        return STYLE_HANDLER;
    }

    private final String folder_path = System.getProperty("user.home") + File.separator + ".film_app";

    private final ObjectProperty<StyleMode> currentStyle = new SimpleObjectProperty<>(StyleMode.DARK);
    private final ObjectProperty<Color> primaryColor = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> secondaryColor = new SimpleObjectProperty<>(Color.WHITE);
    private final BooleanProperty dyslexicFont = new SimpleBooleanProperty();


    private StyleHandler(){}

    //read the config file and update the scene along with it
    public void init(Scene scene)
    {
        try
        {
            Path folderPath = Path.of(folder_path);
            Files.createDirectories(folderPath);

            Path filePath = Path.of(folder_path + File.separator + "config.txt");
            Properties properties = new Properties();

            if(!Files.exists(filePath))
                saveConfigurationOnFile(properties);

            FileReader fileReader = new FileReader(filePath.toFile());
            properties.load(fileReader);
            this.dyslexicFont.set(properties.getProperty("use_dyslexic_font","false").equals("true"));
            this.primaryColor.set(Color.web(properties.getProperty("primary_color","0XFFFFFF")));
            this.secondaryColor.set(Color.web(properties.getProperty("secondary_color","0XFFFFFF")));
            this.currentStyle.set(StyleMode.values()[Integer.parseInt(properties.getProperty("style_file","0"))]);

            StyleHandler.getInstance().updateScene(scene);

        }  catch (IOException e)
        {
        }

    }


    //this is called when we need to re-apply the style, in our cause this is called when a new scene is showed
    //or when we change the style configurations in runtime.
    public void updateScene(Scene scene)
    {
        //--------dyslexic font----------
        /* String dyslexic_style = Objects.requireNonNull(MainApplication.class.getResource("css/dyslexic_font.css")).toExternalForm();
        if(this.dyslexicFont.get())
            scene.getStylesheets().add(dyslexic_style);
        else
            scene.getStylesheets().remove(dyslexic_style);
         */
    }

    public void saveConfigurationOnFile(Properties properties) throws IOException
    {
        Path filePath = Path.of(folder_path + File.separator + "config.txt");
        properties.setProperty("use_dyslexic_font",String.valueOf(this.dyslexicFont.get()));
        properties.setProperty("style_file", String.valueOf(this.currentStyle.get().ordinal()));
        properties.setProperty("primary_color", primaryColor.get().toString());
        properties.setProperty("secondary_color", secondaryColor.get().toString());
        properties.store(new FileWriter(filePath.toFile()),"Configurazione app film");
    }

    public ObjectProperty<StyleMode> getCurrentStyle()
    {
        return currentStyle;
    }

    public ObjectProperty<Color> getPrimaryColor()
    {
        return primaryColor;
    }

    public ObjectProperty<Color> getSecondaryColor()
    {
        return secondaryColor;
    }

    public BooleanProperty getDyslexicFont()
    {
        return this.dyslexicFont;
    }



    private String getFilePath(StyleMode styleMode)
    {
        switch(styleMode)
        {
            case LIGHT -> {
                return MainApplication.class.getResource("css/light.css").toExternalForm();
            }
            case DARK -> {
                return MainApplication.class.getResource("css/dark.css").toExternalForm();
            }
            case CUSTOM -> {
                return folder_path + File.separator + "custom.css";
            }
        }
        return null;
    }
}
