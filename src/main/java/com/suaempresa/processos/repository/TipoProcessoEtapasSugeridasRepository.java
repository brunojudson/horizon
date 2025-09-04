package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.TipoProcessoEtapasSugeridas;
import com.suaempresa.processos.model.TipoProcessoEtapasSugeridasId;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class TipoProcessoEtapasSugeridasRepository {

    @PersistenceContext
    private EntityManager em;

    public TipoProcessoEtapasSugeridas findById(TipoProcessoEtapasSugeridasId id) {
        return em.find(TipoProcessoEtapasSugeridas.class, id);
    }

    public List<TipoProcessoEtapasSugeridas> findByTipoProcessoId(Integer tipoProcessoId) {
        return em.createQuery("SELECT tpes FROM TipoProcessoEtapasSugeridas tpes WHERE tpes.tipoProcesso.id = :tipoProcessoId ORDER BY tpes.ordem ASC", TipoProcessoEtapasSugeridas.class)
                 .setParameter("tipoProcessoId", tipoProcessoId)
                 .getResultList();
    }

    @Transactional
    public void save(TipoProcessoEtapasSugeridas entity) {
        if (em.contains(entity)) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
    }

    @Transactional
    public void delete(TipoProcessoEtapasSugeridas entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}


