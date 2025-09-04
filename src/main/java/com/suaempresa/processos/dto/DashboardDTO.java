package com.suaempresa.processos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DashboardDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String titulo;
    private String status;
    private String tempoAberto;
    private String tempoTotalFinalizacao;
    private LocalDate dataPrevisao;
    private BigDecimal valorTotal;
    private String alertaPrazo;
    private int porcentagemConclusao; // Para o ProgressBar
    private String clienteRazaoSocial;

    public String getClienteRazaoSocial() {
        return clienteRazaoSocial;
    }

    public void setClienteRazaoSocial(String clienteRazaoSocial) {
        this.clienteRazaoSocial = clienteRazaoSocial;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTempoAberto() {
        return tempoAberto;
    }

    public void setTempoAberto(String tempoAberto) {
        this.tempoAberto = tempoAberto;
    }

    public String getTempoTotalFinalizacao() {
        return tempoTotalFinalizacao;
    }

    public void setTempoTotalFinalizacao(String tempoTotalFinalizacao) {
        this.tempoTotalFinalizacao = tempoTotalFinalizacao;
    }

    public LocalDate getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(LocalDate dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getAlertaPrazo() {
        return alertaPrazo;
    }

    public void setAlertaPrazo(String alertaPrazo) {
        this.alertaPrazo = alertaPrazo;
    }

    public int getPorcentagemConclusao() {
        return porcentagemConclusao;
    }

    public void setPorcentagemConclusao(int porcentagemConclusao) {
        this.porcentagemConclusao = porcentagemConclusao;
    }
}
