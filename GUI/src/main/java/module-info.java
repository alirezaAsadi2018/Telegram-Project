module GUI {
    exports com.telegram.controllers;
    exports com.telegram.utility;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires java.logging;
    requires java.desktop;
    opens com.telegram.controllers to javafx.fxml;
}