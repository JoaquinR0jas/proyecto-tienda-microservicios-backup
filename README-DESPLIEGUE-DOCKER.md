# Puesta en Marcha con Docker — Tienda Microservicios

**DSY1103 — Desarrollo FullStack I**

Este documento explica cómo ejecutar el ecosistema completo de **Tienda Microservicios** utilizando **Docker** y **Docker Compose**.

Con Docker, todos los componentes (MySQL, Eureka, 13 microservicios y API Gateway) se ejecutan en contenedores independientes y se comunican mediante una red interna, sin necesidad de instalar XAMPP, Java ni Maven localmente.

---

## 1. Descripción General

**Tienda Microservicios** es un e-commerce de ropa basado en 13 microservicios Spring Boot.

La puesta en marcha con Docker permite levantar todo el ecosistema con un solo comando:

```bash
docker compose up -d --build
```

---

## 2. Requisito Obligatorio: Docker Desktop Debe Estar Abierto

Antes de ejecutar cualquier comando, Docker Desktop debe estar abierto y funcionando.

```bash
docker --version
# Resultado esperado: Docker version 27.0.0 o superior

docker compose version
# Resultado esperado: Docker Compose version v2.0.0 o superior
```

Si aparece `Cannot connect to the Docker daemon`, abrir Docker Desktop y esperar a que muestre el estado verde.

---

## 3. Componentes del Sistema (14 contenedores)

| Componente | Descripción | Puerto Externo |
|------------|-------------|---------------:|
| **mysql** | Base de datos MySQL 8.0 | 3307 |
| **eureka-server** | Servidor de descubrimiento Eureka | 8761 |
| **api-gateway** | API Gateway centralizado + Swagger | 8080 |
| **auth-service** | Autenticación JWT | 8091 |
| **ms-usuarios** | Microservicio de usuarios | 8090 |
| **ms-catalogo** | Microservicio de catálogo/productos | 8081 |
| **ms-carrito** | Microservicio de carrito de compras | 8082 |
| **ms-inventario** | Microservicio de inventario/stock | 8089 |
| **ms-pedidos** | Microservicio de pedidos | 8087 |
| **ms-pagos** | Microservicio de pagos | 8086 |
| **ms-envios** | Microservicio de envíos | 8083 |
| **ms-notificaciones** | Microservicio de notificaciones | 8085 |
| **ms-marketing** | Microservicio de marketing/promociones | 8084 |
| **ms-resenas** | Microservicio de reseñas | 8088 |

---

## 4. Orden Lógico de Arranque

```
1. mysql                 ← Inicia primero (salud verificada con healthcheck)
2. eureka-server         ← Inicia después (servicios necesitan registrarse)
3. auth-service
4. ms-usuarios
5. ms-catalogo
6. ms-carrito
7. ms-inventario
8. ms-pedidos
9. ms-pagos
10. ms-envios
11. ms-notificaciones
12. ms-marketing
13. ms-resenas
14. api-gateway           ← Inicia último (cuando todos los servicios están UP)
```

---

## 5. Levantar el Sistema

Ubicarse en la raíz del proyecto:

```bash
cd C:\Users\juako\Desktop\tienda-parent
```

Ejecutar:

```bash
docker compose up -d --build
```

La opción `-d` ejecuta los contenedores en segundo plano (detached mode).

**Tiempo esperado:** 30-120 segundos (depende de la máquina).

---

## 6. Verificar que Todo Funciona

### Estado de contenedores

```bash
docker compose ps
```

Resultado esperado: los 14 contenedores en estado `running`. MySQL debe mostrar `healthy`.

### Eureka Server

```
http://localhost:8761
```

Deben aparecer los 12 servicios registrados (Gateway + 11 microservicios de negocio):

```
API-GATEWAY       (1 instance UP)
AUTH-SERVICE      (1 instance UP)
MS-USUARIOS       (1 instance UP)
MS-CATALOGO       (1 instance UP)
MS-CARRITO        (1 instance UP)
MS-INVENTARIO     (1 instance UP)
MS-PEDIDOS        (1 instance UP)
MS-PAGOS          (1 instance UP)
MS-ENVIOS         (1 instance UP)
MS-NOTIFICACIONES (1 instance UP)
MS-MARKETING      (1 instance UP)
MS-RESENAS        (1 instance UP)
```

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## 7. Revisar Logs

