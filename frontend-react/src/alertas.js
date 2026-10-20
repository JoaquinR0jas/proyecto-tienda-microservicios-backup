// alertas de la tienda con SweetAlert2 (igual que en el frontend original)
import Swal from 'sweetalert2'
import { KEYS, getColeccion, agregarAlCarrito } from './datos.js'

export function aviso(titulo, texto, icono = "success") {
  return Swal.fire({ title: titulo, text: texto, icon: icono, confirmButtonColor: "#111111" })
}

// devuelve una promesa con true si el usuario confirmo
export function confirmar(titulo, texto, boton = "Eliminar") {
  return Swal.fire({
    title: titulo,
    text: texto,
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: boton,
    cancelButtonText: "Cancelar",
    confirmButtonColor: "#d32f2f"
  }).then(r => r.isConfirmed)
}

// agrega al carrito y avisa el resultado
export function agregarConAviso(codigo, cantidad = 1) {
  const prod = getColeccion(KEYS.productos).find(p => p.codigo === codigo)
  if (!prod) return
  if (prod.stock <= 0) {
    aviso("Sin stock", "Esta prenda no tiene stock disponible.", "error")
    return
  }
  const error = agregarAlCarrito(codigo, cantidad)
  if (error) {
    aviso("Stock insuficiente", "Solo hay " + prod.stock + " unidades disponibles de " + prod.nombre + ".", "warning")
    return
  }
  aviso("Agregado", prod.nombre + " se anadio al carrito.")
}
