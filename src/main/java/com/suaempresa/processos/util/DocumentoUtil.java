package com.suaempresa.processos.util;

import com.suaempresa.processos.enums.TipoPessoa;

public class DocumentoUtil {
    public static boolean isDocumentoValido(String documento, TipoPessoa tipoPessoa) {
        if (documento == null || tipoPessoa == null) return false;
        if (tipoPessoa.name().equals("FISICA")) {
            return isCPFValido(documento);
        } else {
            return isCNPJValido(documento);
        }
    }

    public static boolean isCPFValido(String cpf) {
        // Implementação simplificada para exemplo
        return cpf != null && cpf.replaceAll("\\D", "").length() == 11;
    }

    public static boolean isCNPJValido(String cnpj) {
        // Implementação simplificada para exemplo
        return cnpj != null && cnpj.replaceAll("\\D", "").length() == 14;
    }
}
