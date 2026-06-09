@echo off
title Build All - Tienda Microservicios
echo ============================================
echo Construyendo todos los modulos...
echo ============================================
echo.

call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo la compilacion.
    echo Revise los errores arriba.
    pause
    exit /b %ERRORLEVEL%
)

echo ============================================
echo Compilacion exitosa!
echo ============================================
echo.
echo Para iniciar el sistema ejecute: start-all.bat
echo.
pause
