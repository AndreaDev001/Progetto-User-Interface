package com.progetto.progetto.model.handlers;

import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            ResultSet result = SQLGetter.getInstance().makeQuery("SELECT * FROM USER WHERE name =?", username);
            if (result.next()) {
                String dbPassword = result.getString(1);
                if (dbPassword.equals(password)) {
                    this.loggedUser = new User(username, result.getString(1));
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
            SQLGetter.getInstance().makeUpdate("INSERT INTO USER (name,password) VALUES (?,?)");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getLoggedUser() {
        return this.loggedUser;
    }





}
