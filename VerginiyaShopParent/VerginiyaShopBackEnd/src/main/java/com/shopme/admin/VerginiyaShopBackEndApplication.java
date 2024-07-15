package com.shopme.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@EntityScan({"com.shopme.common.entity"})
public class VerginiyaShopBackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(VerginiyaShopBackEndApplication.class, args);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // The password to be encrypted
        String passwordToEncode = "nam2020";

        // Password encryption
        String encodedPassword = passwordEncoder.encode(passwordToEncode);

        // Print the encrypted password
        System.out.println("Password nam2020 it is coded: " + encodedPassword);
    }

}
