package com.suaempresa.processos.converter;

import com.suaempresa.processos.enums.PerfilUsuario;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("perfilUsuarioConverter")
public class PerfilUsuarioConverter implements Converter<PerfilUsuario> {
    @Override
    public PerfilUsuario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return PerfilUsuario.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, PerfilUsuario value) {
        if (value == null) {
            return "";
        }
        return value.name();
    }
}
