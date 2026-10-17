// Babel se usa SOLO para las pruebas (Karma/webpack y el runner de Node).
// Vite compila la aplicacion con su propio plugin de React.
module.exports = (api) => {
  const esNode = process.env.BABEL_TARGET === 'node';
  const conCobertura = process.env.COVERAGE === '1' || process.env.BABEL_ENV === 'coverage';
  api.cache.never();
  return {
    presets: [
      ['@babel/preset-env', esNode ? { targets: { node: 'current' } } : {}],
      ['@babel/preset-react', { runtime: 'automatic' }]
    ],
    plugins: conCobertura
      ? [['istanbul', { exclude: ['**/__tests__/**', '**/*.spec.*', 'src/main.jsx'] }]]
      : []
  };
};
