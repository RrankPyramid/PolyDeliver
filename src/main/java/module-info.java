module com.example.comp2411project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires MaterialFX;
    requires ojdbc8;
    requires javafx.plus;

    opens com.example.comp2411project to javafx.fxml;
    exports com.example.comp2411project;
}