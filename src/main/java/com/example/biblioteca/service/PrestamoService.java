package com.example.biblioteca.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Prestamo;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.LibroRepository;
import com.example.biblioteca.repository.PrestamoRepository;
import com.example.biblioteca.repository.UsuarioRepository;

@Service
public class PrestamoService {

	private final LibroRepository libroRepository;
	private final PrestamoRepository prestamoRepository;
	private final UsuarioRepository usuarioRepository;

	public PrestamoService(LibroRepository libroRepository, PrestamoRepository prestamoRepository,
			UsuarioRepository usuarioRepository) {
		this.libroRepository = libroRepository;
		this.prestamoRepository = prestamoRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional
	public Integer registrar(int usuarioId, int libroId) {
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el usuario indicado."));
		Libro libro = libroRepository.findById(libroId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el libro indicado."));
		if (!libro.setCopiasDisponibles(libro.getCopiasDisponibles() - 1)) {
			throw new IllegalStateException("No hay copias disponibles de este libro.");
		}

		libroRepository.save(libro);
		return prestamoRepository.save(new Prestamo(usuario, libro)).getId();
	}

	@Transactional
	public boolean devolver(int prestamoId) {
		Prestamo prestamo = prestamoRepository.findById(prestamoId).orElse(null);
		if (prestamo == null) {
			return false;
		}

		Libro libro = prestamo.getLibro();
		if (!libro.setCopiasDisponibles(libro.getCopiasDisponibles() + 1)) {
			throw new IllegalStateException("No se pudo actualizar la disponibilidad del libro.");
		}
		libroRepository.save(libro);
		prestamoRepository.delete(prestamo);
		return true;
	}
}
