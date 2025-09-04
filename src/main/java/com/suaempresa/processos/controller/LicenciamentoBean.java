package com.suaempresa.processos.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.model.Licenciamento;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.model.TipoLicenciamento;
import com.suaempresa.processos.repository.TipoLicenciamentoRepository;
import com.suaempresa.processos.service.ClienteService;
import com.suaempresa.processos.service.LicenciamentoService;

@Named
@ViewScoped
public class LicenciamentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LicenciamentoService licenciamentoService;
    @Inject
    private TipoLicenciamentoRepository tipoLicenciamentoRepository;

    private Licenciamento licenciamento;
    private List<Licenciamento> licenciamentos;
    private List<TipoLicenciamento> tiposLicenciamento;
    // Campo auxiliar para o ID do cliente selecionado
    private Integer clienteId;

    @Inject
    private ClienteService clienteService;
    private List<Cliente> clientes;

    // Para seleção múltipla na tabela
    private List<Licenciamento> filteredLicenciamentos;

    
    @PostConstruct
    public void init() {
        licenciamento = new Licenciamento();
        
        // Carregar licenciamentos com processos para exibir na tela
        licenciamentos = licenciamentoService.findAllWithProcessos();
        
        
    }
    public boolean globalFilterFunction(Object value, Object filter, java.util.Locale locale) {
        if (filter == null || filter.toString().isEmpty()) {
            return true;
        }
        String filterText = filter.toString().trim().toLowerCase(locale);
        Licenciamento l = (Licenciamento) value;
        return (l.getId() != null && l.getId().toString().contains(filterText))
            || (l.getTipo() != null && l.getTipo().getNome() != null && l.getTipo().getNome().toLowerCase(locale).contains(filterText))
            || (l.getCliente() != null && l.getCliente().getRazaoSocial() != null && l.getCliente().getRazaoSocial().toLowerCase(locale).contains(filterText))
            || (l.getDataVencimento() != null && l.getDataVencimento().toString().contains(filterText))
            || (l.getAtivo() != null && (l.getAtivo() ? "sim" : "não").contains(filterText));
    }
    
    public void novoLicenciamento() {
        this.licenciamento = new Licenciamento();
        tiposLicenciamento = tipoLicenciamentoRepository.findAll();
        clientes = clienteService.findAll();
        this.clienteId = null;
    }
    
    public void salvarLicenciamento() {
        try {
            // Buscar o cliente pelo ID selecionado
            if (clienteId != null) {
                Cliente clienteSelecionado = clienteService.findById(clienteId);
                licenciamento.setCliente(clienteSelecionado);
            } else {
                licenciamento.setCliente(null);
            }
            licenciamentoService.save(licenciamento);
            licenciamentos = licenciamentoService.findAllWithProcessos();
            FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Licenciamento salvo com sucesso!"));
            clienteId = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
            "Erro ao salvar licenciamento: " + e.getMessage()));
        }
    }
    
    public void deletarLicenciamento(Licenciamento l) {
        try {
            licenciamentoService.delete(l);
            licenciamentos = licenciamentoService.findAllWithProcessos();
            FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Licenciamento deletado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
            "Erro ao deletar licenciamento: " + e.getMessage()));
        }
    }
    
    public void carregarLicenciamento() {
        tiposLicenciamento = tipoLicenciamentoRepository.findAll();
        clientes = clienteService.findAll();
        if (licenciamento == null) {
            return;
        }
        Integer id = licenciamento.getId();
        if (id == null) {
            return;
        }
        Licenciamento lic = licenciamentoService.findById(id);
        if (lic != null) {
            licenciamento.setTipo(lic.getTipo());
            licenciamento.setDataEmissao(lic.getDataEmissao());
            licenciamento.setDataVencimento(lic.getDataVencimento());
            licenciamento.setTaxaValor(lic.getTaxaValor());
            licenciamento.setAvisoPrevioDias(lic.getAvisoPrevioDias());
            licenciamento.setAtivo(lic.getAtivo());
            // Preencher o campo auxiliar clienteId para exibir o cliente no select
            if (lic.getCliente() != null) {
                this.clienteId = lic.getCliente().getId();
            } else {
                this.clienteId = null;
            }
        }
    }
    
    public String criarNovoProcessoParaLicenciamento(Licenciamento licenciamento) {
        // Navegar para a página de processo com o licenciamento pré-selecionado
        return "/processos/lista?faces-redirect=true&licenciamentoId=" + licenciamento.getId();
    }
    
    public void carregarProcessosLicenciamento() {
        // Recarregar o licenciamento com os processos para garantir que estão atualizados
        if (licenciamento != null && licenciamento.getId() != null) {
            licenciamento = licenciamentoService.findById(licenciamento.getId());
        }
    }
        // Navegação para detalhes do processo (sobrecarga para Processo)
    public String visualizarProcesso(Processo proc) {
        System.out.println(proc.getId());
        if (proc != null && proc.getId() != null) {
            return "/processos/detalhe?faces-redirect=true&amp;processoId=" + proc.getId();
        }
        return null;
    }
    
    
    // Getters e Setters
    public List<Licenciamento> getFilteredLicenciamentos() {
        return filteredLicenciamentos;
    }

    public void setFilteredLicenciamentos(List<Licenciamento> filteredLicenciamentos) {
        this.filteredLicenciamentos = filteredLicenciamentos;
    }
    public List<Cliente> getClientes() {
        return clientes;
    }
    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }
    
    public Licenciamento getLicenciamento() {
        return licenciamento;
    }
    
    public void setLicenciamento(Licenciamento licenciamento) {
        this.licenciamento = licenciamento;
    }
    
    public List<Licenciamento> getLicenciamentos() {
        return licenciamentos;
    }
    
    public List<TipoLicenciamento> getTiposLicenciamento() {
        return tiposLicenciamento;
    }
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
}
