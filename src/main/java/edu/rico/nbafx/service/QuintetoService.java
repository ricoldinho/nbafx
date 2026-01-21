package edu.rico.nbafx.service;

import edu.rico.nbafx.dao.QuintetoDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para gestionar la lógica de los quintetos ideales.
 */
public class QuintetoService {

    private final QuintetoDAO quintetoDAO = new QuintetoDAO();
    private static final int MAX_JUGADORES = 5;

    public void agregarJugador(int usuarioId, int jugadorId) throws Exception {
        int actuales = quintetoDAO.countJugadoresInQuinteto(usuarioId);
        if (actuales >= MAX_JUGADORES) {
            throw new Exception("Tu quinteto ya está completo (máximo 5 jugadores).");
        }
        quintetoDAO.addJugadorToQuinteto(usuarioId, jugadorId);
    }

    public void eliminarJugador(int usuarioId, int jugadorId) throws SQLException {
        quintetoDAO.removeJugadorFromQuinteto(usuarioId, jugadorId);
    }

    public List<Integer> obtenerIdsQuinteto(int usuarioId) {
        return quintetoDAO.getQuintetoJugadorIds(usuarioId);
    }
    
    public int contarJugadores(int usuarioId) {
        return quintetoDAO.countJugadoresInQuinteto(usuarioId);
    }
}