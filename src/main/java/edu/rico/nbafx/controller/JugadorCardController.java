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
    
    // Usamos una imagen local como fallback si es posible, o una URL segura
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
        // Validar URL básica
        String imageUrl = (url != null && !url.trim().isEmpty()) ? url : DEFAULT_IMAGE_URL;
        
        // Cargar imagen en background para no bloquear UI
        // El constructor de Image con backgroundLoading=true ya hace esto,
        // pero añadimos manejo de errores en el listener de error de la imagen.
        Image image = new Image(imageUrl, true);
        
        image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
            if (newEx != null) {
                // Si falla la carga (ej. 403 Forbidden, 404 Not Found), ponemos el placeholder
                // Es importante hacerlo en el hilo de JavaFX si vamos a tocar la UI, 
                // aunque el listener de properties suele ser seguro, Platform.runLater asegura consistencia.
                Platform.runLater(() -> imagenJugador.setImage(new Image(DEFAULT_IMAGE_URL)));
                System.err.println("Error cargando imagen para " + jugador.getNombre() + ": " + newEx.getMessage());
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