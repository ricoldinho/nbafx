package edu.rico.nbafx.service;

import edu.rico.nbafx.dao.JugadorDAO;
import edu.rico.nbafx.model.Jugador;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que gestiona la lógica de negocio relacionada con los jugadores.
 */
public class JugadorService {

    private final JugadorDAO jugadorDAO;

    /**
     * Constructor por defecto. Inicializa el DAO.
     */
    public JugadorService() {
        this.jugadorDAO = new JugadorDAO();
    }

    /**
     * Obtiene todos los jugadores registrados en el sistema.
     *
     * @return Lista de todos los jugadores.
     */
    public List<Jugador> obtenerTodosLosJugadores() {
        return jugadorDAO.findAll();
    }

    /**
     * Busca un jugador por su identificador único.
     *
     * @param id El ID del jugador.
     * @return Un Optional con el Jugador si existe.
     */
    public Optional<Jugador> obtenerJugadorPorId(int id) {
        return jugadorDAO.findById(id);
    }

    /**
     * Registra un nuevo jugador en el sistema.
     *
     * @param jugador El jugador a registrar.
     * @throws Exception Si los datos son inválidos o hay error en la base de datos.
     */
    public void registrarJugador(Jugador jugador) throws Exception {
        validarJugador(jugador);
        jugadorDAO.save(jugador);
    }

    /**
     * Actualiza la información de un jugador existente.
     *
     * @param jugador El jugador con la información actualizada.
     * @throws Exception Si los datos son inválidos o hay error en la base de datos.
     */
    public void actualizarJugador(Jugador jugador) throws Exception {
        validarJugador(jugador);
        jugadorDAO.update(jugador);
    }

    /**
     * Elimina un jugador del sistema.
     *
     * @param id El ID del jugador a eliminar.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void eliminarJugador(int id) throws SQLException {
        jugadorDAO.delete(id);
    }

    /**
     * Valida que los datos del jugador sean correctos antes de guardar.
     *
     * @param jugador El jugador a validar.
     * @throws IllegalArgumentException Si algún dato es inválido.
     */
    private void validarJugador(Jugador jugador) {
        if (jugador.getNombre() == null || jugador.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        if (jugador.getDorsal() < 0 || jugador.getDorsal() > 99) {
            throw new IllegalArgumentException("El dorsal debe estar entre 0 y 99.");
        }
        if (jugador.getAltura() <= 0) {
            throw new IllegalArgumentException("La altura debe ser mayor que 0.");
        }
        if (jugador.getPeso() <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que 0.");
        }
        if (jugador.getNumeroAnillos() < 0) {
            throw new IllegalArgumentException("El número de anillos no puede ser negativo.");
        }
    }
}