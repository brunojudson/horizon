package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.ItemDisponivel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class ItemDisponivelRepository {

    @PersistenceContext
    private EntityManager em;

    public ItemDisponivel findById(Integer id) {
        return em.find(ItemDisponivel.class, id);
    }

    public List<ItemDisponivel> findAll() {
        return em.createQuery("SELECT i FROM ItemDisponivel i", ItemDisponivel.class).getResultList();
    }

    @Transactional
    public void save(ItemDisponivel itemDisponivel) {
        if (itemDisponivel.getId() == null) {
            em.persist(itemDisponivel);
        } else {
            em.merge(itemDisponivel);
        }
    }

    @Transactional
    public void delete(ItemDisponivel itemDisponivel) {
        em.remove(em.contains(itemDisponivel) ? itemDisponivel : em.merge(itemDisponivel));
    }
}


