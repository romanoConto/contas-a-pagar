package com.contas_a_pagar.domain.repository;

import com.contas_a_pagar.TestContasAPagarApplication;
import com.contas_a_pagar.application.dto.usuario.UsuarioDTO;
import com.contas_a_pagar.domain.entity.Usuario;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestContasAPagarApplication.class)
@Transactional
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    @Transactional
    void findByLoginMustSuccessfully() {
        UsuarioDTO usuarioDTO = new UsuarioDTO("admin2", "admin2");
        Usuario createdUser = createUser(usuarioDTO);

        Optional<Usuario> result = usuarioRepository.findByLogin("admin2");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(createdUser);
    }

    @Test
    @Transactional
    void findByLoginMustEmpty() {
        Optional<Usuario> result = usuarioRepository.findByLogin("other");

        assertThat(result.isPresent()).isFalse();
    }

    private Usuario createUser(UsuarioDTO data) {
        Usuario user = modelMapper.map(data, Usuario.class);
        em.persist(user);
        return user;
    }
}