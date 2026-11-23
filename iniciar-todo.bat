@echo off
chcp 65001 >nul
setlocal
title FreshFits - iniciar todo
cd /d "%~dp0"
set "ROOT=%~dp0"
set "MYSQL_EXE=C:\xampp\mysql\bin\mysqld.exe"
set "MYSQL_INI=C:\xampp\mysql\bin\my.ini"
set "MYSQL_ADMIN=C:\xampp\mysql\bin\mysqladmin.exe"

echo ==========================================
echo   FreshFits - levantando todo el proyecto
echo ==========================================
echo.

rem ---------- 1. Comprobar que los .jar esten compilados ----------
if not exist "%ROOT%eureka-server\target\eureka-server-1.0.0.jar" goto compilar
goto mysql

:compilar
echo [1/3] Compilando el backend (solo la primera vez, puede tardar)...
call mvnw.cmd -q -DskipTests package
if errorlevel 1 (
    echo.
    echo ERROR: no se pudo compilar el backend.
    pause
    exit /b 1
)
echo Backend compilado.
echo.

:mysql
echo [2/3] Revisando MySQL...
netstat -ano | findstr ":3307" | findstr "LISTENING" >nul
if not errorlevel 1 (
    echo MySQL ya esta corriendo en el puerto 3307.
    goto arrancar
)

if not exist "%MYSQL_EXE%" (
    echo ERROR: no se encontro XAMPP en C:\xampp\mysql\bin.
    echo Abre XAMPP y dale Start a MySQL, o corrige la ruta en este archivo.
    pause
    exit /b 1
)

echo Iniciando MySQL de XAMPP...
start "MySQL XAMPP" /min "%MYSQL_EXE%" --defaults-file="%MYSQL_INI%" --console
set /a INTENTOS=0
:esperar_mysql
"%MYSQL_ADMIN%" -u root -h 127.0.0.1 -P 3307 ping >nul 2>&1
if not errorlevel 1 goto mysql_listo
set /a INTENTOS+=1
if %INTENTOS% GEQ 40 goto mysql_error
ping -n 2 127.0.0.1 >nul
goto esperar_mysql

:mysql_error
echo.
echo ERROR: MySQL no respondio en el puerto 3307.
echo Revisalo desde el panel de XAMPP.
pause
exit /b 1

:mysql_listo
echo MySQL listo.
echo.

:arrancar
echo [3/3] Arrancando microservicios...
echo.

echo  - Eureka (8761)
start "Eureka 8761" cmd /k "cd /d "%ROOT%" && java -jar eureka-server\target\eureka-server-1.0.0.jar"
echo    esperando a que Eureka este listo...
ping -n 19 127.0.0.1 >nul

echo  - Usuarios (8090)
start "Usuarios 8090" cmd /k "cd /d "%ROOT%" && java -jar ms-usuarios\target\ms-usuarios-1.0.0.jar"
ping -n 3 127.0.0.1 >nul

echo  - Catalogo (8081)
start "Catalogo 8081" cmd /k "cd /d "%ROOT%" && java -jar ms-catalogo\target\ms-catalogo-1.0.0.jar"
ping -n 3 127.0.0.1 >nul

echo  - Contenido (8083)
start "Contenido 8083" cmd /k "cd /d "%ROOT%" && java -jar ms-contenido\target\ms-contenido-1.0.0.jar"
ping -n 3 127.0.0.1 >nul

echo  - Pagos (8086)
start "Pagos 8086" cmd /k "cd /d "%ROOT%" && java -jar ms-pagos\target\ms-pagos-1.0.0.jar"
ping -n 3 127.0.0.1 >nul

echo  - Pedidos (8087)
start "Pedidos 8087" cmd /k "cd /d "%ROOT%" && java -jar ms-pedidos\target\ms-pedidos-1.0.0.jar"
ping -n 3 127.0.0.1 >nul

echo  - Auth (8091)
start "Auth 8091" cmd /k "cd /d "%ROOT%" && java -jar auth-service\target\auth-service-1.0.0.jar"
ping -n 5 127.0.0.1 >nul

echo  - Gateway (8080)
start "Gateway 8080" cmd /k "cd /d "%ROOT%" && java -jar api-gateway\target\api-gateway-1.0.0.jar"
ping -n 9 127.0.0.1 >nul

echo.
echo Abriendo el frontend...
if not exist "%ROOT%frontend-react\node_modules" (
    start "Frontend 5173" cmd /k "cd /d "%ROOT%frontend-react" && npm install && npm run dev"
) else (
    start "Frontend 5173" cmd /k "cd /d "%ROOT%frontend-react" && npm run dev"
)

ping -n 7 127.0.0.1 >nul
start "" http://localhost:5173

echo.
echo ==========================================
echo   Listo. Se abrieron las ventanas:
echo   Eureka   http://localhost:8761
echo   Swagger  http://localhost:8080/swagger-ui.html
echo   Tienda   http://localhost:5173
echo.
echo   Usuarios de prueba:
echo   admin@gmail.com     / admin123
echo   vendedor@gmail.com  / vend1234
echo   camila@gmail.com    / cliente1
echo.
echo   Para apagar todo usa detener-todo.bat
echo ==========================================
echo.
pause
