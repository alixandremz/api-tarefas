# 📝 API de Tarefas

API REST simples de lista de tarefas, feita com **Java 21 + Spring Boot 4**.
Projeto de estudo: cobre CRUD completo, validação, tratamento de erros e
testes de integração.

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 (Spring Web MVC) |
| Persistência | Spring Data JPA + Hibernate |
| Banco | H2 em memória |
| Validação | Jakarta Bean Validation |
| Testes | JUnit 5 + MockMvc |

## 🚀 Como rodar

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. O console do H2 fica em
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tarefas`, usuário `sa`).

## ✅ Testes

```bash
./mvnw test
```

## 🔌 Endpoints

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| `GET` | `/tarefas` | lista todas; `?concluida=true\|false` filtra | `200` |
| `GET` | `/tarefas/{id}` | busca uma tarefa | `200`, `404` |
| `POST` | `/tarefas` | cria uma tarefa | `201`, `400` |
| `PUT` | `/tarefas/{id}` | atualiza uma tarefa | `200`, `400`, `404` |
| `PATCH` | `/tarefas/{id}/conclusao` | alterna concluída/não concluída | `200`, `404` |
| `DELETE` | `/tarefas/{id}` | remove uma tarefa | `204`, `404` |

### Corpo da requisição

```json
{
  "titulo": "estudar Spring Boot",
  "descricao": "revisar JPA e validação",
  "prioridade": "ALTA"
}
```

- `titulo` é obrigatório (máx. 120 caracteres).
- `descricao` é opcional (máx. 1000 caracteres).
- `prioridade` é opcional (`BAIXA`, `MEDIA` ou `ALTA`; padrão `MEDIA`).

### Exemplo

```bash
curl -X POST http://localhost:8080/tarefas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"comprar leite","prioridade":"ALTA"}'
```

```json
{
  "id": 1,
  "titulo": "comprar leite",
  "descricao": null,
  "prioridade": "ALTA",
  "concluida": false,
  "criadaEm": "2026-10-11T20:29:31.504906Z",
  "atualizadaEm": "2026-10-11T20:29:31.504906Z"
}
```

### Erros

Erros de validação retornam `400` com os campos inválidos:

```json
{ "erro": "dados inválidos", "campos": { "titulo": "o título é obrigatório" } }
```

Tarefa inexistente retorna `404`:

```json
{ "erro": "tarefa não encontrada: id 999", "campos": {} }
```

## 📂 Estrutura

```
src/main/java/com/alixandremz/tarefas/
├── ApiTarefasApplication.java
├── model/        # Tarefa (entidade), Prioridade (enum)
├── dto/          # TarefaRequest, TarefaResponse (records)
├── repository/   # TarefaRepository (Spring Data JPA)
├── service/      # TarefaService (regras) + exceção de não encontrada
└── web/          # TarefaController, ErroResposta, RestExceptionHandler
```

## 🗺️ Próximos passos (ideias)

- Trocar o H2 por PostgreSQL (via `spring.datasource.*` ou Docker Compose).
- Paginação e ordenação com `Pageable`.
- Autenticação com Spring Security + JWT.
- Documentação automática com springdoc-openapi (Swagger UI).
- Prazo/data de conclusão e ordenação por prioridade.
