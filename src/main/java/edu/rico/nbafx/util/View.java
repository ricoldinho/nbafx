package edu.rico.nbafx.util;

/**
 * Enumerado que define las vistas disponibles en la aplicación.
 * Contiene la ruta al archivo FXML y el título de la ventana.
 */
public enum View {
    LOGIN("/fxml/login_view.fxml", "Login System"),
    USUARIOS("/fxml/usuarios-view.fxml", "Gestión de Usuarios - NBA FX");

    private final String fxmlPath;
    private final String title;

    View(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
}