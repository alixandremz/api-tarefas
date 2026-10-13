<p align="center">
  <img alt="java" src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img alt="spring boot" src="https://img.shields.io/badge/Spring%20Boot-4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img alt="tests" src="https://img.shields.io/badge/tests-10%20passing-27AE60?style=for-the-badge&logo=checkmarx&logoColor=white">
  <img alt="license" src="https://img.shields.io/badge/license-MIT-blue?style=for-the-badge">
  <img alt="docker" src="https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white">
</p>

# 📝 API de Tarefas

API REST de lista de tarefas feita com **Java 21 + Spring Boot 4**. É um
projeto de estudo: parto do zero, explico cada decisão e escrevo testes para
cada comportamento. A ideia é aprender construindo algo real, pequeno e
completo — em vez de só seguir tutoriais.

> **Em uma frase:** você cria, lista, atualiza, conclui e remove tarefas via
> HTTP, e o projeto demonstra na prática as camadas de uma API Spring.

---

## 🎯 Por que este projeto existe

Meu objetivo com este repositório é **praticar e consolidar** os fundamentos
de uma API REST profissional em Java. Por isso ele é deliberadamente pequeno
(cabe na cabeça), mas cuidado nos detalhes: validação, códigos HTTP corretos,
tratamento de erro e testes de integração.

Cada arquivo tem um papel claro, e o README explica o porquê de cada um.

## 🧠 O que eu pratiquei aqui

- **Spring Boot** — subir uma aplicação web sem configurar servidor na mão.
- **Spring Web (MVC)** — controllers REST, mapeamento de rotas, `@RequestBody`,
  `@PathVariable`, `@RequestParam` e `ResponseEntity`.
- **Injeção de dependência** — o Spring cria e injeta os objetos que cada
  classe precisa (construtor injection).
- **Spring Data JPA / Hibernate** — mapear uma classe Java para tabela e
  ganhar operações de banco de graça (`JpaRepository`).
- **Bean Validation** — regras no DTO (`@NotBlank`, `@Size`) com mensagens.
- **DTOs com `record`** — separar a API da entidade do banco.
- **Tratamento de erros** — respostas 400/404 consistentes com
  `@RestControllerAdvice`.
- **Testes de integração** — MockMvc simulando requisições reais.
- **Empacotamento** — Maven Wrapper e `Dockerfile` multi-stage.

---

## 🏗️ Arquitetura

A aplicação segue a divisão clássica em camadas. Cada camada só conversa com a
vizinha:

```
        HTTP                         objetos Java                  SQL
┌──────────┐   JSON   ┌────────────┐  DTOs  ┌─────────┐  Entity ┌────────────┐
│  Cliente │ ───────▶ │ Controller │ ─────▶ │ Service │ ──────▶ │ Repository │
│ (curl,   │ ◀─────── │  (web)     │ ◀───── │(regras) │ ◀────── │  (JPA)     │
│ Insomnia)│  JSON    └────────────┘  DTOs  └─────────┘  Entity └─────┬──────┘
└──────────┘                                                          │ JDBC
                                                                 ┌────▼─────┐
                                                                 │   H2     │
                                                                 │(memória) │
                                                                 └──────────┘
```

| Camada | Responsabilidade | Não faz |
|---|---|---|
| **Controller** | recebe HTTP, valida entrada, devolve HTTP | regra de negócio |
| **Service** | regras, transações, orquestração | conhece HTTP |
| **Repository** | acesso ao banco | regra de negócio |
| **Model/Entity** | representação da tabela | sabe de HTTP |
| **DTO** | contrato de entrada/saída da API | é persistido |

### O caminho de uma requisição

Exemplo: `POST /tarefas` com `{"titulo":"comprar leite"}`.

1. O **Tomcat** embutido recebe a conexão e o Spring roteia para o método certo
   do `TarefaController` (`criar`).
2. O JSON é convertido para um `TarefaRequest` e validado (`@Valid`). Se o
   título vier vazio, o Spring lança uma exceção antes de entrar no método.
3. O controller chama `TarefaService.criar(pedido)`.
4. O service monta uma entidade `Tarefa`, aplica defaults e chama
   `TarefaRepository.save(...)`.
