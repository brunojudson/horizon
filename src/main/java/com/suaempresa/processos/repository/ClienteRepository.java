package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.Cliente;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ClienteRepository {
    @PersistenceContext
    private EntityManager em;

    public Cliente findById(Integer id) {
        return em.find(Cliente.class, id);
    }

    public List<Cliente> findAll() {
        return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
    }

    public void save(Cliente cliente) {
        if (cliente.getId() == null) {
            em.persist(cliente);
        } else {
            em.merge(cliente);
        }
    }

    public int countByAtivoTrue() {
        return em.createQuery("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true", Long.class)
                 .getSingleResult().intValue();
    }
    public void delete(Cliente cliente) {
        Cliente c = em.find(Cliente.class, cliente.getId());
        if (c != null) {
            em.remove(c);
        }
    }
}
