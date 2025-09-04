package com.suaempresa.processos.service;

import com.suaempresa.processos.model.TipoLicenciamento;
import com.suaempresa.processos.repository.TipoLicenciamentoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TipoLicenciamentoService {

    @Inject
    private TipoLicenciamentoRepository tipoLicenciamentoRepository;

    public List<TipoLicenciamento> findAll() {
        return tipoLicenciamentoRepository.findAll();
    }

    public TipoLicenciamento findById(Integer id) {
        return tipoLicenciamentoRepository.findById(id);
    }

    @Transactional
    public void save(TipoLicenciamento tipoLicenciamento) {
        tipoLicenciamentoRepository.save(tipoLicenciamento);
    }

    @Transactional
    public void delete(TipoLicenciamento tipoLicenciamento) {
        tipoLicenciamentoRepository.delete(tipoLicenciamento);
    }
}
