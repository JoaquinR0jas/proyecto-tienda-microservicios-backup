import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { KEYS, getColeccion, setColeccion, imgProducto } from '../datos.js'
import { textoOk, numeroOk, enteroOk, claseCampo } from '../validaciones.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso } from '../alertas.js'

const VACIO = {
  codigo: "", nombre: "", descripcion: "", precio: "", descuento: 0,
  stock: "", stockCritico: "", categoriaId: "", imagen: "", estado: "Activo", destacado: false
}

export default function AdminProductoForm() {
  const { codigo } = useParams()
  const nav = useNavigate()
  const sesion = useAdmin(true)
  const editando = !!codigo
  const [cats] = useState(() => getColeccion(KEYS.categorias))
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)
  const [form, setForm] = useState(() => {
    const p = codigo && getColeccion(KEYS.productos).find(x => x.codigo === codigo)
    if (!p) return VACIO
    return {
      codigo: p.codigo, nombre: p.nombre, descripcion: p.descripcion || "",
      precio: p.precio, descuento: p.descuento || 0, stock: p.stock, stockCritico: p.stockCritico ?? "",
      categoriaId: p.categoriaId, imagen: p.imagen || "", estado: p.estado, destacado: !!p.destacado
    }
  })
  if (!sesion) return null

  function cambiar(e) {
    const { name, value, type, checked } = e.target
    setForm({ ...form, [name]: type === "checkbox" ? checked : value })
  }

  // lee la imagen elegida, la achica a 800px de ancho y la guarda como jpg comprimido
  function subirImagen(e) {
    const archivo = e.target.files[0]
    if (!archivo) return
    const lector = new FileReader()
    lector.onload = () => {
      const img = new Image()
      img.onload = () => {
        const escala = Math.min(1, 800 / img.width)
        const canvas = document.createElement("canvas")
        canvas.width = Math.round(img.width * escala)
        canvas.height = Math.round(img.height * escala)
        canvas.getContext("2d").drawImage(img, 0, 0, canvas.width, canvas.height)
        setForm(f => ({ ...f, imagen: canvas.toDataURL("image/jpeg", 0.8) }))
      }
      img.src = lector.result
    }
    lector.readAsDataURL(archivo)
  }

  async function guardar(e) {
    e.preventDefault()
    const todos = getColeccion(KEYS.productos)
    const err = {}
    if (form.codigo.trim().length < 3) err.codigo = "Codigo obligatorio, minimo 3 caracteres."
    else if (!editando && todos.some(p => p.codigo === form.codigo.trim())) err.codigo = "Ese codigo ya existe."
    if (!textoOk(form.nombre, 100)) err.nombre = true
    if (form.descripcion.trim().length > 500) err.descripcion = true
    if (!numeroOk(form.precio, 0)) err.precio = true
    if (!enteroOk(form.descuento, 0) || Number(form.descuento) > 90) err.descuento = true
    if (!enteroOk(form.stock, 0)) err.stock = true
    if (form.stockCritico !== "" && !enteroOk(form.stockCritico, 0)) err.stockCritico = true
    if (!form.categoriaId) err.categoriaId = true
    setErrores(err)
    setValidado(true)

    if (Object.keys(err).length > 0) {
      aviso("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error")
      return
    }

    const prod = {
      codigo: form.codigo.trim(),
      nombre: form.nombre.trim(),
      descripcion: form.descripcion.trim(),
      precio: Number(form.precio),
      descuento: Number(form.descuento) || 0,
      stock: Number(form.stock),
      stockCritico: form.stockCritico === "" ? null : Number(form.stockCritico),
      categoriaId: Number(form.categoriaId),
      imagen: form.imagen.trim() || "logo.svg",
      estado: form.estado,
      destacado: form.destacado
    }

    if (editando) {
      setColeccion(KEYS.productos, todos.map(p => p.codigo === codigo ? prod : p))
    } else {
      todos.push(prod)
      setColeccion(KEYS.productos, todos)
    }

    let extra = ""
    if (prod.stockCritico != null && prod.stock <= prod.stockCritico) extra = " El stock esta en o bajo el nivel critico."
    await aviso("Correcto", "Producto guardado correctamente." + extra)
    nav("/admin/productos")
  }

  const clase = (campo, base) => claseCampo(errores, validado, campo, base)

  return (
    <AdminLayout>
      <h1 className="section-title">{editando ? "Editar producto" : "Nuevo producto"}</h1>
      <form onSubmit={guardar} className="bg-ff-panel rounded p-4" noValidate>
        <div className="row g-3">
          <div className="col-md-4">
            <label htmlFor="codigoProducto" className="form-label">Codigo</label>
            <input id="codigoProducto" name="codigo" className={clase("codigo")} placeholder="Ej: FF-009" value={form.codigo} onChange={cambiar} readOnly={editando} />
            <div className="invalid-feedback">{errores.codigo}</div>
          </div>
          <div className="col-md-8">
            <label htmlFor="nombreProducto" className="form-label">Nombre</label>
            <input id="nombreProducto" name="nombre" className={clase("nombre")} maxLength="100" value={form.nombre} onChange={cambiar} />
            <div className="invalid-feedback">El nombre es obligatorio (maximo 100 caracteres).</div>
          </div>
          <div className="col-12">
            <label htmlFor="descripcionProducto" className="form-label">Descripcion</label>
            <textarea id="descripcionProducto" name="descripcion" className={clase("descripcion")} rows="3" maxLength="500" value={form.descripcion} onChange={cambiar}></textarea>
            <div className="invalid-feedback">Maximo 500 caracteres.</div>
          </div>
          <div className="col-md-3">
            <label htmlFor="precioProducto" className="form-label">Precio</label>
            <input id="precioProducto" name="precio" type="number" min="0" step="1" className={clase("precio")} value={form.precio} onChange={cambiar} />
            <div className="invalid-feedback">El precio debe ser mayor o igual a 0.</div>
          </div>
          <div className="col-md-3">
            <label htmlFor="stockProducto" className="form-label">Stock</label>
            <input id="stockProducto" name="stock" type="number" min="0" step="1" className={clase("stock")} value={form.stock} onChange={cambiar} />
            <div className="invalid-feedback">El stock debe ser un entero mayor o igual a 0.</div>
          </div>
          <div className="col-md-3">
            <label htmlFor="stockCriticoProducto" className="form-label">Stock critico</label>
            <input id="stockCriticoProducto" name="stockCritico" type="number" min="0" step="1" className={clase("stockCritico")} value={form.stockCritico} onChange={cambiar} />
            <div className="invalid-feedback">Debe ser un entero mayor o igual a 0.</div>
          </div>
          <div className="col-md-3">
            <label htmlFor="categoriaProducto" className="form-label">Categoria</label>
            <select id="categoriaProducto" name="categoriaId" className={clase("categoriaId", "form-select")} value={form.categoriaId} onChange={cambiar}>
              <option value="">Seleccione una categoria</option>
              {cats.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
            </select>
            <div className="invalid-feedback">Selecciona una categoria.</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="descuentoProducto" className="form-label">Descuento % (si es mayor a 0 sale en Ofertas)</label>
            <input id="descuentoProducto" name="descuento" type="number" min="0" max="90" step="1" className={clase("descuento")} value={form.descuento} onChange={cambiar} />
            <div className="invalid-feedback">Debe ser un entero entre 0 y 90.</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="imagenProducto" className="form-label">Imagen</label>
            <input id="imagenProducto" type="file" accept="image/*" className="form-control" onChange={subirImagen} />
            <input
              name="imagen"
              className="form-control form-control-sm mt-1"
              placeholder="o escribe una ruta: productos/polera-basica.webp"
              value={form.imagen.startsWith("data:") ? "" : form.imagen}
              onChange={cambiar}
            />
            {form.imagen && (
              <img src={imgProducto(form)} alt="Vista previa" className="img-thumbnail mt-2" style={{ maxHeight: 120 }} />
            )}
          </div>
          <div className="col-md-4">
            <label htmlFor="estadoProducto" className="form-label">Estado</label>
            <select id="estadoProducto" name="estado" className="form-select" value={form.estado} onChange={cambiar}>
              <option>Activo</option>
              <option>Inactivo</option>
            </select>
          </div>
          <div className="col-12">
            <div className="form-check">
              <input id="destacadoProducto" name="destacado" type="checkbox" className="form-check-input" checked={form.destacado} onChange={cambiar} />
              <label htmlFor="destacadoProducto" className="form-check-label">Mostrar en productos destacados</label>
            </div>
          </div>
        </div>
        <div className="d-flex gap-2 mt-4">
          <button type="submit" className="btn btn-accent">Guardar</button>
          <Link to="/admin/productos" className="btn btn-outline-accent">Cancelar</Link>
        </div>
      </form>
    </AdminLayout>
  )
}
