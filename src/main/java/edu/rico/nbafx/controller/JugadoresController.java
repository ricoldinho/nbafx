package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Jugador;
import edu.rico.nbafx.model.Posicion;
import edu.rico.nbafx.service.JugadorService;
import edu.rico.nbafx.util.AppShell;
import edu.rico.nbafx.util.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de gestión de jugadores.
 * Muestra los jugadores en formato de tarjetas (Cards).
 */
public class JugadoresController {

    @FXML private TilePane jugadoresContainer;

    private final JugadorService jugadorService = new JugadorService();
    // URL de imagen placeholder por defecto
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/150";

    @FXML
    public void initialize() {
        cargarJugadores();
    }

    /**
     * Carga los jugadores desde la base de datos de forma asíncrona.
     */
    private void cargarJugadores() {
        Task<List<Jugador>> task = new Task<>() {
            @Override
            protected List<Jugador> call() throws Exception {
                return jugadorService.obtenerTodosLosJugadores();
            }
        };

        task.setOnSucceeded(e -> {
            jugadoresContainer.getChildren().clear();
            List<Jugador> jugadores = task.getValue();
            for (Jugador jugador : jugadores) {
                jugadoresContainer.getChildren().add(crearTarjetaJugador(jugador));
            }
        });

        task.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los jugadores: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Crea un nodo gráfico (Card) que representa a un jugador.
     *
     * @param jugador El jugador a visualizar.
     * @return Un VBox estilizado como tarjeta.
     */
    private VBox crearTarjetaJugador(Jugador jugador) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new javafx.geometry.Insets(15));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(250);

        // Imagen del jugador
        ImageView imageView = new ImageView();
        try {
            // En un caso real, la URL vendría del objeto Jugador
            imageView.setImage(new Image(DEFAULT_IMAGE_URL, true)); 
        } catch (Exception e) {
            // Fallback si no carga la imagen
        }
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        // Datos del jugador
        Label nombreLabel = new Label(jugador.getNombre());
        nombreLabel.getStyleClass().add("card-title");

        Label equipoLabel = new Label(jugador.getEquipo() + " - #" + jugador.getDorsal());
        equipoLabel.getStyleClass().add("card-subtitle");

        VBox detalles = new VBox(5);
        detalles.setAlignment(Pos.CENTER_LEFT);
        detalles.getChildren().addAll(
            new Label("Posición: " + jugador.getPosicion()),
            new Label("Altura: " + jugador.getAltura() + "m | Peso: " + jugador.getPeso() + "kg"),
            new Label("Anillos: " + jugador.getNumeroAnillos())
        );

        // Botones de acción
        HBox acciones = new HBox(10);
        acciones.setAlignment(Pos.CENTER);
        
        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add("button-warning");
        btnEditar.setOnAction(e -> handleEditarJugador(jugador));

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("button-danger");
        btnEliminar.setOnAction(e -> handleEliminarJugador(jugador));

        acciones.getChildren().addAll(btnEditar, btnEliminar);

        card.getChildren().addAll(imageView, nombreLabel, equipoLabel, detalles, acciones);
        return card;
    }

    @FXML
    private void handleAgregarJugador() {
        mostrarDialogoJugador(null);
    }

    private void handleEditarJugador(Jugador jugador) {
        mostrarDialogoJugador(jugador);
    }

    private void handleEliminarJugador(Jugador jugador) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar a " + jugador.getNombre() + "?");
        alert.setContentText("Esta acción es irreversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    jugadorService.eliminarJugador(jugador.getId());
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                cargarJugadores();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Jugador eliminado.");
            });

            task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", task.getException().getMessage()));

            new Thread(task).start();
        }
    }

    private void mostrarDialogoJugador(Jugador jugadorExistente) {
        Dialog<Jugador> dialog = new Dialog<>();
        dialog.setTitle(jugadorExistente == null ? "Nuevo Jugador" : "Editar Jugador");
        dialog.setHeaderText("Ingrese los datos del jugador");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Formulario
        VBox content = new VBox(10);
        TextField nombreField = new TextField();
        TextField dorsalField = new TextField();
        TextField equipoField = new TextField();
        ComboBox<Posicion> posicionBox = new ComboBox<>(FXCollections.observableArrayList(Posicion.values()));
        TextField anillosField = new TextField();
        TextField alturaField = new TextField();
        TextField pesoField = new TextField();

        nombreField.setPromptText("Nombre");
        dorsalField.setPromptText("Dorsal");
        equipoField.setPromptText("Equipo");
        anillosField.setPromptText("Anillos");
        alturaField.setPromptText("Altura (m)");
        pesoField.setPromptText("Peso (kg)");

        if (jugadorExistente != null) {
            nombreField.setText(jugadorExistente.getNombre());
            dorsalField.setText(String.valueOf(jugadorExistente.getDorsal()));
            equipoField.setText(jugadorExistente.getEquipo());
            posicionBox.setValue(jugadorExistente.getPosicion());
            anillosField.setText(String.valueOf(jugadorExistente.getNumeroAnillos()));
            alturaField.setText(String.valueOf(jugadorExistente.getAltura()));
            pesoField.setText(String.valueOf(jugadorExistente.getPeso()));
        }

        content.getChildren().addAll(
            new Label("Nombre:"), nombreField,
            new Label("Dorsal:"), dorsalField,
            new Label("Equipo:"), equipoField,
            new Label("Posición:"), posicionBox,
            new Label("Anillos:"), anillosField,
            new Label("Altura (m):"), alturaField,
            new Label("Peso (kg):"), pesoField
        );
        
        // ScrollPane para el diálogo por si es muy alto
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nombre = nombreField.getText();
                    int dorsal = Integer.parseInt(dorsalField.getText());
                    String equipo = equipoField.getText();
                    Posicion posicion = posicionBox.getValue();
                    int anillos = Integer.parseInt(anillosField.getText());
                    double altura = Double.parseDouble(alturaField.getText());
                    double peso = Double.parseDouble(pesoField.getText());

                    Jugador j = jugadorExistente != null ? jugadorExistente : new Jugador();
                    j.setNombre(nombre);
                    j.setDorsal(dorsal);
                    j.setEquipo(equipo);
                    j.setPosicion(posicion);
                    j.setNumeroAnillos(anillos);
                    j.setAltura(altura);
                    j.setPeso(peso);
                    return j;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error de formato", "Por favor ingrese números válidos.");
                    return null;
                }
            }
            return null;
        });

        Optional<Jugador> result = dialog.showAndWait();
        result.ifPresent(jugador -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    if (jugador.getId() == 0) { // Nuevo (ID 0 por defecto)
                        jugadorService.registrarJugador(jugador);
                    } else {
                        jugadorService.actualizarJugador(jugador);
                    }
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                cargarJugadores();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Operación realizada correctamente.");
            });

            task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", task.getException().getMessage()));

            new Thread(task).start();
        });
    }

    @FXML
    private void handleVolver() {
        // Volver a la vista principal de usuarios (o menú principal)
        // Como no tenemos el usuario actual aquí guardado, podríamos necesitar pasarlo
        // o simplemente volver al login si es lo deseado.
        // Asumiremos volver a la vista de usuarios, pero necesitamos el usuario logueado.
        // Por simplicidad en este paso, volveremos al Login o podríamos implementar un SessionManager.
        // Para este ejemplo, volveremos a la vista de usuarios asumiendo que el AppShell mantiene estado o volvemos al login.
        AppShell.getInstance().loadView(View.USUARIOS);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}