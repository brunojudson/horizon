package com.suaempresa.processos.model;

import java.io.Serializable;
import java.util.Objects;

public class TipoProcessoEtapasSugeridasId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Integer tipoProcesso;
    private Integer etapaDisponivel;

    public TipoProcessoEtapasSugeridasId() {
    }

    public TipoProcessoEtapasSugeridasId(Integer tipoProcesso, Integer etapaDisponivel) {
        this.tipoProcesso = tipoProcesso;
        this.etapaDisponivel = etapaDisponivel;
    }

    // Getters e Setters
    public Integer getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(Integer tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Integer getEtapaDisponivel() {
        return etapaDisponivel;
    }

    public void setEtapaDisponivel(Integer etapaDisponivel) {
        this.etapaDisponivel = etapaDisponivel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoProcessoEtapasSugeridasId that = (TipoProcessoEtapasSugeridasId) o;
        return Objects.equals(tipoProcesso, that.tipoProcesso) &&
               Objects.equals(etapaDisponivel, that.etapaDisponivel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoProcesso, etapaDisponivel);
    }
}


