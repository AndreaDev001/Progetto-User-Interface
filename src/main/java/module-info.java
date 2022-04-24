module com.progetto.progetto {
    requires javafx.controls;
    requires javafx.fxml;
    requires themoviedbapi;
    requires java.sql;
    requires spring.security.crypto;


    opens com.progetto.progetto to javafx.fxml;
    exports com.progetto.progetto;
    exports com.progetto.progetto.controller;
    opens com.progetto.progetto.controller to javafx.fxml;
}