package com.example.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.biblioteca.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

	@EntityGraph(attributePaths = { "usuario", "libro", "libro.genero" })
	@Query("SELECT p FROM Prestamo p ORDER BY p.id")
	List<Prestamo> findAllOrdenados();

	List<Prestamo> findByUsuarioIdOrderById(Integer usuarioId);
}
