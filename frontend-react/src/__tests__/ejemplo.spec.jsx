// EJEMPLO: una prueba de cada tipo que pide el enunciado (Jasmine + React Testing Library).
// Usa mini-componentes definidos aqui mismo, asi funciona sin depender de tu codigo.
// Para probar TUS componentes: borra los mini-componentes, importa los reales y adapta los textos.
import { useState } from 'react';
import { render, screen, fireEvent } from '@testing-library/react';

// --- Componentes de ejemplo (reemplazar por los reales) ---
const Boton = ({ etiqueta, onClick }) => <button onClick={onClick}>{etiqueta}</button>;
const ListaProductos = ({ productos }) => (
  <ul>{productos.map((p) => <li key={p.id}>{p.nombre}</li>)}</ul>
);
const MensajeError = ({ mensaje }) => (mensaje ? <p role="alert">{mensaje}</p> : null);
function FormularioNombre() {
  const [nombre, setNombre] = useState('');
  return <input aria-label="Nombre" value={nombre} onChange={(e) => setNombre(e.target.value)} />;
}
function Contador() {
  const [n, setN] = useState(0);
  return <button onClick={() => setN(n + 1)}>Clics: {n}</button>;
}

describe('Pruebas de renderizado', () => {
  it('la lista renderiza TODOS los elementos del conjunto de datos', () => {
    const datos = [{ id: 1, nombre: 'Polera' }, { id: 2, nombre: 'Jeans' }, { id: 3, nombre: 'Gorra' }];
    render(<ListaProductos productos={datos} />);
    expect(screen.getAllByRole('listitem').length).toBe(3);
    datos.forEach((d) => expect(screen.getByText(d.nombre)).toBeTruthy());
  });

  it('el mensaje de error solo se muestra cuando hay un error', () => {
    const { rerender } = render(<MensajeError mensaje="" />);
    expect(screen.queryByRole('alert')).toBeNull();
    rerender(<MensajeError mensaje="Algo salio mal" />);
    expect(screen.getByRole('alert').textContent).toBe('Algo salio mal');
  });
});

describe('Pruebas de propiedades (props)', () => {
  it('el boton recibe la etiqueta y la funcion onClick', () => {
    const onClick = jasmine.createSpy('onClick');
    render(<Boton etiqueta="Guardar" onClick={onClick} />);
    expect(screen.getByText('Guardar')).toBeTruthy();
    fireEvent.click(screen.getByText('Guardar'));
    expect(onClick).toHaveBeenCalledTimes(1);
  });
});

describe('Pruebas de estado (state)', () => {
  it('el estado del formulario cambia cuando el usuario escribe', () => {
    render(<FormularioNombre />);
    const input = screen.getByLabelText('Nombre');
    fireEvent.change(input, { target: { value: 'Ana' } });
    expect(input.value).toBe('Ana');
  });
});

describe('Pruebas de eventos', () => {
  it('un clic en el boton cambia el estado del componente', () => {
    render(<Contador />);
    fireEvent.click(screen.getByText('Clics: 0'));
    fireEvent.click(screen.getByText('Clics: 1'));
    expect(screen.getByText('Clics: 2')).toBeTruthy();
  });
});
