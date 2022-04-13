module com.progetto.progetto {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.progetto.progetto to javafx.fxml;
    exports com.progetto.progetto;
}