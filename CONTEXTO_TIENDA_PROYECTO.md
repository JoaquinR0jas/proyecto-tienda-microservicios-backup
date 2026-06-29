# Contexto — Proyecto Tienda Microservicios

> E-commerce de ropa basado en 13 microservicios Spring Boot con descubrimiento Eureka, API Gateway y comunicación síncrona vía OpenFeign.

---

## 1. Identidad del Proyecto

| Atributo | Valor |
|----------|-------|
| **Nombre** | Tienda Microservicios |
| **Grupo** | `DJ.TIENDA` |
| **Artifact** | `tienda-parent` |
| **Versión** | `1.0.0` |
| **Spring Boot** | `3.5.14` |
| **Spring Cloud** | `2025.0.2` |
| **Java** | `21` (compilación target), JDK `24` instalado |
| **Maven** | Wrapper (`mvnw.cmd`) en raíz |

### Repositorio

- **GitHub (backup)**: [`https://github.com/JoaquinR0jas/proyecto-tienda-microservicios-backup`](https://github.com/JoaquinR0jas/proyecto-tienda-microservicios-backup)
- Rama: `main`
- Últimos commits: Dockerfile, docker-compose, Swagger

### Drive (archivos externos)

- Carpeta Drive: [`https://drive.google.com/drive/folders/1drz2OO1u3pfqv2FxR1YncUu3SGA6coZU`](https://drive.google.com/drive/folders/1drz2OO1u3pfqv2FxR1YncUu3SGA6coZU)
- Contiene: scripts .bat originales, ZIP Nativo, ZIP Docker

---

## 2. Arquitectura General

```
                   ┌─────────────────┐
                   │   API Gateway   │  :8080
                   │  (Spring Cloud  │  Swagger centralizado
                   │    Gateway)     │
                   └───────┬─────────┘
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
   ┌──────────┐    ┌──────────┐     ┌──────────┐
   │ auth-svc │    │ms-usuarios│     │  ... 10  │
   │  :8091   │    │  :8090   │     │ servicios │
   └──────────┘    └──────────┘     └──────────┘
          │               │               │
          └───────────────┼───────────────┘
                          ▼
                   ┌──────────┐
                   │ Eureka   │  :8761
                   │ Server   │  (Netflix Eureka)
                   └──────────┘
                          │
                          ▼
                   ┌──────────┐
                   │  MySQL   │  :3307
                   │ (XAMPP)  │  11 bases de datos
                   └──────────┘
```

### 2.1 Comunicación entre servicios

- **Síncrona**: OpenFeign entre microservicios que lo requieren
- **Descubrimiento**: Todos los servicios se registran en Eureka
- **Gateway**: Punto de entrada único, enruta por `spring.application.name`
- **Swagger**: Centralizado en Gateway vía Springdoc (`/swagger-ui/index.html`)
- **Autenticación**: JWT (jjwt 0.12.3), auth-service genera tokens

### 2.2 Patrones aplicados

- **API Gateway**: Punto único de entrada, abstrae la topología interna
- **Service Discovery**: Registro y descubrimiento dinámico vía Eureka
- **Base de datos por servicio**: Cada microservicio tiene su propia BD (11 BBDD)
- **Multi-stage Docker build**: Compila una vez, genera 13 imágenes livianas JRE

---

## 3. Microservicios

### 3.1 Inventario completo

| # | Servicio | Puerto | BD | Función |
|---|----------|--------|----|---------|
| 1 | **eureka-server** | `8761` | — | Descubrimiento de servicios (Netflix Eureka) |
| 2 | **api-gateway** | `8080` | — | Gateway + Swagger centralizado (Spring Cloud Gateway) |
| 3 | **auth-service** | `8091` | `ms_auth` | Login JWT, registro de usuarios |
| 4 | **ms-usuarios** | `8090` | `ms_usuarios` | CRUD de usuarios |
| 5 | **ms-catalogo** | `8081` | `ms_catalogo` | CRUD de productos/precios |
| 6 | **ms-carrito** | `8082` | `ms_carrito` | Gestión de carrito de compras |
| 7 | **ms-envios** | `8083` | `ms_envios` | Gestión de envíos |
| 8 | **ms-marketing** | `8084` | `ms_marketing` | Promociones y descuentos |
| 9 | **ms-notificaciones** | `8085` | `ms_notificaciones` | Notificaciones al usuario |
| 10 | **ms-pagos** | `8086` | `ms_pagos` | Procesamiento de pagos |
| 11 | **ms-pedidos** | `8087` | `ms_pedidos` | Gestión de pedidos |
| 12 | **ms-resenas** | `8088` | `ms_resenas` | Reseñas y valoraciones |
| 13 | **ms-inventario** | `8089` | `ms_inventario` | Control de stock |

### 3.2 Tecnología común

- Spring Boot 3.5.14 + Spring Cloud 2025.0.2
- JPA / Hibernate (`ddl-auto=update`)
- MySQL 8.0 (conector `mysql-connector-j`)
- Lombok
- Swagger/OpenAPI (Springdoc 2.8.8)
- JWT (jjwt 0.12.3)
- OpenFeign (comunicación entre servicios)
- Mockito + JUnit 5 (tests)

---

## 4. Flujo de Compra Completo (10 pasos)

### Paso a paso

| # | Acción | Endpoint | Body/Params |
|---|--------|----------|-------------|
| 1 | Crear usuario | `POST /api/usuarios` | `{nombre, email, password, direccion}` |
| 2 | Login | `POST /auth/login` | `{email, password}` → devuelve JWT |
| 3 | Crear producto | `POST /api/catalogo/productos` | `{nombre, precio, descripcion}` |
| 4 | Registrar stock | `POST /api/inventario/guardar` | `{productoId, cantidad}` |
| 5 | Agregar al carrito | `POST /api/carrito/{usuarioId}/agregar` | `{productoId, cantidad}` |
| 6 | Confirmar carrito | `PATCH /api/carrito/{carritoId}/estado` | — |
| 7 | Crear pedido | `POST /api/pedidos/crear?usuarioId=&carritoId=` | ⚠️ **Query params, NO body** |
| 8 | Procesar pago | `POST /api/pagos/procesar` | `{pedidoId, monto, metodoPago}` |
| 9 | Crear envío | `POST /api/envios/crear` | `{pedidoId, usuarioId}` (solo Longs) |
| 10 | Notificar | `POST /api/notificaciones/enviar` | `{usuarioId, mensaje}` |

### Detalles importantes

- El carrito debe estar en estado **CONFIRMADO** antes de crear el pedido (paso 6 → 7)
- `POST /api/pedidos/crear` recibe parámetros por **query string**, no en el body
- `POST /api/envios/crear` espera un `Map<String, Long>` con `pedidoId` y `usuarioId` (NO acepta `direccionEntrega`)
- Todos los endpoints pasan por el Gateway (`http://localhost:8080`)

---

## 5. Despliegue

### 5.1 Opción A: Docker (recomendada para EV3)

```bash
# Requisito: Docker Desktop
docker compose up -d --build
```

- **14 contenedores**: MySQL (384M) + 13 servicios Java (256M c/u)
- **Memoria total**: ~3.5 GiB
- **Configuración clave**: `JAVA_TOOL_OPTIONS="-Xmx96m"` para evitar OOM
- **Tiempo de inicio**: ~2 minutos (depende de la máquina)
- **Verificar**: `http://localhost:8761` (12/12 servicios UP)
- **Reset limpio**: `docker compose down -v`
- **Dockerfile**: Multi-stage con 13 targets JRE (`eclipse-temurin:21-jre`)
- **docker-compose.yml**: 14 servicios con healthcheck, límites de memoria, depends_on

**Servicios en Docker**: Cada servicio apunta a `mysql:3306` (nombre de contenedor interno), mientras que en Nativo apuntan a `localhost:3307`.

### 5.2 Opación B: Nativo (sin Docker)

```bash
# Requisitos: XAMPP (MySQL :3307) + Java 21+
.\start-all.bat
```

- **Scripts disponibles**:
  - `start-all.bat` — Eureka → servicios → Gateway (todo automático)
  - `start-eureka.bat` — solo Eureka
  - `start-services.bat` — solo microservicios de negocio
  - `start-gateway.bat` — solo Gateway
  - `build-all.bat` — compilar con Maven
  - `check-health.bat` — verificar estado de todos los servicios
  - `stop-all.bat` — detener todo
  - `start-databases.bat` — iniciar MySQL vía XAMPP

- **Orden de arranque manual**: Eureka (`:8761`) → servicios base (auth, usuarios) → servicios de negocio (catálogo, carrito, pedidos, etc.) → API Gateway (`:8080`)

### 5.3 ZIP Nativo (compilado, listo para ejecutar)

- Ruta: `C:\Users\juako\Desktop\tienda-parent-nativo\`
- Contiene: `apps/` (13 JARs) + `arrancar-nativo.bat`
- Tamaño: ~866 MB
- El `.bat` orquesta el arranque en fases con esperas automáticas
- Verificado: **funcional** ✅

---

## 6. Compilación

```bash
# Compilar todo (con tests)
.\mvnw.cmd clean install

# Compilar sin tests
.\mvnw.cmd clean install -DskipTests
```

- **Tests**: 178 tests unitarios con Mockito + @WebMvcTest (sin dependencia de BD)
- **Perfil**: Sin perfil específico (configuración única para dev y prod)

---

## 7. Base de Datos

- **Motor**: MySQL 8.0
- **Puerto**: `3307`
- **Usuario**: `root` (sin contraseña)
- **Esquemas**: 11 BBDD (una por servicio)

```
ms_auth | ms_usuarios | ms_catalogo | ms_inventario | ms_carrito
ms_pedidos | ms_pagos | ms_envios | ms_notificaciones | ms_marketing | ms_resenas
```

- **DDL automático**: `spring.jpa.hibernate.ddl-auto=update` — Hibernate crea/modifica tablas al iniciar
- **Script SQL**: `mysql-init/01-databases.sql` crea las 11 BBDD (usado solo en Docker)

---

## 8. Documentación

| Recurso | Ruta |
|---------|------|
| Guía de uso completa | `docs/GUIA_USO.md` |
| Endpoints y puertos | `docs/endpoints.md` |
| Script SQL completo | `docs/bd-general.sql` |
| README principal | `README.md` |
| Estado de sesión | `C:\Users\juako\Desktop\SESION_ACTUAL.md` |

---

## 9. URLs de acceso

| Servicio | URL |
|----------|-----|
| Eureka Server | http://localhost:8761 |
| Swagger UI (Gateway) | http://localhost:8080/swagger-ui/index.html |
| API Gateway | http://localhost:8080 |

---

## 10. Fixes aplicados durante el desarrollo

1. **JARs no ejecutables**: Movido `spring-boot-maven-plugin` de `pluginManagement` a `build/plugins` en el POM padre
2. **CORS Swagger**: Agregado bean `OpenAPI` en cada microservicio apuntando al Gateway
3. **ID auto-generado en POST**: Agregado `entity.setId(null)` en 5 controladores para evitar error 500 al recibir IDs del cliente
4. **Envíos**: Corregido endpoint — espera `Map<String, Long>` con `pedidoId` y `usuarioId` (sin `direccionEntrega`)
5. **OOM en Docker**: Ajustados límites de memoria (256M por servicio, 384M MySQL, `-Xmx96m` JVM)

---

## 11. Pendientes

- [ ] Actualizar enlaces del README (ZIP Nativo, ZIP Docker, Video)
- [ ] Grabar video de defensa EV3
- [ ] Subir video a Drive o YouTube y agregar link

---

## 12. Glosario técnico

| Término | Descripción |
|---------|-------------|
| **Eureka** | Servidor de descubrimiento de Netflix (Spring Cloud) |
| **API Gateway** | Punto de entrada único con routing dinámico |
| **OpenFeign** | Cliente HTTP declarativo para comunicación entre servicios |
| **JWT** | JSON Web Token, autenticación stateless |
| **Springdoc** | Generación automática de documentación OpenAPI/Swagger |
| **XAMPP** | Paquete Apache + MySQL + PHP local |
| **Multi-stage build** | Dockerfile que compila y genera imágenes en un solo paso |
| **OOM** | Out of Memory, error por falta de RAM |

---

## 13. Comandos rápidos

```bash
# Docker
docker compose up -d --build          # Levantar todo
docker compose down -v                 # Reset completo
docker compose logs -f                 # Ver logs en vivo
docker stats                           # Monitorear recursos

# Nativo
.\start-all.bat                        # Levantar todo
.\mvnw.cmd clean install -DskipTests   # Compilar (rápido)

# Git
git add -A && git commit -m "msg" && git push origin main

# Verificación
curl http://localhost:8761              # Eureka responde?
```
