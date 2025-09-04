package com.suaempresa.processos.service;

import com.suaempresa.processos.dto.RelatorioProcessoDTO;
import com.suaempresa.processos.enums.StatusProcesso;
import com.suaempresa.processos.model.Cliente;
import com.suaempresa.processos.model.Processo;
import com.suaempresa.processos.model.Usuario;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class RelatorioService {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unused")
	@Inject
    private ProcessoService processoService;

    public List<RelatorioProcessoDTO> gerarRelatorioProcessos(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RelatorioProcessoDTO> cq = cb.createQuery(RelatorioProcessoDTO.class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataCriacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataCriacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.select(cb.construct(RelatorioProcessoDTO.class,
                processo.get("id"),
                processo.get("titulo"),
                processo.get("cliente").get("razaoSocial"), // Alterado de \'nome\' para \'razaoSocial\'
                processo.get("responsavel").get("nome"),
                processo.get("dataCriacao"),
                processo.get("dataFinalizacao"),
                processo.get("status"),
                processo.get("tipoProcesso").get("nome"),
                processo.get("taxaValorTotal")))
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(processo.get("dataCriacao")));

        TypedQuery<RelatorioProcessoDTO> query = em.createQuery(cq);
        return query.getResultList();
    }

    public long countProcessosFinalizados(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(processo.get("status"), StatusProcesso.CONCLUIDO));

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataFinalizacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataFinalizacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.select(cb.count(processo)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    public double getTempoMedioConclusao(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(processo.get("status"), StatusProcesso.CONCLUIDO));
        predicates.add(cb.isNotNull(processo.get("dataFinalizacao")));
        predicates.add(cb.isNotNull(processo.get("dataCriacao")));

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataFinalizacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataFinalizacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.multiselect(processo.get("dataCriacao"), processo.get("dataFinalizacao"))
                .where(predicates.toArray(new Predicate[0]));

        List<Object[]> results = em.createQuery(cq).getResultList();

        if (results.isEmpty()) {
            return 0.0;
        }

        long totalDurationMillis = 0;
        for (Object[] result : results) {
            LocalDateTime dataCriacao = (LocalDateTime) result[0];
            LocalDateTime dataFinalizacao = (LocalDateTime) result[1];
            if (dataCriacao != null && dataFinalizacao != null) {
                totalDurationMillis += Duration.between(dataCriacao, dataFinalizacao).toMillis();
            }
        }
        return (double) totalDurationMillis / results.size() / (1000.0 * 60 * 60 * 24); // Convertendo milissegundos para dias
    }

    public BigDecimal getTotalTaxasGerenciadas(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataCriacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataCriacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.select(cb.sum(processo.get("taxaValorTotal"))).where(predicates.toArray(new Predicate[0]));

        BigDecimal result = em.createQuery(cq).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    public long countProcessosAtrasados(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.lessThan(processo.get("dataPrevisaoFinalizacao"), LocalDate.now()));
        predicates.add(cb.notEqual(processo.get("status"), StatusProcesso.CONCLUIDO));
        predicates.add(cb.notEqual(processo.get("status"), StatusProcesso.CANCELADO));

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataCriacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataCriacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.select(cb.count(processo)).where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    public Map<String, Long> getDistribuicaoProcessosPorTipo(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataCriacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataCriacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.multiselect(processo.get("tipoProcesso").get("nome"), cb.count(processo))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(processo.get("tipoProcesso").get("nome"));

        List<Object[]> results = em.createQuery(cq).getResultList();
        return results.stream().collect(Collectors.toMap(o -> (String) o[0], o -> (Long) o[1]));
    }

    public Map<String, Map<StatusProcesso, Long>> getAnaliseCargaTrabalhoPorResponsavel(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Processo> processo = cq.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(processo.get("dataCriacao"), dataInicio.atStartOfDay()));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(processo.get("dataCriacao"), dataFim.atTime(java.time.LocalTime.MAX)));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(processo.get("status").in(status));
        }
        if (clientes != null && !clientes.isEmpty()) {
            predicates.add(processo.get("cliente").in(clientes));
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            predicates.add(processo.get("responsavel").in(responsaveis));
        }

        cq.multiselect(processo.get("responsavel").get("nome"), processo.get("status"), cb.count(processo))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(processo.get("responsavel").get("nome"), processo.get("status"));

        List<Object[]> results = em.createQuery(cq).getResultList();

        return results.stream()
                .collect(Collectors.groupingBy(o -> (String) o[0],
                        Collectors.toMap(o -> (StatusProcesso) o[1], o -> (Long) o[2])));
    }

    public Map<String, Double> getTempoMedioPorEtapa(LocalDate dataInicio, LocalDate dataFim, List<StatusProcesso> status, List<Cliente> clientes, List<Usuario> responsaveis) {
        // Esta consulta é mais complexa e pode exigir JPQL ou SQL nativo para melhor performance
        // Exemplo simplificado, pode precisar de ajustes para cenários reais com muitas etapas/processos
    String jpql = "SELECT ep.etapaDisponivel.titulo, AVG(FUNCTION(\'EXTRACT\', DAY FROM (ep.dataFinalizacao - ep.dataCadastro)))"
                + " FROM EtapaProcesso ep JOIN ep.processo p WHERE ep.status = :concluido ";

        if (dataInicio != null) {
            jpql += " AND p.dataCriacao >= :dataInicio ";
        }
        if (dataFim != null) {
            jpql += " AND p.dataCriacao <= :dataFim ";
        }
        if (status != null && !status.isEmpty()) {
            jpql += " AND p.status IN :status ";
        }
        if (clientes != null && !clientes.isEmpty()) {
            jpql += " AND p.cliente IN :clientes ";
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            jpql += " AND p.responsavel IN :responsaveis ";
        }

        jpql += " GROUP BY ep.etapaDisponivel.titulo";

        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("concluido", StatusProcesso.CONCLUIDO);

        if (dataInicio != null) {
            query.setParameter("dataInicio", dataInicio.atStartOfDay());
        }
        if (dataFim != null) {
            query.setParameter("dataFim", dataFim.atTime(java.time.LocalTime.MAX));
        }
        if (status != null && !status.isEmpty()) {
            query.setParameter("status", status);
        }
        if (clientes != null && !clientes.isEmpty()) {
            query.setParameter("clientes", clientes);
        }
        if (responsaveis != null && !responsaveis.isEmpty()) {
            query.setParameter("responsaveis", responsaveis);
        }

        List<Object[]> results = query.getResultList();
        if (results == null || results.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        return results.stream()
            .filter(o -> o != null && o[0] != null && o[1] != null)
            .collect(Collectors.toMap(
                o -> (String) o[0],
                o -> (Double) o[1],
                (a, b) -> a // em caso de chave duplicada, mantém o primeiro
            ));
    }
}


