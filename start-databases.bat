@echo off
title Crear Bases de Datos - Tienda Microservicios
echo ============================================
echo NOTA: Los servicios usan createDatabaseIfNotExist=true,
echo por lo que las BBDD se crean automaticamente al iniciar.
echo.
echo Este script es solo para creacion explicita via SQL.
echo ============================================
echo.

mysql -h localhost -P 3307 -u root < docs\bd-general.sql 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo conectar a MySQL en localhost:3307
    echo Asegurese de que MySQL este corriendo.
    echo.
    echo Comando alternativo: mysql -h localhost -P 3307 -u root -p ^< docs\bd-general.sql
    pause
    exit /b 1
)

echo Bases de datos creadas correctamente.
pause
