import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Table, Button, Badge } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion, getSesion } from '../datos.js'
import { AdminLayout } from '../components/AdminMenu.jsx'

function nombreRol(id) {
  const r = getColeccion(KEYS.roles).find(x => x.id === id)
  return r ? r.nombre : ""
}

function nombreRegion(id) {
  const r = getColeccion(KEYS.regiones).find(x => x.id === Number(id))
  return r ? r.nombre : "-"
}

export default function AdminUsuarios() {
  const nav = useNavigate()
  const [users, setUsers] = useState([])

  useEffect(() => {
    const s = getSesion()
    if (!s || s.rolId !== 1) {
      alert("Solo el admin ve los usuarios")
      nav("/admin")
      return
    }
    setUsers(getColeccion(KEYS.usuarios))
  }, [])

  function eliminar(run) {
    if (!confirm("Eliminar a este usuario?")) return
    setColeccion(KEYS.usuarios, getColeccion(KEYS.usuarios).filter(u => u.run !== run))
    setUsers(getColeccion(KEYS.usuarios))
  }

  return (
    <AdminLayout>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1 className="section-title mb-0">Usuarios</h1>
        <Button as={Link} to="/admin/usuarios/nuevo" variant="accent" size="sm">+ Nuevo usuario</Button>
      </div>
      <div className="table-responsive bg-ff-panel rounded p-2">
        <Table striped className="mb-0">
          <thead>
            <tr>
              <th>RUN</th>
              <th>Nombre</th>
              <th>Correo</th>
              <th>Rol</th>
              <th>Region</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.run}>
                <td>{u.run}</td>
                <td>{u.nombre} {u.apellidos}</td>
                <td>{u.correo}</td>
                <td>{nombreRol(u.rolId)}</td>
                <td>{nombreRegion(u.regionId)}</td>
                <td><Badge bg={u.estado === "Activo" ? "success" : "secondary"}>{u.estado}</Badge></td>
                <td className="text-nowrap">
                  <Button as={Link} to={"/admin/usuarios/" + u.run} variant="outline-dark" size="sm" className="me-1"><i className="bi bi-pencil"></i></Button>
                  <Button variant="outline-danger" size="sm" onClick={() => eliminar(u.run)}><i className="bi bi-trash"></i></Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      </div>
    </AdminLayout>
  )
}
