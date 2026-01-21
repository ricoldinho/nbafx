package edu.rico.nbafx;

import edu.rico.nbafx.util.AppShell;
import edu.rico.nbafx.util.View;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializamos el AppShell con el Stage principal
        AppShell.getInstance().init(stage);
        
        // Cargamos la vista inicial (Login)
        AppShell.getInstance().loadView(View.LOGIN);
    }

    public static void main(String[] args) {
        launch();
    }
}
