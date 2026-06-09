@echo off
title Start All - Tienda Microservicios
echo ============================================
echo    TIENDA MICROSERVICIOS - ARRANQUE COMPLETO
echo ============================================
echo.

REM ---- Step 1: Build ----
echo [1/5] Compilando proyecto...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo la compilacion. Abortando.
    echo Revise los errores arriba.
    pause
    exit /b %ERRORLEVEL%
)
echo Compilacion exitosa!
echo.

REM ---- Step 2: Eureka ----
echo [2/5] Iniciando Eureka Server...
start "Eureka-Server" cmd /c "mvnw.cmd spring-boot:run -pl eureka-server"
echo Esperando a que Eureka responda...
:wait-eureka
timeout /t 5 /nobreak >nul
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8761' -TimeoutSec 5; if ($r.StatusCode -eq 200) { exit 0 } } catch {}; exit 1" >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Esperando a Eureka...
    goto wait-eureka
)
echo Eureka listo en http://localhost:8761
echo.

REM ---- Step 3: Foundation Services ----
echo [3/5] Iniciando servicios base (auth, usuarios)...
start "auth-service" cmd /c "mvnw.cmd spring-boot:run -pl auth-service"
start "ms-usuarios" cmd /c "mvnw.cmd spring-boot:run -pl ms-usuarios"
echo Esperando 20 segundos para registro inicial...
timeout /t 20 /nobreak >nul
echo.

REM ---- Step 4: Business Services ----
echo [4/5] Iniciando servicios de negocio (9 servicios)...
start "ms-catalogo" cmd /c "mvnw.cmd spring-boot:run -pl ms-catalogo"
start "ms-inventario" cmd /c "mvnw.cmd spring-boot:run -pl ms-inventario"
start "ms-carrito" cmd /c "mvnw.cmd spring-boot:run -pl ms-carrito"
start "ms-pedidos" cmd /c "mvnw.cmd spring-boot:run -pl ms-pedidos"
start "ms-pagos" cmd /c "mvnw.cmd spring-boot:run -pl ms-pagos"
start "ms-envios" cmd /c "mvnw.cmd spring-boot:run -pl ms-envios"
start "ms-notificaciones" cmd /c "mvnw.cmd spring-boot:run -pl ms-notificaciones"
start "ms-marketing" cmd /c "mvnw.cmd spring-boot:run -pl ms-marketing"
start "ms-resenas" cmd /c "mvnw.cmd spring-boot:run -pl ms-resenas"
echo Esperando 40 segundos para registro en Eureka...
timeout /t 40 /nobreak >nul
echo.

REM ---- Step 5: Gateway ----
echo [5/5] Iniciando API Gateway...
start "API-Gateway" cmd /c "mvnw.cmd spring-boot:run -pl api-gateway"
timeout /t 10 /nobreak >nul
echo.

echo ============================================
echo  SISTEMA INICIADO
echo ============================================
echo.
echo  Eureka:  http://localhost:8761
echo  Gateway: http://localhost:8080
echo  Swagger: http://localhost:8080/swagger-ui/index.html
echo.
echo  Verifique en Eureka que TODOS los servicios
echo  esten registrados como UP antes de usar.
echo ============================================
echo.
pause
