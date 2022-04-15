package com.progetto.progetto.model.sql;

import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGetter {
    private static SQLGetter instance = new SQLGetter();
    private final MySQL mySQL;

    private SQLGetter() {
        this.mySQL = new MySQL("","","","","",true);
        //this.mySQL.Connect();
        createTables();
    }
    public void createTables() {
        if (!mySQL.isConnected())
            return;
        try {
            PreparedStatement firstStatement = this.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS USER id int auto_increment primary key, username varchar(50) not null, password varchar(50) not null");
            PreparedStatement secondStatement = this.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS LIBRARY id int primary key, foreign key (id) references Utente(id)");
            PreparedStatement thirdStatement = this.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS FILM api_id int primary key,title varchar(100) not null, description varchar(100) not null, poster varchar(100) not null");
            PreparedStatement fourthStatement = this.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS LIBRARY library_id int references Library(id),film_id int references Film(api_id), primary key (library_id,film_id)");
            firstStatement.executeUpdate();
            secondStatement.executeUpdate();
            thirdStatement.executeUpdate();
            fourthStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(String tableName, String primaryKey, int value) {
        if (!mySQL.isConnected())
            return;
        try {
            PreparedStatement preparedStatement = this.mySQL.getConnection().prepareStatement("DELETE FROM" + " " + tableName + " " + "WHERE" + " " + primaryKey +  "=?");
            preparedStatement.setInt(1, value);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String tableName, String primaryKey, int value) {
        if (!mySQL.isConnected())
            return false;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.mySQL.getConnection().prepareStatement("SELECT * FROM" + " " + tableName + " " + "WHERE" + primaryKey + "=?");
            preparedStatement.setInt(1, value);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getNumberOfRows(String tableName)
    {
        int result = 0;
        if(!mySQL.isConnected())
            return result;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.mySQL.getConnection().prepareStatement("SELECT COUNT(*) FROM" + " " + tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                result = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<User> loadUsers() {
        if (!mySQL.isConnected())
            return null;
        List<User> users = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.mySQL.getConnection().prepareStatement("SELECT * FROM USERS");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String username = resultSet.getString(2);
                String password = resultSet.getString(3);
                User user = new User(id, username, password);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    public List<Film> loadFilms(){
        if(!mySQL.isConnected())
            return null;
        List<Film> films = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.mySQL.getConnection().prepareStatement("SELECT * FROM FILM");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                int id = resultSet.getInt(1);
                String title = resultSet.getString(2);
                String description = resultSet.getString(3);
                String poster = resultSet.getString(4);
                Film film = new Film(id,title,description,poster);
                films.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }
    public List<Library> loadLibraries()
    {
        if(!mySQL.isConnected())
            return null;
        List<Library> libraries = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.mySQL.getConnection().prepareStatement("SELECT * FROM LIBRARY");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next())
            {
                int id = resultSet.getInt(1);
                Library library = new Library(1);
                libraries.add(library);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return libraries;
    }
    public static SQLGetter getInstance() {return instance;}
}
