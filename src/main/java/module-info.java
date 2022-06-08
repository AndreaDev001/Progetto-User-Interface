module com.progetto.progetto {
    requires javafx.controls;
    requires javafx.fxml;
    requires themoviedbapi;
    requires org.kordamp.ikonli.javafx;
    requires json;
    requires org.slf4j.simple;
    requires java.logging;

    opens com.progetto.progetto to javafx.fxml;
    exports com.progetto.progetto;
    exports com.progetto.progetto.controller;
    opens com.progetto.progetto.controller to javafx.fxml;
    opens com.progetto.progetto.view to javafx.fxml;
    opens com.progetto.progetto.model.handlers to javafx.fxml;
}