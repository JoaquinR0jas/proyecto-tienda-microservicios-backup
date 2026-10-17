// Punto de entrada de Karma: carga todos los archivos *.spec.js(x) de esta carpeta.
const contexto = import.meta.webpackContext('./', { recursive: true, regExp: /\.spec\.jsx?$/ });
contexto.keys().forEach(contexto);
