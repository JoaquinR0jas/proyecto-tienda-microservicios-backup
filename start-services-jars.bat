@echo off
set DIR=%~dp0

echo Starting auth-service...
start "auth-service" cmd /c "java -jar %DIR%auth-service\target\auth-service-1.0.0.jar"

echo Starting ms-usuarios...
start "ms-usuarios" cmd /c "java -jar %DIR%ms-usuarios\target\ms-usuarios-1.0.0.jar"

echo Starting ms-catalogo...
start "ms-catalogo" cmd /c "java -jar %DIR%ms-catalogo\target\ms-catalogo-1.0.0.jar"

echo Starting ms-inventario...
start "ms-inventario" cmd /c "java -jar %DIR%ms-inventario\target\ms-inventario-1.0.0.jar"

echo Starting ms-carrito...
start "ms-carrito" cmd /c "java -jar %DIR%ms-carrito\target\ms-carrito-1.0.0.jar"

echo Starting ms-pedidos...
start "ms-pedidos" cmd /c "java -jar %DIR%ms-pedidos\target\ms-pedidos-1.0.0.jar"

echo Starting ms-pagos...
start "ms-pagos" cmd /c "java -jar %DIR%ms-pagos\target\ms-pagos-1.0.0.jar"

echo Starting ms-envios...
start "ms-envios" cmd /c "java -jar %DIR%ms-envios\target\ms-envios-1.0.0.jar"

echo Starting ms-notificaciones...
start "ms-notificaciones" cmd /c "java -jar %DIR%ms-notificaciones\target\ms-notificaciones-1.0.0.jar"

echo Starting ms-marketing...
start "ms-marketing" cmd /c "java -jar %DIR%ms-marketing\target\ms-marketing-1.0.0.jar"

echo Starting ms-resenas...
start "ms-resenas" cmd /c "java -jar %DIR%ms-resenas\target\ms-resenas-1.0.0.jar"

echo All services launched!
pause
