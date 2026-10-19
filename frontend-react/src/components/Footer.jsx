export default function Footer() {
  return (
    <footer className="ff-footer py-5">
      <div className="container">
        <div className="row g-4">
          <div className="col-md-6">
            <span className="brand-text fs-5">FRESH FITS</span>
            <p className="mt-2 mb-0">Tu tienda de ropa de confianza. Moda moderna, comoda y estilosa para todos.</p>
          </div>
          <div className="col-md-6">
            <h6 className="text-black">Siguenos</h6>
            <div className="fs-4">
              <a href="https://www.instagram.com/fresh.fitstore/" target="_blank" rel="noreferrer"><i className="bi bi-instagram"></i></a>
            </div>
          </div>
        </div>
        <hr className="border-secondary my-4" />
        <p className="text-center mb-0 small">&copy; 2026 FreshFits. Proyecto academico - Control 2.</p>
      </div>
    </footer>
  )
}
