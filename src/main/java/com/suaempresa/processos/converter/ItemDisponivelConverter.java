package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.ItemDisponivel;
import com.suaempresa.processos.repository.ItemDisponivelRepository;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@FacesConverter(value = "itemDisponivelConverter", managed = true)
public class ItemDisponivelConverter implements Converter<ItemDisponivel> {

    @Inject
    private ItemDisponivelRepository itemDisponivelRepository;

    @Override
    public ItemDisponivel getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            // Fallback para lookup manual se a injeção falhar
            if (itemDisponivelRepository == null) {
                javax.naming.Context ctx = new javax.naming.InitialContext();
                itemDisponivelRepository = (ItemDisponivelRepository) ctx.lookup("java:module/ItemDisponivelRepository");
            }
            return itemDisponivelRepository != null ? itemDisponivelRepository.findById(id) : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ItemDisponivel value) {
        if (value == null || value.getId() == null) {
            return "";
        }
        return value.getId().toString();
    }
}
