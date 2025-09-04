package com.suaempresa.processos.service;

import com.suaempresa.processos.dto.LicenciamentoDTO;
import com.suaempresa.processos.model.Licenciamento;
import com.suaempresa.processos.repository.LicenciamentoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LicenciamentoService {

    @Inject
    private LicenciamentoRepository licenciamentoRepository;

    public List<LicenciamentoDTO> findProximosDoVencimento() {
        List<Licenciamento> licenciamentos = licenciamentoRepository.findAll(); // Buscar todos para verificar aviso prévio
        List<LicenciamentoDTO> dtos = new ArrayList<>();

        for (Licenciamento l : licenciamentos) {
            if (l.getAtivo() && l.getDataVencimento() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), l.getDataVencimento());
                String alerta = null;

                if (diasRestantes <= 0) {
                    alerta = "VENCIDO";
                } else if (l.getAvisoPrevioDias() != null && diasRestantes <= l.getAvisoPrevioDias()) {
                    alerta = "PRÓXIMO";
                }

                if (alerta != null) {
                    LicenciamentoDTO dto = new LicenciamentoDTO();
                    dto.setId(l.getId());
                    dto.setTipoLicenciamentoNome(l.getTipo().getNome());
                    dto.setClienteRazaoSocial(l.getCliente().getRazaoSocial());
                    dto.setDataEmissao(l.getDataEmissao());
                    dto.setDataVencimento(l.getDataVencimento());
                    dto.setTaxaValor(l.getTaxaValor());
                    dto.setAvisoPrevioDias(l.getAvisoPrevioDias());
                    dto.setAtivo(l.getAtivo());
                    dto.setAlertaVencimento(alerta);
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }

    public List<Licenciamento> findAll() {
        List<Licenciamento> licenciamentos = licenciamentoRepository.findAll();
        for (Licenciamento l : licenciamentos) {
            String alerta = null;
            if (l.getAtivo() && l.getDataVencimento() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), l.getDataVencimento());
                if (diasRestantes <= 0) {
                    alerta = "VENCIDO";
                } else if (l.getAvisoPrevioDias() != null && diasRestantes <= l.getAvisoPrevioDias()) {
                    alerta = "PRÓXIMO";
                }
            }
            l.setAlertaVencimento(alerta);
        }
        return licenciamentos;
    }

    public List<Licenciamento> findAllWithProcessos() {
        List<Licenciamento> licenciamentos = licenciamentoRepository.findAllWithProcessos();
        for (Licenciamento l : licenciamentos) {
            String alerta = null;
            if (l.getAtivo() && l.getDataVencimento() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), l.getDataVencimento());
                if (diasRestantes <= 0) {
                    alerta = "VENCIDO";
                } else if (l.getAvisoPrevioDias() != null && diasRestantes <= l.getAvisoPrevioDias()) {
                    alerta = "PRÓXIMO";
                }
            }
            l.setAlertaVencimento(alerta);
        }
        return licenciamentos;
    }

    public Licenciamento findById(Integer id) {
        return licenciamentoRepository.findById(id);
    }

    @Transactional
    public void save(Licenciamento licenciamento) {
        licenciamentoRepository.save(licenciamento);
    }

    @Transactional
    public void delete(Licenciamento licenciamento) {
        licenciamentoRepository.delete(licenciamento);
    }
}


