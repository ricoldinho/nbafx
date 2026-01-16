package edu.rico.nbafx.service;

import edu.rico.nbafx.dao.UsuarioDAO;
import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los usuarios.
 */
public class UsuarioService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Valida las credenciales de un usuario para iniciar sesión.
     *
     * @param nombre El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @return Un Optional con el Usuario si las credenciales son correctas, o vacío si no.
     */
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

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param nombre El nombre de usuario.
     * @param password La contraseña en texto plano.
     * @param rol El rol del usuario.
     * @throws Exception Si el usuario ya existe o hay un error en el registro.
     */
    public void registrarUsuario(String nombre, String password, Rol rol) throws Exception {
        if (usuarioDAO.findByNombre(nombre).isPresent()) {
            throw new Exception("El nombre de usuario ya existe");
        }
        String hashedPassword = hashPassword(password);
        Usuario nuevoUsuario = new Usuario(nombre, hashedPassword, rol);
        usuarioDAO.save(nuevoUsuario);
    }

    /**
     * Obtiene todos los usuarios del sistema.
     *
     * @return Lista de todos los usuarios.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.findAll();
    }

    /**
     * Actualiza la información de un usuario.
     *
     * @param usuario El usuario con la información actualizada.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void actualizarUsuario(Usuario usuario) throws SQLException {
        // Si la contraseña no está hasheada (esto es una simplificación, idealmente deberíamos saber si se cambió)
        // En un caso real, verificaríamos si la contraseña ha cambiado antes de hashear de nuevo.
        // Aquí asumimos que si viene del servicio, ya se maneja la lógica de cambio de contraseña aparte o se pasa hasheada.
        // Para este ejemplo simple, asumiremos que la contraseña ya viene correcta o se mantiene.
        usuarioDAO.update(usuario);
    }
    
    /**
     * Actualiza un usuario, hasheando la contraseña si es necesario.
     * 
     * @param usuario El usuario a actualizar.
     * @param nuevaPassword La nueva contraseña en texto plano (puede ser null o vacía si no se cambia).
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void actualizarUsuario(Usuario usuario, String nuevaPassword) throws SQLException {
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            usuario.setPassword(hashPassword(nuevaPassword));
        }
        usuarioDAO.update(usuario);
    }

    /**
     * Elimina un usuario del sistema.
     *
     * @param id El ID del usuario a eliminar.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void eliminarUsuario(int id) throws SQLException {
        usuarioDAO.delete(id);
    }

    /**
     * Genera el hash SHA-256 de una contraseña.
     *
     * @param password La contraseña en texto plano.
     * @return El hash de la contraseña en Base64.
     */
    private String hashPassword(String password) {
        try {
            //seleccionamos algoritmo
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //obtenemos el array de bytes
            byte[] hash = md.digest(password.getBytes());
            //codificamos el array bytes a codigo hash para guardar en la base de datos
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     *
     * @param inputPassword La contraseña en texto plano.
     * @param storedHash El hash almacenado.
     * @return true si coinciden, false en caso contrario.
     */
    private boolean verifyPassword(String inputPassword, String storedHash) {
        return hashPassword(inputPassword).equals(storedHash);
    }
}