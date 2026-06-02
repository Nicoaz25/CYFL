# CYFL - CY Football League

Aplicación web para crear y gestionar ligas de fútbol, equipos y jugadores con estadísticas. Proyecto intermodular de DAM.

---

## Requisitos

**Funcionales:** Registro de usuarios, roles GESTOR/ADMIN, CRUD de ligas/equipos/jugadores, subida de imágenes, estadísticas y clasificaciones, panel de administración, HTTPS.

**Técnicos:** Java 21, Spring Boot 3.4.1, MySQL 8.0, Docker, Nginx, Let's Encrypt.

## Funcionalidades

- Autenticación con Spring Security y BCrypt
- Crear ligas con nombre, descripción y foto
- Crear equipos con estadísticas (puntos, goles, partidos...)
- Crear jugadores con dorsal, posición, edad y foto
- Actualizar estadísticas de partidos
- Clasificaciones de equipos y jugadores ordenables
- Panel admin para gestionar usuarios
- Subida de imágenes (JPEG, PNG, WebP)

## Esquema del sistema

```
Usuario → HTTPS :443 → Nginx → :8080 → Spring Boot → JDBC :3306 → MySQL
                             → Certbot (SSL)
```

La app tiene 4 servicios Docker: `db` (MySQL), `app` (Spring Boot), `nginx` (reverse proxy), `certbot` (SSL).

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Data JPA / Hibernate | - |
| Spring Security | - |
| Thymeleaf | - |
| MySQL | 8.0 |
| Maven | 3.9.15 |
| Docker / Docker Compose | - |
| Nginx | 1.25-alpine |
| Certbot / Let's Encrypt | - |
| Bootstrap | 5.3 |
| BCrypt | - |

No se usan PHP, React, Angular, Vue ni Node.js.

## Puertos

| Puerto | Servicio | Acceso |
|---|---|---|
| 80 (TCP) | Nginx HTTP | Público |
| 443 (TCP) | Nginx HTTPS | Público |
| 8080 (TCP) | Spring Boot | Interno Docker |
| 3306 (TCP) | MySQL | Interno Docker |

Solo 80 y 443 están expuestos a Internet.

## Seguridad

- HTTPS con TLS 1.2/1.3
- Contraseñas con BCrypt
- Roles de acceso (GESTOR/ADMIN)
- Límite de subida 10MB
- Aislamento de servicios en red Docker

Pendiente: activar CSRF, no hardcodear admin, SSL en BD, rate limiting.

## Viabilidad económica

- VPS: ~5-10€/mes
- Dominio: ~0-2€/mes
- SSL, MySQL, herramientas: 0€
- **Total: ~5-12€/mes**

Todo el software es gratuito/open-source.

## Copias de seguridad

No implementado actualmente. Propuesta:
- **Semanal:** mysqldump completo + volúmenes (retención 4 semanas)
- **Diaria:** mysqldump --flush-logs (retención 7 días)

### Generar dump
```bash
docker exec $(docker ps -qf "name=db") mysqldump -u root -p"${DB_PASSWORD}" CYFL > backup.sql
```

### Restaurar
```bash
cat backup.sql | docker exec -i $(docker ps -qf "name=db") mysql -u root -p"${DB_PASSWORD}" CYFL
```

## Despliegue

```bash
git clone https://github.com/tu-usuario/CYFL.git
cd CYFL
./scripts/setup-ssl.sh   # primera vez
docker compose up -d
```

Admin por defecto: `admin@cyfl.com` / `admin123`

## Estructura

```
CYFL/
├── docker-compose.yml, Dockerfile, pom.xml, CYFL.sql
├── nginx/         → configs de Nginx
├── scripts/       → setup-ssl.sh
├── uploads/       → imágenes subidas
└── src/main/
    ├── java/com/football/cyfl/
    │   ├── controllers/    → LogController, LigaController, AdminController
    │   ├── models/         → User, League, Team, Player
    │   ├── repositories/   → JPA repositories
    │   ├── services/       → UserService
    │   └── config/         → SecurityConfig, WebConfig, DataInitializer
    └── resources/
        ├── templates/      → 16 HTML (Thymeleaf)
        ├── static/css/     → 4 CSS
        └── static/media/   → logo + vídeo
```

---

Proyecto educativo DAM © 2025-2026 Nico Adria
