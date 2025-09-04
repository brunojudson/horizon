package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.ItemEtapa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class ItemEtapaRepository {

    @PersistenceContext
    private EntityManager em;

    public ItemEtapa findById(Integer id) {
        return em.find(ItemEtapa.class, id);
    }

    public List<ItemEtapa> findAll() {
        return em.createQuery("SELECT i FROM ItemEtapa i", ItemEtapa.class).getResultList();
    }

    public List<ItemEtapa> findByEtapaProcessoId(Integer etapaProcessoId) {
        return em.createQuery("SELECT i FROM ItemEtapa i WHERE i.etapaProcesso.id = :etapaProcessoId", ItemEtapa.class)
                 .setParameter("etapaProcessoId", etapaProcessoId)
                 .getResultList();
    }

    @Transactional
    public void save(ItemEtapa itemEtapa) {
        if (itemEtapa.getId() == null) {
            em.persist(itemEtapa);
        } else {
            em.merge(itemEtapa);
        }
    }

    @Transactional
    public void delete(ItemEtapa itemEtapa) {
        em.remove(em.contains(itemEtapa) ? itemEtapa : em.merge(itemEtapa));
    }
}


