package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.TipoLicenciamento;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TipoLicenciamentoRepository {

    @PersistenceContext
    private EntityManager em;

    public TipoLicenciamento findById(Integer id) {
        return em.find(TipoLicenciamento.class, id);
    }

    public List<TipoLicenciamento> findAll() {
        return em.createQuery("SELECT t FROM TipoLicenciamento t", TipoLicenciamento.class).getResultList();
    }

    @Transactional
    public void save(TipoLicenciamento tipoLicenciamento) {
        if (tipoLicenciamento.getId() == null) {
            em.persist(tipoLicenciamento);
        } else {
            em.merge(tipoLicenciamento);
        }
    }

    @Transactional
    public void delete(TipoLicenciamento tipoLicenciamento) {
        em.remove(em.contains(tipoLicenciamento) ? tipoLicenciamento : em.merge(tipoLicenciamento));
    }
}


