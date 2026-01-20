package edu.rico.nbafx.model;

import java.util.Objects;

/**
 * Entidad que representa a un jugador de baloncesto.
 * Sigue el patrón Java Bean.
 */
public class Jugador {
    private int id;
    private String nombre;
    private int dorsal;
    private String equipo;
    private Posicion posicion;
    private int numeroAnillos;
    private double altura; // En metros
    private double peso;   // En kilogramos
    private String imageUrl; // URL de la imagen del jugador

    /**
     * Constructor vacío por defecto.
     */
    public Jugador() {
    }

    /**
     * Constructor completo para crear un nuevo jugador.
     *
     * @param nombre El nombre del jugador.
     * @param dorsal El número de dorsal.
     * @param equipo El nombre del equipo.
     * @param posicion La posición de juego.
     * @param numeroAnillos La cantidad de anillos de campeonato ganados.
     * @param altura La altura en metros.
     * @param peso El peso en kilogramos.
     * @param imageUrl La URL de la imagen del jugador.
     */
    public Jugador(String nombre, int dorsal, String equipo, Posicion posicion, int numeroAnillos, double altura, double peso, String imageUrl) {
        this.nombre = nombre;
        this.dorsal = dorsal;
        this.equipo = equipo;
        this.posicion = posicion;
        this.numeroAnillos = numeroAnillos;
        this.altura = altura;
        this.peso = peso;
        this.imageUrl = imageUrl;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

    public int getNumeroAnillos() {
        return numeroAnillos;
    }

    public void setNumeroAnillos(int numeroAnillos) {
        this.numeroAnillos = numeroAnillos;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", dorsal=" + dorsal +
                ", equipo='" + equipo + '\'' +
                ", posicion=" + posicion +
                ", numeroAnillos=" + numeroAnillos +
                ", altura=" + altura +
                ", peso=" + peso +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return id == jugador.id &&
                dorsal == jugador.dorsal &&
                numeroAnillos == jugador.numeroAnillos &&
                Double.compare(jugador.altura, altura) == 0 &&
                Double.compare(jugador.peso, peso) == 0 &&
                Objects.equals(nombre, jugador.nombre) &&
                Objects.equals(equipo, jugador.equipo) &&
                posicion == jugador.posicion &&
                Objects.equals(imageUrl, jugador.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, dorsal, equipo, posicion, numeroAnillos, altura, peso, imageUrl);
    }
}