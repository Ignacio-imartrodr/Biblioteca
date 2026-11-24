package com.example.biblioteca;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.example.biblioteca.model.Genero;
import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Usuario;
import com.example.biblioteca.repository.GeneroRepository;
import com.example.biblioteca.repository.LibroRepository;
import com.example.biblioteca.repository.PrestamoRepository;
import com.example.biblioteca.repository.UsuarioRepository;
import com.example.biblioteca.service.PrestamoService;

/**
 * Aplicación de consola con las operaciones CRUD básicas sobre los modelos
 * usando Spring Data JPA Repository.
 */
@Component
@ConditionalOnProperty(prefix = "biblioteca", name = "test-mode", havingValue = "false", matchIfMissing = false)
public class BibliotecaConsole implements CommandLineRunner {

	private final GeneroRepository generoRepository;
	private final LibroRepository libroRepository;
	private final PrestamoRepository prestamoRepository;
	private final UsuarioRepository usuarioRepository;
	private final PrestamoService prestamoService;
	private final Scanner sc = new Scanner(System.in);

	public BibliotecaConsole(GeneroRepository generoRepository, LibroRepository libroRepository,
			PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository,
			PrestamoService prestamoService) {
		this.generoRepository = generoRepository;
		this.libroRepository = libroRepository;
		this.prestamoRepository = prestamoRepository;
		this.usuarioRepository = usuarioRepository;
		this.prestamoService = prestamoService;
	}

	@Override
	public void run(String... args) {
		// Spring Boot ya configura automáticamente el EntityManagerFactory y las transacciones,
        // por lo que puedes usar directamente los repository.
		int opcion;
		do {
			System.out.println("""
				
					===== GESTIÓN DE BIBLIOTECA =====
					1. Gestionar libros
					2. Gestionar géneros
					3. Gestionar usuarios
					4. Gestionar préstamos
					0. Salir""");
			opcion = leerEntero("Opción: ");
			try {
				switch (opcion) {
					case 1 -> menuLibros();
					case 2 -> menuGeneros();
					case 3 -> menuUsuarios();
					case 4 -> menuPrestamos();
					case 0 -> System.out.println("Gracias por usar el programa");
					default -> System.out.println("Opción no válida.");
				}
			} catch (IllegalArgumentException | IllegalStateException e) {
				System.out.println("No se pudo completar la operación: " + e.getMessage());
			} catch (DataIntegrityViolationException e) {
				System.out.println("No se pudo completar la operación porque el registro tiene datos relacionados o duplicados.");
			}
		} while (opcion != 0);
	}

	private void menuLibros() {
		int opcion;
		do {
			System.out.println("""

					--- LIBROS ---
					1. Listar
					2. Buscar por id
					3. Añadir
					4. Modificar
					5. Borrar
					0. Volver""");
			opcion = leerEntero("Opción: ");
			switch (opcion) {
				case 1 -> mostrar(libroRepository.findAllOrdenados());
				case 2 -> mostrarUno(libroRepository.findById(leerEntero("Id del libro: ")),
						"No existe ningún libro con ese id.");
				case 3 -> crearLibro();
				case 4 -> modificarLibro();
				case 5 -> borrarLibro();
				case 0 -> { }
				default -> System.out.println("Opción no válida.");
			}
		} while (opcion != 0);
	}

	private void crearLibro() {
		Genero genero = leerGeneroOpcional();
		Libro libro = new Libro(leerTexto("Título: "), leerTexto("Autor (opcional): "),
				leerFecha("Fecha de publicación (AAAA-MM-DD): "), leerValoracion(), genero,
				leerEntero("Copias totales: "), leerEntero("Copias disponibles: "));
		Libro guardado = libroRepository.save(libro);
		System.out.println("Libro guardado con id " + guardado.getId());
	}

