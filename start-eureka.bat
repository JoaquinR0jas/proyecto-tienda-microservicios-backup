@echo off
title Start Eureka - Tienda Microservicios
echo ============================================
echo Iniciando Eureka Server (puerto 8761)...
echo ============================================
echo.

start "Eureka-Server" cmd /c "mvnw.cmd spring-boot:run -pl eureka-server"

echo Esperando a que Eureka se inicie...
:wait-eureka
timeout /t 5 /nobreak >nul
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8761' -TimeoutSec 5; if ($r.StatusCode -eq 200) { exit 0 } } catch {}; exit 1" >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Esperando a Eureka...
    goto wait-eureka
)

echo Eureka Server listo en http://localhost:8761
pause
