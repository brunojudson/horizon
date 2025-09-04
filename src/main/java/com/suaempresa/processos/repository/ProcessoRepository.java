package com.suaempresa.processos.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import com.suaempresa.processos.model.Processo;
@Stateless
public class ProcessoRepository {

    @PersistenceContext
    private EntityManager em;

    public Processo findById(Integer id) {
        List<Processo> result = em.createQuery(
            "SELECT DISTINCT p FROM Processo p " +
            "LEFT JOIN FETCH p.responsavel " +
            "LEFT JOIN FETCH p.tipoProcesso " +
            "LEFT JOIN FETCH p.cliente " +
            "LEFT JOIN FETCH p.licenciamentoAssociado l " +
            "LEFT JOIN FETCH l.tipo " +
            "LEFT JOIN FETCH p.etapas e " +
            "LEFT JOIN FETCH e.etapaDisponivel " +
            "WHERE p.id = :id", Processo.class)
            .setParameter("id", id)
            .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Processo> findAll() {
        // Busca processos já trazendo responsável, tipoProcesso, cliente, licenciamentoAssociado, etapas e etapaDisponivel (evita N+1)
        return em.createQuery(
            "SELECT DISTINCT p FROM Processo p " +
            "LEFT JOIN FETCH p.responsavel " +
            "LEFT JOIN FETCH p.tipoProcesso " +
            "LEFT JOIN FETCH p.cliente " +
            "LEFT JOIN FETCH p.licenciamentoAssociado l " +
            "LEFT JOIN FETCH l.tipo " +
            "LEFT JOIN FETCH p.etapas e " +
            "LEFT JOIN FETCH e.etapaDisponivel " +
            "ORDER BY p.dataCriacao DESC", Processo.class)
            .getResultList();
    }

    public List<Processo> findAllAtivos() {
    return em.createQuery(
        "SELECT DISTINCT p FROM Processo p " +
        "LEFT JOIN FETCH p.cliente " +
        "LEFT JOIN FETCH p.responsavel " +
        "LEFT JOIN FETCH p.tipoProcesso " +
        "LEFT JOIN FETCH p.etapas e " +
        "LEFT JOIN FETCH e.etapaDisponivel " +
        "WHERE p.status IN ('ABERTO', 'PARADO', 'EM_ANDAMENTO')",
        Processo.class)
        .getResultList();
}

    @Transactional
    public void save(Processo processo) {
        if (processo.getId() == null) {
            em.persist(processo);
        } else {
            em.merge(processo);
        }
    }
    
    @Transactional
    public void saveWithNativeQuery(Processo processo) {
        if (processo.getId() == null) {
            // INSERT com CAST explícito
            String sql = "INSERT INTO processos (titulo, descricao, data_criacao, data_previsao_finalizacao, " +
                        "responsavel_id, tipo_processo_id, status, taxa_valor_total) " +
                        "VALUES (?, ?, ?, ?, ?, ?, CAST(? AS status_processo), ?)";
            
            em.createNativeQuery(sql)
              .setParameter(1, processo.getTitulo())
              .setParameter(2, processo.getDescricao())
              .setParameter(3, processo.getDataCriacao())
              .setParameter(4, processo.getDataPrevisaoFinalizacao())
              .setParameter(5, processo.getResponsavel() != null ? processo.getResponsavel().getId() : null)
              .setParameter(6, processo.getTipoProcesso().getId())
              .setParameter(7, processo.getStatus().name())
              .setParameter(8, processo.getTaxaValorTotal())
              .executeUpdate();
        } else {
            // UPDATE com CAST explícito
            String sql = "UPDATE processos SET titulo = ?, descricao = ?, data_previsao_finalizacao = ?, " +
                        "responsavel_id = ?, tipo_processo_id = ?, status = CAST(? AS status_processo), " +
                        "taxa_valor_total = ? WHERE id = ?";
            
            em.createNativeQuery(sql)
              .setParameter(1, processo.getTitulo())
              .setParameter(2, processo.getDescricao())
              .setParameter(3, processo.getDataPrevisaoFinalizacao())
              .setParameter(4, processo.getResponsavel() != null ? processo.getResponsavel().getId() : null)
              .setParameter(5, processo.getTipoProcesso().getId())
              .setParameter(6, processo.getStatus().name())
              .setParameter(7, processo.getTaxaValorTotal())
              .setParameter(8, processo.getId())
              .executeUpdate();
        }
    }

    @Transactional
    public void delete(Processo processo) {
        em.remove(em.contains(processo) ? processo : em.merge(processo));
    }
    public long countConcluidos() {
        return em.createQuery(
            "SELECT COUNT(p) FROM Processo p WHERE p.status = 'CONCLUIDO'",
            Long.class)
            .getSingleResult();
    }

    public long countConcluidosNoPeriodo(LocalDateTime atStartOfDay, LocalDateTime atTime) {
        return em.createQuery(
            "SELECT COUNT(p) FROM Processo p WHERE p.status = 'CONCLUIDO' AND p.dataFinalizacao BETWEEN :start AND :end",
            Long.class)
            .setParameter("start", atStartOfDay)
            .setParameter("end", atTime)
            .getSingleResult();
    }
}


