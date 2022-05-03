package com.progetto.progetto.model.sql;

import com.progetto.progetto.model.records.Film;
import com.progetto.progetto.model.records.Library;
import com.progetto.progetto.model.records.User;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SQLGetter {
    private static SQLGetter instance = new SQLGetter();
    private final MySQL mySQL;

    private SQLGetter() {
        this.mySQL = new MySQL("localhost","progettoui","root","PierSQL01?","3306",true);
    }

    public void createTables() {
        if (!mySQL.isConnected())
            return;
        try {
            PreparedStatement firstStatement = this.mySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS USER username varchar(50) primary key, password varchar(100) not null");
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

    public ResultSet makeQuery(String query, Object... objects) throws SQLException
    {

        PreparedStatement preparedStatement = SQLGetter.getInstance().getMySQL().getConnection().prepareStatement(query);
        for(int i = 0;i < objects.length;i++)
            preparedStatement.setObject(i + 1,objects[i]);
        return preparedStatement.executeQuery();
    }

    public int makeUpdate(String update, Object... objects) throws SQLException
    {
        PreparedStatement preparedStatement = SQLGetter.getInstance().getMySQL().getConnection().prepareStatement(update);
        for(int i = 0;i < objects.length;i++)
            preparedStatement.setObject(i + 1,objects[i]);
        return preparedStatement.executeUpdate();
    }



    //METODO SUPER GENERICO PER FARE TASK; NON E' UNA COSA SPECIFICA PER SQL, PER IL MOMENTO STA QUA
    //MA POTRA' ESSERE ELIMINATO/SPOSTATO
    //LO HO SCRITTO PER NON DIMENTICARE
    public <T> Task<T> makeTask(ITaskResult<T> taskResult)
    {
        Task<T> resultSetTask = new Task<>() {
            @Override
            protected T call() throws Exception {
                return taskResult.getTaskMethod(this);
            }
        };
        resultSetTask.setOnFailed(event -> taskResult.onFail(event.getSource().getException()));
        resultSetTask.setOnSucceeded(event -> taskResult.onSuccess(resultSetTask.getValue()));
        Thread thread = new Thread(resultSetTask);
        thread.start();
        return resultSetTask;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public static SQLGetter getInstance() {return instance;}
}