5. O Hibernate traduz isso para um `INSERT` no H2 e preenche o `id`.
6. A entidade volta, o service converte para `TarefaResponse` e o controller
   responde **201 Created** com o `Location: /tarefas/{id}`.

---

## 📂 Estrutura do projeto

```
api-tarefas/
├── mvnw, mvnw.cmd, .mvn/            # Maven Wrapper (não precisa instalar Maven)
├── pom.xml                          # dependências e build
├── Dockerfile                       # imagem multi-stage
├── .dockerignore
├── LICENSE                          # MIT
└── src/
    ├── main/
    │   ├── java/com/alixandremz/tarefas/
    │   │   ├── ApiTarefasApplication.java   # ponto de entrada
    │   │   ├── model/
    │   │   │   ├── Tarefa.java               # entidade (tabela "tarefas")
    │   │   │   └── Prioridade.java           # enum BAIXA/MEDIA/ALTA
    │   │   ├── dto/
    │   │   │   ├── TarefaRequest.java        # o que a API recebe
    │   │   │   └── TarefaResponse.java       # o que a API devolve
    │   │   ├── repository/
    │   │   │   └── TarefaRepository.java     # acesso a dados (JpaRepository)
    │   │   ├── service/
    │   │   │   ├── TarefaService.java        # regras de negócio
    │   │   │   └── TarefaNaoEncontradaException.java
    │   │   └── web/
    │   │       ├── TarefaController.java     # rotas HTTP
    │   │       ├── ErroResposta.java         # formato padrão de erro
    │   │       └── RestExceptionHandler.java # traduz exceções para HTTP
    │   └── resources/
    │       └── application.properties        # configuração (banco, console)
    └── test/java/com/alixandremz/tarefas/
        ├── ApiTarefasApplicationTests.java   # o contexto sobe?
        └── TarefaApiTests.java               # testes de integração da API
```

### Por que cada peça

- **`model/Tarefa`** — mapeada com `@Entity`. O `@PrePersist`/`@PreUpdate`
  preenche as datas de criação/atualização automaticamente.
- **`dto/TarefaRequest` e `TarefaResponse`** — são `record`, imutáveis e
  enxutos. Protegem a entidade: o cliente nunca vê campos internos nem pode
  setar `id`/datas. Aqui ficam as validações de entrada.
- **`repository/TarefaRepository`** — só interfaces! O Spring Data gera a
  implementação a partir de nomes de método (`findByConcluidaOrderBy...`).
