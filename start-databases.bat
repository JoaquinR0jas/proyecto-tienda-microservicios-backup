@echo off
title Crear Bases de Datos - Tienda Microservicios
echo ============================================
echo NOTA: Los servicios usan createDatabaseIfNotExist=true,
echo por lo que las BBDD se crean automaticamente al iniciar.
echo.
echo Este script es solo para creacion explicita via SQL.
echo ============================================
echo.

rem Intentar con XAMPP primero, luego con PATH global
set MYSQL_CMD=
if exist "C:\xampp\mysql\bin\mysql.exe" set MYSQL_CMD=C:\xampp\mysql\bin\mysql.exe
if not defined MYSQL_CMD set MYSQL_CMD=mysql

%MYSQL_CMD% -h 127.0.0.1 -P 3307 -u root < docs\bd-general.sql 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: No se pudo conectar a MySQL en localhost:3307
    echo Asegurese de que MySQL este corriendo (XAMPP Control Panel - Start MySQL).
    echo.
    echo Comando alternativo: "C:\xampp\mysql\bin\mysql.exe" -h 127.0.0.1 -P 3307 -u root ^< docs\bd-general.sql
    pause
    exit /b 1
)

echo Bases de datos creadas correctamente.
pause
