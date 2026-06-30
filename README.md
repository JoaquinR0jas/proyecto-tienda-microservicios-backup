# 🚀 TIENDA MICROSERVICIOS - ENTREGA FINAL EV3

E-commerce de ropa basado en **13 microservicios Spring Boot** + **Eureka** + **API Gateway** con comunicación síncrona vía OpenFeign.

**Estudiantes:** Joaquín Rojas + Diego Rivera
**Asignatura:** DSY1103 - Desarrollo FullStack I
**Evaluación:** Parcial 3 (EV3) — Validación, Documentación y Despliegue de Microservicios

---

## 📦 COMPONENTES DE DISTRIBUCIÓN Y DEFENSA TÉCNICA

Utilice los siguientes enlaces externos para descargar las versiones listas para producción y visualizar la defensa del proyecto:

| Componente | Descripción | Enlace de Descarga (Nube externa) |
| :--- | :--- | :--- |
| **📦 Versión Sin Docker**<br>*(Arranque Nativo)* | Archivo `.zip` que contiene la carpeta `apps/` con los `.jar` compilados y el script `arrancar-nativo.bat` ordenado por fases. | [Descargar ZIP Nativo aquí](https://drive.google.com/file/d/1oBCKDV0gWbuZKpxAJTKvyH63wjGfHJD9/view) |
| **🐳 Versión Con Docker**<br>*(Avance Examen Transversal)* | Archivo `.zip` que contiene la carpeta `apps/` con los `.jar`, el archivo `docker-compose.yml` y el script automatizado `arrancar-sistema.bat`. | [Descargar ZIP Docker aquí](https://drive.google.com/file/d/1x37nsNovVLBJEuwmqYI8Ur5PxuBnsln0/view) |
| **🎥 Video de Defensa Técnica**<br>*(Evaluación Individual)* | Enlace directo al video explicativo donde se evidencia el funcionamiento, testing y el aporte técnico individual. **Duración ideal: 15 minutos (Máximo permitido: 18 minutos).** | [Ver Video Explicativo aquí](https://drive.google.com/file/d/1lyHggfgN6TLne1tGC-CPG8dqNxGbfqN-/view) |

---

## ⚡ Quick Start (Desarrollo Local)

### Requisitos previos

| Herramienta | Versión |
|-------------|---------|
| Java JDK | 21+ |
| Maven | 3.8+ (o usar `mvnw.cmd` incluido) |
| MySQL | 8.0+ (XAMPP) |
| Docker Desktop | Opcional (solo para despliegue containerizado) |

### Compilar el proyecto

```bash
cd tienda-parent
.\mvnw.cmd clean install
```

Esto compila los 13 módulos y ejecuta las **178 pruebas unitarias** con JUnit 5 + Mockito.

### Iniciar MySQL

1. Abrir **XAMPP Control Panel**
2. Iniciar **MySQL** (puerto 3307)
3. Crear las 11 bases de datos:

```bash
# Opción 1: Usando el script SQL
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 < docs/bd-general.sql

# Opción 2: Con Maven (Spring Boot crea las BBDD automáticamente con ddl-auto=update)
```

### Ejecutar los microservicios

Iniciar en este orden estricto:

```bash
# Terminal 1: Eureka Server
cd eureka-server
.\mvnw.cmd spring-boot:run

# Terminal 2: Auth Service + Usuarios
cd auth-service
.\mvnw.cmd spring-boot:run

# Terminal 3+: Microservicios de negocio (en cualquier orden)
cd ms-catalogo && .\mvnw.cmd spring-boot:run
cd ms-carrito && .\mvnw.cmd spring-boot:run
cd ms-inventario && .\mvnw.cmd spring-boot:run
cd ms-pedidos && .\mvnw.cmd spring-boot:run
cd ms-pagos && .\mvnw.cmd spring-boot:run
cd ms-envios && .\mvnw.cmd spring-boot:run
cd ms-notificaciones && .\mvnw.cmd spring-boot:run
cd ms-marketing && .\mvnw.cmd spring-boot:run
cd ms-resenas && .\mvnw.cmd spring-boot:run

# Último: API Gateway
cd api-gateway
.\mvnw.cmd spring-boot:run
```

> **Alternativa:** Descargue el ZIP Nativo desde Drive que incluye los scripts `.bat` con arranque automatizado secuencial.

### Verificar

| URL | Qué verifica |
|-----|-------------|
| http://localhost:8761 | Eureka — todos los servicios UP |
| http://localhost:8080/swagger-ui/index.html | Swagger — documentación centralizada |

---

## 🏗️ Arquitectura General

```
┌──────────────────────────────────────────┐
│          CLIENTE EXTERNO / POSTMAN        │
└──────────────────┬───────────────────────┘
                   │
         ┌─────────▼──────────┐
         │   API Gateway      │
         │   (puerto 8080)    │
         │ Swagger centralizado│
         └─────────┬──────────┘
         │         │         │         │
    ┌────▼──┐  ┌──▼────┐  ┌─▼────┐  ┌▼─────┐
    │auth   │  │ms-    │  │ms-   │  │ ms-  │
    │service│  │usuarios│  │catálogo│carrito│
    │8091   │  │8090   │  │8081   │  │8082  │
    └───────┘  └───────┘  └───────┘  └──────┘
         │         │         │         │
         └─────────┼─────────┼─────────┘
                   │
         ┌─────────▼──────────┐
         │  Eureka Server     │
         │  (puerto 8761)     │
         │  Service Discovery │
         └────────────────────┘
                   │
         ┌─────────▼──────────┐
         │      MySQL 8.0     │
         │   (puerto 3307)    │
         │  11 bases de datos │
         └────────────────────┘
```

---

## 📋 Microservicios (13 componentes)

| # | Servicio | Puerto | BD | Responsabilidad |
|---|----------|--------|----|----------------|
| 1 | **eureka-server** | 8761 | — | Descubrimiento de servicios |
| 2 | **api-gateway** | 8080 | — | Gateway + Swagger centralizado |
| 3 | **auth-service** | 8091 | `ms_auth` | Login JWT, validación de tokens |
| 4 | **ms-usuarios** | 8090 | `ms_usuarios` | CRUD de usuarios |
| 5 | **ms-catalogo** | 8081 | `ms_catalogo` | Productos y precios |
| 6 | **ms-carrito** | 8082 | `ms_carrito` | Carrito de compras |
| 7 | **ms-inventario** | 8089 | `ms_inventario` | Control de stock |
| 8 | **ms-pedidos** | 8087 | `ms_pedidos` | Gestión de pedidos |
| 9 | **ms-pagos** | 8086 | `ms_pagos` | Procesamiento de pagos |
| 10 | **ms-envios** | 8083 | `ms_envios` | Gestión de envíos |
| 11 | **ms-notificaciones** | 8085 | `ms_notificaciones` | Notificaciones al usuario |
| 12 | **ms-marketing** | 8084 | `ms_marketing` | Promociones y descuentos |
| 13 | **ms-resenas** | 8088 | `ms_resenas` | Reseñas y valoraciones |

---

## 🛒 Flujo de Compra Completo (10 pasos)

| Paso | Acción | Endpoint | Método |
|------|--------|----------|--------|
| 1 | Crear usuario | `POST /api/usuarios` | JSON body |
| 2 | Login (obtener JWT) | `POST /auth/login` | Email + password |
| 3 | Crear producto | `POST /api/catalogo/productos` | JSON body |
| 4 | Registrar stock | `POST /api/inventario/guardar` | ProductoId + cantidad |
| 5 | Agregar al carrito | `POST /api/carrito/{usuarioId}/agregar` | ProductoId + cantidad |
| 6 | Confirmar carrito | `PATCH /api/carrito/{carritoId}/estado` | Estado = CONFIRMADO |
| 7 | Crear pedido | `POST /api/pedidos/crear?usuarioId=&carritoId=` | Query params |
| 8 | Procesar pago | `POST /api/pagos/procesar` | PedidoId + monto + método |
| 9 | Crear envío | `POST /api/envios/crear` | PedidoId + usuarioId (Map) |
| 10 | Notificar | `POST /api/notificaciones/enviar` | UsuarioId + mensaje |

**Guía detallada:** [`docs/GUIA_USO.md`](docs/GUIA_USO.md)

---

## 🧪 Calidad y Testing

| Elemento | Detalle |
|----------|---------|
| **Framework** | JUnit 5 + Mockito |
| **Total de tests** | 178 unitarios |
| **Cobertura** | Servicios, controladores, repositorios |
| **Ejecución** | `.\mvnw.cmd clean install` (no omitir tests) |
| **Documentación API** | Swagger/OpenAPI con anotaciones `@Operation`, `@ApiResponse`, `@Tag` |
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html |

---

## 📂 Estructura del Repositorio

```
tienda-parent/
├── README.md                          ← Este archivo
├── README-DESARROLLO.md               ← Guía de desarrollo local
├── README-DESPLIEGUE-DOCKER.md        ← Guía de Docker
├── pom.xml                            ← POM padre (Spring Boot 3.5.14)
├── mvnw.cmd                           ← Maven Wrapper (Windows)
│
├── eureka-server/                     ← Puerto 8761
├── api-gateway/                       ← Puerto 8080
├── auth-service/                      ← Puerto 8091
├── ms-usuarios/                       ← Puerto 8090
├── ms-catalogo/                       ← Puerto 8081
├── ms-carrito/                        ← Puerto 8082
├── ms-inventario/                     ← Puerto 8089
├── ms-pedidos/                        ← Puerto 8087
├── ms-pagos/                          ← Puerto 8086
├── ms-envios/                         ← Puerto 8083
├── ms-notificaciones/                 ← Puerto 8085
├── ms-marketing/                      ← Puerto 8084
├── ms-resenas/                        ← Puerto 8088
│
├── docs/
│   ├── GUIA_USO.md                    ← Flujo de compra paso a paso
│   ├── endpoints.md                   ← Especificación de endpoints
│   └── bd-general.sql                 ← Script SQL de BBDD
│
├── mysql-init/
│   └── 01-databases.sql               ← Init SQL para Docker
│
├── docker-compose.yml                 ← Orquestación Docker
├── Dockerfile                         ← Multi-stage build (13 targets)
├── .dockerignore
└── .gitignore
```

---

## 🔧 Compilación

```bash
# Compilar con pruebas unitarias (recomendado)
.\mvnw.cmd clean install

# Compilar sin pruebas (solo verificación de sintaxis)
.\mvnw.cmd clean install -DskipTests

# Compilar un módulo específico
.\mvnw.cmd clean install -pl ms-usuarios -DskipTests
```

---

## 🐳 Docker (Avance Examen Transversal)

El proyecto incluye Dockerfile multi-stage y docker-compose.yml para levantar todo el ecosistema containerizado.

```bash
# Levantar todo
docker compose up -d --build

# Verificar contenedores
docker compose ps

# Ver logs en vivo
docker compose logs -f

# Detener sin eliminar datos
docker compose down

# Detener y eliminar volúmenes (reset total)
docker compose down -v
```

**14 contenedores estables:** MySQL (384M) + 13 servicios Java (256M c/u)
Ver README-DESPLIEGUE-DOCKER.md para guía completa.

---

## 📊 Tecnologías Utilizadas

| Capa | Tecnología |
|------|-----------|
| **Lenguaje** | Java 21 |
| **Framework** | Spring Boot 3.5.14 |
| **Cloud** | Spring Cloud 2025.0.2 |
| **Orquestación** | Eureka Server (Netflix) |
| **Gateway** | Spring Cloud Gateway (Netty) |
| **Comunicación** | OpenFeign (REST síncrona) |
| **BD** | MySQL 8.0 (11 esquemas) |
| **ORM** | Hibernate/JPA |
| **Seguridad** | JWT (jjwt 0.12.3) |
| **Documentación** | Swagger/OpenAPI (Springdoc 2.8.8) |
| **Testing** | JUnit 5 + Mockito |
| **Build** | Maven 3.8+ (Wrapper) |
| **Containerización** | Docker + Docker Compose |

---

## 🔐 Autenticación

- **Tipo:** JWT (JSON Web Token)
- **Flujo:**
  1. `POST /auth/login` con email + password → devuelve JWT
  2. Incluir JWT en header: `Authorization: Bearer <token>`
  3. Los microservicios validan el token contra auth-service vía OpenFeign

---

## 📚 Documentación Complementaria

| Documento | Propósito |
|-----------|-----------|
| [`README-DESARROLLO.md`](README-DESARROLLO.md) | Guía completa de desarrollo local, orden de arranque, estructura de capas |
| [`README-DESPLIEGUE-DOCKER.md`](README-DESPLIEGUE-DOCKER.md) | Guía de Docker: requisitos, compose, healthcheck, volúmenes, troubleshooting |
| [`docs/GUIA_USO.md`](docs/GUIA_USO.md) | Flujo de compra detallado (10 pasos) + JSON de ejemplo para cada endpoint |
| [`docs/endpoints.md`](docs/endpoints.md) | Especificación técnica de endpoints (métodos, parámetros, respuestas) |
| [`docs/bd-general.sql`](docs/bd-general.sql) | Script SQL para inicializar las 11 bases de datos |

---

## 🚀 URLs de Acceso

| Servicio | URL |
|----------|-----|
| Eureka Server | http://localhost:8761 |
| API Gateway (Swagger) | http://localhost:8080/swagger-ui/index.html |
| MySQL | localhost:3307 (root / sin contraseña) |

---

## 📝 Notas Importantes

- **MySQL:** Puerto 3307 (XAMPP). Hibernate maneja la creación de tablas automáticamente (`ddl-auto=update`)
- **Orden de arranque:** Eureka (8761) → Microservicios → API Gateway (8080)
- **OpenFeign:** Los microservicios se comunican entre sí usando el nombre registrado en Eureka
- **Docker:** Sin Docker solo requiere XAMPP + Java 21. Con Docker solo requiere Docker Desktop
- **No hay archivos binarios en Git:** Los `.jar`, `.bat` y videos están exclusivamente en Google Drive

---

## 🤝 Contribuciones

- **Joaquín Rojas:** Arquitectura general, auth-service, ms-usuarios, ms-catalogo, ms-carrito, ms-pedidos (código + testing)
- **Diego Rivera:** Apoyo nominal en integración y testing

---

## 📄 Licencia

Proyecto académico DSY1103. Uso únicamente para propósitos educativos.

---

**Última actualización:** Junio 2026
**Estado:** Listo para EV3 ✅
