package com.alixandremz.tarefas.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alixandremz.tarefas.dto.TarefaRequest;
import com.alixandremz.tarefas.dto.TarefaResponse;
import com.alixandremz.tarefas.service.TarefaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService servico;

    public TarefaController(TarefaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<TarefaResponse> listar(@RequestParam(required = false) Boolean concluida) {
        return servico.listar(concluida);
    }

    @GetMapping("/{id}")
    public TarefaResponse buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TarefaResponse> criar(@Valid @RequestBody TarefaRequest pedido) {
        TarefaResponse criada = servico.criar(pedido);
        return ResponseEntity.created(URI.create("/tarefas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public TarefaResponse atualizar(@PathVariable Long id, @Valid @RequestBody TarefaRequest pedido) {
        return servico.atualizar(id, pedido);
    }

    @PatchMapping("/{id}/conclusao")
    public TarefaResponse alternarConclusao(@PathVariable Long id) {
        return servico.alternarConclusao(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        servico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
