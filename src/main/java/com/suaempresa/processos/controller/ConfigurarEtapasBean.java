package com.suaempresa.processos.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.model.EtapaProcesso;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.service.EtapaDisponivelService;
import com.suaempresa.processos.service.ProcessoService;

@Named
@ViewScoped
public class ConfigurarEtapasBean implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer processoId;
    private Processo processo;
    private List<EtapaDisponivel> etapasDisponiveis;
    private List<EtapaDisponivel> etapasSelecionadas = new ArrayList<>();

    private final ProcessoService processoService;
    private final EtapaDisponivelService etapaDisponivelService;

    @Inject
    public ConfigurarEtapasBean(ProcessoService processoService, EtapaDisponivelService etapaDisponivelService) {
        this.processoService = processoService;
        this.etapaDisponivelService = etapaDisponivelService;
    }

    @PostConstruct
    public void init() {
  
        try {
            if (processoId != null) {
                processo = processoService.findById(processoId);
            }
            etapasDisponiveis = etapaDisponivelService.findAll();
            // Seleciona as etapas já associadas ao processo
            if (processo != null) {
                List<EtapaProcesso> etapasProc = processoService.getEtapasDoProcesso(processoId);
                if (etapasProc != null) {
                    for (EtapaProcesso ep : etapasProc) {
                        for (EtapaDisponivel ed : etapasDisponiveis) {
                            if (ed.getId().equals(ep.getEtapaDisponivel().getId())) {
                                etapasSelecionadas.add(ed);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao carregar dados: " + e.getMessage()));
        }
    }

    public void salvarEtapas() {
        try {
            processoService.atualizarEtapasPorDisponiveis(processoId, etapasSelecionadas);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Etapas atualizadas com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar etapas: " + e.getMessage()));
        }
    }

    // Salva e redireciona para o detalhe do processo
    public String salvarEtapasERedirecionar() {
        salvarEtapas();
        return "/processos/detalhe?faces-redirect=true&amp;processoId=" + processoId;
    }

    // Getters e Setters
    public Integer getProcessoId() { return processoId; }
    public void setProcessoId(Integer processoId) { this.processoId = processoId; }
    public Processo getProcesso() { return processo; }
    public void setProcesso(Processo processo) { this.processo = processo; }
    public List<EtapaDisponivel> getEtapasDisponiveis() { return etapasDisponiveis; }
    public void setEtapasDisponiveis(List<EtapaDisponivel> etapasDisponiveis) { this.etapasDisponiveis = etapasDisponiveis; }
    public List<EtapaDisponivel> getEtapasSelecionadas() { return etapasSelecionadas; }
    public void setEtapasSelecionadas(List<EtapaDisponivel> etapasSelecionadas) { this.etapasSelecionadas = etapasSelecionadas; }
}
