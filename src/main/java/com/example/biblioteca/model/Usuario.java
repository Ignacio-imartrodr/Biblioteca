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
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String correo;

    public Usuario() {
    }

    public Usuario(String nombre, String correo) {
        if (!setNombre(nombre) || !setCorreo(correo)) {
            throw new IllegalArgumentException("El nombre y el correo no pueden ser null o estar vacíos");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        this.nombre = nombre;
        return true;
    }

    public String getCorreo() {
        return correo;
    }

    public boolean setCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        this.correo = correo.trim();
        return true;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + Util.normalizarNombre(nombre) + ", correo=" + correo + "]";
    }
}
