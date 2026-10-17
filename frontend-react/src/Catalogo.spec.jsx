// Guardar en: frontend-react/src/__tests__/Catalogo.spec.jsx
// Ejecutar con: npm test
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// ====================== AJUSTAR SOLO ESTA SECCION ======================
// 1) Import del componente. Si usan "export default": import Catalogo from '../pages/Catalogo';
//    Si usan export con nombre:                         import { Catalogo } from '../pages/Catalogo';
import Catalogo from '../pages/Catalogo';

// 2) Textos reales que muestra su pagina (copiarlos tal cual se ven en pantalla).
const TEXTO_BOTON_AGREGAR = 'Agregar';          // texto del boton de cada producto
const PLACEHOLDER_BUSCADOR = 'Buscar';          // placeholder del input de busqueda
const PRODUCTO_EXISTENTE = 'Polera';            // texto (o parte) de un producto que SI existe
const BUSQUEDA_QUE_COINCIDE = 'polera';         // lo que se escribe para encontrar ese producto
const PRODUCTO_QUE_NO_COINCIDE = 'Jeans';       // un producto que NO coincide con esa busqueda

// 3) Si el catalogo lee los productos desde localStorage, aqui se cargan antes de cada prueba.
//    Reemplazar por la funcion real de datos iniciales, o dejarlo vacio si los datos estan en un archivo JS.
function prepararDatos() {
  localStorage.clear();
  // Ejemplo: import { inicializarDatos } from '../data/db';  y luego:  inicializarDatos();
}
// ========================================================================

const renderCatalogo = () => render(<MemoryRouter><Catalogo /></MemoryRouter>);

describe('Catalogo', () => {
  beforeEach(prepararDatos);

  it('renderiza los productos (lista)', () => {
    renderCatalogo();
    expect(screen.getAllByText(new RegExp(TEXTO_BOTON_AGREGAR, 'i')).length).toBeGreaterThan(0);
  });

  it('muestra el producto esperado', () => {
    renderCatalogo();
    expect(screen.getAllByText(new RegExp(PRODUCTO_EXISTENTE, 'i')).length).toBeGreaterThan(0);
  });

  it('el estado del buscador cambia cuando el usuario escribe', () => {
    renderCatalogo();
    const input = screen.getByPlaceholderText(new RegExp(PLACEHOLDER_BUSCADOR, 'i'));
    fireEvent.change(input, { target: { value: BUSQUEDA_QUE_COINCIDE } });
    expect(input.value).toBe(BUSQUEDA_QUE_COINCIDE);
  });

  it('filtra la lista al buscar (oculta lo que no coincide)', () => {
    renderCatalogo();
    fireEvent.change(screen.getByPlaceholderText(new RegExp(PLACEHOLDER_BUSCADOR, 'i')), { target: { value: BUSQUEDA_QUE_COINCIDE } });
    expect(screen.queryByText(new RegExp(PRODUCTO_QUE_NO_COINCIDE, 'i'))).toBeNull();
  });

  it('un clic en agregar no rompe la pagina (evento)', () => {
    renderCatalogo();
    const boton = screen.getAllByText(new RegExp(TEXTO_BOTON_AGREGAR, 'i'))[0];
    fireEvent.click(boton);
    expect(screen.getAllByText(new RegExp(TEXTO_BOTON_AGREGAR, 'i')).length).toBeGreaterThan(0);
  });
});
