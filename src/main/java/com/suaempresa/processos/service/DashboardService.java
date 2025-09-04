package com.suaempresa.processos.service;

import com.suaempresa.processos.dto.DashboardDTO;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.enums.StatusProcesso;
import com.suaempresa.processos.repository.ProcessoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DashboardService {

    @Inject
    private ProcessoRepository processoRepository;

    public List<DashboardDTO> getProcessosParaDashboard() {
        List<Processo> processos = processoRepository.findAllAtivos();
        List<DashboardDTO> dtos = new ArrayList<>();

        for (Processo p : processos) {
            DashboardDTO dto = new DashboardDTO();
            dto.setId(p.getId());
            dto.setTitulo(p.getTitulo());
            dto.setStatus(p.getStatus().toString());
            dto.setValorTotal(p.getTaxaValorTotal());
            dto.setDataPrevisao(p.getDataPrevisaoFinalizacao());
            // Preencher razão social do cliente
            if (p.getCliente() != null) {
                dto.setClienteRazaoSocial(p.getCliente().getRazaoSocial());
            } else {
                dto.setClienteRazaoSocial("");
            }

            // Calcular percentual de conclusão baseado nas etapas
            int porcentagemConclusao = 0;
            if (p.getEtapas() != null && !p.getEtapas().isEmpty()) {
                long concluidas = p.getEtapas().stream()
                    .filter(e -> e.getStatus() != null && e.getStatus().toString().equals("CONCLUIDO"))
                    .count();
                porcentagemConclusao = (int) ((concluidas * 100) / p.getEtapas().size());
            }
            dto.setPorcentagemConclusao(porcentagemConclusao);

            if (p.getStatus() != StatusProcesso.CONCLUIDO) {
                dto.setTempoAberto(calcularTempoEntre(p.getDataCriacao(), LocalDateTime.now()));
                if (isPrazoVencido(p.getDataPrevisaoFinalizacao())) {
                    dto.setAlertaPrazo("VENCIDO");
                } else if (isPrazoProximo(p.getDataPrevisaoFinalizacao())) {
                    dto.setAlertaPrazo("PRÓXIMO");
                }
            } else {
                dto.setTempoTotalFinalizacao(calcularTempoEntre(p.getDataCriacao(), p.getDataFinalizacao()));
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private String calcularTempoEntre(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            return "N/A";
        }
        Duration duration = Duration.between(inicio, fim);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" dias ");
        }
        if (hours > 0) {
            sb.append(hours).append(" horas ");
        }
        if (minutes > 0 || (days == 0 && hours == 0)) { // Show minutes if no days/hours or if there are minutes
            sb.append(minutes).append(" minutos");
        }
        return sb.toString().trim();
    }
    public int getQuantidadeProcessosConcluidosNoMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        Long quantidade = processoRepository.countConcluidosNoPeriodo(inicio.atStartOfDay(), fim.atTime(23, 59, 59));
        return quantidade != null ? quantidade.intValue() : 0;
    }
    private boolean isPrazoProximo(LocalDate dataPrazo) {
        if (dataPrazo == null) return false;
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataPrazo);
        return diasRestantes > 0 && diasRestantes <= 7;
    }
    
    private boolean isPrazoVencido(LocalDate dataPrazo) {
        if (dataPrazo == null) return false;
        return LocalDate.now().isAfter(dataPrazo);
    }
    public long getQuantidadeProcessosConcluidos() {
        return processoRepository.countConcluidos();
    }
}


