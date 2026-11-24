package com.example.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.biblioteca.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

	/**
	 * El query de @Query trae también los objetos de los parametros de Prestamo indicados en @EntityGraph
	 * 
	 * @return Lista de Prestamo/s encotrados ordenada por id o una lista vacía si no encontró ningúno
	 */
	@EntityGraph(attributePaths = { "usuario", "libro", "libro.genero" })
	@Query("SELECT p FROM Prestamo p ORDER BY p.id")
	List<Prestamo> findAllOrdenados();

	/**
	 * Spring interpreta el nombre de la función por las palabras claves y genera automaticamente el Query
	 * 
	 * @param usuarioId
	 * @return Lista de Prestamo/s encotrados y ordenados por el id del usuario o una lista vacía si no encontró ningúno
	 */
	List<Prestamo> findByUsuarioIdOrderById(Integer usuarioId);

	/**
	 * Spring interpreta el nombre de la función por las palabras claves y genera automaticamente el Query
	 * 
	 * @param libroId
	 * @return Lista de Prestamo/s encotrados y ordenados por el id del libro o una lista vacía si no encontró ningúno
	 */
	List<Prestamo> findByLibroIdOrderById(Integer libroId);
}
