package edu.rico.nbafx.controller;

import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;

import java.util.Optional;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor complete todos los campos");
            return;
        }

        Optional<Usuario> usuario = usuarioService.login(username, password);
        if (usuario.isPresent()) {
            errorLabel.setText("");
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Bienvenido " + usuario.get().getNombre() + " (" + usuario.get().getRol() + ")");
            // Aquí podrías navegar a la pantalla principal
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos");
        }
    }

    @FXML
    private void handleRegister() {
        // Diálogo simple para registro rápido (en una app real sería otra vista)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Registro");
        dialog.setHeaderText("Ingrese nuevo usuario");
        dialog.setContentText("Nombre:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            TextInputDialog passDialog = new TextInputDialog();
            passDialog.setTitle("Registro");
            passDialog.setHeaderText("Ingrese contraseña");
            passDialog.setContentText("Password:");
            
            Optional<String> passResult = passDialog.showAndWait();
            passResult.ifPresent(pass -> {
                ChoiceDialog<Rol> rolDialog = new ChoiceDialog<>(Rol.USER, Rol.values());
                rolDialog.setTitle("Registro");
                rolDialog.setHeaderText("Seleccione Rol");
                rolDialog.setContentText("Rol:");
                
                Optional<Rol> rolResult = rolDialog.showAndWait();
                rolResult.ifPresent(rol -> {
                    try {
                        usuarioService.registrarUsuario(name, pass, rol);
                        showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente");
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
                    }
                });
            });
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}