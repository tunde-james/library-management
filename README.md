# 📚 Library Management System API

A robust, enterprise-ready REST API for managing library operations — built with Spring Boot 3.5, secured with JWT authentication and Argon2 password hashing, and designed with real-world reliability in mind. This project goes beyond typical CRUD tutorials by implementing race-condition-safe registration, deadlock-free concurrent book lending, optimistic and pessimistic locking, rate limiting, soft deletes, and automated account security.

---

## ✨ Key Features

| Category | Features |
|---|---|
| **Authentication** | JWT-based stateless auth, token blacklisting on logout, auto-expiring Caffeine cache |
| **Password Security** | Argon2id hashing (PHC winner), minimum 8-char policy with complexity rules |
| **Account Protection** | Auto-lock after 5 failed attempts, auto-unlock after 30 min, rate limiting (5 req/min) |
| **Book Management** | Full CRUD, pagination, soft delete, duplicate title prevention |
| **Loan System** | Multi-book checkout, 5-book borrow limit, overdue tracking, late fee calculation (₦) |
| **Data Integrity** | Pessimistic locking for inventory, optimistic locking (`@Version`), sorted lock acquisition |
| **Race Conditions** | Double-check registration pattern, database constraint fallback |
| **Infrastructure** | Flyway migrations, health checks, graceful shutdown, environment-based config |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        Client (Yaak / Postman / Frontend)    │
└──────────────────────────┬───────────────────────────────────┘
                           │ HTTP
