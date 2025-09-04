package com.suaempresa.processos.model;

import com.suaempresa.processos.enums.StatusProcesso;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "etapas_processo")
public class EtapaProcesso implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @ManyToOne
    @JoinColumn(name = "etapa_disponivel_id", nullable = false)
    private EtapaDisponivel etapaDisponivel;

    @Column(nullable = false)
    private Integer ordem;

    @Column(name = "data_previsao_finalizacao")
    private LocalDate dataPrevisaoFinalizacao;

    @Column(name = "data_finalizacao", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime dataFinalizacao;

    @Convert(converter = com.suaempresa.processos.converter.StatusProcessoConverter.class)
    @Column(name = "status", nullable = false)
    private StatusProcesso status;

    /*Data do cadastro na verdade é a data de quando a atividade foi colocada em andamento
     * pois como já estava tudo implementado não quis mexer para não criar problema.
     */
    @Column(name = "data_cadastro", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "etapaProcesso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<ItemEtapa> itens = new java.util.ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = StatusProcesso.ABERTO;
        }
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }

    // Getters e Setters
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public java.util.List<ItemEtapa> getItens() {
    	return itens;
    }
    
    public void setItens(java.util.List<ItemEtapa> itens) {
    	this.itens = itens;
    }
    public EtapaDisponivel getEtapaDisponivel() {
        return etapaDisponivel;
    }

    public void setEtapaDisponivel(EtapaDisponivel etapaDisponivel) {
        this.etapaDisponivel = etapaDisponivel;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
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

    public StatusProcesso getStatus() {
        return status;
    }

    public void setStatus(StatusProcesso status) {
        this.status = status;
    }
    @Override
    public boolean equals(Object o) {
    	if (this == o) return true;
    	if (o == null || getClass() != o.getClass()) return false;
    	EtapaProcesso that = (EtapaProcesso) o;
    	return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
    	return id != null ? id.hashCode() : 0;
    }
}