	private void modificarLibro() {
		Optional<Libro> resultado = libroRepository.findById(leerEntero("Id del libro: "));
		if (resultado.isEmpty()) {
			System.out.println("No existe ningún libro con ese id.");
			return;
		}

		Libro libro = resultado.get();
		if (!libro.setTitulo(leerTexto("Nuevo título: "))
				|| !libro.setFechaPub(leerFecha("Nueva fecha (AAAA-MM-DD): "))
				|| !libro.setValoracion(leerValoracion())) {
			throw new IllegalArgumentException("Los datos del libro no son válidos.");
		}
		libro.setAutor(leerTexto("Nuevo autor (opcional): "));
		libro.setGenero(leerGeneroOpcional());

		int copiasTotales = leerEntero("Nuevo número de copias totales: ");
		if (!libro.setCopiasTotales(copiasTotales)) {
			throw new IllegalArgumentException("Las copias totales no pueden ser menores que las copias prestadas.");
		}
		if (!libro.setCopiasDisponibles(leerEntero("Nuevo número de copias disponibles: "))) {
			throw new IllegalArgumentException("Las copias disponibles deben estar entre 0 y el total.");
		}
		libroRepository.save(libro);
		System.out.println("Libro modificado.");
	}

	private void borrarLibro() {
		int id = leerEntero("Id del libro a borrar: ");
		if (!libroRepository.existsById(id)) {
			System.out.println("No existe ningún libro con ese id.");
			return;
		}
		libroRepository.deleteById(id);
		System.out.println("Libro borrado.");
	}

	private void menuGeneros() {
		int opcion;
		do {
			System.out.println("""

					--- GÉNEROS ---
					1. Listar
					2. Buscar por id
					3. Añadir
					4. Modificar
					5. Borrar
					0. Volver""");
			opcion = leerEntero("Opción: ");
			switch (opcion) {
				case 1 -> mostrar(generoRepository.findAllOrdenados());
				case 2 -> mostrarUno(generoRepository.findById(leerEntero("Id del género: ")),
						"No existe ningún género con ese id.");
				case 3 -> {
					Genero guardado = generoRepository.save(new Genero(leerTexto("Nombre: ")));
					System.out.println("Género guardado con id " + guardado.getId());
				}
				case 4 -> modificarGenero();
				case 5 -> borrarGenero();
				case 0 -> { }
				default -> System.out.println("Opción no válida.");
			}
		} while (opcion != 0);
	}

	private void modificarGenero() {
		Optional<Genero> resultado = generoRepository.findById(leerEntero("Id del género: "));
		if (resultado.isEmpty()) {
			System.out.println("No existe ningún género con ese id.");
			return;
		}
		Genero genero = resultado.get();
		genero.setNombre(leerTexto("Nuevo nombre: "));
		generoRepository.save(genero);
		System.out.println("Género modificado.");
	}

	private void borrarGenero() {
		int id = leerEntero("Id del género a borrar: ");
		if (!generoRepository.existsById(id)) {
			System.out.println("No existe ningún género con ese id.");
			return;
		}
		generoRepository.deleteById(id);
		System.out.println("Género borrado.");
	}

	private void menuUsuarios() {
		int opcion;
		do {
			System.out.println("""

					--- USUARIOS ---
					1. Listar
					2. Buscar por id
					3. Buscar por nombre
					4. Añadir
					5. Modificar
					6. Borrar
					0. Volver""");
			opcion = leerEntero("Opción: ");
			switch (opcion) {
				case 1 -> mostrar(usuarioRepository.findAllOrdenados());
				case 2 -> mostrarUno(usuarioRepository.findById(leerEntero("Id del usuario: ")),
						"No existe ningún usuario con ese id.");
				case 3 -> mostrar(usuarioRepository.findByNombreContainingIgnoreCase(
						leerTexto("Texto del nombre: ")));
				case 4 -> {
					Usuario guardado = usuarioRepository.save(
							new Usuario(leerTexto("Nombre: "), leerTexto("Correo: ")));
					System.out.println("Usuario guardado con id " + guardado.getId());
				}
				case 5 -> modificarUsuario();
				case 6 -> borrarUsuario();
				case 0 -> { }
				default -> System.out.println("Opción no válida.");
			}
		} while (opcion != 0);
	}

