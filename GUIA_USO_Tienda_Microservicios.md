# Guía de Uso — Tienda Microservicios

## Requisitos Previos

| Herramienta | Versión |
|-------------|---------|
| Java JDK | 21+ |
| Maven | 3.9+ (o usar `mvnw.cmd`) |
| MySQL | 8+ (o MariaDB vía XAMPP) |

## Estructura del Proyecto

```
tienda-parent/
├── pom.xml                          (padre multi-módulo)
├── mvnw.cmd / mvnw
├── .gitignore
├── docs/
│   ├── bd-general.sql               (creación de BBDD)
│   ├── endpoints.md                 (puertos y rutas)
│   └── GUIA_USO.md                  (este documento)
├── eureka-server/                   (puerto 8761)
├── api-gateway/                     (puerto 8080)
├── auth-service/                    (puerto 8091)
├── ms-usuarios/                     (puerto 8090)
├── ms-catalogo/                     (puerto 8081)
├── ms-carrito/                      (puerto 8082)
├── ms-inventario/                   (puerto 8089)
├── ms-pedidos/                      (puerto 8087)
├── ms-pagos/                        (puerto 8086)
├── ms-envios/                       (puerto 8083)
├── ms-notificaciones/               (puerto 8085)
├── ms-marketing/                    (puerto 8084)
└── ms-resenas/                      (puerto 8088)
```

## 1. Base de Datos

### Configurar MySQL (XAMPP)
- Puerto: `3307`
- Usuario: `root`
- Contraseña: *(vacío)*

### Crear bases de datos
Ejecutar `start-databases.bat` o el script manualmente:

```bash
"C:\xampp\mysql\bin\mysql.exe" -u root -P 3307 < docs/bd-general.sql
```

Esto crea las 11 BBDD: `ms_usuarios`, `ms_catalogo`, `ms_carrito`, `ms_inventario`, `ms_pedidos`, `ms_pagos`, `ms_envios`, `ms_notificaciones`, `ms_marketing`, `ms_resenas`, `ms_auth`.

> Las tablas se crean automáticamente al iniciar cada microservicio (JPA `ddl-auto: update`).

## 2. Compilar

```bash
cd tienda-parent
.\mvnw.cmd clean install
```

Esto compila los 13 módulos y ejecuta las pruebas unitarias (178 tests aprox.).

Saltar pruebas (solo compilación):
```bash
.\mvnw.cmd clean install -DskipTests
```

## 3. Ejecutar el Sistema

### Orden de inicio obligatorio

1. **Eureka Server** (puerto 8761)
2. **Microservicios de negocio** (en cualquier orden)
3. **API Gateway** (puerto 8080)

### Con scripts .bat

```bash
build-all.bat           # Compila todo el proyecto
start-eureka.bat        # Inicia Eureka Server
start-services.bat      # Inicia los 11 microservicios de negocio
start-gateway.bat       # Inicia API Gateway
start-all.bat           # Inicia todo en orden (Eureka → servicios → Gateway)
```

### Verificar estado

```bash
check-health.bat        # Muestra servicios registrados en Eureka
```

O manualmente:
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### Detener todo

```bash
stop-all.bat
```

## 4. Autenticación

Todos los endpoints (excepto `/auth/login`) requieren el header:

```
Authorization: Bearer <token_jwt>
```

### Obtener token

```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "juan@test.com",
  "password": "123456"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "tipo": "Bearer"
}
```

### Validar token
```
GET http://localhost:8080/auth/validate?token=eyJhbG...
```

---

## 5. Endpoints por Microservicio

### 5.1 Usuarios (`ms-usuarios`)
Base: `http://localhost:8080/api/usuarios`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| GET | `/api/usuarios/buscar?email=` | Buscar usuario por email |
| POST | `/api/usuarios` | Crear usuario (registro) |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

**POST /api/usuarios — Crear usuario:**
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@test.com",
  "password": "123456",
  "telefono": "123456789",
  "direccion": "Calle 123, Ciudad",
  "rol": "CLIENTE"
}
```
> `rol` puede ser `CLIENTE` (default) o `ADMIN`.

---

### 5.2 Catálogo (`ms-catalogo`)
Base: `http://localhost:8080/api/catalogo`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/catalogo/productos` | Listar todos los productos |
| GET | `/api/catalogo/productos/{id}` | Obtener producto por ID |
| GET | `/api/catalogo/productos/{id}/detalles` | Producto + stock disponible |
| POST | `/api/catalogo/productos` | Crear producto |
| DELETE | `/api/catalogo/productos/{id}` | Eliminar producto |

**POST /api/catalogo/productos — Crear producto:**
```json
{
  "nombre": "Camiseta Algodón",
  "descripcion": "Camiseta 100% algodón, color blanco",
  "precio": 399.99
}
```

