package com.example.biblioteca;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.example.biblioteca.dao.GeneroDao;
import com.example.biblioteca.dao.LibroDao;
import com.example.biblioteca.dao.PrestamoDao;
import com.example.biblioteca.dao.UsuarioDao;
import com.example.biblioteca.model.Genero;
import com.example.biblioteca.model.Libro;
import com.example.biblioteca.model.Usuario;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("bibliotecaPU")) {
            UsuarioDao usuarios = new UsuarioDao(emf);
            GeneroDao generos = new GeneroDao(emf);
            LibroDao libros = new LibroDao(emf);
            PrestamoDao prestamos = new PrestamoDao(emf);

            int opcion;
            do {
                System.out.println("""

                        ===== GESTIÓN DE BIBLIOTECA =====
                        1. Listar libros
                        2. Listar géneros
                        3. Listar usuarios
                        4. Listar préstamos
                        5. Añadir libro
                        6. Añadir género
                        7. Añadir usuario
                        8. Registrar préstamo
                        9. Registrar devolución
                        0. Salir""");
                opcion = leerEntero("Opción: ");

                try {
                    switch (opcion) {
                        case 1 -> mostrar(libros.listarTodos());
                        case 2 -> mostrar(generos.listarTodos());
                        case 3 -> mostrar(usuarios.listarTodos());
                        case 4 -> mostrar(prestamos.listarTodos());
                        case 5 -> {
                            Genero genero = null;
                            int generoId = leerEntero("Id del género (0 si no tiene): ");
                            if (generoId != 0) {
                                genero = generos.buscarPorId(generoId);
                                if (genero == null) {
                                    System.out.println("No existe ese género.");
                                    break;
                                }
                            }
                            Libro libro = new Libro(leerTexto("Título: "), leerTexto("Autor (opcional): "),
                                    leerFecha("Fecha de publicación (AAAA-MM-DD): "),
                                    leerValoracion(), genero, leerEntero("Copias totales: "),
                                    leerEntero("Copias disponibles: "));
                            System.out.println("Libro guardado con id " + libros.insertar(libro));
                        }
                        case 6 -> System.out.println("Género guardado con id "
                                + generos.insertar(new Genero(leerTexto("Nombre: "))));
                        case 7 -> System.out.println("Usuario guardado con id "
                                + usuarios.insertar(new Usuario(leerTexto("Nombre: "), leerTexto("Correo: "))));
                        case 8 -> System.out.println("Préstamo registrado con id "
                                + prestamos.insertar(leerEntero("Id del usuario: "), leerEntero("Id del libro: ")));
                        case 9 -> {
                            boolean ok = prestamos.borrar(leerEntero("Id del préstamo a devolver: "));
                            System.out.println(ok ? "Devolución registrada." : "No existe ese préstamo.");
                        }
                        case 0 -> System.out.println("¡Hasta luego!");
                        default -> System.out.println("Opción no válida.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("No se pudo completar la operación: " + e.getMessage());
                }
            } while (opcion != 0);
        }
    }

    private static <T> void mostrar(List<T> elementos) {
        if (elementos.isEmpty()) {
            System.out.println("No hay elementos.");
        } else {
            elementos.forEach(System.out::println);
        }
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero.");
            }
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            try {
                return LocalDate.parse(leerTexto(mensaje));
            } catch (java.time.format.DateTimeParseException e) {
                System.out.println("Introduce una fecha válida con formato AAAA-MM-DD.");
            }
        }
    }

    private static Double leerValoracion() {
        String valoracion = leerTexto("Valoración (vacío si no tiene): ");
        if (valoracion.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(valoracion);
        } catch (NumberFormatException e) {
            System.out.println("Introduce un número decimal válido.");
            return leerValoracion();
        }
    }
}
