package DJ.TIENDA.ms_pagos.controller;

import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // POST /api/pagos/procesar → Procesa el pago de un pedido
    // Body: { "pedidoId": 1, "usuarioId": 1, "monto": 1199.96 }
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody Map<String, Object> body) {
        try {
            Long pedidoId = Long.valueOf(body.get("pedidoId").toString());
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            Double monto = Double.valueOf(body.get("monto").toString());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(pagoService.procesarPago(pedidoId, usuarioId, monto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // GET /api/pagos/pedido/{pedidoId} → Ver pagos de un pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagosPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorPedido(pedidoId));
    }

    // GET /api/pagos/usuario/{usuarioId} → Ver todos los pagos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorUsuario(usuarioId));
    }
}