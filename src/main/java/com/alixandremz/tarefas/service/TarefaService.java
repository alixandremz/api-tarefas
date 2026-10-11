package com.alixandremz.tarefas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alixandremz.tarefas.dto.TarefaRequest;
import com.alixandremz.tarefas.dto.TarefaResponse;
import com.alixandremz.tarefas.model.Prioridade;
import com.alixandremz.tarefas.model.Tarefa;
import com.alixandremz.tarefas.repository.TarefaRepository;

@Service
public class TarefaService {

    private final TarefaRepository repositorio;

    public TarefaService(TarefaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<TarefaResponse> listar(Boolean concluida) {
        List<Tarefa> tarefas = concluida == null
                ? repositorio.findAllByOrderByCriadaEmDesc()
                : repositorio.findByConcluidaOrderByCriadaEmDesc(concluida);
        return tarefas.stream().map(TarefaResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscar(Long id) {
        return TarefaResponse.de(obter(id));
    }

    @Transactional
    public TarefaResponse criar(TarefaRequest pedido) {
        Tarefa tarefa = new Tarefa();
        aplicar(tarefa, pedido);
        return TarefaResponse.de(repositorio.save(tarefa));
    }

    @Transactional
    public TarefaResponse atualizar(Long id, TarefaRequest pedido) {
        Tarefa tarefa = obter(id);
        aplicar(tarefa, pedido);
        return TarefaResponse.de(repositorio.save(tarefa));
    }

    @Transactional
    public TarefaResponse alternarConclusao(Long id) {
        Tarefa tarefa = obter(id);
        tarefa.setConcluida(!tarefa.isConcluida());
        return TarefaResponse.de(repositorio.save(tarefa));
    }

    @Transactional
    public void remover(Long id) {
        repositorio.delete(obter(id));
    }

    private Tarefa obter(Long id) {
        return repositorio.findById(id).orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    private void aplicar(Tarefa tarefa, TarefaRequest pedido) {
        tarefa.setTitulo(pedido.titulo().trim());
        tarefa.setDescricao(pedido.descricao());
        if (pedido.prioridade() != null) {
            tarefa.setPrioridade(pedido.prioridade());
        }
    }
}
