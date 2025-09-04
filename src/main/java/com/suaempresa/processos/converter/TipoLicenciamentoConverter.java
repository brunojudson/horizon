package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.TipoLicenciamento;
import com.suaempresa.processos.repository.TipoLicenciamentoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named("tipoLicenciamentoConverter")
@ApplicationScoped
public class TipoLicenciamentoConverter implements Converter<TipoLicenciamento> {

    @Inject
    private TipoLicenciamentoRepository tipoLicenciamentoRepository;

    @Override
    public TipoLicenciamento getAsObject(FacesContext context, UIComponent component, String value) {
        
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        
        try {
            // Verifica se o repository foi injetado
            if (tipoLicenciamentoRepository == null) {
                try {
                    InitialContext ic = new InitialContext();
                    tipoLicenciamentoRepository = (TipoLicenciamentoRepository) ic.lookup("java:app/horizon/TipoLicenciamentoRepository");
                } catch (NamingException e) {
                    return null;
                }
            }
            
            Integer id = Integer.valueOf(value);
            
            TipoLicenciamento result = tipoLicenciamentoRepository.findById(id);
            
            return result;
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, TipoLicenciamento value) {
        
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
