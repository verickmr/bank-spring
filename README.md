🧾 README.md — Sistema Bancário em Spring Boot
# 🏦 Sistema Bancário - Spring Boot

Aplicação Java desenvolvida com **Spring Boot**, **JPA (Hibernate)** e **Lombok**, simulando operações bancárias básicas com **contas correntes**, **contas poupança**, **correntistas** e **transações**.

---

## 🚀 Tecnologias Utilizadas

- Java 8+
- Spring Boot 2.7
- Spring Data JPA
- H2 Database 
- Lombok
- Maven
- Postman (para testes de API)

---

## ⚙️ Configuração do Projeto

### 1️⃣ Clonar o repositório
```bash
git clone https://github.com/seu-usuario/sistema-bancario-spring.git
cd sistema-bancario-spring

2️⃣ Build e execução
mvn spring-boot:run


A aplicação rodará em:
👉 http://localhost:8080

🧠 Endpoints Principais
👤 Correntistas
Criar correntista
POST /api/correntistas


Body:

{
  "cpf": "12345678900",
  "nome": "João Silva",
  "email": "joao@email.com"
}

💳 Contas
Criar conta corrente
POST /api/contas/corrente


Body:

{
  "numero": "001-123",
  "saldo": 1000.0,
  "limite": 500.0,
  "correntistaId": 1
}

Criar conta poupança
POST /api/contas/poupanca


Body:

{
  "numero": "002-987",
  "saldo": 2000.0,
  "correntistaId": 1
}

💰 Transações
Depósito
POST /api/contas/corrente/{id}/depositar?valor=500

Saque
POST /api/contas/poupanca/{id}/sacar?valor=100

Aplicar rendimento (poupança)
POST /api/contas/poupanca/{id}/rendimento?taxa=0.05

Aplicar juros (corrente)
POST /api/contas/corrente/{id}/juros?taxa=0.02

🧾 Exemplo de resposta (conta corrente)
{
  "id": 2,
  "numero": "001-123",
  "saldo": 1000.0,
  "limite": 500.0,
  "correntistaNome": "João Silva"
}

🧰 Banco de Dados H2

Acesse o console:
👉 http://localhost:8080/h2-console

Configuração padrão:

JDBC URL: jdbc:h2:mem:testdb
User: sa
Password:

🧑‍💻 Autor

VICTOR ERICK
📧 verickmr.dev@gmail.com


🔗 github.com/verickmr
