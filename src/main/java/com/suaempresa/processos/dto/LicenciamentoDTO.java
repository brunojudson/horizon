package com.suaempresa.processos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class LicenciamentoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
    private String tipoLicenciamentoNome;
    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private BigDecimal taxaValor;
    private Integer avisoPrevioDias;
    private Boolean ativo;
    private String alertaVencimento;  
    private String clienteRazaoSocial;

  
    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoLicenciamentoNome() {
        return tipoLicenciamentoNome;
    }

    public void setTipoLicenciamentoNome(String tipoLicenciamentoNome) {
        this.tipoLicenciamentoNome = tipoLicenciamentoNome;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getTaxaValor() {
        return taxaValor;
    }

    public void setTaxaValor(BigDecimal taxaValor) {
        this.taxaValor = taxaValor;
    }

    public Integer getAvisoPrevioDias() {
        return avisoPrevioDias;
    }

    public void setAvisoPrevioDias(Integer avisoPrevioDias) {
        this.avisoPrevioDias = avisoPrevioDias;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getAlertaVencimento() {
        return alertaVencimento;
    }

    public void setAlertaVencimento(String alertaVencimento) {
        this.alertaVencimento = alertaVencimento;
    }
    public String getClienteRazaoSocial() {
        return clienteRazaoSocial;
    }

    public void setClienteRazaoSocial(String clienteRazaoSocial) {
        this.clienteRazaoSocial = clienteRazaoSocial;
    }

}


