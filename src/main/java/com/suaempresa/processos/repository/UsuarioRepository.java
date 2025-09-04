package com.suaempresa.processos.repository;

import com.suaempresa.processos.model.Usuario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
@Stateless
public class UsuarioRepository {

    @PersistenceContext
    private EntityManager em;

    public Usuario findById(Integer id) {
        return em.find(Usuario.class, id);
    }

    public Usuario findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }

    @Transactional
    public void save(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
        } else {
            em.merge(usuario);
        }
    }

    @Transactional
    public void delete(Usuario usuario) {
        em.remove(em.contains(usuario) ? usuario : em.merge(usuario));
    }
}


