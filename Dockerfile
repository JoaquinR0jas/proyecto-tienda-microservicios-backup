FROM eclipse-temurin:21-jre AS eureka-server
COPY eureka-server/target/eureka-server-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS auth-service
COPY auth-service/target/auth-service-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-usuarios
COPY ms-usuarios/target/ms-usuarios-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-catalogo
COPY ms-catalogo/target/ms-catalogo-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-inventario
COPY ms-inventario/target/ms-inventario-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-carrito
COPY ms-carrito/target/ms-carrito-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-pedidos
COPY ms-pedidos/target/ms-pedidos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-pagos
COPY ms-pagos/target/ms-pagos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-envios
COPY ms-envios/target/ms-envios-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-notificaciones
COPY ms-notificaciones/target/ms-notificaciones-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-marketing
COPY ms-marketing/target/ms-marketing-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-resenas
COPY ms-resenas/target/ms-resenas-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS api-gateway
COPY api-gateway/target/api-gateway-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
