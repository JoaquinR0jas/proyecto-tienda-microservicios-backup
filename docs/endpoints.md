# Endpoints - FreshFits

## Servicios

| Servicio | Ruta en el gateway | Puerto directo | Base de datos |
|---|---|---|---|
| eureka-server | - | 8761 | - |
| api-gateway | todavia | 8080 | - |
| auth-service | `/api/v1/auth/**` | 8091 | - (consulta ms-usuarios) |
| ms-usuarios | `/api/v1/usuarios/**` | 8090 | `ms_usuarios`: usuarios, regiones, comunas |
| ms-catalogo | `/api/v1/catalogo/**` | 8081 | `ms_catalogo`: productos, categorias |
| ms-pedidos | `/api/v1/pedidos/**` | 8087 | `ms_pedidos`: pedidos, pedido_items |
| ms-pagos | `/api/v1/pagos/**` | 8086 | `ms_pagos`: pagos, boletas |
| ms-contenido | `/api/v1/contenido/**` | 8083 | `ms_contenido`: articulos, mensajes |

Todas las rutas se llaman a traves del gateway (`http://localhost:8080`). Los puertos
directos sirven para depurar, pero no tienen la validacion del token.

## Autenticacion

| Metodo | Ruta | Quien | Descripcion |
|---|---|---|---|
| POST | `/api/v1/auth/login` | publico | Body `{ email, password }`. Devuelve `{ token, tipo, usuario }`. Rechaza usuarios INACTIVO. |
| POST | `/api/v1/auth/refresh` | token vencido | Body `{ token }`. Devuelve un token nuevo si el anterior tiene menos de 7 dias de vencido. |
| GET | `/api/v1/auth/validate?token=` | publico | Revisa la firma y la fecha. Devuelve `{ email, rol, valido }`. |

El token es un JWT firmado con HS256. Adentro van el correo (`sub`), el id del usuario
(`id`) y el rol (`rol`: ADMIN, VENDEDOR o CLIENTE). Dura **1 hora**.

Para llamar a un endpoint protegido:

```
Authorization: Bearer <token>
```

## ms-usuarios

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| GET | `/api/v1/usuarios` | ADMIN | Lista todos los usuarios (sin contrasena) |
| GET | `/api/v1/usuarios/{id}` | ADMIN o el mismo usuario | Detalle de un usuario |
| GET | `/api/v1/usuarios/buscar?email=` | ADMIN | Buscar por correo |
| GET | `/api/v1/usuarios/auth?email=` | interno | Datos de login con el hash (lo usa auth-service) |
| POST | `/api/v1/usuarios` | publico | Registro / crear usuario |
| PUT | `/api/v1/usuarios/{id}` | ADMIN | Actualizar datos, rol y estado |
| DELETE | `/api/v1/usuarios/{id}` | ADMIN | Eliminar usuario |
| GET | `/api/v1/usuarios/regiones` | publico | Listar regiones |
| GET | `/api/v1/usuarios/comunas` | publico | Listar comunas |
| GET | `/api/v1/usuarios/regiones/{regionId}/comunas` | publico | Comunas de una region |

Las contrasenas se guardan con BCrypt y nunca se devuelven en el JSON.

## ms-catalogo

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| GET | `/api/v1/catalogo/productos?soloActivos=true` | publico | Listar productos |
| GET | `/api/v1/catalogo/productos/ofertas` | publico | Productos con descuento |
| GET | `/api/v1/catalogo/productos/criticos` | ADMIN | Productos con stock bajo |
| GET | `/api/v1/catalogo/productos/{id}` | publico | Detalle por id |
| GET | `/api/v1/catalogo/productos/codigo/{codigo}` | publico | Detalle por codigo (ej: FF-001) |
| POST | `/api/v1/catalogo/productos` | ADMIN | Crear producto |
| PUT | `/api/v1/catalogo/productos/{id}` | ADMIN | Actualizar producto |
| DELETE | `/api/v1/catalogo/productos/{id}` | ADMIN | Eliminar producto |
| POST | `/api/v1/catalogo/productos/descontar-stock` | interno | Descontar stock, lo usa ms-pedidos |
| GET | `/api/v1/catalogo/categorias` | publico | Listar categorias |
| POST | `/api/v1/catalogo/categorias` | ADMIN | Crear categoria |
| GET | `/api/v1/catalogo/categorias/{id}` | publico | Detalle de categoria |
| PUT | `/api/v1/catalogo/categorias/{id}` | ADMIN | Actualizar categoria |
| DELETE | `/api/v1/catalogo/categorias/{id}` | ADMIN | Eliminar (solo si no tiene productos) |

