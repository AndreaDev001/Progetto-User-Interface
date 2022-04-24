package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.records.StyleConfiguration;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.ITaskResult;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import com.progetto.progetto.view.StyleHandler;
import com.progetto.progetto.view.StyleMode;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;

public class ProfileHandler {
    private static final ProfileHandler instance = new ProfileHandler();

    private ProfileHandler() {
    }

    public static ProfileHandler getInstance() {
        return instance;
    }

    private User loggedUser;

    public boolean login(String username, String password, boolean bypass) {

        if (bypass)
            return true;

        if (loggedUser != null)
            return false;

        try {
            ResultSet result = SQLGetter.getInstance().makeQuery("SELECT * FROM USER WHERE username = ?", username);
            if (result.next()) {
                String dbPassword = result.getString(2);
                if (BCrypt.checkpw(password,dbPassword)) {
                    StyleMode styleMode = StyleMode.values()[result.getInt(3)];
                    Color foregroundColor = Color.web("#" + result.getString(4));
                    Color backgroundColor = Color.web("#" + result.getString(5));
                    Color textColor = Color.web("#" + result.getString(6));
                    boolean dyslexic = result.getBoolean(7);
                    this.loggedUser = new User(username);
                    StyleConfiguration styleConfiguration = new StyleConfiguration(styleMode,foregroundColor,backgroundColor,textColor,dyslexic);
                    StyleHandler.getInstance().updateConfiguration(styleConfiguration);
                    StyleHandler.getInstance().init(null);
                    return true;
                }
                SceneHandler.getInstance().createAlertMessage("ERROR!","Invalid Password", Alert.AlertType.ERROR);
            }
            else
                SceneHandler.getInstance().createAlertMessage("ERROR!","Invalid Username", Alert.AlertType.ERROR);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean logout() {
        if (this.loggedUser != null) {
            this.loggedUser = null;
            return true;
        }
        return false;
    }

    public boolean createUser(String username, String password)
    {

        try {
            SQLGetter.getInstance().makeUpdate("INSERT INTO USER(username,password) VALUES (?,?)",username,password);
            SceneHandler.getInstance().createAlertMessage("SUCCESS!","Created a new account!", Alert.AlertType.INFORMATION);
            return true;
        }
        catch (SQLIntegrityConstraintViolationException e)//this is called when trying to add an already used username.
        {
            SceneHandler.getInstance().createAlertMessage("ERROR","Username already used!", Alert.AlertType.ERROR);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }





}
