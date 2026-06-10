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
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/swagger-ui/index.html' -TimeoutSec 5 -UseBasicParsing; Write-Host '   [OK] Gateway respondiendo (Swagger UI)' -ForegroundColor Green } catch { Write-Host '   [FAIL] Gateway no responde' -ForegroundColor Red }"

echo.
echo 3. Swagger UI (centralizado)...
powershell -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/swagger-ui/index.html' -TimeoutSec 5 -UseBasicParsing; Write-Host '   [OK] Swagger UI accesible en http://localhost:8080/swagger-ui/index.html' -ForegroundColor Green } catch { Write-Host '   [FAIL] Swagger UI no disponible' -ForegroundColor Red }"

echo.
echo 4. Microservicios registrados en Eureka...
powershell -Command "try { $xml = Invoke-WebRequest -Uri 'http://localhost:8761/eureka/apps' -TimeoutSec 5 -UseBasicParsing; $apps = [regex]::Matches($xml.Content, '<application>(.*?)</application>', 'Singleline'); Write-Host '   Servicios encontrados: ' $apps.Count -ForegroundColor Cyan; foreach ($app in $apps) { $name = [regex]::Match($app.Value, '<name>(.*?)</name>').Groups[1].Value; $status = [regex]::Match($app.Value, '<status>(.*?)</status>').Groups[1].Value; Write-Host \"      $name - $status\" -ForegroundColor Green } } catch { Write-Host '   [FAIL] No se pudo consultar Eureka' -ForegroundColor Red }"

echo.
echo ============================================
echo Health check completado.
echo ============================================
echo.
echo Swagger: http://localhost:8080/swagger-ui/index.html
echo Eureka:  http://localhost:8761
echo.
pause
