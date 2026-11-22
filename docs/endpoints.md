# Endpoints - Tienda Microservicios

## Eureka Server
- URL: http://localhost:8761

## API Gateway
- URL: http://localhost:8080
- Puerta de entrada a todos los microservicios

## Microservicios

| Servicio | Ruta Gateway | Puerto Directo | Base de datos |
|----------|-------------|----------------|---------------|
| auth-service | `/auth/**` | 8091 | — (consulta ms-usuarios) |
| ms-usuarios | `/api/usuarios/**` | 8090 | `ms_usuarios`: usuarios, regiones, comunas |
| ms-catalogo | `/api/catalogo/**` | 8081 | `ms_catalogo`: productos (con stock), categorias |
| ms-pedidos | `/api/pedidos/**` | 8087 | `ms_pedidos`: pedidos, pedido_items |
| ms-pagos | `/api/pagos/**` | 8086 | `ms_pagos`: pagos, boletas |
| ms-contenido | `/api/contenido/**` | 8083 | `ms_contenido`: articulos, mensajes |

## Auth
- POST `/auth/login` - Iniciar sesion `{ email, password }` -> `{ token, tipo, usuario }` (rechaza usuarios INACTIVO)
- GET `/auth/validate?token=` - Validar token

## ms-usuarios
- GET `/api/usuarios` - Listar usuarios
- GET `/api/usuarios/{id}` - Obtener usuario
- GET `/api/usuarios/buscar?email=` - Buscar por email (lo usa auth-service)
- POST `/api/usuarios` - Crear usuario (registro o admin)
- PUT `/api/usuarios/{id}` - Actualizar usuario (incluye rol y estado)
- DELETE `/api/usuarios/{id}` - Eliminar usuario
- GET `/api/usuarios/regiones` - Listar regiones
- GET `/api/usuarios/comunas` - Listar comunas
- GET `/api/usuarios/regiones/{regionId}/comunas` - Comunas de una region

## ms-catalogo
- GET `/api/catalogo/productos?soloActivos=true` - Listar productos (tienda: solo activos)
- GET `/api/catalogo/productos/ofertas` - Productos con descuento
- GET `/api/catalogo/productos/criticos` - Productos con stock critico
- GET `/api/catalogo/productos/{id}` - Obtener producto
- GET `/api/catalogo/productos/codigo/{codigo}` - Obtener producto por codigo (ej: FF-001)
- POST `/api/catalogo/productos` - Crear producto
- PUT `/api/catalogo/productos/{id}` - Actualizar producto
- DELETE `/api/catalogo/productos/{id}` - Eliminar producto
- POST `/api/catalogo/productos/descontar-stock` - Descontar stock `[{ productoId, cantidad }]` (lo usa ms-pedidos)
- GET / POST `/api/catalogo/categorias` - Listar / crear categorias
- GET / PUT / DELETE `/api/catalogo/categorias/{id}` - Obtener / actualizar / eliminar (no elimina si tiene productos)

## ms-pedidos
- POST `/api/pedidos` - Crear pedido y pagar. Body: `{ usuarioId, cliente, entrega, medioPago, simularRechazo, items: [{ productoId, cantidad }] }`. Responde el pedido con `estado` PAGADO (y `folioBoleta`) o RECHAZADO (y `motivoRechazo`)
- GET `/api/pedidos` - Listar pedidos (panel admin)
- GET `/api/pedidos/usuario/{usuarioId}` - Historial de compras
- GET `/api/pedidos/{pedidoId}` - Obtener pedido

## ms-pagos
- POST `/api/pagos/procesar` - Procesar pago (lo usa ms-pedidos). Si se aprueba emite la boleta
- GET `/api/pagos/pedido/{pedidoId}` - Pagos de un pedido
- GET `/api/pagos/usuario/{usuarioId}` - Pagos de un usuario
- GET `/api/pagos/boletas` - Listar boletas
- GET `/api/pagos/boletas/{folio}` - Obtener boleta por folio
- GET `/api/pagos/boletas/pedido/{pedidoId}` - Boleta de un pedido

## ms-contenido
- GET `/api/contenido/articulos?incluirBorradores=true` - Listar articulos del blog (tienda: solo publicados)
- GET `/api/contenido/articulos/{slug}` - Obtener articulo
- POST `/api/contenido/articulos` - Crear articulo
- PUT `/api/contenido/articulos/{id}` - Actualizar articulo
- DELETE `/api/contenido/articulos/{id}` - Eliminar articulo
- GET `/api/contenido/mensajes` - Listar mensajes de contacto
- POST `/api/contenido/mensajes` - Enviar mensaje de contacto
- DELETE `/api/contenido/mensajes/{id}` - Eliminar mensaje

## Swagger UI
- Gateway: http://localhost:8080/swagger-ui.html
- Directo: http://localhost:{puerto}/swagger-ui/index.html
