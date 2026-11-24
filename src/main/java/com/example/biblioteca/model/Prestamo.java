package com.example.biblioteca.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "prestamos", uniqueConstraints = @UniqueConstraint(
        columnNames = {"id_usuario", "id_libro"}))
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    public Prestamo() {
    }

    public Prestamo(Usuario usuario, Libro libro) {
        setUsuario(usuario);
        setLibro(libro);
    }

    public Integer getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario del préstamo no puede ser null");
        }
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro del préstamo no puede ser null");
        }
        this.libro = libro;
    }

    @Override
    public String toString() {
        return "Prestamo [id=" + id + ", usuario=" + usuario + ", libro=" + libro + "]";
    }
}
