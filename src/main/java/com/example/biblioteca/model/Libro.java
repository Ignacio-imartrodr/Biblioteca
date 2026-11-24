package com.example.biblioteca.model;

import java.time.LocalDate;

import com.example.biblioteca.utiles.Util;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "libros", uniqueConstraints = @UniqueConstraint(columnNames = {"titulo", "autor", "fecha_publicacion"}))
public class Libro {
    public static final int N_COPIAS_DEFAULT = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 50)
    private String autor;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPub;

    private Double valoracion;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_genero", nullable = true)
    private Genero genero;

    @Column(name = "copias_totales", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private int copiasTotales = N_COPIAS_DEFAULT;

    @Column(name = "copias_disponibles", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private int copiasDisponibles = N_COPIAS_DEFAULT;

    public Libro() {
    }

    public Libro(String titulo, String autor, LocalDate fechaPub, Double valoracion, Genero genero, Integer copiasTotales, Integer copiasDisponibles) {
        if (!setTitulo(titulo)) {
            throw new IllegalArgumentException("El título no puede ser null o estar vacío");
        }
        if (!setFechaPub(fechaPub)) {
            throw new IllegalArgumentException("La fecha de publicación no puede ser null ni futura");
        }
        if (!setValoracion(valoracion)) {
            throw new IllegalArgumentException("La valoración debe ser null o estar entre 0 y 5");
        }

        int total = copiasTotales == null ? N_COPIAS_DEFAULT : copiasTotales;
        int disponibles = copiasDisponibles == null ? total : copiasDisponibles;

        if (total < 1) {
            throw new IllegalArgumentException("La cantidad total de copias debe ser mayor a 0");
        }
        if (disponibles < 0 || disponibles > total) {
            throw new IllegalArgumentException(
                    "Las copias disponibles deben estar entre 0 y las copias totales");
        }

        setAutor(autor);
        this.genero = genero;
        this.copiasTotales = total;
        this.copiasDisponibles = disponibles;
    }

    public Libro(String titulo, String autor, LocalDate fechaPub, Double valoracion) {
        this(titulo, autor, fechaPub, valoracion, null, null, null);
    }

    public Libro(String titulo, LocalDate fechaPub) {
        this(titulo, null, fechaPub, null);
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return false;
        }
        this.titulo = titulo.trim().toUpperCase();
        return true;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor == null || autor.isBlank() ? null : autor.trim().toUpperCase();
    }

    public LocalDate getFechaPub() {
        return fechaPub;
    }

    public boolean setFechaPub(LocalDate fechaPub) {
        if (fechaPub == null || fechaPub.isAfter(LocalDate.now())) {
            return false;
        }
        this.fechaPub = fechaPub;
        return true;
    }

    public Double getValoracion() {
        return valoracion;
    }

    public boolean setValoracion(Double valoracion) {
        if (valoracion != null
                && (!Double.isFinite(valoracion) || valoracion < 0 || valoracion > 5)) {
            return false;
        }
        this.valoracion = valoracion == null ? null
                : (Math.round(valoracion * 100.0) / 100.0);
        return true;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public int getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public boolean setCopiasDisponibles(Integer nCopias) {
        int disponibles = nCopias == null ? copiasTotales : nCopias;

        if (disponibles < 0 || disponibles > copiasTotales) {
            return false;
        }

        copiasDisponibles = disponibles;
        return true;
    }

    public int getCopiasTotales() {
        return copiasTotales;
    }

    public boolean setCopiasTotales(Integer nCopias) {
        int nuevoTotal = nCopias == null ? N_COPIAS_DEFAULT : nCopias;

        if (nuevoTotal < 1) {
            return false;
        }

        int copiasPrestadas = copiasTotales - copiasDisponibles;
        if (nuevoTotal < copiasPrestadas) {
            return false;
        }

        copiasTotales = nuevoTotal;
        copiasDisponibles = nuevoTotal - copiasPrestadas;
        return true;
    }

    @Override
    public String toString() {
        return "Libro [id=" + id + ", titulo=" + Util.normalizarNombre(titulo) + ", autor=" + Util.normalizarNombre(autor)
                + ", fechaPub=" + fechaPub + ", valoracion=" + valoracion == null ? "Sin valoración" : valoracion
                + ", genero=" + genero == null ? "Sin género" : genero + ", copiasTotales=" + copiasTotales
                + ", copiasDisponibles=" + copiasDisponibles + "]";
    }
}