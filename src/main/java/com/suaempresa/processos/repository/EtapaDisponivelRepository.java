package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.EtapaDisponivel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Stateless
public class EtapaDisponivelRepository {

    @PersistenceContext
    private EntityManager em;

    public EtapaDisponivel findById(Integer id) {
        return em.find(EtapaDisponivel.class, id);
    }

    public List<EtapaDisponivel> findAll() {
        return em.createQuery("SELECT e FROM EtapaDisponivel e", EtapaDisponivel.class).getResultList();
    }

    @Transactional
    public void save(EtapaDisponivel etapaDisponivel) {
        if (etapaDisponivel.getId() == null) {
            em.persist(etapaDisponivel);
        } else {
            em.merge(etapaDisponivel);
        }
    }

    @Transactional
    public void delete(EtapaDisponivel etapaDisponivel) {
        em.remove(em.contains(etapaDisponivel) ? etapaDisponivel : em.merge(etapaDisponivel));
    }
}