	private void modificarUsuario() {
		Optional<Usuario> resultado = usuarioRepository.findById(leerEntero("Id del usuario: "));
		if (resultado.isEmpty()) {
			System.out.println("No existe ningún usuario con ese id.");
			return;
		}
		Usuario usuario = resultado.get();
		if (!usuario.setNombre(leerTexto("Nuevo nombre: "))
				|| !usuario.setCorreo(leerTexto("Nuevo correo: "))) {
			throw new IllegalArgumentException("El nombre y el correo son obligatorios.");
		}
		usuarioRepository.save(usuario);
		System.out.println("Usuario modificado.");
	}

	private void borrarUsuario() {
		int id = leerEntero("Id del usuario a borrar: ");
		if (!usuarioRepository.existsById(id)) {
			System.out.println("No existe ningún usuario con ese id.");
			return;
		}
		usuarioRepository.deleteById(id);
		System.out.println("Usuario borrado.");
	}

	private void menuPrestamos() {
		int opcion;
		do {
			System.out.println("""

					--- PRÉSTAMOS ---
					1. Listar
					2. Buscar por id
					3. Registrar préstamo
					4. Registrar devolución
					0. Volver""");
			opcion = leerEntero("Opción: ");
			switch (opcion) {
				case 1 -> mostrar(prestamoRepository.findAllOrdenados());
				case 2 -> mostrarUno(prestamoRepository.findById(leerEntero("Id del préstamo: ")),
						"No existe ningún préstamo con ese id.");
				case 3 -> {
					int usuarioId = leerEntero("Id del usuario: ");
					int libroId = leerEntero("Id del libro: ");
					System.out.println("Préstamo registrado con id "
							+ prestamoService.registrar(usuarioId, libroId));
				}
				case 4 -> {
					int id = leerEntero("Id del préstamo a devolver: ");
					System.out.println(prestamoService.devolver(id)
							? "Devolución registrada." : "No existe ese préstamo.");
				}
				case 0 -> { }
				default -> System.out.println("Opción no válida.");
			}
		} while (opcion != 0);
	}

	private Genero leerGeneroOpcional() {
		int generoId = leerEntero("Id del género (0 si no tiene): ");
		if (generoId == 0) {
			return null;
		}
		return generoRepository.findById(generoId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el género indicado."));
	}

	private static <T> void mostrar(List<T> elementos) {
		if (elementos.isEmpty()) {
			System.out.println("No hay elementos.");
		} else {
			elementos.forEach(System.out::println);
		}
	}

	private static <T> void mostrarUno(Optional<T> elemento, String mensajeSiNoExiste) {
		if (elemento.isPresent()) {
			System.out.println(elemento.get());
		} else {
			System.out.println(mensajeSiNoExiste);
		}
	}

	private String leerTexto(String mensaje) {
		System.out.print(mensaje);
		if (!sc.hasNextLine()) {
			throw new IllegalStateException("Se terminó la entrada de datos.");
		}
		return sc.nextLine().trim();
	}

	private int leerEntero(String mensaje) {
		while (true) {
			System.out.print(mensaje);
			if (!sc.hasNextLine()) {
				return 0;
			}
			try {
				return Integer.parseInt(sc.nextLine().trim());
			} catch (NumberFormatException e) {
				System.out.println("Introduce un número entero.");
			}
		}
	}

	private LocalDate leerFecha(String mensaje) {
		while (true) {
			try {
				return LocalDate.parse(leerTexto(mensaje));
			} catch (DateTimeParseException e) {
				System.out.println("Introduce una fecha válida con formato AAAA-MM-DD.");
			}
		}
	}

	private Double leerValoracion() {
		while (true) {
			String texto = leerTexto("Valoración (vacío si no tiene): ");
			if (texto.isEmpty()) {
				return null;
			}
			try {
				return Double.valueOf(texto);
			} catch (NumberFormatException e) {
				System.out.println("Introduce un número decimal válido.");
			}
		}
	}
}
