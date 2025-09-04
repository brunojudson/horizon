package com.suaempresa.processos.converter;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Named
@ApplicationScoped
@FacesConverter(value = "localDateTimeConverter", managed = true)
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    private static final Logger logger = Logger.getLogger(LocalDateTimeConverter.class.getName());
    
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Tentar primeiro com formato brasileiro
            return LocalDateTime.parse(value, DISPLAY_FORMATTER);
        } catch (Exception e1) {
            try {
                // Tentar com formato ISO
                return LocalDateTime.parse(value, ISO_FORMATTER);
            } catch (Exception e2) {
                logger.warning("Erro ao converter string para LocalDateTime: " + value + ". Erro: " + e2.getMessage());
                return null;
            }
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDateTime value) {
        if (value == null) {
            return "";
        }

        try {
            return value.format(DISPLAY_FORMATTER);
        } catch (Exception e) {
            logger.warning("Erro ao converter LocalDateTime para string: " + value + ". Erro: " + e.getMessage());
            return value.toString(); // Fallback para toString()
        }
    }
}
