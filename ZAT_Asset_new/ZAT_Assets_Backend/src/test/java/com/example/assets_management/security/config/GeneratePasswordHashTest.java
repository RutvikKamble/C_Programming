package com.example.assets_management.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class GeneratePasswordHashTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generateHash() {
        String password = "Admin@123";

        String hash = passwordEncoder.encode(password);

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PASSWORD HASH:");
        System.out.println(hash);
        System.out.println("==========================================");
        System.out.println();

        System.out.println(
                "Password matches: "
                        + passwordEncoder.matches(password, hash)
        );
    }
}