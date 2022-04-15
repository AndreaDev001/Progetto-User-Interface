package com.progetto.progetto.model;

import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;
import com.progetto.progetto.view.SceneHandler;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class AuthenticatorHandler
{
    private static AuthenticatorHandler instance = new AuthenticatorHandler();
    private List<User> users = new ArrayList<User>();

    private AuthenticatorHandler()
    {
        System.out.println("Instance of authenticator handler created correctly");
        users = SQLGetter.getInstance().loadUsers();
    }
    public boolean login(String username,String password,boolean byPass)
    {
        if(byPass)
            return true;
        if(users == null || users.isEmpty())
        {
            SceneHandler.getInstance().createAlertMessage("Impossibile entrare","Username o password entrati",Alert.AlertType.ERROR);
            return false;
        }
        for(User current : users)
        {
            if(current.username().equals(username) && current.password().equals(password))
                return true;
        }
        SceneHandler.getInstance().createAlertMessage("Impossibile entrare","Username o password errati", Alert.AlertType.ERROR);
        return false;
    }
    public static AuthenticatorHandler getInstance() {return instance;}
    public List<User> getUsers() {return users;}
}
