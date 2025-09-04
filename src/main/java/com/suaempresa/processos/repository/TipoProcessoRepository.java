package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.TipoProcesso;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class TipoProcessoRepository {

    @PersistenceContext
    private EntityManager em;

    public TipoProcesso findById(Integer id) {
        return em.find(TipoProcesso.class, id);
    }

    public List<TipoProcesso> findAll() {
        return em.createQuery("SELECT t FROM TipoProcesso t", TipoProcesso.class).getResultList();
    }

    @Transactional
    public void save(TipoProcesso tipoProcesso) {
        if (tipoProcesso.getId() == null) {
            em.persist(tipoProcesso);
        } else {
            em.merge(tipoProcesso);
        }
    }

    @Transactional
    public void delete(TipoProcesso tipoProcesso) {
        em.remove(em.contains(tipoProcesso) ? tipoProcesso : em.merge(tipoProcesso));
    }
}


