package com.example.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.biblioteca.model.Genero;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {

	@Query("SELECT g FROM Genero g ORDER BY g.nombre")
	List<Genero> findAllOrdenados();
}
