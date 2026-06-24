# Tienda Microservicios 🛒

E-commerce de ropa basado en **12 microservicios Spring Boot** + **Eureka** + **API Gateway** con comunicación síncrona vía OpenFeign.

## Quick Start

```bash
# 1. Iniciar MySQL (XAMPP) en puerto 3307
.\start-databases.bat

# 2. Iniciar todo
.\start-all.bat

# 3. Swagger UI
http://localhost:8080/swagger-ui/index.html
```

## Estructura

```
tienda-parent/
├── eureka-server/          (puerto 8761)
├── api-gateway/            (puerto 8080)
├── auth-service/           (puerto 8091)  → login JWT
├── ms-usuarios/            (puerto 8090)  → CRUD usuarios
├── ms-catalogo/            (puerto 8081)  → productos
├── ms-carrito/             (puerto 8082)  → carrito compras
├── ms-inventario/          (puerto 8089)  → stock
├── ms-pedidos/             (puerto 8087)  → pedidos
├── ms-pagos/               (puerto 8086)  → pagos
├── ms-envios/              (puerto 8083)  → envíos
├── ms-notificaciones/      (puerto 8085)  → notificaciones
├── ms-marketing/           (puerto 8084)  → promociones
└── ms-resenas/             (puerto 8088)  → reseñas
```

## Flujo de Compra (10 pasos)

Ver guía completa → [`docs/GUIA_USO.md`](docs/GUIA_USO.md)

| Paso | Acción | Endpoint |
|------|--------|----------|
| 1 | Crear usuario | `POST /api/usuarios` |
| 2 | Login (JWT) | `POST /auth/login` |
| 3 | Crear producto | `POST /api/catalogo/productos` |
| 4 | Registrar stock | `POST /api/inventario/guardar` |
| 5 | Agregar al carrito | `POST /api/carrito/{usuarioId}/agregar` |
| 6 | Confirmar carrito | `PATCH /api/carrito/{carritoId}/estado` |
| 7 | Crear pedido | `POST /api/pedidos/crear?usuarioId=&carritoId=` |
| 8 | Procesar pago | `POST /api/pagos/procesar` |
| 9 | Crear envío | `POST /api/envios/crear` |
| 10 | Notificar | `POST /api/notificaciones/enviar` |

## Scripts

| Script | Función |
|--------|---------|
| `start-all.bat` | Inicia todo (Eureka → servicios → Gateway) |
| `start-eureka.bat` | Solo Eureka |
| `start-services.bat` | Solo microservicios negocio |
| `start-gateway.bat` | Solo Gateway |
| `build-all.bat` | Compilar proyecto |
| `check-health.bat` | Verificar estado |
| `stop-all.bat` | Detener todo |
| `start-databases.bat` | Iniciar MySQL |

## Compilar

```bash
.\mvnw.cmd clean install        # Con pruebas
.\mvnw.cmd clean install -DskipTests  # Solo compilación
```

## Docker

Levantar todo con un solo comando (requiere Docker Desktop):

```bash
docker compose up -d
```

El build compila los 13 módulos con Maven y crea imágenes individuales para cada servicio. El inicio completo tarda ~2 minutos.

| URL | Servicio |
|-----|----------|
| http://localhost:8761 | Eureka Server |
| http://localhost:8080/swagger-ui/index.html | Swagger Gateway |

## Documentación

- Guía de uso completa → [`docs/GUIA_USO.md`](docs/GUIA_USO.md)
- Endpoints → [`docs/endpoints.md`](docs/endpoints.md)
- Script SQL → [`docs/bd-general.sql`](docs/bd-general.sql)

## Enlaces Externos (EV3)

| Recurso | Link |
|---------|------|
| 📁 Carpeta Drive (scripts .bat) | [Google Drive](https://drive.google.com/drive/folders/1drz2OO1u3pfqv2FxR1YncUu3SGA6coZU?usp=sharing) |
| 📦 ZIP Nativo (proyecto compilado) | _(pendiente por subir)_ |
| 🐳 ZIP Docker (docker-compose) | _(pendiente por subir)_ |
| 🎥 Video defensa EV3 | _(pendiente por subir)_ |
