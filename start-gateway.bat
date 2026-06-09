@echo off
title Start Gateway - Tienda Microservicios
echo ============================================
echo Iniciando API Gateway (puerto 8080)...
echo ============================================
echo.

start "API-Gateway" cmd /c "mvnw.cmd spring-boot:run -pl api-gateway"

echo Gateway iniciado en ventana separada.
echo.
echo Swagger UI: http://localhost:8080/swagger-ui/index.html
echo.
pause
