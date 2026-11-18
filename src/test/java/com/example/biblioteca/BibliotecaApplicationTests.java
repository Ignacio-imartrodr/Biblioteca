package com.example.biblioteca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.biblioteca.model.Genero;
import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.GeneroRepository;
import com.example.biblioteca.repository.LibroRepository;
import com.example.biblioteca.repository.PrestamoRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import com.example.biblioteca.service.PrestamoService;

@SpringBootTest(properties = {
		"biblioteca.console.enabled=false",
		"spring.datasource.url=jdbc:sqlite::memory:"
})
class BibliotecaApplicationTests {

	@Autowired
	private GeneroRepository generoRepository;

	@Autowired
	private LibroRepository libroRepository;

	@Autowired
	private PrestamoRepository prestamoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PrestamoService prestamoService;

	@Test
	void contextLoads() {
	}

	@Test
	void loanUpdatesAvailableCopiesAndReturnRestoresThem() {
		Genero genero = generoRepository.save(new Genero("Novela"));
		Usuario usuario = usuarioRepository.save(new Usuario("Ana", "ana@example.com"));
		Libro libro = libroRepository.save(new Libro("Libro de prueba", "Autor",
				LocalDate.of(2020, 1, 1), null, genero, 1, 1));

		Integer prestamoId = prestamoService.registrar(usuario.getId(), libro.getId());

		assertEquals(0, libroRepository.findById(libro.getId()).orElseThrow().getCopiasDisponibles());
		assertEquals("Novela", prestamoRepository.findAllOrdenados().get(0).getLibro().getGenero().getNombre());
		assertTrue(prestamoService.devolver(prestamoId));
		assertEquals(1, libroRepository.findById(libro.getId()).orElseThrow().getCopiasDisponibles());
		assertTrue(prestamoRepository.findAllOrdenados().isEmpty());
	}
}
