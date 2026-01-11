module edu.rico.nbafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens edu.rico.nbafx to javafx.fxml;
    opens edu.rico.nbafx.controller to javafx.fxml;
    
    exports edu.rico.nbafx;
}