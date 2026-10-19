const fs = require('fs')

if (!process.env.CHROME_BIN) {
  const rutas = [
    'C:/Program Files/Google/Chrome/Application/chrome.exe',
    'C:/Program Files (x86)/Google/Chrome/Application/chrome.exe',
    'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe'
  ]
  const encontrada = rutas.find(r => fs.existsSync(r))
  if (encontrada) process.env.CHROME_BIN = encontrada
}

module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', 'webpack'],
    files: ['src/**/*.spec.jsx'],
    preprocessors: {
      'src/**/*.spec.jsx': ['webpack']
    },
    webpack: {
      mode: 'development',
      resolve: {
        extensions: ['.js', '.jsx']
      },
      module: {
        rules: [
          {
            test: /\.(js|jsx)$/,
            exclude: /node_modules/,
            use: {
              loader: 'babel-loader',
              options: {
                presets: [
                  ['@babel/preset-env', { targets: { chrome: '100' } }],
                  ['@babel/preset-react', { runtime: 'automatic' }]
                ]
              }
            }
          }
        ]
      }
    },
    reporters: ['progress'],
    browsers: ['ChromeHeadless'],
    singleRun: true,
    autoWatch: false,
    concurrency: 1
  })
}
