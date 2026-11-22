# Guia de instalacion y ejecucion

Esta guia es para levantar el proyecto en un computador limpio. Para el uso diario de la
pagina (comprar, administrar productos, revisar boletas) esta el **Manual de Usuario** que se
entrega junto con el proyecto.

## 1. Programas que hay que tener

| Programa | Version | Para que |
|---|---|---|
| Java JDK | 21 o superior | compilar y correr Spring Boot |
| Node.js | 18 o superior | compilar y correr React |
| MySQL | 8 | base de datos |
| Docker Desktop | opcional | levantar MySQL (y todo el backend) sin instalarlo a mano |

Para revisar las versiones:

```bash
java -version
node -v
npm -v
```

## 2. Base de datos

### Opcion A: con Docker (recomendada)

```bash
docker compose up -d mysql
```

El contenedor se llama `tienda-mysql`, escucha en el puerto **3307** y crea las bases la
primera vez. Los scripts de `mysql-init/` se ejecutan solos.

### Opcion B: MySQL instalado en el computador

1. Crear las bases: `ms_usuarios`, `ms_catalogo`, `ms_pedidos`, `ms_pagos` y `ms_contenido`.
2. Revisar en cada `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/ms_usuarios?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

Si el MySQL local usa el puerto 3306 o tiene clave, se cambia ahi. El
`ddl-auto=update` crea las tablas y los `DatosIniciales` cargan los datos de prueba.

## 3. Backend

Hay que partir por Eureka, porque los demas servicios se registran ahi. En terminales
separadas, desde la raiz del proyecto:

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

En Windows: `mvnw.cmd spring-boot:run -pl eureka-server`, y asi con cada uno.

Para comprobar que quedaron arriba:

- Eureka: <http://localhost:8761> (deben aparecer los servicios registrados)
- Gateway: <http://localhost:8080/swagger-ui.html>

### Todo con Docker

```bash
docker compose up --build
```

Levanta MySQL, Eureka, gateway, auth y los cinco microservicios. La primera vez demora
bastante porque compila cada imagen.

## 4. Frontend

```bash
cd frontend-react
npm install
npm run dev
```

Queda en <http://localhost:5173>. Para probar la version compilada:

```bash
npm run build
npm run preview
```

`frontend-react/src/config.js` toma `VITE_API_URL` si existe; si no, usa
`http://localhost:8080`. Se puede crear un archivo `.env` en `frontend-react/`:

```
VITE_API_URL=http://localhost:8080
```

## 5. Usuarios de prueba

| Rol | Correo | Contrasena |
|---|---|---|
| Administrador | admin@gmail.com | admin123 |
| Vendedor | vendedor@gmail.com | vend1234 |
| Cliente | camila@gmail.com | cliente1 |

## 6. Pruebas

```bash
./mvnw test
```

Corre las 124 pruebas del backend. La cobertura queda en
`<modulo>/target/site/jacoco/index.html`.

Para el frontend, con Chrome instalado:

```bash
cd frontend-react
npm test
```

## 7. Problemas comunes

| Problema | Causa | Solucion |
|---|---|---|
| `Communications link failure` | MySQL apagado o en otro puerto | revisar que este en 3307 y que la URL coincida |
| El gateway devuelve 503 | el servicio no esta en Eureka | esperar unos segundos o revisar el orden de arranque |
| El frontend no carga datos | el backend no esta en 8080 | revisar `VITE_API_URL` |
| 401 al probar en Swagger | falta el token | hacer login y usar el boton Authorize |
| 403 con el token del admin | el rol del token no corresponde | revisar el rol del usuario en la base |
| `Port 8080 already in use` | hay otro proceso en ese puerto | cerrarlo o cambiar `server.port` |

## 8. Orden recomendado para una presentacion

1. Mostrar Eureka con los servicios registrados.
2. Abrir la tienda y navegar productos y ofertas.
3. Registrarse o entrar como cliente y hacer una compra.
4. Mostrar el rechazo de pago simulado y despues la compra aprobada con boleta.
5. Entrar como vendedor: solo productos y ordenes.
6. Entrar como admin: usuarios, categorias, reportes y mensajes.
7. Abrir Swagger, hacer login y probar un endpoint con el boton Authorize.
8. Mostrar `./mvnw test` y el reporte de cobertura.
