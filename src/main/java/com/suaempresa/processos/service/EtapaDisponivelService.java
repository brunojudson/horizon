
package com.suaempresa.processos.service;

import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.repository.EtapaDisponivelRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class EtapaDisponivelService {

    @Inject
    private EtapaDisponivelRepository etapaDisponivelRepository;

    public List<EtapaDisponivel> findAll() {
        return etapaDisponivelRepository.findAll();
    }

    public EtapaDisponivel findById(Integer id) {
        return etapaDisponivelRepository.findById(id);
    }

    @Transactional
    public void save(EtapaDisponivel etapaDisponivel) {
        etapaDisponivelRepository.save(etapaDisponivel);
    }

    @Transactional
    public void delete(EtapaDisponivel etapaDisponivel) {
        etapaDisponivelRepository.delete(etapaDisponivel);
    }
}


