# GJP Admin API — Spring Boot 3.x backend

A robust, enterprise-ready Spring Boot 3.x REST API with JWT authentication, role-based authorization, and asynchronous audit logging.

## 🚀 Quick Start (Local Development)

1.  **Database Setup**:
    ```bash
    ./tooling/scripts/util/setup-mysql-db.sh
    ```
2.  **Environment Variables**:
    Ensure `MYSQL_USERNAME`, `MYSQL_PASSWORD`, and `JWT_SECRET_KEY` are exported.
3.  **Run Application**:
    ```bash
    ./tooling/scripts/util/run.sh
    ```
    The API will be available at `http://localhost:8083/api/`.

## 📚 Documentation & Developer Assets

All non-source code assets are consolidated in the **[`tooling/`](./tooling/)** directory:

- [**Documentation Index**](./tooling/docs/README.md) — Architecture, API references, and guides.
- [**Postman Collection**](./tooling/postman/collections/gjp-admin-api-boot.postman_collection.json) — Import this into Postman to test endpoints.
- [**Utility Scripts**](./tooling/scripts/util/) — Tools for DB setup, password generation, and more.

## ⚙️ Tech Stack

- **Core**: Java 21, Spring Boot 3.4.0
- **Security**: Spring Security, JWT (JJWT)
- **Data**: Spring Data JPA, Hibernate, MySQL 8
- **Utilities**: Lombok, MapStruct, Jackson
- **Monitoring**: Spring Boot Actuator

---
*For more detailed instructions, see [CLAUDE.md](./CLAUDE.md) or the [Documentation Index](./tooling/docs/README.md).*
