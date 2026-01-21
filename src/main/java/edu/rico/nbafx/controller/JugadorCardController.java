package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Jugador;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * Controlador para el componente individual de tarjeta de jugador.
 */
public class JugadorCardController {

    @FXML private ImageView imagenJugador;
    @FXML private Label lblNombre;
    @FXML private Label lblEquipo;
    @FXML private Label lblPosicion;
    @FXML private Label lblFisico;
    @FXML private Label lblAnillos;

    private Jugador jugador;
    private Consumer<Jugador> onEditarListener;
    private Consumer<Jugador> onEliminarListener;
    
    // Ruta al recurso en el classpath (src/main/resources/imagenes/default.png)
    private static final String DEFAULT_IMAGE_RESOURCE = "/imagenes/default.png";
    // Fallback online por si no existe el archivo local
    private static final String FALLBACK_ONLINE = "https://via.placeholder.com/150";

    public void setJugador(Jugador jugador, Consumer<Jugador> onEditar, Consumer<Jugador> onEliminar) {
        this.jugador = jugador;
        this.onEditarListener = onEditar;
        this.onEliminarListener = onEliminar;

        lblNombre.setText(jugador.getNombre());
        lblEquipo.setText(jugador.getEquipo() + " - #" + jugador.getDorsal());
        lblPosicion.setText("Posición: " + jugador.getPosicion());
        lblFisico.setText(String.format("Altura: %.2fm | Peso: %.1fkg", jugador.getAltura(), jugador.getPeso()));
        lblAnillos.setText("Anillos: " + jugador.getNumeroAnillos());

        cargarImagen(jugador.getImageUrl());
    }

    private void cargarImagen(String url) {
        String urlParaCargar = null;

        // 1. Intentar usar la URL del jugador si existe
        if (url != null && !url.trim().isEmpty()) {
            String urlTrimmed = url.trim();
            
            if (urlTrimmed.startsWith("http")) {
                urlParaCargar = urlTrimmed;
            } else if (urlTrimmed.startsWith("file:")) {
                urlParaCargar = urlTrimmed;
            } else {
                // Ruta local (relativa o absoluta)
                try {
                    File file = new File(urlTrimmed);
                    if (!file.isAbsolute()) {
                        // Resolver ruta relativa (ej: "imagenes/foto.jpg") respecto al directorio del proyecto
                        file = Paths.get(System.getProperty("user.dir"), urlTrimmed).toFile();
                    }

                    if (file.exists()) {
                        urlParaCargar = file.toURI().toString();
                    } else {
                        System.err.println("Imagen local no encontrada: " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 2. Si no hay URL válida, usar la imagen por defecto del classpath
        if (urlParaCargar == null) {
            URL resource = getClass().getResource(DEFAULT_IMAGE_RESOURCE);
            if (resource != null) {
                urlParaCargar = resource.toExternalForm();
            } else {
                // Si no existe default.png, usar placeholder online
                System.err.println("No se encontró " + DEFAULT_IMAGE_RESOURCE + " en resources.");
                urlParaCargar = FALLBACK_ONLINE;
            }
        }

        final String finalUrl = urlParaCargar;

        // Carga asíncrona
        Image image = new Image(finalUrl, 150, 150, true, true, true);
        
        image.errorProperty().addListener((obs, oldErr, newErr) -> {
            if (newErr) {
                System.err.println("Error cargando imagen: " + finalUrl);
                // Si falla la carga, intentar cargar el default del classpath
                Platform.runLater(() -> {
                    URL res = getClass().getResource(DEFAULT_IMAGE_RESOURCE);
                    if (res != null) {
                        imagenJugador.setImage(new Image(res.toExternalForm()));
                    } else {
                        imagenJugador.setImage(new Image(FALLBACK_ONLINE));
                    }
                });
            }
        });

        imagenJugador.setImage(image);
    }

    @FXML
    private void handleEditar() {
        if (onEditarListener != null) onEditarListener.accept(jugador);
    }

    @FXML
    private void handleEliminar() {
        if (onEliminarListener != null) onEliminarListener.accept(jugador);
    }
}