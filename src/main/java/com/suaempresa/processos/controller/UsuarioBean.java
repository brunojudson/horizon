package com.suaempresa.processos.controller;

import com.suaempresa.processos.dto.UsuarioSessaoDTO;
import com.suaempresa.processos.enums.PerfilUsuario;
import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.service.UsuarioService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Arrays;

@Named
@ViewScoped
public class UsuarioBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private UsuarioService usuarioService;
    
    private Usuario usuario;
    private List<Usuario> usuarios;
    private List<PerfilUsuario> perfisUsuario;
    
    // Filtro global
  
    @PostConstruct
    public void init() {
        usuario = new Usuario();
        usuarios = usuarioService.findAll();
        perfisUsuario = Arrays.asList(PerfilUsuario.values());
    }

    public void novoUsuario() {
        usuario = new Usuario();
    }

    public void salvarUsuario() {
        try {
            usuarioService.save(usuario);
            usuarios = usuarioService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar usuário: " + e.getMessage()));
        }
    }
        public boolean getAdmin() {
            UsuarioSessaoDTO currentUser = (UsuarioSessaoDTO) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currentUser");
            return currentUser != null && currentUser.getPerfil() == PerfilUsuario.ADMINISTRADOR;
        }

    public void deletarUsuario(Usuario u) {
        try {
            usuarioService.delete(u);
            usuarios = usuarioService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário deletado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao deletar usuário: " + e.getMessage()));
        }
    }

    public void carregarUsuario(Usuario u) {
        this.usuario = usuarioService.findById(u.getId());
    }

    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
    Usuario u = (Usuario) value;
    return (u.getId() != null && u.getId().toString().contains(filterText))
        || (u.getNome() != null && u.getNome().toLowerCase(locale).contains(filterText))
        || (u.getEmail() != null && u.getEmail().toLowerCase(locale).contains(filterText))
        || (u.getPerfil() != null && u.getPerfil().name().toLowerCase(locale).contains(filterText));
    }



    // Getters e Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<PerfilUsuario> getPerfisUsuario() {
        return perfisUsuario;
    }

    public PerfilUsuario getPerfilAdministrador() {
        return PerfilUsuario.ADMINISTRADOR;
    }
}


