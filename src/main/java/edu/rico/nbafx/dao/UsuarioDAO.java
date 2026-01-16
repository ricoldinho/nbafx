package edu.rico.nbafx.dao;

import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Usuario.
 * Gestiona las operaciones CRUD contra la base de datos MySQL.
 */
public class UsuarioDAO {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombre El nombre de usuario a buscar.
     * @return Un Optional que contiene el Usuario si se encuentra, o vacío si no.
     */
    public Optional<Usuario> findByNombre(String nombre) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Recupera todos los usuarios de la base de datos.
     *
     * @return Una lista de objetos Usuario.
     */
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto Usuario a guardar.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public void save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, password, rol) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getRol().name());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param usuario El objeto Usuario con la información actualizada.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, password = ?, rol = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getRol().name());
            stmt.setInt(4, usuario.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Mapea un ResultSet a un objeto Usuario.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return El objeto Usuario mapeado.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(Rol.valueOf(rs.getString("rol")));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return usuario;
    }
}