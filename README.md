# Core Backend - Gerenciador Financeiro

API REST em Java / Spring Boot para gerenciamento financeiro pessoal.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.4 |
| Spring Data JPA | — |
| Spring Security | — |
| PostgreSQL | — |
| JWT (Auth0) | 4.4.0 |
| Bean Validation | Jakarta |
| OpenAPI / Swagger UI | Springdoc 2.8.6 |
| Maven | Wrapper incluso |

---

## Pré-requisitos

- JDK 21
- PostgreSQL rodando com banco criado

---

## Variáveis de ambiente obrigatórias

| Variável | Exemplo | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/gerenciador_fin` | URL JDBC do banco |
| `DB_USERNAME` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `senhaSegura` | Senha do banco |
| `API_SECURITY_TOKEN_SECRET` | `minha-chave-secreta-longa-123!` | Chave secreta para assinar tokens JWT (mínimo 32 caracteres recomendado) |
| `APP_TIMEZONE` *(opcional)* | `America/Sao_Paulo` | Timezone para expiração do JWT. Padrão: `America/Sao_Paulo` |

---

## Como executar

```bash
# Clone e entre na pasta
git clone <repo-url>
cd core-java-gerenciador-fin

# Configure as variáveis de ambiente (exemplo Linux/Mac)
export DB_URL=jdbc:postgresql://localhost:5432/gerenciador_fin
export DB_USERNAME=postgres
export DB_PASSWORD=senhaSegura
export API_SECURITY_TOKEN_SECRET=minha-chave-super-secreta-123

# Execute
./mvnw spring-boot:run
```

### Executar testes (usa H2 em memória, sem PostgreSQL)

```bash
./mvnw test
```

---

## Documentação da API (Swagger UI)

Após subir a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

---

## Fluxo de autenticação

1. Crie um usuário: `POST /api/users`
2. Faça login: `POST /api/auth/login` → recebe um `token` JWT
3. Use o token no header de todas as requisições protegidas:
   ```
   Authorization: Bearer <token>
   ```

---

## Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/users` | Criar novo usuário | ❌ |
| `POST` | `/api/auth/login` | Login e obter token JWT | ❌ |

### Contas (`/api/accounts`)

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/accounts` | Criar conta | ✅ |
| `GET` | `/api/accounts/user/{userId}?page=0&size=20&sort=createdAt&direction=desc` | Listar contas ativas | ✅ |
| `PUT` | `/api/accounts/{id}` | Atualizar conta | ✅ |
| `PATCH` | `/api/accounts/{id}/inactivate` | Inativar conta | ✅ |
| `PATCH` | `/api/accounts/{id}/reactivate` | Reativar conta | ✅ |

**Tipos de conta:** `CHECKING`, `SAVINGS`, `CREDIT_CARD`, `CASH`, `INVESTMENT`

### Categorias (`/api/categories`)

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/categories` | Criar categoria | ✅ |
| `GET` | `/api/categories` | Listar todas as categorias | ✅ |
| `GET` | `/api/categories/user/{userId}?type=EXPENSE&page=0&size=20` | Listar categorias do usuário (filtro por tipo) | ✅ |
| `PUT` | `/api/categories/{id}` | Atualizar categoria | ✅ |
| `DELETE` | `/api/categories/{id}` | Inativar categoria (soft delete) | ✅ |

**Tipos de categoria:** `INCOME`, `EXPENSE`

### Transações (`/api/transactions`)

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/transactions` | Criar transação | ✅ |
| `GET` | `/api/transactions/user/{userId}?type=EXPENSE&status=PAID&page=0&size=20` | Listar transações do usuário (filtros opcionais) | ✅ |

**Tipos de transação:** `INCOME`, `EXPENSE`, `TRANSFER`  
**Status:** `PENDING`, `PAID`, `CANCELED`

---

## Exemplos de Request/Response

### POST /api/users
```json
{
  "name": "Maria Silva",
  "email": "maria@email.com",
  "password": "senha123"
}
```

### POST /api/auth/login
```json
{
  "email": "maria@email.com",
  "password": "senha123"
}
```
**Response:**
```json
{
  "token": "eyJhbGci...",
  "userId": "uuid-aqui",
  "name": "Maria Silva"
}
```

### POST /api/accounts
```json
{
  "userId": "uuid-do-usuario",
  "name": "Conta Corrente",
  "type": "CHECKING",
  "balance": 1500.00
}
```

### POST /api/categories
```json
{
  "userId": "uuid-do-usuario",
  "name": "Alimentação",
  "type": "EXPENSE",
  "colorHex": "#FF5733",
  "parentId": null
}
```

### POST /api/transactions
```json
{
  "userId": "uuid-do-usuario",
  "accountId": "uuid-da-conta",
  "categoryId": "uuid-da-categoria",
  "type": "EXPENSE",
  "amount": 150.00,
  "description": "Supermercado",
  "transactionDate": "2026-04-01",
  "status": "PAID"
}
```

---

## Modelo de dados

```
users
  id (UUID PK)
  name
  email (único)
  password_hash
  created_at
  updated_at

accounts
  id (UUID PK)
  user_id (FK → users)
  name
  type (CHECKING | SAVINGS | CREDIT_CARD | CASH | INVESTMENT)
  balance (decimal 15,2)
  active (boolean)
  created_at
  updated_at

categories
  id (UUID PK)
  user_id (FK → users)
  parent_id (FK → categories, nullable)
  name
  type (INCOME | EXPENSE)
  color_hex
  active (boolean)
  created_at
  updated_at

transactions
  id (UUID PK)
  user_id (FK → users)
  account_id (FK → accounts)
  category_id (FK → categories)
  type (INCOME | EXPENSE | TRANSFER)
  amount (decimal 15,2)
  description
  transaction_date
  status (PENDING | PAID | CANCELED)
  reference_id (UUID nullable — liga transferências)
  active (boolean)
  created_at
  updated_at
```
