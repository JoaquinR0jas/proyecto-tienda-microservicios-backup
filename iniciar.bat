@echo off
echo Iniciando Eureka Server...
start cmd /k "cd eureka-server && mvnw.cmd spring-boot:run"

timeout /t 15

echo Iniciando ms-inventario...
start cmd /k "cd ms-inventario && mvnw.cmd spring-boot:run"

timeout /t 10

echo Iniciando ms-catalogo...
start cmd /k "cd ms-catalogo && mvnw.cmd spring-boot:run"

timeout /t 10

echo Iniciando ms-usuarios...
start cmd /k "cd ms-usuarios && mvnw.cmd spring-boot:run"

timeout /t 10

echo Iniciando auth-service...
start cmd /k "cd auth-service && mvnw.cmd spring-boot:run"

timeout /t 10

echo Iniciando ms-carrito...
start cmd /k "cd ms-carrito && mvnw.cmd spring-boot:run"

timeout /t 10

echo Iniciando api-gateway...
start cmd /k "cd api-gateway && mvnw.cmd spring-boot:run"

echo Todos los servicios iniciados. Verifica en http://localhost:8761
pause