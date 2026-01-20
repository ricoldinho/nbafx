package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Jugador;
import edu.rico.nbafx.model.Posicion;
import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.service.JugadorService;
import edu.rico.nbafx.util.AppShell;
import edu.rico.nbafx.util.View;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de gestión de jugadores.
 * Muestra los jugadores cargando componentes FXML dinámicos (tarjetas).
 */
public class JugadoresController {

    @FXML private TilePane jugadoresContainer;
    @FXML private Button btnUsuarios;

    private final JugadorService jugadorService = new JugadorService();

    @FXML
    public void initialize() {
        configurarPermisos();
        cargarJugadores();
    }

    /**
     * Configura la visibilidad de los elementos según el rol del usuario actual.
     */
    private void configurarPermisos() {
        Usuario currentUser = AppShell.getInstance().getCurrentUser();
        
        if (currentUser != null && currentUser.getRol() == Rol.USER) {
            // Si es usuario normal, ocultamos el botón de gestión de usuarios
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false); // Para que no ocupe espacio
        } else {
            btnUsuarios.setVisible(true);
            btnUsuarios.setManaged(true);
        }
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
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/jugador-card.fxml"));
                    Parent cardNode = loader.load();
                    
                    JugadorCardController cardController = loader.getController();
                    // Pasamos el jugador y las acciones (callbacks) al controlador de la tarjeta
                    cardController.setJugador(jugador, this::handleEditarJugador, this::handleEliminarJugador);
                    
                    jugadoresContainer.getChildren().add(cardNode);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        task.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los jugadores: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleAgregarJugador() {
        mostrarDialogoJugador(null);
    }

    @FXML
    private void handleIrAUsuarios() {
        AppShell.getInstance().loadView(View.USUARIOS);
    }

    @FXML
    private void handleLogout() {
        AppShell.getInstance().setCurrentUser(null);
        AppShell.getInstance().loadView(View.LOGIN);
    }

    // Estos métodos ahora son llamados desde el JugadorCardController a través de callbacks
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
        TextField imageUrlField = new TextField();

        nombreField.setPromptText("Nombre");
        dorsalField.setPromptText("Dorsal");
        equipoField.setPromptText("Equipo");
        anillosField.setPromptText("Anillos");
        alturaField.setPromptText("Altura (m)");
        pesoField.setPromptText("Peso (kg)");
        imageUrlField.setPromptText("URL de Imagen");

        if (jugadorExistente != null) {
            nombreField.setText(jugadorExistente.getNombre());
            dorsalField.setText(String.valueOf(jugadorExistente.getDorsal()));
            equipoField.setText(jugadorExistente.getEquipo());
            posicionBox.setValue(jugadorExistente.getPosicion());
            anillosField.setText(String.valueOf(jugadorExistente.getNumeroAnillos()));
            alturaField.setText(String.valueOf(jugadorExistente.getAltura()));
            pesoField.setText(String.valueOf(jugadorExistente.getPeso()));
            imageUrlField.setText(jugadorExistente.getImageUrl());
        }

        content.getChildren().addAll(
            new Label("Nombre:"), nombreField,
            new Label("Dorsal:"), dorsalField,
            new Label("Equipo:"), equipoField,
            new Label("Posición:"), posicionBox,
            new Label("Anillos:"), anillosField,
            new Label("Altura (m):"), alturaField,
            new Label("Peso (kg):"), pesoField,
            new Label("URL Imagen:"), imageUrlField
        );
        
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
                    String imageUrl = imageUrlField.getText();

                    Jugador j = jugadorExistente != null ? jugadorExistente : new Jugador();
                    j.setNombre(nombre);
                    j.setDorsal(dorsal);
                    j.setEquipo(equipo);
                    j.setPosicion(posicion);
                    j.setNumeroAnillos(anillos);
                    j.setAltura(altura);
                    j.setPeso(peso);
                    j.setImageUrl(imageUrl);
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
                    if (jugador.getId() == 0) {
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}