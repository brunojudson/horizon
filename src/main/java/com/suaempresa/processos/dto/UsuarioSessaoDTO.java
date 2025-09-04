package com.suaempresa.processos.dto;

import com.suaempresa.processos.enums.PerfilUsuario;
import java.io.Serializable;

public class UsuarioSessaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;

    public UsuarioSessaoDTO(Integer id, String nome, String email, PerfilUsuario perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
}
