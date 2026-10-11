package com.alixandremz.tarefas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alixandremz.tarefas.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByConcluidaOrderByCriadaEmDesc(boolean concluida);

    List<Tarefa> findAllByOrderByCriadaEmDesc();
}
