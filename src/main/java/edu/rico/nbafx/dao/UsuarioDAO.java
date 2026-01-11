package edu.rico.nbafx.dao;

import edu.rico.nbafx.model.Rol;
import edu.rico.nbafx.model.Usuario;
import edu.rico.nbafx.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

public class UsuarioDAO {

    public Optional<Usuario> findByNombre(String nombre) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPassword(rs.getString("password"));
                usuario.setRol(Rol.valueOf(rs.getString("rol")));
                usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

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
}