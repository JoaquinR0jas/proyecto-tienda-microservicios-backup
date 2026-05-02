# proyecto-tienda-microservicios

--------------------------------------------------------------------------------
🛒 Ecosistema de Microservicios - Tienda FullStack
Este proyecto consiste en una arquitectura distribuida y desacoplada para la gestión de una tienda, utilizando el framework Spring Cloud. El sistema permite la escalabilidad independiente de cada módulo y garantiza un punto de entrada único y seguro.
🏗️ Arquitectura de Infraestructura
El sistema depende de dos pilares fundamentales para el descubrimiento y enrutamiento de servicios:
Eureka Server (eureka-server)
Puerto: 8761
Rol: Actúa como el "Directorio Telefónico". Registra las IPs y puertos de todos los microservicios activos.
Dependencia: Eureka Server.
API Gateway (api-gateway)
Puerto: 8080
Rol: El "Portero". Único punto de acceso para el cliente (Postman/Frontend). Redirige el tráfico mediante rutas dinámicas.
Dependencias: Gateway (Reactive) y Eureka Discovery Client.
📦 Microservicios de Negocio (Base Común)
Todos los servicios (como ms-catalogo o ms-stock) comparten esta estructura base para asegurar consistencia:
🛠️ Dependencias Obligatorias
Spring Web: Para endpoints REST.
Spring Data JPA: Para persistencia.
MySQL Driver: Conexión a DB (XAMPP - Puerto 3307).
Eureka Discovery Client: Registro automático en Eureka.
Lombok: Reducción de código repetitivo (Getters/Setters).
Validation: Validación de datos de entrada (@NotBlank, @NotNull).
OpenFeign: (Solo para servicios que "hablan" con otros, ej: ms-pedidos).
📂 Estructura de Carpetas (Paquetes)
Seguimos el estándar profesional para mantener el código ordenado:
com.tienda.[servicio].controller: Manejo de peticiones HTTP.
com.tienda.[servicio].service: Lógica de negocio central.
com.tienda.[servicio].repository: Interacción con la base de datos (JPA).
com.tienda.[servicio].model: Entidades de datos (tablas).
com.tienda.[servicio].exception: Manejo global de errores (GlobalExceptionHandler).
com.tienda.[servicio].client: Interfaces Feign para comunicación interservicios.
🚦 Ritual de Inicio (Orden Obligatorio)
Para que el sistema se reconozca correctamente, se debe encender en este orden:
MySQL (XAMPP): Servicio activo en puerto 3307.
Eureka Server: Esperar a que el dashboard esté listo.
Microservicios de Negocio: Verificar que aparezcan en estado UP en Eureka.
API Gateway: Ejecutar al final para mapear todas las rutas activas.

--------------------------------------------------------------------------------