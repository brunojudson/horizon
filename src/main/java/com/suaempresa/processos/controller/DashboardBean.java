package com.suaempresa.processos.controller;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.suaempresa.processos.dto.DashboardDTO;
import com.suaempresa.processos.dto.LicenciamentoDTO;
import com.suaempresa.processos.service.ClienteService;
import com.suaempresa.processos.service.DashboardService;
import com.suaempresa.processos.service.LicenciamentoService;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    // Nova propriedade para quantidade de processos concluídos direto do banco
    private long quantidadeProcessosConcluidos;

    private static final long serialVersionUID = 1L;
    private final DashboardService dashboardService;
    private final LicenciamentoService licenciamentoService;

    @Inject
    private ClienteService clienteService;

    @Inject
    public DashboardBean(DashboardService dashboardService, LicenciamentoService licenciamentoService) {
        this.dashboardService = dashboardService;
        this.licenciamentoService = licenciamentoService;
    }
    
    private List<DashboardDTO> processos;
    private List<LicenciamentoDTO> licenciamentosProximos;
    
    // Propriedades para os cartões do dashboard
    private int processosParados;
    private int processosConcluidos;
    private int processosAtrasados;
    private java.math.BigDecimal valorTotalProcessos;

    @PostConstruct
    public void init() {
        try {
            carregarDados();
            quantidadeProcessosConcluidos = dashboardService.getQuantidadeProcessosConcluidos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getQuantidadeProcessosConcluidos() {
        return quantidadeProcessosConcluidos;
    }

    public void carregarDados() {
        try {
            processos = dashboardService.getProcessosParaDashboard();
                   
            licenciamentosProximos = licenciamentoService.findProximosDoVencimento();
                
            if (licenciamentosProximos != null && !licenciamentosProximos.isEmpty()) {
                for (int i = 0; i < Math.min(3, licenciamentosProximos.size()); i++) {
                    @SuppressWarnings("unused")
					LicenciamentoDTO lic = licenciamentosProximos.get(i);
                }
            }
            calcularEstatisticas();  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calcularEstatisticas() {
        if (processos != null) {
            // Contar processos concluídos no mês
            processosConcluidos = dashboardService.getQuantidadeProcessosConcluidosNoMesAtual();
            
            // Contar processos atrasados
            processosAtrasados = (int) processos.stream()
                .filter(p -> "VENCIDO".equals(p.getAlertaPrazo()))
                .count();
            
            processosParados = (int) processos.stream()
				.filter(p -> "PARADO".equals(p.getStatus()))
				.count();
            
            // Somar valor total
            valorTotalProcessos = processos.stream()
                .filter(p -> p.getValorTotal() != null)
                .map(DashboardDTO::getValorTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        } else {
            processosConcluidos = 0;
            processosAtrasados = 0;
            processosParados = 0;
            valorTotalProcessos = java.math.BigDecimal.ZERO;
        }
    }

    // Navegação para detalhes do processo
    public String visualizarProcesso(DashboardDTO proc) {
        if (proc != null && proc.getId() != null) {
            return "/processos/detalhe?faces-redirect=true&amp;processoId=" + proc.getId();
        }
        return null;
    }
    // Getters para as novas propriedades do dashboard
    public int getTotalProcessosAtivos() {
        if (processos != null) {
            return (int) processos.stream()
                .filter(p -> !"CONCLUIDO".equals(p.getStatus()) && !"CANCELADO".equals(p.getStatus()))
                .count();
        }
        return 0;
    }
    
    public int getTotalClientes() {
        // Buscar quantidade de clientes ativos usando ClienteService
        // Supondo que exista um método clienteService.getQuantidadeClientesAtivos()
        return clienteService.quantidadeClientesAtivos();
    }
    
    public double getTaxaConclusao() {
        if (processos != null && !processos.isEmpty()) {
            return Math.round((double) quantidadeProcessosConcluidos / (processos.size()+quantidadeProcessosConcluidos) * 100 * 100.0) / 100.0;
        }
        return 0.0;
    }
    
    // Método para atualizar dados do dashboard
    public void atualizarDados() {
        carregarDados();
    }
    
    // Getter para data atual
    public LocalDate getDataAtual() {
        return LocalDate.now();
    }
    

    // Getters e Setters
    public List<DashboardDTO> getProcessos() {
        return processos;
    }

    public List<LicenciamentoDTO> getLicenciamentosProximos() {
        return licenciamentosProximos;
    }

    public int getProcessosConcluidos() {
        return processosConcluidos;
    }

    public int getProcessosAtrasados() {
        return processosAtrasados;
    }

    public java.math.BigDecimal getValorTotalProcessos() {
        return valorTotalProcessos;
    }
    
    public int getProcessosParados() {
		return processosParados;
	}
	
}


