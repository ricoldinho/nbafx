package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.service.UsuarioService;
import edu.rico.nbafx.util.AppShell;
import edu.rico.nbafx.util.View;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Controlador para la vista de inicio de sesión.
 * Gestiona la autenticación y el registro de usuarios de forma asíncrona.
 */
public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    // Inyección de dependencia del servicio
    private UsuarioService usuarioService = new UsuarioService();

    /**
     * Maneja el evento de inicio de sesión.
     * Ejecuta la validación en un hilo separado para no congelar la UI.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor complete todos los campos");
            return;
        }

        // Feedback visual y bloqueo de inputs
        errorLabel.setText("Verificando credenciales...");
        setInputsDisabled(true);

        // Tarea asíncrona para el login
        Task<Optional<Usuario>> loginTask = new Task<>() {
            @Override
            protected Optional<Usuario> call() throws Exception {
                return usuarioService.login(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            setInputsDisabled(false);
            Optional<Usuario> usuarioOpt = loginTask.getValue();
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                errorLabel.setText("Login correcto");
                
                // Guardar usuario en sesión
                AppShell.getInstance().setCurrentUser(usuario);
                
                // Redirección basada en Rol
                if (usuario.getRol() == Rol.ADMIN) {
                    abrirVista(View.USUARIOS, usuario);
                } else {
                    abrirVista(View.JUGADORES, usuario);
                }
                
            } else {
                errorLabel.setText("Usuario o contraseña incorrectos");
            }
        });

        loginTask.setOnFailed(event -> {
            setInputsDisabled(false);
            errorLabel.setText("Error de conexión");
            Throwable ex = loginTask.getException();
            ex.printStackTrace(); // En producción usar logger
        });

        new Thread(loginTask).start();
    }

    /**
     * Maneja el evento de registro de un nuevo usuario.
     * Por defecto, todos los nuevos registros tienen el rol USER.
     */
    @FXML
    private void handleRegister() {
        // Diálogo para el nombre
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registro");
        dialog.setHeaderText("Crear nueva cuenta");
        dialog.setContentText("Nombre de usuario:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aviso", "El nombre no puede estar vacío");
                return;
            }

            // Diálogo para la contraseña
            TextInputDialog passDialog = new TextInputDialog();
            passDialog.setTitle("Registro");
            passDialog.setHeaderText("Establecer contraseña");
            passDialog.setContentText("Contraseña:");
            
            Optional<String> passResult = passDialog.showAndWait();
            passResult.ifPresent(pass -> {
                if (pass.trim().isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Aviso", "La contraseña no puede estar vacía");
                    return;
                }
                // Registramos directamente con Rol.USER
                registrarUsuarioAsync(name, pass, Rol.USER);
            });
        });
    }

    /**
     * Realiza el registro del usuario en segundo plano.
     */
    private void registrarUsuarioAsync(String name, String pass, Rol rol) {
        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                usuarioService.registrarUsuario(name, pass, rol);
                return null;
            }
        };

        registerTask.setOnSucceeded(e -> 
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente. Ya puede iniciar sesión.")
        );

        registerTask.setOnFailed(e -> 
            showAlert(Alert.AlertType.ERROR, "Error", "Fallo al registrar: " + registerTask.getException().getMessage())
        );

        new Thread(registerTask).start();
    }

    /**
     * Carga y muestra la vista correspondiente usando AppShell.
     */
    private void abrirVista(View view, Usuario usuario) {
        Object controller = AppShell.getInstance().loadView(view);
        
        // Configuramos el controlador si es necesario
        if (controller instanceof UsuariosController) {
            ((UsuariosController) controller).setUsuario(usuario);
        } 
    }

    private void setInputsDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}