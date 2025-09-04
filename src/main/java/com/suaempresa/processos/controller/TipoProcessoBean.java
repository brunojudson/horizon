package com.suaempresa.processos.controller;

import com.suaempresa.processos.model.TipoProcesso;
import com.suaempresa.processos.service.TipoProcessoService;

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
public class TipoProcessoBean implements Serializable {

    private static final long serialVersionUID = 1L;

	@Inject
    private TipoProcessoService tipoProcessoService;

    private TipoProcesso tipoProcesso;
    private List<TipoProcesso> tiposProcesso;

    @PostConstruct
    public void init() {
        this.tipoProcesso = new TipoProcesso();
        tiposProcesso = tipoProcessoService.findAll();
    }

    public void novoTipoProcesso() {
        tipoProcesso = new TipoProcesso();
    }

    public void salvarTipoProcesso() {
    	
        try {
            tipoProcessoService.save(tipoProcesso);
            tiposProcesso = tipoProcessoService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Tipo de Processo salvo com sucesso!"));
           
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar Tipo de Processo: " + e.getMessage()));
        }
        
    }

    public void deletarTipoProcesso(TipoProcesso tp) {
        try {
            tipoProcessoService.delete(tp);
            tiposProcesso = tipoProcessoService.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Tipo de Processo deletado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao deletar Tipo de Processo: " + e.getMessage()));
        }
    }

    public void carregarTipoProcesso(TipoProcesso tp) {
        this.tipoProcesso = tipoProcessoService.findById(tp.getId());
    }


    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        TipoProcesso tipo = (TipoProcesso) value;
        return (tipo.getId() != null && tipo.getId().toString().contains(filterText))
            || (tipo.getNome() != null && tipo.getNome().toLowerCase(locale).contains(filterText))
            || (tipo.getDescricao() != null && tipo.getDescricao().toLowerCase(locale).contains(filterText));
    }

    // Getters e Setters
    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public List<TipoProcesso> getTiposProcesso() {
        return tiposProcesso;
    }
}


