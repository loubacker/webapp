package com.pushbait.webapp.payload;

import java.util.Optional;

public record PixRequestPayload(
    String chave,           // Chave Pix para cobrança
    String valor,           // Valor da cobrança
    String nome,            // Nome do comprador
    String telefone,        // Telefone do comprador
    Optional<String> cpf    // CPF do comprador (opcional)
) {
    // Construtor para casos onde o CPF não é necessário
    public PixRequestPayload(String chave, String valor, String nome, String telefone) {
        this(chave, valor, nome, telefone, Optional.empty());
    }
}
