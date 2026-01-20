package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Jugador;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/150";

    /**
     * Configura los datos de la tarjeta.
     *
     * @param jugador El objeto Jugador a mostrar.
     * @param onEditar Callback para la acción de editar.
     * @param onEliminar Callback para la acción de eliminar.
     */
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
        String imageUrl = (url != null && !url.trim().isEmpty()) ? url : DEFAULT_IMAGE_URL;
        
        // OPTIMIZACIÓN CRÍTICA:
        // Pasamos el ancho (150) y alto (150) al constructor de Image.
        // true, true -> preserveRatio, smooth
        // true -> backgroundLoading (Carga en hilo secundario para no congelar la UI)
        Image image = new Image(imageUrl, 150, 150, true, true, true);
        
        image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) {
                // Si falla, cargamos el placeholder
                Platform.runLater(() -> imagenJugador.setImage(new Image(DEFAULT_IMAGE_URL)));
            }
        });

        imagenJugador.setImage(image);
    }

    @FXML
    private void handleEditar() {
        if (onEditarListener != null) {
            onEditarListener.accept(jugador);
        }
    }

    @FXML
    private void handleEliminar() {
        if (onEliminarListener != null) {
            onEliminarListener.accept(jugador);
        }
    }
}