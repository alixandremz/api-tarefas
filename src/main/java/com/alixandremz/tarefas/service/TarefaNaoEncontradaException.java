package com.alixandremz.tarefas.service;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException(Long id) {
        super("tarefa não encontrada: id " + id);
    }
}
