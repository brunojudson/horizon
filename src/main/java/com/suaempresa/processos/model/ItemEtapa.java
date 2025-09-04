package com.suaempresa.processos.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "itens_etapa")
public class ItemEtapa implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Column(length = 255)
    private String descricao;

    @Column(name = "data_criacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dataCriacao;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "etapa_processo_id", nullable = false)
    private EtapaProcesso etapaProcesso;

    @ManyToOne
    @JoinColumn(name = "item_disponivel_id", nullable = false)
    private ItemDisponivel itemDisponivel;

    @Column(name = "data_previsao_finalizacao")
    private LocalDate dataPrevisaoFinalizacao;

    @Column(name = "data_finalizacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dataFinalizacao;

    @Column(nullable = false)
    private Boolean concluido;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @PrePersist
    protected void onCreate() {
        if (concluido == null) {
            concluido = false;
        }
    }

    // Getters e Setters
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
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EtapaProcesso getEtapaProcesso() {
        return etapaProcesso;
    }

    public void setEtapaProcesso(EtapaProcesso etapaProcesso) {
        this.etapaProcesso = etapaProcesso;
    }

    public ItemDisponivel getItemDisponivel() {
        return itemDisponivel;
    }

    public void setItemDisponivel(ItemDisponivel itemDisponivel) {
        this.itemDisponivel = itemDisponivel;
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

    public Boolean getConcluido() {
        return concluido;
    }

    public void setConcluido(Boolean concluido) {
        this.concluido = concluido;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

}


