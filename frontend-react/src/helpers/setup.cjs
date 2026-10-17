// Runner alternativo en Node (jsdom) para correr las MISMAS pruebas Jasmine sin Chrome.
// Karma sigue siendo el runner oficial de la evaluacion (npm test).
process.env.BABEL_TARGET = 'node';
require('@babel/register')({ extensions: ['.js', '.jsx'], ignore: [/node_modules/], cache: false });

const { JSDOM } = require('jsdom');
const dom = new JSDOM('<!doctype html><html><body></body></html>', { url: 'http://localhost/', pretendToBeVisual: true });
const { window } = dom;
global.window = window;
global.document = window.document;
Object.defineProperty(global, 'navigator', { value: window.navigator, configurable: true });
global.localStorage = window.localStorage;
global.HTMLElement = window.HTMLElement;
global.Node = window.Node;
global.Event = window.Event;
global.MouseEvent = window.MouseEvent;
global.KeyboardEvent = window.KeyboardEvent;
global.IS_REACT_ACT_ENVIRONMENT = true;
global.requestAnimationFrame = (cb) => setTimeout(cb, 0);
global.cancelAnimationFrame = (id) => clearTimeout(id);
window.scrollTo = () => {};
window.print = () => {};

if (process.env.BABEL_ENV === 'coverage') {
  const fs = require('fs');
  process.on('exit', () => {
    fs.mkdirSync('.nyc_output', { recursive: true });
    fs.writeFileSync('.nyc_output/out.json', JSON.stringify(global.__coverage__ || {}));
  });
}
