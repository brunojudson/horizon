package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.Usuario;
import com.suaempresa.processos.repository.UsuarioRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named("usuarioConverter")
@ApplicationScoped
public class UsuarioConverter implements Converter<Usuario> {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        
        try {
            // Verifica se o repository foi injetado
            if (usuarioRepository == null) {
                try {
                    InitialContext ic = new InitialContext();
                    usuarioRepository = (UsuarioRepository) ic.lookup("java:app/horizon/UsuarioRepository");
                } catch (NamingException e) {
                    return null;
                }
            }
            Integer id = Integer.valueOf(value);
            Usuario result = usuarioRepository.findById(id);
            
            return result;
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Usuario value) {
        
        if (value == null) {
            return "";
        }
        
        if (value.getId() == null) {
            return "";
        }
        
        String result = value.getId().toString();
        return result;
    }
}


