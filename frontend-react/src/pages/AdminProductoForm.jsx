import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Row, Col, Form, Button } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion, getSesion } from '../datos.js'
import { AdminLayout } from '../components/AdminMenu.jsx'

export default function AdminProductoForm() {
  const { codigo } = useParams()
  const nav = useNavigate()
  const editando = !!codigo
  const [cats, setCats] = useState([])
  const [form, setForm] = useState({
    codigo: "", nombre: "", descripcion: "", precio: "",
    stock: "", stockCritico: "", categoriaId: "", imagen: "", estado: "Activo", destacado: false
  })

  useEffect(() => {
    const s = getSesion()
    if (!s || s.rolId !== 1) {
      alert("Solo el admin puede editar productos")
      nav("/admin")
      return
    }
    setCats(getColeccion(KEYS.categorias))
    if (codigo) {
      const p = getColeccion(KEYS.productos).find(x => x.codigo === codigo)
      if (p) {
        setForm({
          codigo: p.codigo, nombre: p.nombre, descripcion: p.descripcion || "",
          precio: p.precio, stock: p.stock, stockCritico: p.stockCritico ?? "",
          categoriaId: p.categoriaId, imagen: p.imagen || "", estado: p.estado, destacado: !!p.destacado
        })
      }
    }
  }, [codigo])

  function cambiar(e) {
    const { name, value, type, checked } = e.target
    setForm({ ...form, [name]: type === "checkbox" ? checked : value })
  }

  function guardar(e) {
    e.preventDefault()
    if (form.codigo.trim().length < 3 || !form.nombre.trim() || !form.categoriaId) {
      alert("Completa codigo, nombre y categoria")
      return
    }
    if (Number(form.precio) < 0 || Number(form.stock) < 0) {
      alert("Precio y stock no pueden ser negativos")
      return
    }
    const todos = getColeccion(KEYS.productos)
    if (!editando && todos.some(p => p.codigo === form.codigo.trim())) {
      alert("Ese codigo ya existe")
      return
    }

    const prod = {
      codigo: form.codigo.trim(),
      nombre: form.nombre.trim(),
      descripcion: form.descripcion.trim(),
      precio: Number(form.precio),
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
    alert("Producto guardado")
    nav("/admin/productos")
  }

  return (
    <AdminLayout>
      <h1 className="section-title">{editando ? "Editar producto" : "Nuevo producto"}</h1>
      <Form onSubmit={guardar} className="bg-ff-panel rounded p-4">
        <Row className="g-3">
          <Col md={6}>
            <Form.Label>Codigo</Form.Label>
            <Form.Control name="codigo" value={form.codigo} onChange={cambiar} readOnly={editando} />
          </Col>
          <Col md={6}>
            <Form.Label>Nombre</Form.Label>
            <Form.Control name="nombre" maxLength="100" value={form.nombre} onChange={cambiar} />
          </Col>
          <Col xs={12}>
            <Form.Label>Descripcion</Form.Label>
            <Form.Control as="textarea" name="descripcion" rows={2} value={form.descripcion} onChange={cambiar} />
          </Col>
          <Col md={4}>
            <Form.Label>Precio</Form.Label>
            <Form.Control name="precio" type="number" value={form.precio} onChange={cambiar} />
          </Col>
          <Col md={4}>
            <Form.Label>Stock</Form.Label>
            <Form.Control name="stock" type="number" value={form.stock} onChange={cambiar} />
          </Col>
          <Col md={4}>
            <Form.Label>Stock critico</Form.Label>
            <Form.Control name="stockCritico" type="number" value={form.stockCritico} onChange={cambiar} />
          </Col>
          <Col md={6}>
            <Form.Label>Categoria</Form.Label>
            <Form.Select name="categoriaId" value={form.categoriaId} onChange={cambiar}>
              <option value="">Seleccione una categoria</option>
              {cats.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
            </Form.Select>
          </Col>
          <Col md={6}>
            <Form.Label>Imagen (ruta dentro de /img)</Form.Label>
            <Form.Control name="imagen" value={form.imagen} onChange={cambiar} placeholder="productos/polera-basica.webp" />
          </Col>
          <Col md={6}>
            <Form.Label>Estado</Form.Label>
            <Form.Select name="estado" value={form.estado} onChange={cambiar}>
              <option>Activo</option>
              <option>Inactivo</option>
            </Form.Select>
          </Col>
          <Col md={6} className="d-flex align-items-end">
            <Form.Check name="destacado" type="checkbox" label="Destacado en el home" checked={form.destacado} onChange={cambiar} />
          </Col>
        </Row>
        <div className="d-flex gap-2 mt-4">
          <Button type="submit" variant="accent">Guardar</Button>
          <Button as={Link} to="/admin/productos" variant="outline-accent">Volver</Button>
        </div>
      </Form>
    </AdminLayout>
  )
}
