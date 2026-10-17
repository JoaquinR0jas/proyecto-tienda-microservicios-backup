// Limpia carpetas de cobertura (funciona igual en Windows, Mac y Linux).
const fs = require('fs');
['.nyc_output', 'coverage-node'].forEach((d) => fs.rmSync(d, { recursive: true, force: true }));
fs.mkdirSync('.nyc_output', { recursive: true });
