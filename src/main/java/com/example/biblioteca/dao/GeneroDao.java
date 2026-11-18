package com.example.biblioteca.dao;

import java.util.List;

import com.example.biblioteca.model.Genero;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class GeneroDao {

    private final EntityManagerFactory emf;

    public GeneroDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer insertar(Genero genero) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(genero);
                tx.commit();
                return genero.getId();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Genero buscarPorId(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Genero.class, id);
        }
    }

    public List<Genero> listarTodos() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Genero g ORDER BY g.nombre", Genero.class)
                    .getResultList();
        }
    }

    public boolean actualizar(int id, String nombre) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Genero genero = em.find(Genero.class, id);
                if (genero == null) {
                    tx.rollback();
                    return false;
                }
                genero.setNombre(nombre);
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
                Genero genero = em.find(Genero.class, id);
                if (genero == null) {
                    tx.rollback();
                    return false;
                }
                em.remove(genero);
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
