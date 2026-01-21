package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Jugador;
import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

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
    @FXML private HBox accionesContainer; // Contenedor de botones

    private Jugador jugador;
    private Consumer<Jugador> onEditarListener;
    private Consumer<Jugador> onEliminarListener;
    
    private static final String DEFAULT_IMAGE_RESOURCE = "/imagenes/default.png";
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

    /**
     * Configura la visibilidad de los botones de acción según el rol del usuario.
     * @param currentUser El usuario actual de la sesión.
     */
    public void configurarPermisos(Usuario currentUser) {
        if (currentUser != null && currentUser.getRol() == Rol.USER) {
            accionesContainer.setVisible(false);
            accionesContainer.setManaged(false);
        } else {
            accionesContainer.setVisible(true);
            accionesContainer.setManaged(true);
        }
    }

    private void cargarImagen(String url) {
        String urlParaCargar = null;

        if (url != null && !url.trim().isEmpty()) {
            String urlTrimmed = url.trim();
            
            if (urlTrimmed.startsWith("http")) {
                urlParaCargar = urlTrimmed;
            } else if (urlTrimmed.startsWith("file:")) {
                urlParaCargar = urlTrimmed;
            } else {
                try {
                    File file = new File(urlTrimmed);
                    if (!file.isAbsolute()) {
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

        if (urlParaCargar == null) {
            URL resource = getClass().getResource(DEFAULT_IMAGE_RESOURCE);
            if (resource != null) {
                urlParaCargar = resource.toExternalForm();
            } else {
                urlParaCargar = FALLBACK_ONLINE;
            }
        }

        final String finalUrl = urlParaCargar;

        Image image = new Image(finalUrl, 150, 150, true, true, true);
        
        image.errorProperty().addListener((obs, oldErr, newErr) -> {
            if (newErr) {
                System.err.println("Error cargando imagen: " + finalUrl);
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