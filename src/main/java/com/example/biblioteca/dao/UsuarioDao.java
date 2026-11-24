package com.example.biblioteca.dao;

import java.util.List;

import com.example.biblioteca.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

public class UsuarioDao {

    private final EntityManagerFactory emf;

    public UsuarioDao(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Integer insertar(Usuario usuario) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(usuario);
                tx.commit();
                return usuario.getId();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                throw e;
            }
        }
    }

    public Usuario buscarPorId(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Usuario.class, id);
        }
    }

    public List<Usuario> listarTodos() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nombre", Usuario.class)
                    .getResultList();
        }
    }

    public boolean actualizar(int id, String nombre, String correo) {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Usuario usuario = em.find(Usuario.class, id);
                if (usuario == null) {
                    tx.rollback();
                    return false;
                }
                usuario.setNombre(nombre);
                usuario.setCorreo(correo);
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
                Usuario usuario = em.find(Usuario.class, id);
                if (usuario == null) {
                    tx.rollback();
                    return false;
                }
                em.remove(usuario);
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
