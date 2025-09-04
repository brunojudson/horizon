package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.service.EtapaDisponivelService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@FacesConverter(value = "etapaDisponivelConverter", managed = true)
public class EtapaDisponivelConverter implements Converter<EtapaDisponivel> {

    @Inject
    private EtapaDisponivelService etapaDisponivelService;

    @Override
    public EtapaDisponivel getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return etapaDisponivelService.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, EtapaDisponivel value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
