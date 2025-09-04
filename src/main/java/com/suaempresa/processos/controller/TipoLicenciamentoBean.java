package com.suaempresa.processos.controller;

import com.suaempresa.processos.model.TipoLicenciamento;
import com.suaempresa.processos.service.TipoLicenciamentoService;

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
public class TipoLicenciamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoLicenciamentoService tipoLicenciamentoService;

    private TipoLicenciamento tipoLicenciamento;
    private List<TipoLicenciamento> tiposLicenciamento;

    @PostConstruct
    public void init() {
        tipoLicenciamento = new TipoLicenciamento();
        tiposLicenciamento = tipoLicenciamentoService.findAll();
    }

    public void novoTipoLicenciamento() {
        this.tipoLicenciamento = new TipoLicenciamento();
    }

    public void salvarTipoLicenciamento() {
        try {
            tipoLicenciamentoService.save(tipoLicenciamento);
            tiposLicenciamento = tipoLicenciamentoService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Tipo de Licenciamento salvo com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar Tipo de Licenciamento: " + e.getMessage()));
        }
    }

    public void deletarTipoLicenciamento(TipoLicenciamento tl) {
        try {
            tipoLicenciamentoService.delete(tl);
            tiposLicenciamento = tipoLicenciamentoService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Tipo de Licenciamento deletado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao deletar Tipo de Licenciamento: " + e.getMessage()));
        }
    }

    public void carregarTipoLicenciamento(TipoLicenciamento tl) {
        this.tipoLicenciamento = tipoLicenciamentoService.findById(tl.getId());
    }


    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        TipoLicenciamento tipo = (TipoLicenciamento) value;
        return (tipo.getId() != null && tipo.getId().toString().contains(filterText))
            || (tipo.getNome() != null && tipo.getNome().toLowerCase(locale).contains(filterText))
            || (tipo.getDescricao() != null && tipo.getDescricao().toLowerCase(locale).contains(filterText));
    }

    // Getters e Setters
    public TipoLicenciamento getTipoLicenciamento() {
        return tipoLicenciamento;
    }

    public void setTipoLicenciamento(TipoLicenciamento tipoLicenciamento) {
        this.tipoLicenciamento = tipoLicenciamento;
    }

    public List<TipoLicenciamento> getTiposLicenciamento() {
        return tiposLicenciamento != null ? tiposLicenciamento : java.util.Collections.emptyList();
    }
}
