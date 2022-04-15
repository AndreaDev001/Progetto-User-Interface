package com.progetto.progetto.model;

import com.progetto.progetto.model.records.User;
import com.progetto.progetto.model.sql.SQLGetter;

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

    public boolean login(String username, String password,boolean bypass) {

        if(bypass)
            return true;

        if(loggedUser != null)
            return false;

        try {
            ResultSet result = SQLGetter.getInstance().makeQuery("SELECT * FROM USER WHERE name =?",username);
            if(result.next()) {
                String dbPassword = result.getString(1);
                if(dbPassword.equals(password)) {
                    this.loggedUser = new User(username, result.getString(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logout()
    {
        if(this.loggedUser != null)
        {
            this.loggedUser = null;
            return true;
        }
        return false;
    }
    public User getLoggedUser() {
        return this.loggedUser;
    }





}