- **`service/TarefaService`** — o único lugar com regra de negócio. Se a busca
  não acha a tarefa, lança `TarefaNaoEncontradaException` (domínio fala "não
  encontrei", não "404" — quem traduz para HTTP é o handler).
- **`web/RestExceptionHandler`** — `@RestControllerAdvice` centraliza a
  tradução de exceções para respostas HTTP no mesmo formato (`ErroResposta`).
- **`web/ErroResposta`** — o "contrato" de erro: `erro` + mapa de `campos`.

---

## 🔌 Endpoints

Base: `http://localhost:8080`

| Método | Rota | Descrição | Status |
|---|---|---|---|
| `GET` | `/tarefas` | lista todas; `?concluida=true\|false` filtra | `200` |
| `GET` | `/tarefas/{id}` | busca uma tarefa | `200`, `404` |
| `POST` | `/tarefas` | cria uma tarefa | `201`, `400` |
| `PUT` | `/tarefas/{id}` | atualiza uma tarefa | `200`, `400`, `404` |
| `PATCH` | `/tarefas/{id}/conclusao` | alterna concluída/não concluída | `200`, `404` |
| `DELETE` | `/tarefas/{id}` | remove uma tarefa | `204`, `404` |

### Campos da tarefa

| Campo | Obrigatório | Regra |
|---|---|---|
| `titulo` | ✅ | não em branco, até 120 caracteres |
| `descricao` | ❌ | até 1000 caracteres |
| `prioridade` | ❌ | `BAIXA`, `MEDIA` (padrão) ou `ALTA` |

### Exemplos

**Criar**

```bash
curl -X POST http://localhost:8080/tarefas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"estudar Spring Boot","descricao":"revisar JPA","prioridade":"ALTA"}'
```

```json
{
  "id": 1,
  "titulo": "estudar Spring Boot",
  "descricao": "revisar JPA",
  "prioridade": "ALTA",
  "concluida": false,
  "criadaEm": "2026-10-11T20:29:31.504906Z",
  "atualizadaEm": "2026-10-11T20:29:31.504906Z"
}
```

**Listar** (opcionalmente filtrando)

```bash
curl http://localhost:8080/tarefas
curl "http://localhost:8080/tarefas?concluida=false"
```

**Concluir / desconcluir**

```bash
curl -X PATCH http://localhost:8080/tarefas/1/conclusao
```

**Atualizar**

```bash
curl -X PUT http://localhost:8080/tarefas/1 \
  -H "Content-Type: application/json" \
  -d '{"titulo":"estudar mais","prioridade":"MEDIA"}'
```

**Remover**

```bash
curl -X DELETE http://localhost:8080/tarefas/1
```

### Erros

Formato único para qualquer erro:

```json
{ "erro": "mensagem", "campos": { } }
```

| Situação | Status | Exemplo de corpo |
|---|---|---|
| Título em branco | `400` | `{"erro":"dados inválidos","campos":{"titulo":"o título é obrigatório"}}` |
| Prioridade inválida | `400` | `{"erro":"corpo da requisição inválido ou valor não aceito"}` |
| Tarefa não existe | `404` | `{"erro":"tarefa não encontrada: id 999","campos":{}}` |

---

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 (Spring Web MVC) |
| Persistência | Spring Data JPA + Hibernate |
| Banco | H2 em memória |
| Validação | Jakarta Bean Validation |
| Testes | JUnit 5 + MockMvc |
| Build | Maven (via Wrapper) |

---

## 🚀 Como rodar

Pré-requisito: **JDK 21** (não precisa ter Maven instalado — o Wrapper baixa).

### Local

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Console do H2 em
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tarefas`, usuário
`sa`, senha vazia).

### Empacotar e rodar o jar

```bash
./mvnw clean package
java -jar target/api-tarefas-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
docker build -t api-tarefas .
docker run --rm -p 8080:8080 api-tarefas
```

---

## ✅ Testes

```bash
./mvnw test
```

**10 testes** que cobrem: criação com defaults e `201`, listagem e filtro,
busca inexistente (`404`), validação de título (`400`), atualização, toggle de
conclusão, remoção (`204`), atualização inexistente (`404`) e prioridade
inválida (`400`). Os testes usam **MockMvc**, que exercita o controller, o
service e o banco de verdade — só não abrem porta de rede.

---

## 🧭 Decisões de projeto

- **DTO, não entidade, na API.** O cliente não deve ditar `id` nem datas, e a
  entidade poder mudar sem quebrar o contrato.
- **Códigos HTTP corretos.** `201` + `Location` ao criar, `204` ao remover,
  `404` quando não existe, `400` para entrada inválida.
- **Erros padronizados.** Um só formato (`erro`/`campos`) para todos os erros,
  centralizado no `RestExceptionHandler`.
- **H2 em memória.** Zero configuração para rodar e testar; trocar por
  PostgreSQL é só mudar `application.properties`.
- **`record` para DTOs.** Menos código, imutáveis por padrão.
- **Maven Wrapper.** Quem clonar roda sem instalar Maven.

---

## 🗺️ Próximos estudos

- [ ] Paginação e ordenação com `Pageable`.
- [ ] Autenticação e autorização com Spring Security + JWT.
- [ ] Documentação automática com springdoc-openapi (Swagger UI).
- [ ] Trocar H2 por PostgreSQL (perfil de configuração / Docker Compose).
- [ ] Data de prazo e ordenação por prioridade.
- [ ] Migrations com Flyway.
- [ ] Docker Compose subindo API + banco.
- [ ] CI no GitHub Actions rodando os testes.

---

## 📜 Licença

Distribuído sob a licença **MIT** — veja [`LICENSE`](LICENSE).
