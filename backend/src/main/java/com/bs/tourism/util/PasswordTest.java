package com.bs.tourism.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "123456";
        String dbHash = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2";

        System.out.println("=== BCrypt Password Test ===");
        System.out.println("Password: " + password);
        System.out.println("DB Hash: " + dbHash);
        System.out.println("Hash length: " + dbHash.length());
        System.out.println();

        boolean matches = encoder.matches(password, dbHash);
        System.out.println("Direct match result: " + matches);
        System.out.println();

        String newHash = encoder.encode(password);
        System.out.println("New generated hash: " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
        System.out.println();

        String testHash = encoder.encode("test");
        System.out.println("Test hash for 'test': " + testHash);
        System.out.println("Test hash matches 'test': " + encoder.matches("test", testHash));
        System.out.println("Test hash matches '123456': " + encoder.matches("123456", testHash));
    }
}
