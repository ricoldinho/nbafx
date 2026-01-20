package edu.rico.nbafx.model;

/**
 * Enumerado que define las posiciones de juego en baloncesto.
 */
public enum Posicion {
    BASE("Base"),
    ESCOLTA("Escolta"),
    ALERO("Alero"),
    ALA_PIVOT("Ala-Pívot"),
    PIVOT("Pívot");

    private final String descripcion;

    Posicion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}