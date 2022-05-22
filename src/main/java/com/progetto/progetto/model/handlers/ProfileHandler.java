package com.progetto.progetto.model.handlers;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Alert;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class ProfileHandler {
    private static final ProfileHandler instance = new ProfileHandler();

    private ProfileHandler() {
    }

    public static ProfileHandler getInstance() {
        return instance;
    }

    private final ReadOnlyObjectWrapper<User> loggedUserProperty = new ReadOnlyObjectWrapper<>(null);
    public boolean login(String username, String password, boolean bypass) {

        if (bypass)
            return true;

        if (loggedUserProperty.get() != null)
            return false;

        try
        {
            /**ResultSet result = SQLGetter.getInstance().makeQuery("SELECT * FROM USER WHERE username = ?", username);
            if (result.next()) {
                String dbPassword = result.getString(2);
                if (BCrypt.checkpw(password,dbPassword)) {
                    this.loggedUserProperty.set(new User(username));
                    return true;
                }
                SceneHandler.getInstance().createAlertMessage("ERROR!","Invalid Password", Alert.AlertType.ERROR);
            }
            else
                SceneHandler.getInstance().createAlertMessage("ERROR!","Invalid Username", Alert.AlertType.ERROR);
             **/
            Client.getInstance().login(username,password);
            return true;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            SceneHandler.getInstance().createAlertMessage("ERROR!","Invalid User",Alert.AlertType.ERROR);
        }
        return false;
    }

    public boolean logout() throws IOException, ConnectionException {
        if (this.loggedUserProperty.get() != null)
        {
            Client.getInstance().logout();
            this.loggedUserProperty.set(null);
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

    public ReadOnlyObjectProperty<User> getLoggedUser() {
        return this.loggedUserProperty.getReadOnlyProperty();
    }





}
