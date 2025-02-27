package com.contas_a_pagar.application.dto.usuario;

public class UsuarioDTO {
    private Integer id;
    private String login;
    private String senha;

    public UsuarioDTO(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
