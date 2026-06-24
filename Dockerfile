FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS eureka-server
COPY --from=build /app/eureka-server/target/eureka-server-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS auth-service
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-usuarios
COPY --from=build /app/ms-usuarios/target/ms-usuarios-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-catalogo
COPY --from=build /app/ms-catalogo/target/ms-catalogo-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-inventario
COPY --from=build /app/ms-inventario/target/ms-inventario-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-carrito
COPY --from=build /app/ms-carrito/target/ms-carrito-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-pedidos
COPY --from=build /app/ms-pedidos/target/ms-pedidos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-pagos
COPY --from=build /app/ms-pagos/target/ms-pagos-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-envios
COPY --from=build /app/ms-envios/target/ms-envios-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-notificaciones
COPY --from=build /app/ms-notificaciones/target/ms-notificaciones-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-marketing
COPY --from=build /app/ms-marketing/target/ms-marketing-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS ms-resenas
COPY --from=build /app/ms-resenas/target/ms-resenas-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM eclipse-temurin:21-jre AS api-gateway
COPY --from=build /app/api-gateway/target/api-gateway-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
