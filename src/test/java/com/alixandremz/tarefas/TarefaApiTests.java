package com.alixandremz.tarefas;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.alixandremz.tarefas.repository.TarefaRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TarefaApiTests {

    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TarefaRepository repositorio;

    @BeforeEach
    void limpar() {
        repositorio.deleteAll();
    }

    private long criarTarefa() throws Exception {
        String resposta = mvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"estudar Spring\",\"descricao\":\"revisar JPA\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Matcher matcher = ID.matcher(resposta);
        matcher.find();
        return Long.parseLong(matcher.group(1));
    }

    @Test
    void criaComDefaultsEStatus201() throws Exception {
        mvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"comprar leite\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("comprar leite"))
                .andExpect(jsonPath("$.prioridade").value("MEDIA"))
                .andExpect(jsonPath("$.concluida").value(false));
    }

    @Test
    void listaTodasEFiltraPorConcluidas() throws Exception {
        criarTarefa();
        mvc.perform(get("/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/tarefas").param("concluida", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void buscaTarefaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/tarefas/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void tituloEmBrancoRetorna400ComCampo() throws Exception {
        mvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists());
    }

    @Test
    void atualizaTarefa() throws Exception {
        long id = criarTarefa();
        mvc.perform(put("/tarefas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"estudar muito\",\"prioridade\":\"ALTA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("estudar muito"))
                .andExpect(jsonPath("$.prioridade").value("ALTA"));
    }

    @Test
    void alternaConclusao() throws Exception {
        long id = criarTarefa();
        mvc.perform(patch("/tarefas/{id}/conclusao", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concluida").value(true));
    }

    @Test
    void removeTarefa() throws Exception {
        long id = criarTarefa();
        mvc.perform(delete("/tarefas/{id}", id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/tarefas/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizaTarefaInexistenteRetorna404() throws Exception {
        mvc.perform(put("/tarefas/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"qualquer\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    void prioridadeInvalidaRetorna400() throws Exception {
        mvc.perform(post("/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"x\",\"prioridade\":\"URGENTE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
    }
}
