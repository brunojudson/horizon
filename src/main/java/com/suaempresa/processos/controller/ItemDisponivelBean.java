package com.suaempresa.processos.controller;

import com.suaempresa.processos.model.ItemDisponivel;
import com.suaempresa.processos.service.ItemDisponivelService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ItemDisponivelBean implements Serializable {

    private static final long serialVersionUID = 1L;

	@Inject
    private ItemDisponivelService itemDisponivelService;

    private ItemDisponivel itemDisponivel;
    private List<ItemDisponivel> itensDisponiveis;

    @PostConstruct
    public void init() {
        itemDisponivel = new ItemDisponivel();
        itensDisponiveis = itemDisponivelService.findAll();
    }

    public void novoItemDisponivel() {
        this.itemDisponivel = new ItemDisponivel();
    }

    public void salvarItemDisponivel() {
        try {
            itemDisponivelService.save(itemDisponivel);
            itensDisponiveis = itemDisponivelService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Item Disponível salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar Item Disponível: " + e.getMessage()));
        }
    }

    public void deletarItemDisponivel(ItemDisponivel id) {
        try {
            itemDisponivelService.delete(id);
            itensDisponiveis = itemDisponivelService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Item Disponível deletado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao deletar Item Disponível: " + e.getMessage()));
        }
    }

    public void carregarItemDisponivel(ItemDisponivel id) {
        this.itemDisponivel = itemDisponivelService.findById(id.getId());
    }


    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        ItemDisponivel item = (ItemDisponivel) value;
        return (item.getId() != null && item.getId().toString().contains(filterText))
            || (item.getTitulo() != null && item.getTitulo().toLowerCase(locale).contains(filterText))
            || (item.getDescricao() != null && item.getDescricao().toLowerCase(locale).contains(filterText));
    }

    // Getters e Setters
    public ItemDisponivel getItemDisponivel() {
        return itemDisponivel;
    }

    public void setItemDisponivel(ItemDisponivel itemDisponivel) {
        this.itemDisponivel = itemDisponivel;
    }

    public List<ItemDisponivel> getItensDisponiveis() {
        return itensDisponiveis;
    }
}


