package com.example.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.biblioteca.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	@Query("SELECT u FROM Usuario u ORDER BY u.nombre")
	List<Usuario> findAllOrdenados();

	List<Usuario> findByNombreContainingIgnoreCase(String texto);
}