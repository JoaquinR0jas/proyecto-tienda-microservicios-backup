// Karma + Jasmine para pruebas unitarias de componentes React (requisito Evaluacion 2).
// Ejecutar:  npm test            (una sola vez)
//            npm run test:watch  (modo observador)
//            npm run test:coverage (con informe de cobertura en /coverage)
const conCobertura = process.env.COVERAGE === '1';

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', 'webpack'],
    plugins: [
      'karma-jasmine', 'karma-webpack', 'karma-chrome-launcher',
      'karma-sourcemap-loader', 'karma-spec-reporter', 'karma-coverage'
    ],
    files: [{ pattern: 'src/__tests__/index.js', watched: false }],
    preprocessors: { 'src/__tests__/index.js': ['webpack', 'sourcemap'] },
    webpack: {
      mode: 'development',
      devtool: 'inline-source-map',
      resolve: { extensions: ['.js', '.jsx'] },
      module: {
        rules: [
          {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            use: {
              loader: 'babel-loader',
              options: {
                babelrc: false,
                configFile: false,
                presets: ['@babel/preset-env', ['@babel/preset-react', { runtime: 'automatic' }]],
                plugins: conCobertura ? [['istanbul', { exclude: ['**/__tests__/**'] }]] : []
              }
            }
          },
          { test: /\.css$/, use: ['style-loader', 'css-loader'] },
          { test: /\.(png|jpe?g|gif|svg|webp|woff2?|ttf|eot)$/i, type: 'asset/resource' }
        ]
      },
      performance: { hints: false }
    },
    webpackMiddleware: { stats: 'errors-only' },
    reporters: conCobertura ? ['spec', 'coverage'] : ['spec'],
    coverageReporter: {
      dir: 'coverage',
      reporters: [{ type: 'html', subdir: 'html' }, { type: 'text-summary' }, { type: 'lcovonly', subdir: '.' }]
    },
    browsers: ['ChromeHeadlessNoSandbox'],
    customLaunchers: {
      ChromeHeadlessNoSandbox: { base: 'ChromeHeadless', flags: ['--no-sandbox'] }
    },
    client: { jasmine: { random: false } },
    singleRun: false,
    restartOnFileChange: true
  });
};