┌──────────────────────────▼───────────────────────────────────┐
│  Rate Limiting Filter → JWT Auth Filter → Security Config    │
│                     (Filter Chain)                            │
├──────────────────────────────────────────────────────────────┤
│                        Controllers                           │
│   AuthController │ BookController │ BookLoanController │ Admin│
├──────────────────────────────────────────────────────────────┤
│                         Services                             │
│   AuthService │ BookService │ BookLoanService │ JwtService    │
├──────────────────────────────────────────────────────────────┤
│                    Repositories (JPA)                         │
│       UserRepo │ BookRepo │ BookLoanRepo                     │
├──────────────────────────────────────────────────────────────┤
│                     MySQL Database                           │
│         (Flyway-managed schema migrations)                   │
└──────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.13 |
| Security | Spring Security 6, JWT (jjwt 0.12.6), Argon2id |
| Database | MySQL 8+ |
| ORM | Spring Data JPA / Hibernate 6 |
| Migrations | Flyway |
| Caching | Caffeine (token blacklist, rate limiter buckets) |
| Rate Limiting | Bucket4j (Token Bucket algorithm) |
| Mapping | MapStruct 1.6.3 |
| Monitoring | Spring Boot Actuator |
| Validation | Jakarta Bean Validation |
| Build | Maven |
| Code Gen | Lombok |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21+** — [Download](https://adoptium.net/)
- **Maven 3.9+** — or use the included `./mvnw` wrapper
- **MySQL 8+** — [Download](https://dev.mysql.com/downloads/)

### 1. Clone the Repository

```bash
git clone https://github.com/tunde-james/library-management.git
cd library-management
```

### 2. Create the Database

```sql
CREATE DATABASE librarymanagement;
```

### 3. Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` with your values:

```properties
DB_URL=jdbc:mysql://localhost:3306/librarymanagement
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_64_char_hex_secret_here
```

Generate a secure JWT secret:
```bash
openssl rand -hex 32
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:4000`.

### 5. Verify It's Running

```bash
curl http://localhost:4000/actuator/health
```
```json
{"status": "UP"}
```

---

## 📡 API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register a new user (returns JWT) |
| `POST` | `/api/v1/auth/login` | Login (returns JWT) |
| `POST` | `/api/v1/auth/logout` | Logout (blacklists token) |

### Books (Public Read / Admin Write)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/books` | — | List all books (paginated) |
| `GET` | `/api/v1/books/{id}` | — | Get book by ID |
| `POST` | `/api/v1/books` | Admin | Add a new book |
| `PUT` | `/api/v1/books/{id}` | Admin | Update a book |
| `DELETE` | `/api/v1/books/{id}` | Admin | Soft-delete a book |

### Book Loans (Admin Only)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/book-loans` | Issue books to a user |
| `PUT` | `/api/v1/book-loans/return` | Return borrowed books |

### Admin (Admin Only)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/admin/register` | Create a new admin |

### Health (Public)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/actuator/health` | Application health status |
| `GET` | `/actuator/health/liveness` | Liveness probe (K8s) |
| `GET` | `/actuator/health/readiness` | Readiness probe (K8s) |

---

## 📦 Request/Response Examples

### Register
```bash
curl -X POST http://localhost:4000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "tunde",
    "email": "tunde@example.com",
    "password": "SecurePass123"
  }'
```

### Add a Book (Admin)
```bash
curl -X POST http://localhost:4000/api/v1/books \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Things Fall Apart",
    "author": "Chinua Achebe",
    "isbn": "9780385474542",
    "quantity": 3,
    "isAvailable": true
  }'
```

### Issue Loans (Admin)
```bash
curl -X POST http://localhost:4000/api/v1/book-loans \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "bookIds": [1, 2],
    "dueDays": 14
  }'
```

### Browse Books (Public, Paginated)
```bash
curl "http://localhost:4000/api/v1/books?page=0&size=10&sort=title,asc"
```

---

## 🔐 Environment Variables

| Variable | Required | Default | Description |
|---|---|---|---|
| `DB_URL` | No | `jdbc:mysql://localhost:3306/librarymanagement` | Database connection URL |
| `DB_USERNAME` | No | `root` | Database username |
| `DB_PASSWORD` | **Yes** | — | Database password |
| `JWT_SECRET` | **Yes** | — | 64-char hex string for token signing |
| `JWT_EXPIRATION` | No | `1d` | Token expiration duration |
| `SERVER_PORT` | No | `4000` | Application port |
| `DDL_AUTO` | No | `update` | Hibernate DDL mode (`validate` for production) |
| `SHOW_SQL` | No | `false` | Log SQL queries |
| `LOG_LEVEL` | No | `DEBUG` | Root logging level |

> ⚠️ `JWT_SECRET` and `DB_PASSWORD` have no defaults — the app will fail to start if they're missing. This is intentional for security.

---

## 📂 Project Structure

```
src/main/java/com/example/librarymanagement/
├── config/                      # Rate limiting filter, JWT properties
├── controller/                  # REST controllers
│   ├── AdminController.java     # Admin registration
│   ├── AuthController.java      # Login, register, logout
│   ├── BookController.java      # Book CRUD
│   └── BookLoanController.java  # Issue & return loans
├── dto/                         # Request/response data transfer objects
│   ├── book/                    # BookRequestDto, BookResponseDto
│   ├── bookloan/                # BookLoanRequestDto, BookReturnRequestDto
│   └── user/                    # LoginDto, RegisterDto, AdminDto
├── entity/                      # JPA entities
│   ├── BaseEntity.java          # Shared: id, version, audit fields, soft delete
│   ├── Book.java
│   ├── BookLoans.java
│   └── User.java
├── exception/                   # Custom exceptions + global handler
│   ├── ErrorResponse.java       # RFC 7807 structured error response
│   └── GlobalExceptionHandler.java
├── jwt/                         # JWT filter + service
│   ├── JwtAuthenticationFilter.java
│   └── JwtService.java
├── mapper/                      # Entity ↔ DTO mappers (MapStruct)
├── repository/                  # Spring Data JPA repositories
├── security/                    # UserPrincipal, UserDetailsService
├── securityconfig/              # SecurityConfig (filter chain, Argon2)
└── service/                     # Business logic
    ├── AccountUnlockScheduler.java  # Scheduled auto-unlock
    ├── AuthService.java
    ├── BookLoanService.java
    ├── BookService.java
    └── TokenBlacklistService.java

src/main/resources/
├── application.yml              # Config with ${ENV_VAR} placeholders
├── db/migration/                # Flyway SQL migrations
│   ├── V1__create_initial_schema.sql
│   ├── V2__add_audit_fields.sql
│   ├── V3__add_indexes_and_user_fields.sql
│   ├── V4__add_soft_delete.sql
│   └── V5__add_locked_until.sql
└── logback-spring.xml           # Logging configuration
```

---

## 🔒 Security Highlights

- **Argon2id** password hashing — winner of the 2015 Password Hashing Competition, resistant to GPU brute-force attacks
- **Stateless JWT** authentication — no server-side sessions
- **Token blacklisting** via Caffeine cache — enables secure logout with auto-expiration
- **Rate limiting** on login — Bucket4j token-bucket algorithm (5 requests/minute per IP)
- **Account locking** — auto-locks after 5 failed login attempts, auto-unlocks after 30 minutes
- **Sorted lock acquisition** — prevents database deadlocks during concurrent multi-book operations
- **Double-check registration** — handles race conditions using app-level check + DB constraint fallback
- **Optimistic locking** (`@Version`) — prevents silent overwrites on concurrent edits
- **Pessimistic locking** (`SELECT ... FOR UPDATE`) — ensures inventory accuracy during checkout
- **No hardcoded secrets** — all sensitive config via environment variables

---

## 🗄️ Database Migrations

This project uses **Flyway** for version-controlled schema management. Migrations run automatically on startup.

| Migration | Description |
|---|---|
| `V1` | Initial schema — users, books, book_loans, user_roles |
| `V2` | Audit fields — created_at, updated_at, version |
| `V3` | Performance indexes + account security fields |
| `V4` | Soft delete support |
| `V5` | Account lock expiration (locked_until) + composite index |

To reset the database during development:
```sql
DROP DATABASE librarymanagement;
CREATE DATABASE librarymanagement;
```

---

## 🧪 Testing the API

This project is tested using [Yaak](https://yaak.app/) (or any API client like Postman, Insomnia, or cURL).

**Quick smoke test:**
```bash
# 1. Health check
curl http://localhost:4000/actuator/health

# 2. Register
curl -X POST http://localhost:4000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"tunde","email":"tunde@example.com","password":"SecurePass123"}'

# 3. Browse books (public)
curl http://localhost:4000/api/v1/books
```

---

## 📬 Error Handling

All errors follow **RFC 7807 Problem Details** format:

```json
{
    "timestamp": "2026-05-03T20:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Book not found with ID: 42",
    "path": "/api/v1/books/42"
}
```

| Status | Meaning |
|---|---|
| `400` | Validation error or bad input |
| `401` | Invalid credentials or expired/blacklisted token |
| `403` | Insufficient permissions or locked account |
| `404` | Resource not found |
| `409` | Conflict (duplicate, unavailable, version conflict) |
| `429` | Too many requests (rate limited) |

---

## 🛣️ Roadmap

- [ ] Email verification workflow (using `isEnabled` field)
- [ ] Password reset via email
- [ ] Book search and filtering (by title, author, ISBN)
- [ ] User borrowing history endpoint
- [ ] Overdue notification system
- [ ] Docker & Docker Compose setup
- [ ] Swagger/OpenAPI documentation
- [ ] Unit and integration tests

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 👤 Author

**Tunde James**

- GitHub: [@tunde-james](https://github.com/tunde-james)

---
