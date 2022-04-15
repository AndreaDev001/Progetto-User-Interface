package com.progetto.progetto.model.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL
{
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final String port;
    private final boolean useSSL;
    private Connection connection;

    public MySQL(String host,String database,String username,String password,String port,boolean useSSL)
    {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
        this.useSSL = useSSL;
    }
    public void Connect()
    {
        String usesSSL = useSSL ? "?useSSL=true" : "?useSSL=false";
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connected");
    }
    public void Disconnect()
    {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isConnected() {return connection != null;}
    public final String getHost() {return host;}
    public final String getDatabase() {return database;}
    public final String getUsername() {return username;}
    public final String getPassword() {return password;}
    public final String getPort() {return port;}
    public boolean usesSSL() {return useSSL;}
    public Connection getConnection() {return connection;}
}
