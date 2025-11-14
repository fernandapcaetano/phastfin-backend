Aqui está um **README.md completo, profissional, bem estruturado e totalmente alinhado aos controllers e às classes de domínio que você enviou**, incluindo tecnologias utilizadas (pgvector, RAG, embeddings, AI, Docker, etc).

Você pode copiar e colar diretamente no seu repositório.

---

# 📄 **Phastfin - AI Statement Extractor - MVP**

## 🧠 Visão Geral

Este projeto é uma **API para extração, interpretação e consulta de transações financeiras** utilizando **IA, RAG (Retrieval-Augmented Generation), embeddings vetoriais e processamento automatizado de extratos bancários**.

A aplicação permite que usuários:

* Enviem consultas textuais sobre transações já processadas
* Façam upload de extratos bancários (PDF ou texto) para extração automática
* Recuperem extratos, insights e transações
* Utilizem filtros avançados e paginação
* Aproveitem busca semântica baseada em *embeddings* via **pgvector**

O sistema foi projetado para ser **escalável, seguro, modular e orientado ao domínio (DDD)**.

---

# 🏗️ **Arquitetura da Aplicação**

A aplicação segue uma arquitetura moderna baseada em:

* **Spring Boot**
* **Domain-Driven Design (DDD)**
* * **Monolito modular**
* **AI Services para processamento de arquivos**
* **RAG (Retrieval-Augmented Generation)** para consultas inteligentes
* **Armazenamento vetorial com pgvector**
* **Banco PostgreSQL**
* **Containerização com Docker**

### Principais módulos:

* **Ingestão de Extratos** → Upload, leitura por IA e extração de transações
* **Consulta Inteligente** → Usuário envia texto livre e a IA responde baseado nos dados armazenados
* **Busca e Filtros** → Endpoints para filtrar extratos e transações
* **Persistência** → Entidades de domínio versionadas e rastreáveis
* **Embeddings** → Conteúdo vetorizado de extratos para busca semântica

---

# 🚀 **Tecnologias Utilizadas**

| Tecnologia                               | Uso                                                  |
| ---------------------------------------- | ---------------------------------------------------- |
| **Spring Boot 3**                        | Backend e APIs REST                                  |
| **Hibernate / JPA**                      | Mapeamento e persistência                            |
| **PostgreSQL**                           | Banco relacional                                     |
| **pgvector**                             | Armazenamento de embeddings e busca vetorial         |
| **AI Embeddings**                        | Vetorização de conteúdo de extratos                  |
| **RAG - Retrieval-Augmented Generation** | Respostas inteligentes usando dados reais do usuário |
| **OpenAI / LLM**                         | Interpretação dos extratos e consultas               |
| **Docker & Docker Compose**              | Infraestrutura e containerização                     |
| **Java 17+**                             | Linguagem base                                       |
| **Swagger / OpenAPI**                    | Documentação automática da API                       |

---

# 📡 **Documentação dos Endpoints**

A API exposta é totalmente documentada com **@Operation (OpenAPI)**.
Aqui está um resumo dos principais endpoints.

---

## 🔍 **1. Consulta de transações via linguagem natural**

`POST /query`

Permite que o usuário faça uma pergunta como:

> “Quanto eu gastei com transporte no último mês?”

O serviço utiliza **RAG + Embeddings** para interpretar e responder.

```java
@PostMapping()
public ResponseEntity<Result<String>> execute(@RequestBody String query)
```

---

## 📤 **2. Upload de Extrato Bancário (PDF ou Texto)**

`POST /statement`

Processa o arquivo com IA e extrai:

* Metadados do extrato
* Intervalo de datas
* Transações (crédito, débito, categoria, descrição etc.)
* Gera embedding do conteúdo

```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Result<StatementResponse>> execute(@RequestPart("file") MultipartFile file)
```

---

## 📄 **3. Buscar Extrato por ID Externo**

`GET /statement/{statementExternalId}`

