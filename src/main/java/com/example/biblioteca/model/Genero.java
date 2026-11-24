package com.example.biblioteca.model;

import com.example.biblioteca.utiles.Util;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "generos", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    public Genero() {
    }

    public Genero(String nombre) {
        setNombre(nombre);
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del género no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    @Override
    public String toString() {
        return "Genero [id=" + id + ", nombre=" + Util.normalizarNombre(nombre) + "]";
    }
}
