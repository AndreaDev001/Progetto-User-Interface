package com.progetto.progetto.model.handlers;

import com.progetto.progetto.client.Client;
import com.progetto.progetto.client.ConnectionException;
import com.progetto.progetto.model.records.User;
import com.progetto.progetto.view.SceneHandler;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Alert;

import java.io.IOException;

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
            this.loggedUserProperty.set(new User(username));
            return true;
        }
        catch (Exception exception)
        {
            this.loggedUserProperty.set(null);
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


    public ReadOnlyObjectProperty<User> getLoggedUser() {
        return this.loggedUserProperty.getReadOnlyProperty();
    }





}
