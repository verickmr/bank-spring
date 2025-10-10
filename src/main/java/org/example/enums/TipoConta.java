package org.example.enums;

public enum TipoConta {
    CORRENTE, POUPANCA;

    public static TipoConta fromString(String tipo) {
        try {
            return TipoConta.valueOf(tipo.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de conta inválido. Use 'corrente' ou 'poupanca'.");
        }
    }
}
