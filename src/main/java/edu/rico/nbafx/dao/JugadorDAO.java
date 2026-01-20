package edu.rico.nbafx.dao;

import edu.rico.nbafx.model.Jugador;
import edu.rico.nbafx.model.Posicion;
import edu.rico.nbafx.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Jugador.
 * Gestiona las operaciones CRUD contra la tabla 'jugadores' en la base de datos.
 */
public class JugadorDAO {

    /**
     * Recupera todos los jugadores de la base de datos.
     *
     * @return Una lista de objetos Jugador.
     */
    public List<Jugador> findAll() {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT * FROM jugadores";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                jugadores.add(mapResultSetToJugador(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jugadores;
    }

    /**
     * Busca un jugador por su ID.
     *
     * @param id El ID del jugador a buscar.
     * @return Un Optional que contiene al Jugador si existe.
     */
    public Optional<Jugador> findById(int id) {
        String sql = "SELECT * FROM jugadores WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToJugador(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Guarda un nuevo jugador en la base de datos.
     *
     * @param jugador El objeto Jugador a guardar.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public void save(Jugador jugador) throws SQLException {
        String sql = "INSERT INTO jugadores (nombre, dorsal, equipo, posicion, numero_anillos, altura, peso, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, jugador.getNombre());
            stmt.setInt(2, jugador.getDorsal());
            stmt.setString(3, jugador.getEquipo());
            stmt.setString(4, jugador.getPosicion().name());
            stmt.setInt(5, jugador.getNumeroAnillos());
            stmt.setDouble(6, jugador.getAltura());
            stmt.setDouble(7, jugador.getPeso());
            stmt.setString(8, jugador.getImageUrl());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    jugador.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza la información de un jugador existente.
     *
     * @param jugador El objeto Jugador con la información actualizada.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public void update(Jugador jugador) throws SQLException {
        String sql = "UPDATE jugadores SET nombre = ?, dorsal = ?, equipo = ?, posicion = ?, numero_anillos = ?, altura = ?, peso = ?, image_url = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, jugador.getNombre());
            stmt.setInt(2, jugador.getDorsal());
            stmt.setString(3, jugador.getEquipo());
            stmt.setString(4, jugador.getPosicion().name());
            stmt.setInt(5, jugador.getNumeroAnillos());
            stmt.setDouble(6, jugador.getAltura());
            stmt.setDouble(7, jugador.getPeso());
            stmt.setString(8, jugador.getImageUrl());
            stmt.setInt(9, jugador.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un jugador de la base de datos por su ID.
     *
     * @param id El ID del jugador a eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM jugadores WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Mapea un ResultSet a un objeto Jugador.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return El objeto Jugador mapeado.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
    private Jugador mapResultSetToJugador(ResultSet rs) throws SQLException {
        Jugador jugador = new Jugador();
        jugador.setId(rs.getInt("id"));
        jugador.setNombre(rs.getString("nombre"));
        jugador.setDorsal(rs.getInt("dorsal"));
        jugador.setEquipo(rs.getString("equipo"));
        jugador.setPosicion(Posicion.valueOf(rs.getString("posicion")));
        jugador.setNumeroAnillos(rs.getInt("numero_anillos"));
        jugador.setAltura(rs.getDouble("altura"));
        jugador.setPeso(rs.getDouble("peso"));
        jugador.setImageUrl(rs.getString("image_url"));
        return jugador;
    }
}