@echo off
title Health Check - Tienda Microservicios
echo ============================================
echo Verificando estado del sistema...
echo ============================================
echo.

echo 1. Eureka Server (localhost:8761)...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8761' -TimeoutSec 5 -UseBasicParsing; Write-Host '   [OK] Eureka respondiendo' -ForegroundColor Green } catch { Write-Host '   [FAIL] Eureka no responde' -ForegroundColor Red }"

echo.
echo 2. API Gateway (localhost:8080)...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080' -TimeoutSec 5 -UseBasicParsing; Write-Host '   [OK] Gateway respondiendo' -ForegroundColor Green } catch { Write-Host '   [FAIL] Gateway no responde' -ForegroundColor Red }"

echo.
echo 3. Swagger UI...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/swagger-ui/index.html' -TimeoutSec 5 -UseBasicParsing; Write-Host '   [OK] Swagger UI accesible' -ForegroundColor Green } catch { Write-Host '   [FAIL] Swagger UI no disponible' -ForegroundColor Red }"

echo.
echo 4. Microservicios (via Gateway)...
for %%s in (
    "auth-service|http://localhost:8080/auth/validate?token=test"
    "ms-usuarios|http://localhost:8080/api/usuarios/"
    "ms-catalogo|http://localhost:8080/api/catalogo/"
    "ms-inventario|http://localhost:8080/api/inventario/"
    "ms-carrito|http://localhost:8080/api/carrito/"
    "ms-pedidos|http://localhost:8080/api/pedidos/"
    "ms-pagos|http://localhost:8080/api/pagos/"
    "ms-envios|http://localhost:8080/api/envios/"
    "ms-notificaciones|http://localhost:8080/api/notificaciones/"
    "ms-marketing|http://localhost:8080/api/marketing/"
    "ms-resenas|http://localhost:8080/api/resenas/"
) do (
    for /f "tokens=1,2 delims=|" %%a in (%%s) do (
        echo    %%~a...
        powershell -Command "try { $r = Invoke-WebRequest -Uri '%%~b' -TimeoutSec 5 -UseBasicParsing; Write-Host '      [OK]' -ForegroundColor Green } catch { Write-Host '      [--] (puede requerir autenticacion o no responder a GET raiz)' -ForegroundColor Yellow }"
    )
)

echo.
echo ============================================
echo Health check completado.
echo ============================================
pause
