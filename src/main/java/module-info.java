module com.example.comp2411project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.comp2411project to javafx.fxml;
    exports com.example.comp2411project;
}