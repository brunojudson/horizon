package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.TipoProcesso;
import com.suaempresa.processos.repository.TipoProcessoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named("tipoProcessoConverter")
@ApplicationScoped
public class TipoProcessoConverter implements Converter<TipoProcesso> {

    @Inject
    private TipoProcessoRepository tipoProcessoRepository;

    @Override
    public TipoProcesso getAsObject(FacesContext context, UIComponent component, String value) {
        
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        
        try {
            // Verifica se o repository foi injetado
            if (tipoProcessoRepository == null) {
                // Fallback: busca manual via JNDI se a injeção falhar
                try {
                    InitialContext ic = new InitialContext();
                    tipoProcessoRepository = (TipoProcessoRepository) ic.lookup("java:app/horizon/TipoProcessoRepository");
                } catch (NamingException e) {
                    return null;
                }
            }
            
            Integer id = Integer.valueOf(value);
            TipoProcesso result = tipoProcessoRepository.findById(id);
            
            return result;
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoProcesso value) {
        
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