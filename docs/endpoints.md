# Endpoints - Tienda Microservicios

## Eureka Server
- URL: http://localhost:8761

## API Gateway
- URL: http://localhost:8080
- Puerta de entrada a todos los microservicios

## Microservicios

| Servicio | Ruta Gateway | Puerto Directo |
|----------|-------------|----------------|
| auth-service | `/auth/**` | 8091 |
| ms-usuarios | `/api/usuarios/**` | 8090 |
| ms-catalogo | `/api/catalogo/**` | 8081 |
| ms-inventario | `/api/inventario/**` | 8089 |
| ms-carrito | `/api/carrito/**` | 8082 |
| ms-pedidos | `/api/pedidos/**` | 8087 |
| ms-pagos | `/api/pagos/**` | 8086 |
| ms-envios | `/api/envios/**` | 8083 |
| ms-notificaciones | `/api/notificaciones/**` | 8085 |
| ms-marketing | `/api/marketing/**` | 8084 |
| ms-resenas | `/api/resenas/**` | 8088 |

## Auth
- POST `/auth/login` - Iniciar sesion (email, password) -> JWT
- GET `/auth/validate?token=` - Validar token

## Swagger UI (por implementar)
- http://localhost:{puerto}/swagger-ui/index.html
