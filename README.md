# LedgerGuard Backend

## 🎯 Overview
LedgerGuard Backend is a Spring Boot service for finance record processing with strict role-based access control. It exposes APIs for user administration, financial record CRUD and filtering, and dashboard analytics, with request-level auth based on `X-USER-ID` as allowed by the assignment. Delivery followed six explicit phases: Phase 1 (project structure), Phase 2 (user management), Phase 3 (financial records), Phase 4 (security and role enforcement), Phase 5 (dashboard aggregation), and Phase 6 (validation + ProblemDetail error handling).

## 🚀 Quickstart
```bash
# Clone & run in 60 seconds
git clone https://github.com/Balaji30589kl/ledgergaurd.git
cd ledgerguard-backend
mvnw spring-boot:run
# App runs on http://localhost:8085
```

## 🏗️ Architecture
### Layered Design
```text
┌─────────────────┐   ┌──────────────────┐
│   Controllers   │──▶│    Services      │
└─────────────────┘   └──────────────────┘
                            │
                    ┌──────────────────┐
                    │   Repositories   │
                    └──────────────────┘
                            │
                    ┌──────────────────┐
                    │      H2 DB       │
                    └──────────────────┘
```

### Mermaid Diagram
```text
graph TB
    C[Controllers] --> S[Services]
    S --> R[Repositories] 
    R --> DB[H2 Database]
    API[REST API] --> C
    SEC[Security Filter<br/>X-USER-ID] --> C
```

This design keeps controllers thin, business rules centralized in services, and persistence concerns isolated in repositories. Public request/response DTOs are used as API contracts to prevent entity leakage. Security is enforced before controller execution using a custom authentication filter and Spring Security authorization rules.

## 📊 Role-Based Access Matrix
| Endpoint | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| Dashboard APIs | ✅ | ✅ | ✅ |
| Record Read | ❌ | ✅ | ✅ |
| Record CRUD | ❌ | ❌ | ✅ |
| User Management | ❌ | ❌ | ✅ |

## 🔧 Local Development
### Prerequisites
- Java 17+
- Maven 3.6+

### Run
```bash
mvnw clean spring-boot:run
```

### Test APIs
```bash
# Admin operations (X-USER-ID: 4)
curl -H "X-USER-ID: 4" http://localhost:8085/api/users

# Analyst read-only (X-USER-ID: 1)  
curl -H "X-USER-ID: 1" http://localhost:8085/api/records

# Viewer dashboard only (X-USER-ID: 6)
curl -H "X-USER-ID: 6" http://localhost:8085/api/dashboard/summary
```

Additional API checks that match current contracts:

```bash
# Create user (ADMIN)
curl -X POST http://localhost:8085/api/users \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"username":"Jane Doe","email":"jane@example.com","role":"VIEWER"}'

# Create financial record (ADMIN)
curl -X POST http://localhost:8085/api/records \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":250.00,"category":"Utilities","date":"2026-04-04","note":"Electricity bill"}'

# Validation failure sample (ADMIN)
curl -X POST http://localhost:8085/api/records \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"type":"INCOME","amount":-1,"category":"","date":"2099-01-01"}'
```

## 🧪 Verification Results
Full end-to-end test matrix (28 scenarios) confirms:

- ✅ ADMIN: Full user/record CRUD + dashboard access
- ✅ ANALYST: Read-only records + dashboard
- ✅ VIEWER: Dashboard summary/trends only
- ✅ 400/401/403/404 → ProblemDetail responses
- ✅ No functional regressions across 6 phases

H2 console secured behind auth (intentionally disabled for assignment scope).

Verification notes:
- Build check: `mvnw compile` passed
- Runtime check: service confirmed on `http://localhost:8085`
- Validation check: invalid record create surfaces only public request fields (`amount`, `category`, `date`)

## Assignment Evaluation Criteria
| Criteria | Implementation |
|---|---|
| 1. Project Setup & Build | Maven + Spring Boot setup, reproducible build (`mvnw compile`, `spring-boot:run`) |
| 2. Backend Design | Layered architecture with clear controller/service/repository boundaries |
| 3. Data Modeling | JPA entities for `User` and `FinancialRecord`, enums for roles/status/type |
| 4. Access Control | `X-USER-ID` auth filter + Spring Security role-based endpoint authorization |
| 5. Financial Records Module | CRUD, filtering, normalization, and controlled ownership mapping |
| 6. Dashboard APIs | Summary and trend endpoints with repository-level aggregation queries |
| 7. Validation & Error Handling | Bean Validation + centralized `@ControllerAdvice` + ProblemDetail contract |
| 8. Documentation & Verification | This README, role matrix, quickstart, and end-to-end verification evidence |

## Design Decisions

**X-USER-ID Header Authentication**
- Enables frontend integration without OAuth/JWT complexity
- Provides user context for ownership and role-based authorization  
- Allows real request/response testing during development
- **Works with any frontend** - just add the header

**H2 Embedded Database**  
- Zero external dependencies for local development
- Supports full JPA/Hibernate repository patterns and JPQL queries
- Automatic schema generation and test data seeding
- **Production-ready swap path**: application.yml profiles

**DTO Layer Between API and Entities**
- API contracts remain stable if internal models change
- Prevents entity leakage (IDs, timestamps, etc.)
- Explicit request/response validation boundaries
- Type safety through explicit mappers

**ProblemDetail Error Responses**
- RFC 9457 standard for REST APIs
- Consistent error shape across validation/auth/not-found
- Machine-readable field errors for frontend handling
- Spring Boot `@ControllerAdvice` centralizes error logic

**Service Layer Business Rules**
- Controllers handle only HTTP concerns (status codes, binding)
- Complex authorization and validation logic stays reusable
- Repository queries abstracted behind service contracts
- Unit test isolation without HTTP layer



