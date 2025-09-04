
package com.suaempresa.processos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.suaempresa.processos.enums.StatusProcesso;
@Entity
@Table(name = "processos")
public class Processo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "data_criacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dataCriacao;

    @Column(name = "data_previsao_finalizacao")
    private LocalDate dataPrevisaoFinalizacao;

    @Column(name = "data_finalizacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dataFinalizacao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @ManyToOne
    @JoinColumn(name = "tipo_processo_id", nullable = false)
    private TipoProcesso tipoProcesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusProcesso status;

    @Column(name = "taxa_valor_total", precision = 12, scale = 2)
    private BigDecimal taxaValorTotal;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<EtapaProcesso> etapas;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
	public Cliente cliente;


    public Processo() {
        this.etapas = new java.util.ArrayList<>();
    }
    
    @ManyToOne
    @JoinColumn(name = "licenciamento_associado_id")
    private Licenciamento licenciamentoAssociado;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusProcesso.ABERTO;
        }
    }


    public java.util.List<EtapaProcesso> getEtapas() {
        if (etapas == null) return Collections.emptyList();
        etapas.sort(java.util.Comparator.comparing(EtapaProcesso::getOrdem, java.util.Comparator.nullsLast(Integer::compareTo)));
        return etapas;
    }

    public void setEtapas(java.util.List<EtapaProcesso> etapas) {
        this.etapas = etapas;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataPrevisaoFinalizacao() {
        return dataPrevisaoFinalizacao;
    }

    public void setDataPrevisaoFinalizacao(LocalDate dataPrevisaoFinalizacao) {
        this.dataPrevisaoFinalizacao = dataPrevisaoFinalizacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public StatusProcesso getStatus() {
        return status;
    }

    public void setStatus(StatusProcesso status) {
        this.status = status;
    }

    public BigDecimal getTaxaValorTotal() {
        return taxaValorTotal;
    }

    public void setTaxaValorTotal(BigDecimal taxaValorTotal) {
        this.taxaValorTotal = taxaValorTotal;
    }

    public Licenciamento getLicenciamentoAssociado() {
        return licenciamentoAssociado;
    }

    public void setLicenciamentoAssociado(Licenciamento licenciamentoAssociado) {
        this.licenciamentoAssociado = licenciamentoAssociado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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
        Processo other = (Processo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}


