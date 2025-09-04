package com.suaempresa.processos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "licenciamentos")
public class Licenciamento implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoLicenciamento tipo;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "taxa_valor", precision = 10, scale = 2)
    private BigDecimal taxaValor;

    @Column(name = "aviso_previo_dias")
    private Integer avisoPrevioDias;

    @Column(name = "ativo")
    private Boolean ativo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Transient
    private String alertaVencimento;

    @OneToMany(mappedBy = "licenciamentoAssociado", fetch = FetchType.LAZY)
    private List<Processo> processos = new ArrayList<>();

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoLicenciamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLicenciamento tipo) {
        this.tipo = tipo;
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

    // --- Cálculo de vencimento e aviso ---
    public Integer getDiasParaVencimento() {
        if (dataVencimento == null)
            return null;
        return java.time.Period.between(java.time.LocalDate.now(), dataVencimento).getDays();
    }

    public boolean isVencido() {
        Integer dias = getDiasParaVencimento();
        return dias != null && dias < 0;
    }

    public boolean isEmAviso() {
        Integer dias = getDiasParaVencimento();
        return dias != null && avisoPrevioDias != null && dias >= 0 && dias <= avisoPrevioDias;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Licenciamento other = (Licenciamento) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Licenciamento [id=" + id + ", tipo=" + (tipo != null ? tipo.getNome() : "null") 
               + ", cliente=" + (cliente != null ? cliente.getRazaoSocial() : "null") + "]";
    }

}
