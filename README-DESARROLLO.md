# 🛒 Guía de Desarrollo Local — Tienda Microservicios

**DSY1103 — Desarrollo FullStack I**

Este documento explica cómo ejecutar **Tienda Microservicios** en el ambiente local (sin Docker) utilizando XAMPP para MySQL, Maven para compilación y Spring Boot para ejecución.

---

## 1. Descripción General

**Tienda Microservicios** es un e-commerce de ropa basado en 13 microservicios Spring Boot con descubrimiento Eureka, API Gateway centralizado y comunicación síncrona vía OpenFeign.

El flujo de desarrollo permite levantar cada microservicio de forma independiente, facilitando debugging y testing unitario durante el ciclo de desarrollo.

---

## 2. Requisitos Previos

| Requisito | Versión | Instalación |
|-----------|---------|-------------|
| **Java JDK** | 21+ | [Oracle Java](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html) o [OpenJDK](https://jdk.java.net/) |
| **Maven** | 3.8.1+ | [Apache Maven](https://maven.apache.org/) o usar `mvnw.cmd` (incluido) |
| **MySQL** | 8.0+ | Incluido en XAMPP |
| **XAMPP** | Última versión | [XAMPP](https://www.apachefriends.org/es/index.html) |
| **Git** | Última versión | [Git](https://git-scm.com/) (opcional) |

### Verificación de instalación

```bash
java -version
# Resultado esperado: openjdk version "21" o superior

.\mvnw.cmd --version
# Resultado esperado: Apache Maven 3.8.1 o superior
```

---

## 3. Base de Datos MySQL

### 3.1 Iniciar XAMPP

1. Abrir **XAMPP Control Panel**
2. Hacer clic en **Start** junto a **MySQL**
3. MySQL estará disponible en `localhost:3307`

### 3.2 Crear bases de datos

```bash
cd tienda-parent
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 < docs/bd-general.sql
```

El script crea 11 bases de datos:

```
ms_auth | ms_usuarios | ms_catalogo | ms_inventario | ms_carrito
ms_pedidos | ms_pagos | ms_envios | ms_notificaciones | ms_marketing | ms_resenas
```

> Las tablas se crean automáticamente al iniciar cada microservicio gracias a `spring.jpa.hibernate.ddl-auto=update`.

---

## 4. Compilación del Proyecto

```bash
cd tienda-parent
.\mvnw.cmd clean install
```

Esto:
1. Limpia artifacts anteriores
2. Compila los 13 módulos del proyecto multi-módulo
3. Ejecuta las 178 pruebas unitarias (JUnit 5 + Mockito)
4. Genera JARs en carpeta `target/` de cada módulo

**Compilación sin pruebas (más rápido):**

```bash
.\mvnw.cmd clean install -DskipTests
```

**Compilar un módulo específico:**

```bash
.\mvnw.cmd clean install -pl ms-usuarios -DskipTests
```

---

## 5. Estructura Multi-Módulo Maven

```
tienda-parent/
├── pom.xml (padre)
│   ├── <groupId>DJ.TIENDA</groupId>
│   ├── <artifactId>tienda-parent</artifactId>
│   └── <modules>
│       ├── eureka-server
│       ├── api-gateway
│       ├── auth-service
│       ├── ms-usuarios
│       ├── ms-catalogo
│       ├── ms-carrito
│       ├── ms-inventario
│       ├── ms-pedidos
│       ├── ms-pagos
│       ├── ms-envios
│       ├── ms-notificaciones
│       ├── ms-marketing
│       └── ms-resenas
│
├── eureka-server/pom.xml
├── api-gateway/pom.xml
└── ... (cada microservicio con su pom.xml hijo)
```

---

## 6. Orden de Arranque

| Orden | Servicio | Puerto | Función |
|-------|----------|--------|---------|
| 1 | **eureka-server** | 8761 | Inicia primero (descubrimiento) |
| 2 | **auth-service** | 8091 | Autenticación JWT |
| 3 | **ms-usuarios** | 8090 | CRUD usuarios |
| 4 | **ms-catalogo** | 8081 | Catálogo/productos |
| 5 | **ms-carrito** | 8082 | Carrito de compras |
| 6 | **ms-inventario** | 8089 | Inventario/stock |
| 7 | **ms-pedidos** | 8087 | Gestión de pedidos |
| 8 | **ms-pagos** | 8086 | Procesamiento de pagos |
| 9 | **ms-envios** | 8083 | Gestión de envíos |
| 10 | **ms-notificaciones** | 8085 | Notificaciones |
| 11 | **ms-marketing** | 8084 | Promociones |
| 12 | **ms-resenas** | 8088 | Reseñas |
| 13 | **api-gateway** | 8080 | Inicia último (Gateway) |

**Razón:** Eureka debe estar disponible antes de que los microservicios se registren, y el Gateway debe iniciarse después de que los servicios estén registrados.

---

## 7. Ejecución Manual (Terminal)

### Terminal 1 — Eureka Server

```bash
cd tienda-parent/eureka-server
.\mvnw.cmd spring-boot:run
```

Esperar hasta ver: `Started EurekaServerApplication in X.XXX seconds`

### Terminal 2 — Auth Service

```bash
cd tienda-parent/auth-service
.\mvnw.cmd spring-boot:run
```

### Terminal 3+ — Microservicios de negocio

```bash
cd tienda-parent/ms-usuarios
.\mvnw.cmd spring-boot:run
```

Repetir para cada microservicio (catalogo, carrito, inventario, pedidos, pagos, envios, notificaciones, marketing, resenas).

### Última Terminal — API Gateway

```bash
cd tienda-parent/api-gateway
.\mvnw.cmd spring-boot:run
```

---

## 8. Verificación de Estado

### Consola Eureka

```
http://localhost:8761
```

Se debe visualizar:

```
AUTH-SERVICE          (1 instance UP)
MS-USUARIOS           (1 instance UP)
MS-CATALOGO           (1 instance UP)
MS-CARRITO            (1 instance UP)
MS-INVENTARIO         (1 instance UP)
MS-PEDIDOS            (1 instance UP)
MS-PAGOS              (1 instance UP)
MS-ENVIOS             (1 instance UP)
MS-NOTIFICACIONES     (1 instance UP)
MS-MARKETING          (1 instance UP)
MS-RESENAS            (1 instance UP)
API-GATEWAY           (1 instance UP)
```

### API Gateway / Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

## 9. Pruebas Unitarias

### Ejecutar todas las pruebas

```bash
.\mvnw.cmd test
```

Resultado esperado: `Tests run: 178, Failures: 0, Errors: 0, Skipped: 0`

### Ejecutar pruebas de un módulo

```bash
.\mvnw.cmd test -pl ms-usuarios
```

### Ejecutar una clase específica

```bash
.\mvnw.cmd test -Dtest=UsuarioControllerTest
```

### Generar reporte de cobertura

```bash
.\mvnw.cmd clean test jacoco:report
```

---

## 10. Capas de Arquitectura

Cada microservicio sigue la estructura CSR (Controller-Service-Repository):

```
ms-usuarios/
├── src/main/java/com/dj/tienda/usuarios/
│   ├── controller/       ← Recibe peticiones HTTP
│   ├── service/          ← Lógica de negocio
│   ├── repository/       ← Acceso a BD
│   ├── entity/           ← Modelos JPA
│   ├── dto/              ← Objetos de transferencia
│   ├── exception/        ← Manejo de errores
│   └── config/           ← Configuración Spring
│
├── src/test/java/
│   ├── controller/       ← Tests @WebMvcTest
│   ├── service/          ← Tests @ExtendWith(MockitoExtension)
│   └── repository/       ← Tests @DataJpaTest
│
└── pom.xml
```

---

## 11. Configuración por Microservicio

Cada microservicio tiene su `application.properties`:

```properties
server.port=8090

spring.datasource.url=jdbc:mysql://127.0.0.1:3307/ms_usuarios?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

spring.application.name=MS-USUARIOS
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 12. Documentación de Endpoints

Cada microservicio incluye anotaciones Swagger:

```java
@Operation(summary = "Crear usuario", description = "Crea un nuevo usuario")
@ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
@ApiResponse(responseCode = "400", description = "Datos inválidos")
@PostMapping("/usuarios")
public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioRequest request) {
    // ...
}
```

Documentación disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 13. Detener el Sistema

Presionar `Ctrl + C` en cada terminal donde se ejecuta un microservicio.

Para detener MySQL, ir a XAMPP Control Panel y hacer clic en **Stop** junto a MySQL.

Para limpiar el proyecto compilado:

```bash
.\mvnw.cmd clean
```

---

## 14. Troubleshooting

### "Port already in use"

El puerto ya está ocupado por otro proceso.

**Solución:**
- Detener la aplicación que ocupa el puerto
- Ejecutar `netstat -ano | findstr :8080` para identificar el proceso
- Cambiar puerto en `application.properties` si es necesario

### "Connection refused to MySQL"

MySQL no está corriendo en `localhost:3307`.

**Solución:**
- Abrir XAMPP Control Panel
- Iniciar MySQL
- Esperar a que muestre el estado verde

### Microservicio no aparece en Eureka

El servicio no se registró correctamente.

**Solución:**
- Verificar que Eureka esté corriendo en `http://localhost:8761`
- Revisar en `application.properties`: `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`
- Revisar logs del microservicio

### "Unknown database 'ms_usuarios'"

La base de datos no existe.

**Solución:**
- Ejecutar el script SQL: `"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 < docs/bd-general.sql`
- O usar `createDatabaseIfNotExist=true` en la URL de conexión

---

## 15. Comandos Rápidos

| Acción | Comando |
|--------|---------|
| Compilar todo | `.\mvnw.cmd clean install` |
| Compilar sin tests | `.\mvnw.cmd clean install -DskipTests` |
| Ejecutar tests | `.\mvnw.cmd test` |
| Iniciar Eureka | `.\mvnw.cmd spring-boot:run -pl eureka-server` |
| Iniciar un servicio | `.\mvnw.cmd spring-boot:run -pl ms-usuarios` |
| Limpiar | `.\mvnw.cmd clean` |
| Swagger | http://localhost:8080/swagger-ui/index.html |
| Eureka | http://localhost:8761 |

---

## 16. Resumen Importante

```
✓ Eureka debe iniciar primero
✓ Microservicios se registran automáticamente en Eureka
✓ API Gateway se inicia después de los microservicios
✓ JWT se obtiene vía auth-service
✓ Comunicación entre servicios usa OpenFeign
✓ Cada microservicio tiene su propia base de datos
✓ Hibernate crea tablas automáticamente (ddl-auto=update)
✓ Pruebas unitarias se ejecutan con mvn test
✓ Swagger documenta todos los endpoints
✓ Los scripts .bat están disponibles en el ZIP de Drive
```

---

**Última actualización:** Junio 2026
**Estado:** Listo para desarrollo local ✅
