package com.suaempresa.processos.service;


import java.util.ArrayList;
import com.suaempresa.processos.model.TipoProcesso;
import com.suaempresa.processos.model.EtapaDisponivel;
import com.suaempresa.processos.model.TipoProcessoEtapasSugeridas;
import com.suaempresa.processos.repository.TipoProcessoRepository;
import com.suaempresa.processos.repository.TipoProcessoEtapasSugeridasRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TipoProcessoService {

    @Inject
    private TipoProcessoRepository tipoProcessoRepository;
    @Inject
    private TipoProcessoEtapasSugeridasRepository tipoProcessoEtapasSugeridasRepository;

    // Retorna as etapas sugeridas para um tipo de processo
    public List<EtapaDisponivel> findEtapasSugeridas(Integer tipoProcessoId) {
        List<EtapaDisponivel> sugeridas = new ArrayList<>();
        List<TipoProcessoEtapasSugeridas> relacoes = tipoProcessoEtapasSugeridasRepository.findByTipoProcessoId(tipoProcessoId);
        for (TipoProcessoEtapasSugeridas rel : relacoes) {
            if (rel.getEtapaDisponivel() != null) {
                sugeridas.add(rel.getEtapaDisponivel());
            }
        }
        return sugeridas;
    }

    public List<TipoProcesso> findAll() {
        return tipoProcessoRepository.findAll();
    }

    public TipoProcesso findById(Integer id) {
        return tipoProcessoRepository.findById(id);
    }

    @Transactional
    public void save(TipoProcesso tipoProcesso) {
        tipoProcessoRepository.save(tipoProcesso);
    }

    @Transactional
    public void delete(TipoProcesso tipoProcesso) {
        tipoProcessoRepository.delete(tipoProcesso);
    }

    @Transactional
    public void addEtapaSugerida(TipoProcesso tipoProcesso, EtapaDisponivel etapaDisponivel, Integer ordem) {
        TipoProcessoEtapasSugeridas tpes = new TipoProcessoEtapasSugeridas();
        tpes.setTipoProcesso(tipoProcesso);
        tpes.setEtapaDisponivel(etapaDisponivel);
        tpes.setOrdem(ordem);
        tipoProcessoEtapasSugeridasRepository.save(tpes);
    }

    @Transactional
    public void removeEtapaSugerida(TipoProcessoEtapasSugeridas tpes) {
        tipoProcessoEtapasSugeridasRepository.delete(tpes);
    }

    public List<TipoProcessoEtapasSugeridas> findEtapasSugeridasByTipoProcesso(Integer tipoProcessoId) {
        return tipoProcessoEtapasSugeridasRepository.findByTipoProcessoId(tipoProcessoId);
    }
}


