package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.Licenciamento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.time.LocalDate;
@Stateless
public class LicenciamentoRepository {

    @PersistenceContext
    private EntityManager em;

    public Licenciamento findById(Integer id) {
        return em.find(Licenciamento.class, id);
    }

    public List<Licenciamento> findAll() {
        return em.createQuery("SELECT l FROM Licenciamento l JOIN FETCH l.tipo JOIN FETCH l.cliente", Licenciamento.class).getResultList();
    }

    public List<Licenciamento> findAllWithProcessos() {
        return em.createQuery("SELECT DISTINCT l FROM Licenciamento l JOIN FETCH l.tipo JOIN FETCH l.cliente LEFT JOIN FETCH l.processos", Licenciamento.class).getResultList();
    }

    public List<Licenciamento> findProximosDoVencimento(int diasAviso) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasAviso);
        return em.createQuery("SELECT l FROM Licenciamento l WHERE l.dataVencimento <= :dataLimite AND l.ativo = TRUE", Licenciamento.class)
                 .setParameter("dataLimite", dataLimite)
                 .getResultList();
    }

    @Transactional
    public void save(Licenciamento licenciamento) {
        if (licenciamento.getId() == null) {
            em.persist(licenciamento);
        } else {
            em.merge(licenciamento);
        }
    }

    @Transactional
    public void delete(Licenciamento licenciamento) {
        em.remove(em.contains(licenciamento) ? licenciamento : em.merge(licenciamento));
    }
}


