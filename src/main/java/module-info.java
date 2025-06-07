module com.example.projectoop {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.projectoop to javafx.fxml;
    opens Test2.javafx.view to javafx.fxml;
    opens Test2.Java.model to java.base;

    exports com.example.projectoop;
    exports Test1;
    exports Test2.Java.model;
    exports Test2.Java.util;
    exports Test2.javafx;
    exports Test2.javafx.view;


}