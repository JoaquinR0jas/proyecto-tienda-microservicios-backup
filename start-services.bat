@echo off
title Start Services - Tienda Microservicios
echo ============================================
echo Iniciando microservicios...
echo ============================================
echo.

echo [1/2] Iniciando servicios base (auth, usuarios)...
start "auth-service" cmd /c "mvnw.cmd spring-boot:run -pl auth-service"
start "ms-usuarios" cmd /c "mvnw.cmd spring-boot:run -pl ms-usuarios"
echo Esperando 15 segundos para que los servicios base se registren en Eureka...
timeout /t 15 /nobreak >nul

echo [2/2] Iniciando servicios de negocio...
start "ms-catalogo" cmd /c "mvnw.cmd spring-boot:run -pl ms-catalogo"
start "ms-inventario" cmd /c "mvnw.cmd spring-boot:run -pl ms-inventario"
start "ms-carrito" cmd /c "mvnw.cmd spring-boot:run -pl ms-carrito"
start "ms-pedidos" cmd /c "mvnw.cmd spring-boot:run -pl ms-pedidos"
start "ms-pagos" cmd /c "mvnw.cmd spring-boot:run -pl ms-pagos"
start "ms-envios" cmd /c "mvnw.cmd spring-boot:run -pl ms-envios"
start "ms-notificaciones" cmd /c "mvnw.cmd spring-boot:run -pl ms-notificaciones"
start "ms-marketing" cmd /c "mvnw.cmd spring-boot:run -pl ms-marketing"
start "ms-resenas" cmd /c "mvnw.cmd spring-boot:run -pl ms-resenas"

echo.
echo Todos los servicios iniciados en ventanas separadas.
echo Verifique en Eureka (http://localhost:8761) que esten registrados.
echo Luego inicie el Gateway con: start-gateway.bat
echo.
pause