Retorna o extrato processado, incluindo metadados e transações.

```java
@GetMapping("/{statementExternalId}")
```

---

## 📚 **4. Listar Extratos com Filtros + Paginação**

`GET /statement`

Filtros possíveis:

* Conta bancária
* Moeda
* Data inicial / final
* Página / tamanho

```java
@GetMapping()
```

---

## 💳 **5. Listar Transações com Filtros Avançados**

`GET /statement/transaction`

Filtros disponíveis:

* statementExternalId
* Categoria
* Tipo (CREDIT/DEBIT)
* Intervalo de datas
* Nome / descrição
* Intervalo de valores

```java
@GetMapping("/transaction")
```

---

# 🧬 **Modelo de Domínio**

A aplicação possui entidades fortemente orientadas ao domínio (DDD), todas estendendo a classe base `Base`.

---

## 🏛️ **Base (Entidade Abstrata)**

Inclui:

* `id` interno (PK)
* `externalId` (UUID único)
* `createdAt`, `updatedAt`
* `active` (soft delete)

---

## 🏦 **Organization**

Representa uma instituição financeira.

Campos:

* `name`
* `code`
* `imagePath`

---

## 📄 **Statement (Extrato)**

Representa um extrato bancário processado pela IA.

Inclui:

* `userId`
* `organizationId`
* `account`
* `currency`
* `initialDate`, `finalDate`

---

## 🧠 **StatementEmbedding**

Armazena:

* Conteúdo textual processado
* Vetor embedding (1536 dimensões via pgvector)

```java
@Column(columnDefinition = "vector(1536)")
private float[] vector;
```

---

## 💰 **Transaction**

Extrai informações individuais como:

* Categoria (`TransactionCategory`)
* Tipo (`CREDIT` / `DEBIT`)
* Nome
* Descrição
* Data
* Valor

---

## 🧩 **Insight**

Armazena análises da IA sobre o extrato:

* `praise` (pontos positivos)
* `critic` (oportunidades de melhoria)

---

## 👤 **User**

Armazena o e-mail do usuário e associa tudo via `UserId`.

---

# 🌐 **Fluxo Geral da Aplicação**

```
Upload de extrato → IA extrai dados → Salva Statement e Transactions
                                 → Gera embedding → Salva StatementEmbedding

Consulta textual → Query recebe pergunta → RAG busca informações →
                → IA responde baseada no contexto real do usuário
```

---

# 🐳 **Docker**

O projeto está preparado para rodar via Docker.

Exemplo de stack:

```yaml
services:
  pgvector:
    image: pgvector/pgvector:0.8.0-pg17
    environment:
      POSTGRES_DB: phastfin_database
      POSTGRES_USER: user
      POSTGRES_PASSWORD: pwd
    ports:
      - "5432:5432"
```

---

# 📘 **Como Rodar o Projeto**

### 1. Criar uma conta na OpenRouter
Vá no site https://openrouter.ai/, crie uma conta e crie também uma api key e atribua essa API Key na sua váriavel de ambiente do seguinte modo:

```bash
OPENROUTER_API_KEY=<SUA_API_KEY> 
```

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-projeto
cd seu-projeto
```

### 2. Subir o ambiente com Docker

```bash
docker compose up -d
```

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

---

# 📚 **Documentação Swagger**

Após rodar o projeto:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

# 🛡️ Segurança & Boas Práticas

* O sistema utiliza `externalId` em vez de IDs internos para evitar exposição
* Todas as entidades possuem `UUID` automático
* Paginação padrão aplicada a todas as consultas
* IA restringida ao contexto do usuário (`userId`)
* Uso de `@Embedded` para IDs de domínio

---

# 🧾 **Conclusão**

Este projeto entrega uma plataforma completa de **processamento inteligente de extratos bancários**, combinando:

* AI
* RAG
* Busca semântica
* Modelagem de domínio
* APIs REST robustas
* Integração com pgvector

Criada para ser extensível, escalável e profissional.
