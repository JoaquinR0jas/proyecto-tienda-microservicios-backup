# Contexto — Proyecto Tienda Microservicios

## Estado: COMPLETO ✅

## Proyecto
`C:\Users\juako\Desktop\tienda-parent\`
Repositorio backup: `https://github.com/JoaquinR0jas/proyecto-tienda-microservicios-backup.git`

## Resumen de lo realizado
- Migrados 13 microservicios Spring Boot a Maven multi-módulo (`tienda-parent/pom.xml`)
- Swagger/OpenAPI (Springdoc) en todos los módulos
- Swagger centralizado en Gateway: `http://localhost:8080/swagger-ui/index.html`
- 8 scripts `.bat` de ejecución (build-all, start-eureka, start-services, start-gateway, start-all, stop-all, check-health, start-databases)
- Pruebas unitarias (178 tests, Mockito + @WebMvcTest, sin dependencia de BD)
- `.\mvnw.cmd clean install` compila y pasa pruebas
- `.gitignore` actualizado excluyendo `HELP.md`, `*.pdf`, `*.code-workspace`, `.vscode/`
- Limpieza de basura Spring Initializr (HELP.md, README.pdf, .mvn wrapper duplicado)
- README.md + GUIA_USO.md en `docs/` + copia en escritorio
- **Fix JARs no ejecutables**: Movido `spring-boot-maven-plugin` de `pluginManagement` a `build/plugins` en `pom.xml` padre
- **Fix CORS Swagger**: Agregado bean `OpenAPI` en cada microservicio para que apunten a `http://localhost:8080` (Gateway)
- **Fix ID auto-generado en POST**: Agregado `entity.setId(null)` en 5 controladores (ms-catalogo, ms-usuarios, ms-inventario, ms-marketing, ms-resenas) para ignorar IDs que el cliente envíe y evitar error 500

## Documentos de referencia
- `C:\Users\juako\Desktop\FULLSTACK\` — documentos originales de las etapas
- `C:\Users\juako\Desktop\tienda-parent\docs\GUIA_USO.md` — guía completa de uso con endpoints y ejemplos
- `C:\Users\juako\Desktop\tienda-parent\docs\endpoints.md` — puertos y rutas
- `C:\Users\juako\Desktop\tienda-parent\docs\bd-general.sql` — creación de BBDD

## Configuración técnica
| Elemento | Detalle |
|----------|---------|
| Java | 21 (compila con release 21, JDK 24 instalado) |
| Maven | `mvnw.cmd` en raíz |
| MySQL | `localhost:3307`, root, sin pass (XAMPP) |
| Eureka | `http://localhost:8761` |
| Gateway | `http://localhost:8080` |
| 11 BBDD | `ms_usuarios`, `ms_catalogo`, `ms_carrito`, `ms_inventario`, `ms_pedidos`, `ms_pagos`, `ms_envios`, `ms_notificaciones`, `ms_marketing`, `ms_resenas`, `ms_auth` |

## Puertos de microservicios
| Servicio | Puerto |
|----------|--------|
| eureka-server | 8761 |
| api-gateway | 8080 |
| auth-service | 8091 |
| ms-usuarios | 8090 |
| ms-catalogo | 8081 |
| ms-carrito | 8082 |
| ms-inventario | 8089 |
| ms-pedidos | 8087 |
| ms-pagos | 8086 |
| ms-envios | 8083 |
| ms-notificaciones | 8085 |
| ms-marketing | 8084 |
| ms-resenas | 8088 |

## Endpoints clave
- Login: `POST /auth/login` (body: `{"email","password"}` → devuelve JWT)
- Usuarios: `POST /api/usuarios`
- Catálogo: `POST /api/catalogo/productos`
- Inventario: `POST /api/inventario/guardar`
- Carrito: `POST /api/carrito/{usuarioId}/agregar`
- Carrito estado: `PATCH /api/carrito/{carritoId}/estado`
- **Pedido: `POST /api/pedidos/crear?usuarioId=1&carritoId=1` (query params, NO body)**
- Pago: `POST /api/pagos/procesar`
- Envío: `POST /api/envios/crear`
- Notificación: `POST /api/notificaciones/enviar`

## Paso a paso para continuar
1. Iniciar MySQL: `.\start-databases.bat`
2. Iniciar todo: `.\start-all.bat`
3. Swagger: `http://localhost:8080/swagger-ui/index.html`
4. Si hay cambios: `.\mvnw.cmd clean install` (verificar que compile)
5. Commit: `git add -A && git commit -m "mensaje" && git push origin main`

## Advertencias
- `POST /api/pedidos/crear` usa **query params**, NO body JSON
- Carrito debe estar `CONFIRMADO` antes de crear pedido
- Test no requieren MySQL (usan Mockito puro)
