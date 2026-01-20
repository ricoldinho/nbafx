package edu.rico.nbafx.util;

import edu.rico.nbafx.model.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase AppShell que implementa el patrón Singleton para gestionar la navegación
 * y la carga de vistas en la aplicación. Actúa como el contenedor principal.
 */
public class AppShell {

    private static AppShell instance;
    private Stage primaryStage;
    private BorderPane mainLayout;
    // Cache para almacenar los controladores si se desea mantener estado (opcional)
    private Map<View, Object> controllers = new HashMap<>();
    
    // Usuario actualmente logueado (Sesión)
    private Usuario currentUser;

    private AppShell() {}

    /**
     * Obtiene la instancia única del AppShell.
     * @return La instancia de AppShell.
     */
    public static AppShell getInstance() {
        if (instance == null) {
            instance = new AppShell();
        }
        return instance;
    }

    /**
     * Inicializa el AppShell con el Stage principal.
     * @param stage El Stage principal de la aplicación JavaFX.
     */
    public void init(Stage stage) {
        this.primaryStage = stage;
        this.mainLayout = new BorderPane();
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
    }

    /**
     * Carga y muestra una vista en el centro del layout principal.
     *
     * @param view El enumerado de la vista a cargar.
     * @return El controlador asociado a la vista cargada, o null si hubo error.
     */
    public Object loadView(View view) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlPath()));
            Parent viewNode = loader.load();
            
            // Actualizamos el título de la ventana
            primaryStage.setTitle(view.getTitle());
            
            // Reemplazamos la raíz de la escena actual.
            primaryStage.getScene().setRoot(viewNode);
            
            primaryStage.show();
            primaryStage.centerOnScreen();

            Object controller = loader.getController();
            controllers.put(view, controller);
            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método auxiliar para obtener el Stage principal si es necesario (ej. para diálogos modales).
     * @return El Stage principal.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Usuario getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Usuario currentUser) {
        this.currentUser = currentUser;
    }
}