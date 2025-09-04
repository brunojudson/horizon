package com.suaempresa.processos.enums;

// public enum StatusProcesso {
//     ABERTO,
//     PARADO,
//     EM_ANDAMENTO,
//     CONCLUIDO,
//     CANCELADO,
//     PENDENTE
// }


public enum StatusProcesso {
    ABERTO("Aberto"),
    PARADO("Parado"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado"),
    PENDENTE("Pendente");

    private final String label;

    StatusProcesso(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

