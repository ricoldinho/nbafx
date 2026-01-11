package edu.rico.nbafx.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilidad auxiliar para generar hashes de contraseñas manualmente.
 * Útil para insertar usuarios iniciales en la base de datos.
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        String password = "admin123";
        String hash = hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hash para BD: " + hash);
        
        // Generar SQL de ejemplo
        System.out.println("\nSQL para actualizar usuario admin:");
        System.out.println("UPDATE usuarios SET password = '" + hash + "' WHERE nombre = 'admin';");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear la contraseña", e);
        }
    }
}