```bash
# Todos los servicios
docker compose logs -f

# Un servicio específico
docker compose logs -f eureka-server
docker compose logs -f ms-usuarios
docker compose logs -f api-gateway
```

Para salir de los logs: `Ctrl + C` (no detiene los contenedores).

---

## 8. URLs de Acceso

| Componente | URL |
|------------|-----|
| Eureka Server | http://localhost:8761 |
| API Gateway (Swagger) | http://localhost:8080/swagger-ui/index.html |
| API Gateway | http://localhost:8080 |
| MySQL | localhost:3307 (root / root) |

---

## 9. Persistencia de Datos MySQL

El sistema usa un volumen Docker para MySQL:

```yaml
volumes:
  mysql-data:
```

Los datos persisten aunque se reinicien los contenedores o el computador.

**Para eliminar datos (reset total):**

```bash
docker compose down -v
```

> **Advertencia:** `down -v` elimina TODOS los datos. Usar solo cuando se quiera reiniciar desde cero.

---

## 10. Comandos Principales

| Acción | Comando |
|--------|---------|
| Levantar todo | `docker compose up -d --build` |
| Ver estado | `docker compose ps` |
| Ver logs | `docker compose logs -f` |
| Detener (mantener datos) | `docker compose down` |
| Detener y limpiar datos | `docker compose down -v` |
| Reiniciar | `docker compose down && docker compose up -d` |
| Ver CPU/RAM | `docker stats` |

---

## 11. Errores Comunes

### "Cannot connect to the Docker daemon"

Docker Desktop no está abierto.

**Solución:** Abrir Docker Desktop y esperar a que esté listo.

### "Port already in use"

Otro proceso usa el puerto (ej: 8080).

**Solución:** Detener el otro proceso o cambiar el puerto en `docker-compose.yml`.

### Microservicio no aparece en Eureka

El servicio apunta a `localhost:8761` en lugar del nombre del contenedor.

**Solución:** Verificar que use `eureka-server:8761` (no `localhost`) en la property de Docker:

```properties
eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
```

### "Unknown database"

Las bases de datos no se crearon porque el volumen MySQL ya existía.

**Solución:**
```bash
docker compose down -v
docker compose up -d
```

### Contenedor sale con Exit Code 1

El microservicio no pudo conectar a MySQL o Eureka.

**Solución:** Revisar logs: `docker compose logs -f <nombre-servicio>`

---

## 12. Configuración Docker Clave

### Límites de memoria

Cada servicio Java tiene límite de 256M con `-Xmx96m` para evitar OOM en PCs con poca RAM:

```yaml
deploy:
  resources:
    limits:
      memory: 256M
environment:
  JAVA_TOOL_OPTIONS: "-Xmx96m"
```

MySQL tiene 384M:

```yaml
deploy:
  resources:
    limits:
      memory: 384M
```

**Memoria total usada:** ~3.5 GiB

### Healthcheck MySQL

```yaml
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
  interval: 15s
  timeout: 5s
  retries: 10
  start_period: 30s
```

### Red interna

Todos los servicios se comunican por la red interna de Docker Compose (`tienda-parent_default`) y usan el nombre del servicio (ej: `mysql:3306`) en lugar de `localhost`.

---

## 13. Estado Final Esperado

```
✓ 14 contenedores levantados
✓ MySQL en estado healthy
✓ Eureka con 12 servicios registrados y UP
✓ API Gateway accesible en http://localhost:8080
✓ Swagger disponible en http://localhost:8080/swagger-ui/index.html
✓ Flujo de compra de 10 pasos funcional
✓ Volumen MySQL configurado para persistencia
✓ Límites de memoria ajustados para estabilidad
```

---

## 14. Resumen Importante

```
► Docker Desktop DEBE estar abierto para que docker compose funcione
► mysql debe estar healthy antes de que los microservicios se inicien
► eureka-server se inicia automáticamente antes que los microservicios (dependencia en docker-compose)
► API Gateway se inicia al final (depende de eureka-server)
► Los microservicios usan eureka-server (no localhost) para registrarse en Eureka
► Los microservicios usan mysql (no localhost:3307) para conectar a BD
► Volumen mysql-data persiste datos aunque los contenedores se detengan
► docker compose down -v elimina datos; usar solo para reset total
► Límites de memoria evitan OOM en PCs con poca RAM
► El ZIP de Drive contiene los JARs pre-compilados para usar con Docker
```

---

**Última actualización:** Junio 2026
**Estado:** Probado y funcional ✅
