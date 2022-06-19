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
    exports com.progetto.progetto.model.enums;
    opens com.progetto.progetto.controller to javafx.fxml;
    opens com.progetto.progetto.view to javafx.fxml;
    opens com.progetto.progetto.model.handlers to javafx.fxml;
    opens com.progetto.progetto.model.enums to javafx.fxml;
    opens com.progetto.progetto.model.handlers.listeners to javafx.fxml;
    opens com.progetto.progetto.view.nodes to javafx.fxml;
}