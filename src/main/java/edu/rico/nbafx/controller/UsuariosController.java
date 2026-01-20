package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.service.UsuarioService;
import edu.rico.nbafx.util.AppShell;
import edu.rico.nbafx.util.View;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista de gestión de usuarios (usuarios-view.fxml).
 * Maneja la lógica de visualización según el rol del usuario y las operaciones CRUD.
 */
public class UsuariosController {

    @FXML private Label welcomeLabel;
    @FXML private VBox adminPanel;
    @FXML private TableView<Usuario> usuariosTable;
    @FXML private TableColumn<Usuario, Integer> idColumn;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, Rol> rolColumn;
    @FXML private TableColumn<Usuario, String> fechaColumn;

    private UsuarioService usuarioService = new UsuarioService();
    private Usuario currentUser;
    private ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();

    /**
     * Inicializa el controlador. Configura las columnas de la tabla.
     */
    @FXML
    public void initialize() {
        // Configuración de columnas
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        rolColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRol()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        fechaColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaCreacion() != null) {
                return new SimpleStringProperty(cellData.getValue().getFechaCreacion().format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Enlazar la lista observable a la tabla
        usuariosTable.setItems(usuariosList);
        
        // Recuperar usuario de la sesión si existe (al volver de otra pantalla)
        Usuario sessionUser = AppShell.getInstance().getCurrentUser();
        if (sessionUser != null) {
            setUsuario(sessionUser);
        }
    }

    /**
     * Configura la vista con el usuario actual que ha iniciado sesión.
     * Determina la visibilidad de los componentes según el rol.
     *
     * @param usuario El usuario que ha iniciado sesión.
     */
    public void setUsuario(Usuario usuario) {
        this.currentUser = usuario;
        welcomeLabel.setText("Bienvenido, " + usuario.getNombre());

        if (usuario.getRol() == Rol.ADMIN) {
            adminPanel.setVisible(true);
            adminPanel.setManaged(true);
            cargarUsuarios();
        } else {
            adminPanel.setVisible(false);
            adminPanel.setManaged(false);
        }
    }

    /**
     * Navega a la vista de gestión de jugadores.
     */
    @FXML
    private void handleIrAJugadores() {
        AppShell.getInstance().loadView(View.JUGADORES);
    }

    /**
     * Carga la lista de usuarios desde la base de datos en un hilo separado.
     */
    private void cargarUsuarios() {
        Task<List<Usuario>> task = new Task<>() {
            @Override
            protected List<Usuario> call() throws Exception {
                return usuarioService.obtenerTodosLosUsuarios();
            }
        };

        task.setOnSucceeded(e -> {
            usuariosList.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los usuarios: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Maneja la acción de agregar un nuevo usuario.
     */
    @FXML
    private void handleAgregarUsuario() {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Agregar Usuario");
        dialog.setHeaderText("Ingrese los datos del nuevo usuario");

        ButtonType loginButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");
        ComboBox<Rol> rolComboBox = new ComboBox<>(FXCollections.observableArrayList(Rol.values()));
        rolComboBox.setValue(Rol.USER);

        content.getChildren().addAll(new Label("Nombre:"), nombreField, new Label("Contraseña:"), passwordField, new Label("Rol:"), rolComboBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Usuario(nombreField.getText(), passwordField.getText(), rolComboBox.getValue());
            }
            return null;
        });

        Optional<Usuario> result = dialog.showAndWait();

        result.ifPresent(nuevoUsuario -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    usuarioService.registrarUsuario(nuevoUsuario.getNombre(), nuevoUsuario.getPassword(), nuevoUsuario.getRol());
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                cargarUsuarios();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario agregado correctamente");
            });

            task.setOnFailed(e -> {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo agregar el usuario: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });
    }

    /**
     * Maneja la acción de editar un usuario seleccionado.
     */
    @FXML
    private void handleEditarUsuario() {
        Usuario selectedUsuario = usuariosTable.getSelectionModel().getSelectedItem();
        if (selectedUsuario == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Por favor seleccione un usuario para editar.");
            return;
        }

        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Editar datos del usuario: " + selectedUsuario.getNombre());

        ButtonType saveButtonType = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField nombreField = new TextField(selectedUsuario.getNombre());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nueva contraseña (dejar vacío para no cambiar)");
        ComboBox<Rol> rolComboBox = new ComboBox<>(FXCollections.observableArrayList(Rol.values()));
        rolComboBox.setValue(selectedUsuario.getRol());

        content.getChildren().addAll(new Label("Nombre:"), nombreField, new Label("Contraseña:"), passwordField, new Label("Rol:"), rolComboBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selectedUsuario.setNombre(nombreField.getText());
                selectedUsuario.setRol(rolComboBox.getValue());
                // Retornamos el usuario modificado, la contraseña se maneja aparte si no es vacía
                return selectedUsuario;
            }
            return null;
        });

        Optional<Usuario> result = dialog.showAndWait();

        result.ifPresent(usuarioEditado -> {
            String nuevaPass = passwordField.getText();
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    usuarioService.actualizarUsuario(usuarioEditado, nuevaPass);
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                cargarUsuarios();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente");
            });

            task.setOnFailed(e -> {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el usuario: " + task.getException().getMessage());
            });

            new Thread(task).start();
        });
    }

    /**
     * Maneja la acción de eliminar un usuario seleccionado.
     */
    @FXML
    private void handleEliminarUsuario() {
        Usuario selectedUsuario = usuariosTable.getSelectionModel().getSelectedItem();
        if (selectedUsuario == null) {
            showAlert(Alert.AlertType.WARNING, "Selección requerida", "Por favor seleccione un usuario para eliminar.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de que desea eliminar al usuario " + selectedUsuario.getNombre() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    usuarioService.eliminarUsuario(selectedUsuario.getId());
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                cargarUsuarios();
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario eliminado correctamente");
            });

            task.setOnFailed(e -> {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario: " + task.getException().getMessage());
            });

            new Thread(task).start();
        }
    }

    /**
     * Cierra la sesión actual y vuelve a la pantalla de login usando AppShell.
     */
    @FXML
    private void handleLogout() {
        AppShell.getInstance().setCurrentUser(null); // Limpiar sesión
        AppShell.getInstance().loadView(View.LOGIN);
    }

    /**
     * Muestra una alerta al usuario.
     *
     * @param type Tipo de alerta.
     * @param title Título de la alerta.
     * @param content Contenido del mensaje.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}