module com.progetto.progetto {
    requires javafx.controls;
    requires javafx.fxml;
    requires themoviedbapi;
    requires java.sql;
    requires spring.security.crypto;
    requires org.kordamp.ikonli.javafx;
    requires json;


    opens com.progetto.progetto to javafx.fxml;
    exports com.progetto.progetto;
    exports com.progetto.progetto.controller;
    opens com.progetto.progetto.controller to javafx.fxml;
    opens com.progetto.progetto.view to javafx.fxml;
}