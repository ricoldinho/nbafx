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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador para la vista de gestión de jugadores.
 */
public class JugadoresController {

    @FXML private TilePane jugadoresContainer;
    @FXML private Button btnUsuarios;
    @FXML private Button btnNuevoJugador; // Nuevo ID inyectado

    private final JugadorService jugadorService = new JugadorService();
    private File imagenSeleccionadaTemp = null;

    @FXML
    public void initialize() {
        configurarPermisos();
        cargarJugadores();
    }

    private void configurarPermisos() {
        Usuario currentUser = AppShell.getInstance().getCurrentUser();
        
        if (currentUser != null && currentUser.getRol() == Rol.USER) {
            // Ocultar botón de gestión de usuarios
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
            
            // Ocultar botón de crear nuevo jugador
            btnNuevoJugador.setVisible(false);
            btnNuevoJugador.setManaged(false);
        } else {
            btnUsuarios.setVisible(true);
            btnUsuarios.setManaged(true);
            
            btnNuevoJugador.setVisible(true);
            btnNuevoJugador.setManaged(true);
        }
    }

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
                    
                    // Pasamos el jugador y las acciones
                    cardController.setJugador(jugador, this::handleEditarJugador, this::handleEliminarJugador);
                    
                    // IMPORTANTE: Configurar permisos de la tarjeta individualmente
                    cardController.configurarPermisos(AppShell.getInstance().getCurrentUser());
                    
                    jugadoresContainer.getChildren().add(cardNode);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los jugadores: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML private void handleAgregarJugador() { mostrarDialogoJugador(null); }
    @FXML private void handleIrAUsuarios() { AppShell.getInstance().loadView(View.USUARIOS); }
    @FXML private void handleLogout() {
        AppShell.getInstance().setCurrentUser(null);
        AppShell.getInstance().loadView(View.LOGIN);
    }

    private void handleEditarJugador(Jugador jugador) { mostrarDialogoJugador(jugador); }

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
        imagenSeleccionadaTemp = null;

        Dialog<Jugador> dialog = new Dialog<>();
        dialog.setTitle(jugadorExistente == null ? "Nuevo Jugador" : "Editar Jugador");
        dialog.setHeaderText("Ingrese los datos del jugador");

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField nombreField = new TextField();
        TextField dorsalField = new TextField();
        TextField equipoField = new TextField();
        ComboBox<Posicion> posicionBox = new ComboBox<>(FXCollections.observableArrayList(Posicion.values()));
        TextField anillosField = new TextField();
        TextField alturaField = new TextField();
        TextField pesoField = new TextField();
        
        TextField imageUrlField = new TextField();
        imageUrlField.setPromptText("URL web o ruta local");
        imageUrlField.setEditable(false);
        
        Button btnExaminar = new Button("Seleccionar Foto...");
        btnExaminar.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (selectedFile != null) {
                imagenSeleccionadaTemp = selectedFile;
                imageUrlField.setText(selectedFile.getAbsolutePath());
            }
        });
        
        HBox imageBox = new HBox(10);
        HBox.setHgrow(imageUrlField, Priority.ALWAYS);
        imageBox.getChildren().addAll(imageUrlField, btnExaminar);

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
            imageUrlField.setText(jugadorExistente.getImageUrl());
        }

        content.getChildren().addAll(
            new Label("Nombre:"), nombreField, new Label("Dorsal:"), dorsalField,
            new Label("Equipo:"), equipoField, new Label("Posición:"), posicionBox,
            new Label("Anillos:"), anillosField, new Label("Altura (m):"), alturaField,
            new Label("Peso (kg):"), pesoField, new Label("Imagen:"), imageBox
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        dialog.getDialogPane().setContent(scrollPane);

        final Button btnGuardar = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        btnGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                if (nombreField.getText().trim().isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio");
                if (equipoField.getText().trim().isEmpty()) throw new IllegalArgumentException("El equipo es obligatorio");
                if (posicionBox.getValue() == null) throw new IllegalArgumentException("Seleccione una posición");
                
                Integer.parseInt(dorsalField.getText().trim());
                Integer.parseInt(anillosField.getText().trim());
                Double.parseDouble(alturaField.getText().replace(",", ".").trim());
                Double.parseDouble(pesoField.getText().replace(",", ".").trim());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Datos inválidos", e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Jugador j = jugadorExistente != null ? jugadorExistente : new Jugador();
                j.setNombre(nombreField.getText());
                j.setDorsal(Integer.parseInt(dorsalField.getText().trim()));
                j.setEquipo(equipoField.getText());
                j.setPosicion(posicionBox.getValue());
                j.setNumeroAnillos(Integer.parseInt(anillosField.getText().trim()));
                j.setAltura(Double.parseDouble(alturaField.getText().replace(",", ".").trim()));
                j.setPeso(Double.parseDouble(pesoField.getText().replace(",", ".").trim()));
                
                if (imagenSeleccionadaTemp == null) {
                    j.setImageUrl(imageUrlField.getText());
                }
                return j;
            }
            return null;
        });

        Optional<Jugador> result = dialog.showAndWait();
        result.ifPresent(jugador -> {
            final File archivoParaCopiar = imagenSeleccionadaTemp;
            
            Platform.runLater(() -> {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        if (archivoParaCopiar != null) {
                            File carpetaImagenes = new File("imagenes");
                            if (!carpetaImagenes.exists()) {
                                carpetaImagenes.mkdir();
                            }

                            String extension = "";
                            String nombreOriginal = archivoParaCopiar.getName();
                            int i = nombreOriginal.lastIndexOf('.');
                            if (i > 0) extension = nombreOriginal.substring(i);
                            
                            String nombreArchivo = UUID.randomUUID().toString() + extension;
                            File destino = new File(carpetaImagenes, nombreArchivo);
                            
                            Files.copy(archivoParaCopiar.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            
                            jugador.setImageUrl("imagenes" + File.separator + nombreArchivo);
                        }

                        if (jugador.getId() == 0) jugadorService.registrarJugador(jugador);
                        else jugadorService.actualizarJugador(jugador);
                        return null;
                    }
                };
                
                task.setOnSucceeded(e -> {
                    cargarJugadores();
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Jugador guardado correctamente.");
                });
                
                task.setOnFailed(e -> {
                    e.getSource().getException().printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Error al guardar: " + task.getException().getMessage());
                });
                
                new Thread(task).start();
            });
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