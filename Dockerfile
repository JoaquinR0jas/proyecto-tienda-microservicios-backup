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

FROM eclipse-temurin:21-jre AS ms-pedidos
COPY ms-pedidos/target/ms-pedidos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-pagos
COPY ms-pagos/target/ms-pagos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-contenido
COPY ms-contenido/target/ms-contenido-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS api-gateway
COPY api-gateway/target/api-gateway-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