---

### 5.3 Inventario (`ms-inventario`)
Base: `http://localhost:8080/api/inventario`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/inventario/stock` | Listar todo el stock |
| GET | `/api/inventario/producto/{productoId}` | Stock por producto |
| POST | `/api/inventario/guardar` | Registrar/actualizar stock |
| DELETE | `/api/inventario/{id}` | Eliminar registro de stock |

**POST /api/inventario/guardar:**
```json
{
  "productoId": 1,
  "cantidad": 100
}
```

---

### 5.4 Carrito (`ms-carrito`)
Base: `http://localhost:8080/api/carrito`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/carrito/{usuarioId}` | Obtener carrito activo del usuario |
| GET | `/api/carrito/detalle/{carritoId}` | Ver detalle de carrito por ID |
| POST | `/api/carrito/{usuarioId}/agregar` | Agregar producto al carrito |
| DELETE | `/api/carrito/{carritoId}/item/{itemId}` | Quitar un item del carrito |
| DELETE | `/api/carrito/{usuarioId}/vaciar` | Vaciar carrito |
| PATCH | `/api/carrito/{carritoId}/estado` | Cambiar estado del carrito |

**POST /api/carrito/{usuarioId}/agregar — Agregar producto:**
```json
{
  "productoId": 1,
  "cantidad": 2
}
```

**PATCH /api/carrito/{carritoId}/estado — Cambiar estado:**
```json
{
  "estado": "CONFIRMADO"
}
```
> Estados válidos: `ACTIVO`, `CONFIRMADO`, `CANCELADO`.

---

### 5.5 Pedidos (`ms-pedidos`)
Base: `http://localhost:8080/api/pedidos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/pedidos/crear?usuarioId=1&carritoId=1` | Crear pedido desde carrito (query params) |
| GET | `/api/pedidos/usuario/{usuarioId}` | Listar pedidos de un usuario |
| GET | `/api/pedidos/{pedidoId}` | Ver detalle de pedido |

> **IMPORTANTE:** El endpoint de creación usa **query params**, no body JSON.
> El carrito debe estar en estado `CONFIRMADO` antes de crear el pedido.

---

### 5.6 Pagos (`ms-pagos`)
Base: `http://localhost:8080/api/pagos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/pagos/procesar` | Procesar pago de pedido |
| GET | `/api/pagos/pedido/{pedidoId}` | Ver pagos de un pedido |
| GET | `/api/pagos/usuario/{usuarioId}` | Ver pagos de un usuario |

**POST /api/pagos/procesar:**
```json
{
  "pedidoId": 1,
  "usuarioId": 1,
  "monto": 1497.00
}
```

---

### 5.7 Envíos (`ms-envios`)
Base: `http://localhost:8080/api/envios`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/envios/crear` | Crear envío para pedido |
| PATCH | `/api/envios/{envioId}/estado` | Actualizar estado de envío |
| GET | `/api/envios/pedido/{pedidoId}` | Ver envío por pedido |
| GET | `/api/envios/usuario/{usuarioId}` | Listar envíos de usuario |

**POST /api/envios/crear:**
```json
{
  "pedidoId": 1,
  "usuarioId": 1,
  "direccionEntrega": "Calle 123, Ciudad"
}
```

**PATCH /api/envios/{envioId}/estado:**
```json
{
  "estado": "EN_CAMINO"
}
```
> Estados válidos: `PENDIENTE`, `EN_CAMINO`, `ENTREGADO`, `FALLIDO`.

---

### 5.8 Notificaciones (`ms-notificaciones`)
Base: `http://localhost:8080/api/notificaciones`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/notificaciones/enviar` | Enviar notificación |
| GET | `/api/notificaciones/usuario/{usuarioId}` | Listar notificaciones |
| GET | `/api/notificaciones/usuario/{usuarioId}/no-leidas` | Notificaciones sin leer |
| PATCH | `/api/notificaciones/{id}/leer` | Marcar como leída |

**POST /api/notificaciones/enviar:**
```json
{
  "usuarioId": 1,
  "tipo": "PEDIDO_CREADO",
  "mensaje": "Tu pedido #1 ha sido creado exitosamente"
}
```
> Tipos válidos: `PEDIDO_CREADO`, `PAGO_COMPLETADO`, `ENVIO_ENTREGADO`.

---

