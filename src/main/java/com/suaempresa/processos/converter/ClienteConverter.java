package com.suaempresa.processos.converter;

import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.repository.ClienteRepository;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import java.util.List;

@FacesConverter(value = "clienteConverter", managed = true)
public class ClienteConverter implements Converter<Cliente> {
    @Inject
    private ClienteRepository clienteRepository;

    @Override
    public Cliente getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            Integer id = Integer.parseInt(value);
            // Busca a lista de clientes disponíveis no bean
            List<Cliente> clientes = (List<Cliente>) context.getApplication().evaluateExpressionGet(context, "#{relatorioBean.todosClientes}", List.class);
            if (clientes != null) {
                for (Cliente c : clientes) {
                    if (c != null && c.getId() != null && c.getId().equals(id)) {
                        return c;
                    }
                }
            }
            // fallback para o repository
            return clienteRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Cliente value) {
        if (value == null || value.getId() == null) return "";
        return value.getId().toString();
    }
}
