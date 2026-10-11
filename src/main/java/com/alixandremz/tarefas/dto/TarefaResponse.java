package com.alixandremz.tarefas.dto;

import java.time.Instant;

import com.alixandremz.tarefas.model.Prioridade;
import com.alixandremz.tarefas.model.Tarefa;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        Prioridade prioridade,
        boolean concluida,
        Instant criadaEm,
        Instant atualizadaEm
) {
    public static TarefaResponse de(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getPrioridade(),
                tarefa.isConcluida(),
                tarefa.getCriadaEm(),
                tarefa.getAtualizadaEm()
        );
    }
}
