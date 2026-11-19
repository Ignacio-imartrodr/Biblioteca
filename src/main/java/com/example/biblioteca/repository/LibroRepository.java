package com.example.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.biblioteca.model.Libro;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

	@Query("SELECT l FROM Libro l ORDER BY l.titulo")
	List<Libro> findAllOrdenados();

	List<Libro> findByGeneroIdOrderByTitulo(Integer generoId);

	@Query("SELECT l FROM Libro l WHERE l.titulo LIKE CONCAT('%', UPPER(:texto), '%') ORDER BY l.titulo")
	List<Libro> findByTituloContiene(@Param("texto") String texto);
}
