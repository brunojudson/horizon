package com.suaempresa.processos.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipo_processo_etapas_sugeridas")
@IdClass(TipoProcessoEtapasSugeridasId.class)
public class TipoProcessoEtapasSugeridas implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    @Id
    @ManyToOne
    @JoinColumn(name = "tipo_processo_id", nullable = false)
    private TipoProcesso tipoProcesso;

    @Id
    @ManyToOne
    @JoinColumn(name = "etapa_disponivel_id", nullable = false)
    private EtapaDisponivel etapaDisponivel;

    @Column(nullable = false)
    private Integer ordem;

    // Getters e Setters
    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
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
}


