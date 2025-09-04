package com.suaempresa.processos.converter;

import com.suaempresa.processos.enums.StatusProcesso;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class StatusProcessoConverter implements AttributeConverter<StatusProcesso, String> {

    @Override
    public String convertToDatabaseColumn(StatusProcesso status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public StatusProcesso convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        try {
            return StatusProcesso.valueOf(dbData.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return StatusProcesso.ABERTO;
        }
    }
}
