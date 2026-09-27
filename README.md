# API de Contas Bancárias

API REST desenvolvida para o desafio técnico de estágio em desenvolvimento Back-End da Pacto Mais.

O sistema gerencia correntistas, contas correntes, contas poupança e transações financeiras. As regras de negócio ficam encapsuladas nas entidades, enquanto os serviços coordenam persistência e registro das transações.

Esta branch parte da entrega base disponível em [`main`](https://github.com/verickmr/bank-spring/tree/main) e adiciona autenticação, autorização e migrações de banco. A interface web está separada na branch [`feat/frontend-react`](https://github.com/verickmr/bank-spring/tree/feat/frontend-react).

## Funcionalidades

- Cadastro e consulta de correntistas
- Abertura e consulta de contas
- Depósitos e saques
- Extrato de transações por conta
- Limite para saque em conta corrente
- Bloqueio de saldo negativo em conta poupança
- Aplicação de juros sobre saldo negativo da conta corrente
- Aplicação de rendimento na conta poupança
- Precisão monetária com `BigDecimal` e arredondamento bancário
- Validação das requisições
- Tratamento padronizado de erros
- Documentação interativa com Swagger
- Perfis para H2 e MySQL
- MySQL configurado com Docker Compose
- Testes unitários e de controllers
- Autenticação stateless com Spring Security, BCrypt e JWT
- Autorização por proprietário para correntistas, contas e transações
- Migrações versionadas de banco de dados com Flyway

## Tecnologias

- Java 8
- Spring Boot 2.7.18
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- BigDecimal para valores monetários
- H2 Database
- MySQL 8
- Docker Compose
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Maven
- Lombok
- Spring Security e JWT
- Flyway

## Pré-requisitos

Para executar com o perfil H2:

- JDK 8
- Maven 3.6 ou superior

Para executar com MySQL:

- Docker com Docker Compose
- JDK 8
- Maven 3.6 ou superior

## Como executar

### Clonar o projeto

```bash
git clone https://github.com/verickmr/bank-spring.git
cd bank-spring
```

### Executar com H2

O perfil H2 é utilizado por padrão e não exige a instalação de um banco de dados externo.

```bash
mvn spring-boot:run
```

A API estará disponível em:

```text
http://localhost:8080
```

O console do H2 estará disponível em:

```text
http://localhost:8080/h2-console
```

Utilize estas credenciais:

```text
JDBC URL: jdbc:h2:mem:bank
User Name: sa
Password:
```

### Executar com MySQL e Docker

Crie o arquivo local de variáveis de ambiente:

```bash
cp .env.example .env
```

Inicie o MySQL:

```bash
docker compose up -d mysql
```

Confira se o container está saudável:

```bash
docker compose ps
```

Execute a aplicação com o perfil MySQL:

```bash
export JWT_SECRET='troque-por-um-segredo-com-pelo-menos-32-bytes'
mvn -Dspring-boot.run.profiles=mysql spring-boot:run
```

No PowerShell, defina a variável com:

```powershell
$env:JWT_SECRET='troque-por-um-segredo-com-pelo-menos-32-bytes'
mvn -Dspring-boot.run.profiles=mysql spring-boot:run
```

O segredo JWT é obrigatório no perfil MySQL e deve possuir pelo menos 32 bytes. O arquivo `.env` configura o container do MySQL; a variável `JWT_SECRET` deve estar disponível no terminal que inicia a aplicação.

O Flyway cria e atualiza o schema pelas migrações em [`src/main/resources/db/migration`](src/main/resources/db/migration). A migração V2 preserva os dados de instalações anteriores e adiciona a coluna de senha. Como senhas inexistentes não podem ser recuperadas, correntistas legados recebem um hash aleatório inacessível e precisam definir uma nova senha por um fluxo administrativo seguro antes de entrar.

Para encerrar o container:

```bash
docker compose down
```

## Documentação da API

Com a aplicação em execução, acesse:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

A interface do Swagger permite consultar os contratos e executar requisições diretamente no navegador. Para testar endpoints protegidos, execute o login, copie o token retornado e use o botão **Authorize**. O Swagger adicionará o prefixo `Bearer` automaticamente.

## Testes

Execute a suíte automatizada com:

```bash
mvn test
```

A suíte cobre regras de depósito, saque, juros, rendimento, validações, respostas dos controllers, autenticação e isolamento dos recursos por correntista.

## Endpoints

As requisições com corpo devem utilizar o cabeçalho:

```text
Content-Type: application/json
```

Com exceção do login e do cadastro de correntista, os endpoints exigem:

```text
Authorization: Bearer <token>
```

### Autenticação

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/api/auth/login` | Autentica com CPF e senha | `200 OK` |

```http
POST /api/auth/login
```

```json
{
  "cpf": "12345678900",
  "senha": "senhaSegura123"
}
```

A resposta contém o token JWT usado nas demais requisições:

```json
{
  "token": "eyJ...",
  "tipo": "Bearer"
}
```

### Correntistas

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/correntistas` | Lista o correntista autenticado | `200 OK` |
| `GET` | `/api/correntistas/{id}` | Busca o próprio cadastro pelo ID | `200 OK` |
| `POST` | `/api/correntistas` | Cadastra um correntista | `201 Created` |

#### Cadastrar um correntista

```http
POST /api/correntistas
```

```json
{
  "cpf": "12345678900",
  "nome": "Victor Erick",
  "email": "victor@email.com",
  "senha": "senhaSegura123"
}
```

O CPF deve conter 11 dígitos e não pode pertencer a outro correntista.

### Contas

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/contas` | Lista as contas do correntista autenticado | `200 OK` |
| `GET` | `/api/contas/{id}` | Busca uma conta própria pelo ID | `200 OK` |
| `POST` | `/api/contas/{tipo}` | Abre uma conta | `201 Created` |
| `POST` | `/api/contas/{tipo}/{id}/depositar` | Realiza um depósito | `200 OK` |
| `POST` | `/api/contas/{tipo}/{id}/sacar` | Realiza um saque | `200 OK` |
| `POST` | `/api/contas/{tipo}/{id}/taxa` | Aplica juros ou rendimento | `200 OK` |

O parâmetro `{tipo}` aceita `corrente` ou `poupanca`.

#### Abrir uma conta corrente

```http
POST /api/contas/corrente
```

```json
{
  "numero": "0001-01",
  "limite": 500.00,
  "correntistaId": 1
}
```

#### Abrir uma conta poupança

```http
POST /api/contas/poupanca
```

```json
{
  "numero": "0001-02",
  "correntistaId": 1
}
```

Toda conta é aberta com saldo igual a zero. O saldo inicial não pode ser informado pelo cliente.
O `correntistaId` deve ser o identificador do usuário autenticado; tentativas de abrir ou operar uma conta de outro correntista retornam `403 Forbidden`.

#### Realizar um depósito

```http
POST /api/contas/corrente/1/depositar
```

```json
{
  "valor": 1000.00
}
```

#### Realizar um saque

```http
POST /api/contas/corrente/1/sacar
```

```json
{
  "valor": 200.00
}
```

A conta corrente permite utilizar o saldo mais o limite. A conta poupança permite sacar somente o saldo disponível.

#### Aplicar juros ou rendimento

```http
POST /api/contas/corrente/1/taxa
```

```json
{
  "taxa": 0.02
}
```

A taxa é informada no formato decimal. Por exemplo, `0.02` representa 2%.

Para conta corrente, os juros são aplicados somente quando o saldo está negativo. Para conta poupança, a mesma operação aplica rendimento ao saldo. Ambas registram uma transação.

### Transações

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/transacoes` | Lista as transações do correntista autenticado | `200 OK` |
| `GET` | `/api/transacoes/conta/{contaId}` | Consulta o extrato de uma conta própria | `200 OK` |

#### Consultar o extrato

```http
GET /api/transacoes/conta/1
```

## Tratamento de erros

Os erros seguem uma estrutura padronizada:

```json
{
  "timestamp": "2026-09-24T15:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Saldo insuficiente (saldo + limite).",
  "path": "/api/contas/corrente/1/sacar"
}
```

| Status | Situação |
|---|---|
| `400 Bad Request` | Corpo inválido, campos inválidos ou tipo de conta desconhecido |
| `401 Unauthorized` | Token ausente, inválido ou expirado |
| `403 Forbidden` | Usuário autenticado tentou acessar um recurso de outro correntista |
| `404 Not Found` | Correntista ou conta não encontrado |
| `409 Conflict` | CPF já cadastrado |
| `422 Unprocessable Entity` | Violação de uma regra de negócio |

## Arquitetura

```mermaid
flowchart LR
    Swagger[Swagger / Postman] -->|HTTP + JWT| Controller
    Controller --> Service
    Service --> Autorizacao[Verificação de proprietário]
    Service --> Dominio[Entidades de domínio]
    Service --> Repository
    Repository --> Banco[(H2 ou MySQL)]
```

O projeto está organizado nas seguintes responsabilidades:

- **Controller:** recebe requisições, valida os dados de entrada e define as respostas HTTP.
- **Service:** coordena casos de uso, persistência e registro das transações.
- **Security:** autentica o JWT e impede acesso a recursos pertencentes a outro correntista.
- **Model:** representa o domínio e encapsula regras de depósito, saque, juros e rendimento.
- **Repository:** fornece acesso ao banco de dados com Spring Data JPA.
- **DTO:** define os contratos de entrada e saída da API.
- **Exception:** centraliza erros de negócio e respostas HTTP padronizadas.

### Decisões técnicas

- `Conta` é uma classe abstrata especializada por `ContaCorrente` e `ContaPoupanca`.
- A herança é persistida com a estratégia JPA `JOINED`.
- As regras financeiras ficam nas entidades para preservar o encapsulamento do domínio.
- Valores monetários usam `BigDecimal`, duas casas decimais e arredondamento `HALF_EVEN`.
- Os serviços utilizam transações para manter a atualização do saldo e o registro da operação consistentes.
- O perfil H2 facilita a execução local e os testes.
- O perfil MySQL aproxima a aplicação de um ambiente real.
- O saldo inicial é sempre zero e só pode mudar por meio de uma operação financeira registrada.
- A autenticação é stateless: a senha é armazenada com BCrypt e o cliente envia um JWT em cada requisição protegida.
- A autorização ocorre na camada de serviço: listagens são filtradas pelo CPF autenticado e acessos por ID validam o proprietário.
- O MySQL usa migrações Flyway versionadas, o que permite evoluir o schema sem apagar os dados existentes.

## Escopo da entrega

Todos os requisitos obrigatórios foram implementados, incluindo gerenciamento de correntistas e contas, operações financeiras, persistência e extrato.

Também foram implementados os diferenciais sugeridos: rendimento da poupança, juros da conta corrente, testes automatizados, Swagger/OpenAPI e tratamento padronizado de erros.

Como evolução adicional, esta branch inclui autenticação JWT, senhas protegidas com BCrypt, isolamento dos recursos por proprietário e migrações Flyway. O frontend foi mantido em uma branch própria para não misturar a avaliação do backend com a interface.

Nenhum requisito obrigatório ou diferencial listado no desafio ficou pendente.

## Autor

**Victor Erick**

- GitHub: [verickmr](https://github.com/verickmr)
- E-mail: <verickmr.dev@gmail.com>