### 5.9 Marketing (`ms-marketing`)
Base: `http://localhost:8080/api/marketing`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/marketing/promociones` | Crear promoción |
| GET | `/api/marketing/promociones` | Listar todas |
| GET | `/api/marketing/promociones/activas` | Solo promociones activas |
| GET | `/api/marketing/promociones/{id}` | Ver promoción por ID |
| PATCH | `/api/marketing/promociones/{id}/estado` | Cambiar estado |
| DELETE | `/api/marketing/promociones/{id}` | Eliminar promoción |

**POST /api/marketing/promociones:**
```json
{
  "nombre": "Black Friday",
  "descripcion": "30% de descuento en toda la tienda",
  "descuentoPorcentaje": 30,
  "estado": "ACTIVO",
  "fechaExpiracion": "2026-12-31T23:59:59"
}
```

**PATCH /api/marketing/promociones/{id}/estado:**
```json
{
  "estado": "INACTIVO"
}
```

---

### 5.10 Reseñas (`ms-resenas`)
Base: `http://localhost:8080/api/resenas`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/resenas` | Crear reseña |
| GET | `/api/resenas/producto/{productoId}` | Reseñas de un producto |
| GET | `/api/resenas/producto/{productoId}/promedio` | Promedio de puntuación |
| GET | `/api/resenas/usuario/{usuarioId}` | Reseñas de un usuario |
| DELETE | `/api/resenas/{id}` | Eliminar reseña |

**POST /api/resenas:**
```json
{
  "productoId": 1,
  "usuarioId": 1,
  "puntuacion": 5,
  "comentario": "Excelente producto, muy recomendado"
}
```
> `puntuacion` debe ser entre 1 y 5.

---

### 5.11 Auth (`auth-service`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Iniciar sesión (obtener JWT) |
| GET | `/auth/validate?token=` | Validar token JWT |

---

## 6. Flujo Completo de Compra (ejemplo paso a paso)

### Paso 1: Crear usuario
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan Pérez","email":"juan@test.com","password":"123456","direccion":"Calle 123, Ciudad","rol":"CLIENTE"}'
```

### Paso 2: Iniciar sesión y obtener JWT
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@test.com","password":"123456"}'
```
Guardar el token de la respuesta.

### Paso 3: Crear productos en el catálogo
```bash
curl -X POST http://localhost:8080/api/catalogo/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"nombre":"Camiseta Algodón","descripcion":"Camiseta blanca","precio":399.99}'
```

### Paso 4: Registrar stock en inventario
```bash
curl -X POST http://localhost:8080/api/inventario/guardar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"productoId":1,"cantidad":100}'
```

### Paso 5: Agregar productos al carrito
```bash
curl -X POST http://localhost:8080/api/carrito/1/agregar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"productoId":1,"cantidad":2}'
```

### Paso 6: Confirmar carrito
```bash
curl -X PATCH http://localhost:8080/api/carrito/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"estado":"CONFIRMADO"}'
```

### Paso 7: Crear pedido
```bash
curl -X POST "http://localhost:8080/api/pedidos/crear?usuarioId=1&carritoId=1" \
  -H "Authorization: Bearer <token>"
```
> El carrito debe estar `CONFIRMADO`.

### Paso 8: Procesar pago
```bash
curl -X POST http://localhost:8080/api/pagos/procesar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"pedidoId":1,"usuarioId":1,"monto":1497.00}'
```

### Paso 9: Crear envío
```bash
curl -X POST http://localhost:8080/api/envios/crear \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"pedidoId":1,"usuarioId":1,"direccionEntrega":"Calle 123, Ciudad"}'
```

### Paso 10: Enviar notificación
```bash
curl -X POST http://localhost:8080/api/notificaciones/enviar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"usuarioId":1,"tipo":"PEDIDO_CREADO","mensaje":"Tu pedido #1 ha sido creado"}'
```

---

## 7. Puertos y URLs Útiles

| Componente | URL |
|------------|-----|
| Eureka Dashboard | http://localhost:8761 |
| Swagger UI Gateway | http://localhost:8080/swagger-ui/index.html |
| Gateway (API) | http://localhost:8080 |
| MySQL (XAMPP) | localhost:3307 |

## 8. Swagger UI por Microservicio

Cada microservicio expone su propia documentación OpenAPI. Desde el Gateway se accede a todas:

- http://localhost:8080/swagger-ui/index.html

O directamente a cada uno:

| Servicio | Swagger Directo |
|----------|-----------------|
| ms-usuarios | http://localhost:8090/swagger-ui/index.html |
| ms-catalogo | http://localhost:8081/swagger-ui/index.html |
| ms-carrito | http://localhost:8082/swagger-ui/index.html |
| ms-inventario | http://localhost:8089/swagger-ui/index.html |
| ms-pedidos | http://localhost:8087/swagger-ui/index.html |
| ms-pagos | http://localhost:8086/swagger-ui/index.html |
| ms-envios | http://localhost:8083/swagger-ui/index.html |
| ms-notificaciones | http://localhost:8085/swagger-ui/index.html |
| ms-marketing | http://localhost:8084/swagger-ui/index.html |
| ms-resenas | http://localhost:8088/swagger-ui/index.html |
