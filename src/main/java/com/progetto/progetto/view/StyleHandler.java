package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.records.StyleConfiguration;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

//STYLE HANDLER CLASS written by Pierlugi, aka PierKnight
//FOR the moment the config file is saved in the home folder inside a directory named "film_app"
public class StyleHandler {

    private static final StyleHandler STYLE_HANDLER = new StyleHandler();

    public static StyleHandler getInstance() {
        return STYLE_HANDLER;
    }

    private StyleConfiguration styleConfiguration = new StyleConfiguration();


    private StyleHandler() {
    }

    //read the config file and update the scene along with it
    public void init(Scene scene) {
        try {

            Path folderPath = Path.of(getFolderPath());
            Files.createDirectories(folderPath);

            Path filePath = Path.of(getFolderPath() + File.separator + "config.txt");
            Properties properties = new Properties();

            if (!Files.exists(filePath))
                saveConfigurationOnFile(properties);

            if(scene == null)
                return;

            FileReader fileReader = new FileReader(filePath.toFile());
            properties.load(fileReader);
            this.styleConfiguration.dyslexic = properties.getProperty("use_dyslexic_font", "false").equals("true");
            this.styleConfiguration.foregroundColor = Color.web(properties.getProperty("primary_color", "0XFFFFFFFF"));
            this.styleConfiguration.backgroundColor = Color.web(properties.getProperty("secondary_color", "0XFFFFFFFF"));
            this.styleConfiguration.textColor = Color.web(properties.getProperty("text_color","0X00000000"));
            this.styleConfiguration.styleMode = StyleMode.values()[Integer.parseInt(properties.getProperty("style_file", "0"))];
            StyleHandler.getInstance().updateScene(scene);


            //LOAD FONT FOR DYSLEXIC
            //Font.loadFont(Objects.requireNonNull(MainApplication.class.getResource("fonts/OpenDyslexic3-Regular.ttf")).toExternalForm(),10);

        } catch (IOException e) {
        }

    }

    public StyleConfiguration getStyleConfiguration() {
        if(styleConfiguration == null)
            styleConfiguration = new StyleConfiguration();
        return styleConfiguration;
    }

    //this is called when we need to re-apply the style, in our cause this is called when a new scene is showed
    //or when we change the style configurations in runtime.
    public void updateScene(Scene scene) {
        scene.getStylesheets().clear();
        //ADD NEW CSS PATH
        scene.getStylesheets().add(getCssPath(this.styleConfiguration.styleMode));

        //--------dyslexic font----------
        String dyslexic_style = Objects.requireNonNull(MainApplication.class.getResource("css/dyslexic.css")).toExternalForm();
        if(this.styleConfiguration.dyslexic)
            scene.getStylesheets().add(dyslexic_style);
        else
            scene.getStylesheets().remove(dyslexic_style);
    }


    public void saveConfigurationOnFile(Properties properties) throws IOException {
        Path filePath = Path.of(getFolderPath() + File.separator + "config.txt");
        properties.setProperty("use_dyslexic_font", String.valueOf(this.styleConfiguration.dyslexic));
        properties.setProperty("style_file", String.valueOf(this.styleConfiguration.styleMode.ordinal()));
        properties.setProperty("primary_color", this.styleConfiguration.foregroundColor.toString());
        properties.setProperty("secondary_color", this.styleConfiguration.backgroundColor.toString());
        properties.setProperty("text_color", this.styleConfiguration.textColor.toString());
        properties.store(new FileWriter(filePath.toFile()), "FILM APP CONFIGURATION");
        saveCustomCSS();
    }
    private void saveCustomCSS() throws IOException {
        URL cssCopy = Objects.requireNonNull(MainApplication.class.getResource("css/dark.css"));

        try {
            String cssString = Files.readString(Path.of(cssCopy.toURI()));
            //DANNATAMENTE INEFFICENTE TODO: da fixare il substitute nel custom css
            cssString = cssString.replace("rgb(24,24,24)","#" + this.styleConfiguration.foregroundColor.toString().substring(2));
            cssString = cssString.replace("rgb(60,60,60)","#" + this.styleConfiguration.backgroundColor.toString().substring(2));
            cssString = cssString.replace("white","#" + this.styleConfiguration.textColor.toString().substring(2));
            Files.writeString(Path.of(getFolderPath() + File.separator + "custom.css"),cssString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private String getCssPath(StyleMode styleMode) {
        if(styleMode != StyleMode.CUSTOM)
            return MainApplication.class.getResource("css/" + styleMode.getName() + ".css").toExternalForm();
        try {
            return Path.of(getFolderPath() + File.separator + "custom.css").toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getCssPath(StyleMode.DARK);
    }


    private String getFolderPath()
    {
        return System.getProperty("user.home") + File.separator + ".film_app";
    }
}