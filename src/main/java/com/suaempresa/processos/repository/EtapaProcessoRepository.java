package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.EtapaProcesso;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class EtapaProcessoRepository {

    @Transactional
    public void updateOrdem(Integer id, Integer ordem) {
        em.createQuery("UPDATE EtapaProcesso e SET e.ordem = :ordem WHERE e.id = :id")
            .setParameter("ordem", ordem)
            .setParameter("id", id)
            .executeUpdate();
    }

    // Busca etapas do processo já com os itens carregados (evita LazyInitializationException)
    public List<EtapaProcesso> findByProcessoIdWithItens(Integer processoId) {
        return em.createQuery(
                "SELECT DISTINCT e FROM EtapaProcesso e " +
                "LEFT JOIN FETCH e.itens i " +
                "LEFT JOIN FETCH i.itemDisponivel " +
                "WHERE e.processo.id = :processoId ORDER BY e.ordem ASC",
                EtapaProcesso.class)
                .setParameter("processoId", processoId)
                .getResultList();
    }

    @PersistenceContext
    private EntityManager em;

    public EtapaProcesso findById(Integer id) {
        return em.find(EtapaProcesso.class, id);
    }

    public List<EtapaProcesso> findAll() {
        return em.createQuery("SELECT e FROM EtapaProcesso e", EtapaProcesso.class).getResultList();
    }

    public List<EtapaProcesso> findByProcessoId(Integer processoId) {
        return em.createQuery("SELECT e FROM EtapaProcesso e WHERE e.processo.id = :processoId ORDER BY e.ordem ASC", EtapaProcesso.class)
                 .setParameter("processoId", processoId)
                 .getResultList();
    }

    @Transactional
    public void save(EtapaProcesso etapaProcesso) {
        if (etapaProcesso.getId() == null) {
            em.persist(etapaProcesso);
        } else {
            em.merge(etapaProcesso);
        }
    }

    @Transactional
    public void delete(EtapaProcesso etapaProcesso) {
        em.remove(em.contains(etapaProcesso) ? etapaProcesso : em.merge(etapaProcesso));
    }
}


