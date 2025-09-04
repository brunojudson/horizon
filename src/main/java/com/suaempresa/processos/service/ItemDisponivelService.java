package com.suaempresa.processos.service;

import com.suaempresa.processos.model.ItemDisponivel;
import com.suaempresa.processos.repository.ItemDisponivelRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ItemDisponivelService {

    @Inject
    private ItemDisponivelRepository itemDisponivelRepository;

    public List<ItemDisponivel> findAll() {
        return itemDisponivelRepository.findAll();
    }

    public ItemDisponivel findById(Integer id) {
        return itemDisponivelRepository.findById(id);
    }

    @Transactional
    public void save(ItemDisponivel itemDisponivel) {
        itemDisponivelRepository.save(itemDisponivel);
    }

    @Transactional
    public void delete(ItemDisponivel itemDisponivel) {
        itemDisponivelRepository.delete(itemDisponivel);
    }
}