## ms-pedidos

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| POST | `/api/v1/pedidos` | CLIENTE o ADMIN | Crear pedido y pagar |
| GET | `/api/v1/pedidos` | ADMIN o VENDEDOR | Listar todos los pedidos |
| GET | `/api/v1/pedidos/usuario/{usuarioId}` | ADMIN o el mismo cliente | Historial de compras |
| GET | `/api/v1/pedidos/{pedidoId}` | ADMIN, VENDEDOR o el dueno | Detalle del pedido |

Body para crear un pedido:

```json
{
  "usuarioId": 3,
  "cliente": { "nombre": "Camila Soto", "email": "camila@gmail.com" },
  "entrega": { "direccion": "Av. Siempre Viva 123", "comuna": "Maipu" },
  "medioPago": "TARJETA",
  "simularRechazo": false,
  "items": [{ "productoId": 1, "cantidad": 2 }]
}
```

Si el pago se aprueba, el pedido vuelve como `PAGADO` con `folioBoleta`. Si se rechaza,
vuelve como `RECHAZADO` con `motivoRechazo` y el stock se devuelve.

## ms-pagos

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| POST | `/api/v1/pagos/procesar` | interno | Procesa el pago y emite la boleta |
| GET | `/api/v1/pagos/pedido/{pedidoId}` | ADMIN | Pagos de un pedido |
| GET | `/api/v1/pagos/usuario/{usuarioId}` | ADMIN | Pagos de un usuario |
| GET | `/api/v1/pagos/boletas` | ADMIN | Listar boletas |
| GET | `/api/v1/pagos/boletas/{folio}` | ADMIN | Boleta por folio |
| GET | `/api/v1/pagos/boletas/pedido/{pedidoId}` | ADMIN | Boleta de un pedido |

## ms-contenido

| Metodo | Ruta | Rol | Descripcion |
|---|---|---|---|
| GET | `/api/v1/contenido/articulos?incluirBorradores=true` | publico | Listar articulos del blog |
| GET | `/api/v1/contenido/articulos/{slug}` | publico | Articulo por slug |
| POST | `/api/v1/contenido/articulos` | ADMIN | Crear articulo |
| PUT | `/api/v1/contenido/articulos/{id}` | ADMIN | Actualizar articulo |
| DELETE | `/api/v1/contenido/articulos/{id}` | ADMIN | Eliminar articulo |
| GET | `/api/v1/contenido/mensajes` | ADMIN | Listar mensajes de contacto |
| POST | `/api/v1/contenido/mensajes` | publico | Enviar mensaje de contacto |
| DELETE | `/api/v1/contenido/mensajes/{id}` | ADMIN | Eliminar mensaje |

## Codigos de respuesta

| Codigo | Significado |
|---|---|
| 200 | todo bien |
| 201 | se creo el recurso |
| 400 | datos invalidos o incompletos |
| 401 | falta el token o esta vencido |
| 403 | el rol no tiene permiso |
| 404 | no se encontro el recurso |
| 409 | el dato ya existe (run o correo repetido) |

## Swagger

- Gateway (los seis servicios juntos): <http://localhost:8080/swagger-ui.html>
- Directo a un servicio: `http://localhost:{puerto}/swagger-ui/index.html`

Para probar rutas protegidas: hacer login, copiar el `token` y usar **Authorize** en Swagger.
