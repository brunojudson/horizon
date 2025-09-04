package com.suaempresa.processos.controller;

import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.service.EtapaDisponivelService;

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
public class EtapaDisponivelBean implements Serializable {

    private static final long serialVersionUID = 1L;

	@Inject
    private EtapaDisponivelService etapaDisponivelService;

    private EtapaDisponivel etapaDisponivel;
    private List<EtapaDisponivel> etapasDisponiveis;

    @PostConstruct
    public void init() {
        etapaDisponivel = new EtapaDisponivel();
        etapasDisponiveis = etapaDisponivelService.findAll();
    }

    public void novaEtapaDisponivel() {
        this.etapaDisponivel = new EtapaDisponivel();
    }

    public void salvarEtapaDisponivel() {
        try {
            etapaDisponivelService.save(etapaDisponivel);
            etapasDisponiveis = etapaDisponivelService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Etapa Disponível salva com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar Etapa Disponível: " + e.getMessage()));
        }
    }

    public void deletarEtapaDisponivel(EtapaDisponivel ed) {
        try {
            etapaDisponivelService.delete(ed);
            etapasDisponiveis = etapaDisponivelService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Etapa Disponível deletada com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao deletar Etapa Disponível: " + e.getMessage()));
        }
    }

    public void carregarEtapaDisponivel(EtapaDisponivel ed) {
        this.etapaDisponivel = etapaDisponivelService.findById(ed.getId());
    }


    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        EtapaDisponivel etapa = (EtapaDisponivel) value;
        return (etapa.getId() != null && etapa.getId().toString().contains(filterText))
            || (etapa.getTitulo() != null && etapa.getTitulo().toLowerCase(locale).contains(filterText))
            || (etapa.getDescricao() != null && etapa.getDescricao().toLowerCase(locale).contains(filterText));
    }

    // Getters e Setters
    public EtapaDisponivel getEtapaDisponivel() {
        return etapaDisponivel;
    }

    public void setEtapaDisponivel(EtapaDisponivel etapaDisponivel) {
        this.etapaDisponivel = etapaDisponivel;
    }

    public List<EtapaDisponivel> getEtapasDisponiveis() {
        return etapasDisponiveis;
    }
}


