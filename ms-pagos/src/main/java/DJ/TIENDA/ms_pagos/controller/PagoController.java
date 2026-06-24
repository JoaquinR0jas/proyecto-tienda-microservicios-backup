package DJ.TIENDA.ms_pagos.controller;

import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Pagos", description = "Procesamiento de pagos")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(summary = "Procesar pago", description = "Procesa el pago de un pedido. Body: { pedidoId, usuarioId, monto }")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al procesar el pago")
    })
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

    @Operation(summary = "Pagos por pedido", description = "Obtiene todos los pagos realizados para un pedido")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida")
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagosPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorPedido(pedidoId));
    }

    @Operation(summary = "Pagos por usuario", description = "Obtiene todos los pagos realizados por un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPagosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pagoService.obtenerPagosPorUsuario(usuarioId));
    }
}