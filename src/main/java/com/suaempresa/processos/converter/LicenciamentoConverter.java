package com.suaempresa.processos.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.suaempresa.processos.model.Licenciamento;
import com.suaempresa.processos.repository.LicenciamentoRepository;

@FacesConverter(value = "licenciamentoConverter", managed = true)
public class LicenciamentoConverter implements Converter<Licenciamento> {

    @Inject
    private LicenciamentoRepository licenciamentoRepository;
    
    // Construtor padrão necessário para JSF
    public LicenciamentoConverter() {
    }

    @Override
    public Licenciamento getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return null;
        }

        try {
            Integer id = Integer.valueOf(value);
            Licenciamento licenciamento = licenciamentoRepository.findById(id);
            if (licenciamento == null) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Conversão",
                        "Licenciamento não encontrado com ID: " + value));
            }
            return licenciamento;
        } catch (NumberFormatException e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Conversão",
                    "ID do licenciamento inválido: " + value), e);
        } catch (Exception e) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de Conversão",
                    "Erro ao buscar licenciamento: " + e.getMessage()), e);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Licenciamento licenciamento) {
        if (licenciamento == null || licenciamento.getId() == null) {
            return "";
        }
        return licenciamento.getId().toString();
    }
}
