package com.progetto.progetto.view;

import com.progetto.progetto.MainApplication;
import com.progetto.progetto.model.handlers.ProfileHandler;
import com.progetto.progetto.model.records.StyleConfiguration;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
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
import java.sql.SQLException;
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

    private StyleConfiguration styleConfiguration = new StyleConfiguration();


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
            this.styleConfiguration.dyslexic = properties.getProperty("use_dyslexic_font", "false").equals("true");
            this.styleConfiguration.foregroundColor = Color.web(properties.getProperty("primary_color", "0XFFFFFFFF"));
            this.styleConfiguration.backgroundColor = Color.web(properties.getProperty("secondary_color", "0XFFFFFFFF"));
            this.styleConfiguration.textColor = Color.web(properties.getProperty("text_color","0X00000000"));
            this.styleConfiguration.styleMode = StyleMode.values()[Integer.parseInt(properties.getProperty("style_file", "0"))];
            StyleHandler.getInstance().updateScene(scene);

        } catch (IOException e) {
        }

    }

    public void updateConfiguration(StyleConfiguration styleConfiguration)
    {
        this.styleConfiguration = styleConfiguration;
    }

    public StyleConfiguration getStyleConfiguration() {
        if(styleConfiguration == null)
            styleConfiguration = new StyleConfiguration();
        return styleConfiguration;
    }

    //this is called when we need to re-apply the style, in our cause this is called when a new scene is showed
    //or when we change the style configurations in runtime.
    public void updateScene(Scene scene) {
        //--------dyslexic font----------
        String dyslexic_style = Objects.requireNonNull(MainApplication.class.getResource("css/dyslexic.css")).toExternalForm();
        if(this.styleConfiguration.dyslexic)
            scene.getStylesheets().add(dyslexic_style);
        else
            scene.getStylesheets().remove(dyslexic_style);

        //DELETE (IF EXIST) THE OLD STYLES
        scene.getStylesheets().removeIf(styleString -> Arrays.stream(StyleMode.values()).anyMatch(styleMode -> styleString.endsWith(styleMode.getName() + ".css")));
        //ADD NEW CSS PATH
        scene.getStylesheets().add(getCssPath(this.styleConfiguration.styleMode));
    }


    public void saveConfigurationOnFile(Properties properties) throws IOException {
        Path filePath = Path.of(folder_path + File.separator + "config.txt");
        properties.setProperty("use_dyslexic_font", String.valueOf(this.styleConfiguration.dyslexic));
        properties.setProperty("style_file", String.valueOf(this.styleConfiguration.styleMode.ordinal()));
        properties.setProperty("primary_color", this.styleConfiguration.foregroundColor.toString());
        properties.setProperty("secondary_color", this.styleConfiguration.backgroundColor.toString());
        properties.setProperty("text_color", this.styleConfiguration.textColor.toString());
        properties.store(new FileWriter(filePath.toFile()), "FILM APP CONFIGURATION");
        saveCustomCSS();
    }

    public boolean saveConfigurationOnDatabase()
    {
        User user = ProfileHandler.getInstance().getLoggedUser();
        if(user == null)
            return false;

        String queryString = "update user set style_mode = ? , foregroundColor = ? , backgroundColor = ? , textColor = ? , dyslexic = ? where username = ?";

        int style_mode = this.styleConfiguration.styleMode.ordinal();
        String foregroundColor = this.styleConfiguration.foregroundColor.toString().substring(2,8);
        String backgroundColor = this.styleConfiguration.backgroundColor.toString().substring(2,8);
        String textColor = this.styleConfiguration.textColor.toString().substring(2,8);
        boolean dyslexic = this.styleConfiguration.dyslexic;

        try {
            SQLGetter.getInstance().makeUpdate(queryString,style_mode,foregroundColor,backgroundColor,textColor,dyslexic,user.username());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    private void saveCustomCSS() throws IOException {
        URL cssCopy = Objects.requireNonNull(MainApplication.class.getResource("css/dark.css"));

        try {
            String cssString = Files.readString(Path.of(cssCopy.toURI()));
            //DANNATAMENTE INEFFICENTE TODO: da fixare il substitute nel custom css
            cssString = cssString.replace("rgb(24,24,24)","#" + this.styleConfiguration.foregroundColor.toString().substring(2));
            cssString = cssString.replace("rgb(60,60,60)","#" + this.styleConfiguration.backgroundColor.toString().substring(2));
            cssString = cssString.replace("white","#" + this.styleConfiguration.textColor.toString().substring(2));
            Files.writeString(Path.of(folder_path + File.separator + getCustomCssFileName()),cssString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private String getCssPath(StyleMode styleMode) {
        if(styleMode != StyleMode.CUSTOM)
            return MainApplication.class.getResource("css/" + styleMode.getName() + ".css").toExternalForm();
        try {
            return Path.of(folder_path + File.separator + getCustomCssFileName()).toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return getCssPath(StyleMode.DARK);
    }
    private String getCustomCssFileName()
    {
        return ProfileHandler.getInstance().getLoggedUser() != null ? "profile_custom.css" : "custom.css";
    }
}