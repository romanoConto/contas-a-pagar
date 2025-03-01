package com.contas_a_pagar;

import com.contas_a_pagar.domain.entity.Usuario;
import com.contas_a_pagar.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ContasAPagarApplication {

    @Autowired
    UsuarioRepository usuarioRepository;

    public static void main(String[] args) {
        SpringApplication.run(ContasAPagarApplication.class, args);
    }

    @Bean
    ApplicationRunner runner(PasswordEncoder passwordEncoder) {
        if (usuarioRepository.findAll().isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setLogin("admin");
            usuario.setSenha(passwordEncoder.encode("admin"));
            usuarioRepository.save(usuario);
        }
        // Encripted password for user using BCrypt encoder
        return args -> System.out.println("password: " + passwordEncoder.encode("admin"));
    }
}
