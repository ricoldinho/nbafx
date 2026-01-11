package edu.rico.nbafx.service;

import edu.rico.nbafx.dao.UsuarioDAO;
import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

public class UsuarioService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Optional<Usuario> login(String nombre, String password) {
        Optional<Usuario> usuarioOpt = usuarioDAO.findByNombre(nombre);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (verifyPassword(password, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public void registrarUsuario(String nombre, String password, Rol rol) throws Exception {
        if (usuarioDAO.findByNombre(nombre).isPresent()) {
            throw new Exception("El nombre de usuario ya existe");
        }
        String hashedPassword = hashPassword(password);
        Usuario nuevoUsuario = new Usuario(nombre, hashedPassword, rol);
        usuarioDAO.save(nuevoUsuario);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        return hashPassword(inputPassword).equals(storedHash);
    }
}