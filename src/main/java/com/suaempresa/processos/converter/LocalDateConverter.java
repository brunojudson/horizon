package com.suaempresa.processos.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Named("localDateConverter")
@ApplicationScoped
@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }
        
        try {
            // Primeiro tenta o formato brasileiro dd/MM/yyyy
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                // Se falhar, tenta o formato ISO yyyy-MM-dd que pode vir do banco
                return LocalDate.parse(value);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        if (value == null) {
            return "";
        }
        
        try {
            String result = value.format(FORMATTER);
            return result;
        } catch (Exception e) {
            return "";
        }
    }
}
