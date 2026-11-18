package com.example.biblioteca.dao;

import java.time.LocalDate;
import java.util.List;

import com.example.biblioteca.model.Genero;
import com.example.biblioteca.model.Libro;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class LibroDao {

    private final EntityManagerFactory emf;

    public LibroDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer insertar(Libro libro) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                if (libro.getGenero() != null && libro.getGenero().getId() != null) {
                    libro.setGenero(em.getReference(Genero.class, libro.getGenero().getId()));
                }
                em.persist(libro);
                tx.commit();
                return libro.getId();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Libro buscarPorId(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Libro.class, id);
        }
    }

    public List<Libro> listarTodos() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT l FROM Libro l ORDER BY l.titulo", Libro.class)
                    .getResultList();
        }
    }

    public List<Libro> listarPorGenero(int generoId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT l FROM Libro l WHERE l.genero.id = :id ORDER BY l.titulo",
                    Libro.class)
                    .setParameter("id", generoId)
                    .getResultList();
        }
    }

    public boolean actualizar(int id, String titulo, String autor, LocalDate fechaPublicacion,
            Double valoracion, Integer generoId, Integer copiasTotales, Integer copiasDisponibles) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Libro libro = em.find(Libro.class, id);
                if (libro == null) {
                    tx.rollback();
                    return false;
                }
                Genero genero = generoId == null ? null : em.find(Genero.class, generoId);
                if (generoId != null && genero == null) {
                    throw new IllegalArgumentException("No existe el género indicado");
                }
                if (!libro.setTitulo(titulo) || !libro.setFechaPub(fechaPublicacion)
                        || !libro.setValoracion(valoracion) || !libro.setCopiasTotales(copiasTotales)
                        || !libro.setCopiasDisponibles(copiasDisponibles)) {
                    throw new IllegalArgumentException("Los datos del libro no son válidos");
                }
                libro.setAutor(autor);
                libro.setGenero(genero);
                tx.commit();
                return true;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public boolean borrar(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Libro libro = em.find(Libro.class, id);
                if (libro == null) {
                    tx.rollback();
                    return false;
                }
                em.remove(libro);
                tx.commit();
                return true;
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }
}
