package com.suaempresa.processos.dto;

import com.suaempresa.processos.enums.StatusProcesso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RelatorioProcessoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer id;
    private String titulo;
    private String clienteRazaoSocial;
    private String responsavelNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFinalizacao;
    private Long tempoTotalEmDias;
    private StatusProcesso status;
    private String tipoProcessoNome;
    private BigDecimal taxaValorTotal;

    public RelatorioProcessoDTO(Integer id, String titulo, String clienteRazaoSocial, String responsavelNome, LocalDateTime dataCriacao, LocalDateTime dataFinalizacao, StatusProcesso status, String tipoProcessoNome, BigDecimal taxaValorTotal) {
        this.id = id;
        this.titulo = titulo;
        this.clienteRazaoSocial = clienteRazaoSocial;
        this.responsavelNome = responsavelNome;
        this.dataCriacao = dataCriacao;
        this.dataFinalizacao = dataFinalizacao;
        this.status = status;
        this.tipoProcessoNome = tipoProcessoNome;
        this.taxaValorTotal = taxaValorTotal;
        if (dataCriacao != null && dataFinalizacao != null) {
            this.tempoTotalEmDias = java.time.Duration.between(dataCriacao, dataFinalizacao).toDays();
        } else {
            this.tempoTotalEmDias = null;
        }
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getClienteRazaoSocial() {
        return clienteRazaoSocial;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public Long getTempoTotalEmDias() {
        return tempoTotalEmDias;
    }

    public StatusProcesso getStatus() {
        return status;
    }

    public String getTipoProcessoNome() {
        return tipoProcessoNome;
    }

    public BigDecimal getTaxaValorTotal() {
        return taxaValorTotal;
    }

    public java.util.Date getDataCriacaoAsDate() {
        return dataCriacao == null ? null : java.util.Date.from(dataCriacao.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    public java.util.Date getDataFinalizacaoAsDate() {
        return dataFinalizacao == null ? null : java.util.Date.from(dataFinalizacao.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}


