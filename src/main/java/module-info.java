/**
 * Definición del módulo para la aplicación NbaFX.
 * <p>
 * Este archivo configura las dependencias y permisos de acceso necesarios
 * para que la aplicación funcione bajo la arquitectura modular de Java,
 * eliminando la necesidad de una clase Launcher auxiliar.
 */
module edu.rico.nbafx {
    // Módulos de JavaFX necesarios
    requires javafx.controls;
    requires javafx.fxml;
    
    // Módulo para acceso a Base de Datos (JDBC)
    requires java.sql;

    // Permitir que JavaFX acceda a los controladores y a la clase principal (reflexión)
    opens edu.rico.nbafx to javafx.fxml;
    opens edu.rico.nbafx.controller to javafx.fxml;

    // Exportar el paquete principal para que pueda ser iniciado
    exports edu.rico.nbafx;
}