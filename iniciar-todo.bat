@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion
title FreshFits - iniciar todo
cd /d "%~dp0"
set "ROOT=%~dp0"

echo ==========================================
echo   FreshFits - levantando todo el proyecto
echo ==========================================
echo.

rem ---------- 1. Compilar si faltan los .jar ----------
if exist "%ROOT%eureka-server\target\eureka-server-1.0.0.jar" goto mysql
if exist "%ROOT%api-gateway\target\api-gateway-1.0.0.jar" goto mysql

echo [1/4] Compilando el backend (solo la primera vez, puede tardar unos minutos)...
call mvnw.cmd -q -DskipTests package
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar. Revisa que Java 21 o superior este instalado.
    pause
    exit /b 1
)
echo Backend compilado.
echo.

:mysql
set "DB_PORT="
echo [2/4] Buscando MySQL...
call :puerto_escuchando 3307
if not errorlevel 1 set "DB_PORT=3307"
if not defined DB_PORT (
    call :puerto_escuchando 3306
    if not errorlevel 1 set "DB_PORT=3306"
)
if defined DB_PORT (
    echo MySQL ya esta corriendo en el puerto !DB_PORT!.
    goto datos
)

rem Buscar el ejecutable de MySQL en las ubicaciones tipicas
set "MYSQL_EXE="
set "MYSQL_INI="
if exist "C:\xampp\mysql\bin\mysqld.exe" (
    set "MYSQL_EXE=C:\xampp\mysql\bin\mysqld.exe"
    set "MYSQL_INI=C:\xampp\mysql\bin\my.ini"
)
if not defined MYSQL_EXE if exist "D:\xampp\mysql\bin\mysqld.exe" (
    set "MYSQL_EXE=D:\xampp\mysql\bin\mysqld.exe"
    set "MYSQL_INI=D:\xampp\mysql\bin\my.ini"
)
if not defined MYSQL_EXE for /f "delims=" %%M in ('where mysqld 2^>nul') do if not defined MYSQL_EXE set "MYSQL_EXE=%%M"

if not defined MYSQL_EXE (
    echo.
    echo ERROR: no encontre MySQL.
    echo Instala XAMPP ^(https://www.apachefriends.org^) o MySQL, abre el panel
    echo y dale Start a MySQL. Despues vuelve a ejecutar este archivo.
    pause
    exit /b 1
)

echo Iniciando MySQL...
if defined MYSQL_INI if exist "!MYSQL_INI!" (
    start "MySQL" /min "!MYSQL_EXE!" --defaults-file="!MYSQL_INI!" --console
) else (
    start "MySQL" /min "!MYSQL_EXE!" --console
)

rem Esperar hasta 90 segundos a que abra el puerto (3307 o 3306)
set /a INTENTOS=0
:esperar_mysql
if defined DB_PORT goto datos
call :puerto_escuchando 3307
if not errorlevel 1 set "DB_PORT=3307"
call :puerto_escuchando 3306
if not errorlevel 1 set "DB_PORT=3306"
if defined DB_PORT goto datos
set /a INTENTOS+=1
if !INTENTOS! GEQ 90 (
    echo.
    echo ERROR: MySQL no respondio. Revisalo desde el panel de XAMPP.
    pause
    exit /b 1
)
ping -n 2 127.0.0.1 >nul
goto esperar_mysql

:datos
echo Base de datos en el puerto !DB_PORT!.
echo.

rem ---------- 3. Arrancar microservicios ----------
echo [3/4] Arrancando microservicios...

call :arrancar "Eureka 8761" eureka-server 8761
call :esperar_puerto 8761

call :arrancar "Usuarios 8090" ms-usuarios 8090
call :arrancar "Catalogo 8081" ms-catalogo 8081
call :arrancar "Contenido 8083" ms-contenido 8083
call :arrancar "Pagos 8086" ms-pagos 8086
call :arrancar "Pedidos 8087" ms-pedidos 8087
call :arrancar "Auth 8091" auth-service 8091

rem El gateway va al final: necesita que los servicios esten registrados
call :arrancar "Gateway 8080" api-gateway 8080
call :esperar_puerto 8080

echo.
echo Servicios arriba. Esperando 15 segundos a que Eureka los registre...
ping -n 16 127.0.0.1 >nul

rem ---------- 4. Frontend ----------
echo [4/4] Abriendo el frontend...
where npm >nul 2>&1
if errorlevel 1 goto sin_node

if exist "%ROOT%frontend-react\node_modules" goto front_listo
echo Instalando dependencias del frontend, solo la primera vez...
start "Frontend 5173" /d "%ROOT%frontend-react" cmd /k npm install ^&^& npm run dev
goto front_arrancado

:front_listo
start "Frontend 5173" /d "%ROOT%frontend-react" cmd /k npm run dev

:front_arrancado
call :esperar_puerto 5173
start "" http://localhost:5173
goto resumen

:sin_node
echo.
echo AVISO: no encontre Node.js. Instalalo desde https://nodejs.org
echo El backend quedo funcionando, pero la tienda no se puede abrir.
pause
exit /b 1

:resumen
echo.
echo ==========================================
echo   Listo. Ventanas abiertas:
echo   Tienda   http://localhost:5173
echo   Swagger  http://localhost:8080/swagger-ui.html
echo   Eureka   http://localhost:8761
echo.
echo   Usuarios de prueba:
echo   admin@gmail.com     / admin123
echo   vendedor@gmail.com  / vend1234
echo   camila@gmail.com    / cliente1
echo.
echo   Para apagar todo: detener-todo.bat
echo ==========================================
echo.
pause
exit /b 0

rem ---------- subrutinas ----------

:arrancar
rem %1 = titulo de la ventana, %2 = carpeta del modulo, %3 = puerto
call :puerto_escuchando %3
if not errorlevel 1 (
    echo  - %2 ya estaba corriendo en el puerto %3
    exit /b 0
)
echo  - %2 ^(puerto %3^)
start "%~1" /d "%ROOT%%2" cmd /k java -jar target\%2-1.0.0.jar
exit /b 0

:puerto_escuchando
netstat -ano | findstr ":%~1 " | findstr "LISTENING" >nul 2>&1
exit /b %errorlevel%

:esperar_puerto
set /a ESPERA=0
:esperar_puerto_bucle
call :puerto_escuchando %~1
if not errorlevel 1 exit /b 0
set /a ESPERA+=1
if !ESPERA! GEQ 90 (
    echo    AVISO: el puerto %~1 no respondio en 90 segundos.
    exit /b 1
)
ping -n 2 127.0.0.1 >nul
goto esperar_puerto_bucle
