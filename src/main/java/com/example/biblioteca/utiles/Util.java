package com.example.biblioteca.utiles;

public final class Util {
    public static String normalizarNombre(String nombre){
        if (nombre == null) {
            return null;
        }
        String resultado = "";
        String[] palabras = nombre.strip().toLowerCase().split(" ");
        for (String palabra : palabras) {
            resultado += Character.toUpperCase(palabra.charAt(0)) + palabra.substring(1) + " ";
        }
        resultado = resultado.substring(0, resultado.length() - 1);
        return resultado;
    }
}
