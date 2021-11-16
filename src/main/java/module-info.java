module com.example.comp2411project {
    requires javafx.controls;
    requires javafx.fxml;
    requires ojdbc7;
    requires java.sql;


    opens com.example.comp2411project to javafx.fxml;
    exports com.example.comp2411project;
}