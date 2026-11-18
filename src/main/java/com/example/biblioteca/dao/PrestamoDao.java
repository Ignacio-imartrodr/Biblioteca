package com.example.biblioteca.dao;

import java.util.List;

import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Prestamo;
import com.example.biblioteca.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class PrestamoDao {

    private final EntityManagerFactory emf;

    public PrestamoDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer insertar(int usuarioId, int libroId) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Usuario usuario = em.find(Usuario.class, usuarioId);
                Libro libro = em.find(Libro.class, libroId);
                if (usuario == null || libro == null) {
                    throw new IllegalArgumentException("El usuario y el libro deben existir");
                }
                if (libro.getCopiasDisponibles() < 1) {
                    throw new IllegalStateException("No hay copias disponibles de este libro");
                }

                Prestamo prestamo = new Prestamo(usuario, libro);
                libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1);
                em.persist(prestamo);
                tx.commit();
                return prestamo.getId();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Prestamo buscarPorId(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Prestamo.class, id);
        }
    }

    public List<Prestamo> listarTodos() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Prestamo p ORDER BY p.id", Prestamo.class)
                    .getResultList();
        }
    }

    public boolean borrar(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Prestamo prestamo = em.find(Prestamo.class, id);
                if (prestamo == null) {
                    tx.rollback();
                    return false;
                }
                Libro libro = prestamo.getLibro();
                if (!libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1)) {
                    throw new IllegalStateException("No se pudo actualizar la disponibilidad del libro");
                }
                em.remove(prestamo);
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
