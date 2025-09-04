package com.suaempresa.processos.controller;

import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.dto.UsuarioSessaoDTO;
import com.suaempresa.processos.service.UsuarioService;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

	// Alias para action do botão de logout
    public String logout() {
        return doLogout();
    }

    // Retorna o usuário logado da sessão
    public UsuarioSessaoDTO getUsuarioLogado() {
        Object user = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
        if (user instanceof UsuarioSessaoDTO) {
            return (UsuarioSessaoDTO) user;
        }
        return null;
    }

    @Inject
    private UsuarioService usuarioService;

    private String email;
    private String senha;

    public String doLogin() {
        try {
            Usuario usuario = usuarioService.autenticar(email, senha);
            UsuarioSessaoDTO usuarioSessao = new UsuarioSessaoDTO(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil()
            );
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currentUser", usuarioSessao);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Login", "Bem-vindo, " + usuario.getNome() + "!"));
            return "/dashboard.xhtml?faces-redirect=true";
        } catch (SecurityException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Erro", e.getMessage()));
            return null;
        }
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String doLogout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }
}