package edu.rico.nbafx.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos.
 * Implementa el patrón Singleton para asegurar una única instancia de conexión compartida.
 */
public class DatabaseConnection {
    
    private static Connection connection = null;
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final Properties PROPERTIES = new Properties();

    // Carga estática de la configuración para hacerlo solo una vez
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            } else {
                LOGGER.log(Level.SEVERE, "No se encontró el archivo config.properties");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar la configuración de base de datos", ex);
        }
    }

    /**
     * Constructor privado para evitar instanciación de la clase utilitaria.
     */
    private DatabaseConnection() { }

    /**
     * Obtiene la conexión actual a la base de datos.
     * Si la conexión no existe o está cerrada, intenta establecer una nueva.
     * El método es sincronizado para garantizar seguridad en entornos multihilo (Tasks).
     *
     * @return La conexión activa a la base de datos o null si ocurre un error.
     */
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        PROPERTIES.getProperty("db.url"),
                        PROPERTIES.getProperty("db.user"),
                        PROPERTIES.getProperty("db.password"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con la base de datos", e);
        }
        return connection;
    }
}