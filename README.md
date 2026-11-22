# FreshFits - Tienda online con microservicios

Proyecto de la asignatura **DSY1104 (Desarrollo Fullstack I)**. La idea es una tienda de ropa
deportiva llamada FreshFits, con un frontend en React, un backend en Spring Boot dividido en
microservicios y una base de datos MySQL.

El repositorio tiene dos partes:

- `frontend-react/`: la pagina web (React + Vite + Bootstrap).
- Los modulos de Spring Boot: `eureka-server`, `api-gateway`, `auth-service` y los
  microservicios `ms-usuarios`, `ms-catalogo`, `ms-pedidos`, `ms-pagos` y `ms-contenido`.

## Equipo

- Joaquin Rojas
- Andres Cereno
- Julian Silva
- Diego Rivera

## Arquitectura

```
Navegador (React :5173)
        |
        v
API Gateway (:8080)  --->  Eureka (:8761)  (registro de servicios)
   |       |       |       |        |
   v       v       v       v        v
 auth   usuarios catalogo pedidos  pagos   contenido
 :8091    :8090    :8081   :8087   :8086    :8083
```

Todas las peticiones del frontend pasan por el gateway. El gateway valida el token JWT y
revisa el rol antes de derivar la peticion al microservicio correspondiente.

## Tecnologias

| Parte | Tecnologias |
|---|---|
| Frontend | React 18, Vite, React Router, Bootstrap 5, SweetAlert2, fetch |
| Backend | Java 21, Spring Boot 3.5, Spring Cloud Gateway, Eureka, OpenFeign |
| Seguridad | Spring Security, JWT (jjwt), BCrypt |
| Base de datos | MySQL 8 (JPA / Hibernate) |
| Documentacion | springdoc-openapi (Swagger UI) |
| Pruebas | JUnit 5, Mockito, MockMvc, Jasmine y Karma |

## Requisitos

- Java 21 o superior
- Node.js 18 o superior
- MySQL 8 (el proyecto usa el puerto **3307** para no chocar con un MySQL instalado)
- Docker (opcional, para levantar todo con `docker compose`)

## Como ejecutar el proyecto

### 1. Base de datos

La forma mas simple es levantar solo el contenedor de MySQL:

```bash
docker compose up -d mysql
```

Eso crea el puerto 3307 y las seis bases (`ms_usuarios`, `ms_catalogo`, `ms_pedidos`,
`ms_pagos`, `ms_contenido` y la de contenido). Si prefieren un MySQL propio, basta con
cambiar `spring.datasource.url` en cada `application.properties`. Las tablas se crean solas
porque `spring.jpa.hibernate.ddl-auto=update`.

Los datos de prueba (regiones, comunas, productos, usuarios) se cargan automaticamente la
primera vez que parte cada microservicio.

### 2. Backend

Primero Eureka, despues el gateway y los servicios. En terminales separadas:

```bash
./mvnw spring-boot:run -pl eureka-server
./mvnw spring-boot:run -pl api-gateway
./mvnw spring-boot:run -pl auth-service
./mvnw spring-boot:run -pl ms-usuarios
./mvnw spring-boot:run -pl ms-catalogo
./mvnw spring-boot:run -pl ms-pedidos
./mvnw spring-boot:run -pl ms-pagos
./mvnw spring-boot:run -pl ms-contenido
```

En Windows usar `mvnw.cmd` en vez de `./mvnw`.

### 3. Frontend

```bash
cd frontend-react
npm install
npm run dev
```

La pagina queda en <http://localhost:5173>. Si el backend corre en otra direccion, se puede
cambiar con la variable `VITE_API_URL` (por defecto es `http://localhost:8080`).

## Usuarios de prueba

| Rol | Correo | Contrasena |
|---|---|---|
| Administrador | admin@gmail.com | admin123 |
| Vendedor | vendedor@gmail.com | vend1234 |
| Cliente | camila@gmail.com | cliente1 |

Las contrasenas se guardan encriptadas con BCrypt. Las de la tabla son las claves originales
de los datos de prueba; en la base solo queda el hash.

## Roles y permisos

| Rol | Puede |
|---|---|
| Administrador | Todo: usuarios, productos, categorias, pedidos, pagos, contenido y reportes |
| Vendedor | Ver la lista y el detalle de productos y de ordenes |
| Cliente | Ver la tienda, comprar y revisar sus propias compras |
| Sin sesion | Ver productos, categorias, blog y enviar el formulario de contacto; registrarse e iniciar sesion |

El menu del panel se arma segun el rol y ademas el gateway responde **401** si falta el token
o **403** si el rol no tiene permiso. La sesion se guarda en `localStorage`, se revisa la
fecha de vencimiento del token y, cuando esta por vencer, el frontend pide uno nuevo a
`/api/v1/auth/refresh`.

## Documentacion de la API

Con el gateway y los servicios arriba:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI de cada servicio: `http://localhost:8080/api/v1/<servicio>/v3/api-docs`

Para probar endpoints protegidos hay que apretar **Authorize** y pegar el token que devuelve
el login (sin la palabra `Bearer`).

El detalle de todos los endpoints esta en [`docs/endpoints.md`](docs/endpoints.md).

## Pruebas

Backend:

```bash
./mvnw test
```

Son 124 pruebas entre pruebas de controladores (MockMvc), de servicios (Mockito) y de carga
del contexto. El reporte de cobertura queda en `target/site/jacoco/index.html` de cada modulo.

Frontend (necesita Chrome instalado):

```bash
cd frontend-react
npm test
```

## Estructura del repositorio

```
.
├── api-gateway/          # entrada unica, valida JWT y enruta
├── auth-service/         # login, refresh y validacion de tokens
├── eureka-server/        # registro de servicios
├── ms-usuarios/          # usuarios, regiones y comunas
├── ms-catalogo/          # productos, categorias y stock
├── ms-pedidos/           # pedidos y detalle de pedido
├── ms-pagos/             # pagos y boletas
├── ms-contenido/         # blog y mensajes de contacto
├── frontend-react/       # aplicacion React
├── docs/                 # endpoints, guia de uso y script de la base
└── docker-compose.yml    # MySQL + servicios
```

## Documentos de la entrega

Los documentos pedidos en la evaluacion (ERS, manual de usuario, cobertura de testing,
documentacion de APIs y documento de APIs e integracion) se entregan aparte, en la carpeta
`Entregable EFT FreshFits`.

---

Proyecto academico de DSY1104 - Duoc UC.
