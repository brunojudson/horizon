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
import java.util.logging.Logger;

@Named("robustLocalDateConverter")
@ApplicationScoped
@FacesConverter("robustLocalDateConverter")
public class RobustLocalDateConverter implements Converter<LocalDate> {

    private static final Logger logger = Logger.getLogger(RobustLocalDateConverter.class.getName());
    
    private static final DateTimeFormatter BRAZILIAN_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter[] FORMATTERS = {
        BRAZILIAN_FORMATTER,
        ISO_FORMATTER,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    @Override
    public LocalDate getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }

        String cleanValue = value.trim();
        
        // Tenta todos os formatters possíveis
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalDate result = LocalDate.parse(cleanValue, formatter);
                logger.fine("RobustLocalDateConverter - Convertido com sucesso: '" + cleanValue + "' -> " + result);
                return result;
            } catch (DateTimeParseException e) {
                // Continua tentando outros formatters
            }
        }
        
        logger.warning("RobustLocalDateConverter - Não foi possível converter: '" + cleanValue + "'");
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, LocalDate value) {
        if (value == null) {
            return "";
        }
        
        try {
            return value.format(BRAZILIAN_FORMATTER);
        } catch (Exception e) {
            logger.severe("RobustLocalDateConverter - Erro ao formatar data: " + value + " - " + e.getMessage());
            return value.toString();
        }
    }
}
