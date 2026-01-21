package edu.rico.nbafx.dao;

import edu.rico.nbafx.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar la tabla 'quintetos'.
 */
public class    QuintetoDAO {

    /**
     * Añade un jugador al quinteto de un usuario.
     */
    public void addJugadorToQuinteto(int usuarioId, int jugadorId) throws SQLException {
        String sql = "INSERT INTO quintetos (usuario_id, jugador_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, jugadorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un jugador del quinteto de un usuario.
     */
    public void removeJugadorFromQuinteto(int usuarioId, int jugadorId) throws SQLException {
        String sql = "DELETE FROM quintetos WHERE usuario_id = ? AND jugador_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, jugadorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene los IDs de los jugadores en el quinteto de un usuario.
     */
    public List<Integer> getQuintetoJugadorIds(int usuarioId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT jugador_id FROM quintetos WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("jugador_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * Cuenta cuántos jugadores tiene un usuario en su quinteto.
     */
    public int countJugadoresInQuinteto(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM quintetos WHERE usuario_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}