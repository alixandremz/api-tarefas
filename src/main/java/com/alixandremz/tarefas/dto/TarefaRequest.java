package com.alixandremz.tarefas.dto;

import com.alixandremz.tarefas.model.Prioridade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TarefaRequest(
        @NotBlank(message = "o título é obrigatório")
        @Size(max = 120, message = "o título deve ter no máximo 120 caracteres")
        String titulo,

        @Size(max = 1000, message = "a descrição deve ter no máximo 1000 caracteres")
        String descricao,

        Prioridade prioridade
) {
}
