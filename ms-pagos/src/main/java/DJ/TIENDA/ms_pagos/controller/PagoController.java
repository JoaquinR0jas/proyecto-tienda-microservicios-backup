package DJ.TIENDA.ms_pagos.controller;

import DJ.TIENDA.ms_pagos.dto.PagoRequestDTO;
import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Boleta;
import DJ.TIENDA.ms_pagos.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pagos", description = "Procesamiento de pagos y emision de boletas")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Operation(summary = "Procesar pago", description = "Procesa el pago de un pedido. Si se aprueba emite la boleta. Body: { pedidoId, usuarioId, monto, medio, simularRechazo }")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago procesado (APROBADO o RECHAZADO)"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody PagoRequestDTO solicitud) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarPago(solicitud));
        } catch (IllegalArgumentException e) {
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

    @Operation(summary = "Listar boletas", description = "Obtiene todas las boletas emitidas")
    @ApiResponse(responseCode = "200", description = "Lista de boletas obtenida")
    @GetMapping("/boletas")
    public ResponseEntity<List<Boleta>> listarBoletas() {
        return ResponseEntity.ok(pagoService.obtenerBoletas());
    }

    @Operation(summary = "Obtener boleta por folio", description = "Busca una boleta por su numero de folio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Boleta encontrada"),
        @ApiResponse(responseCode = "404", description = "Boleta no encontrada")
    })
    @GetMapping("/boletas/{folio}")
    public ResponseEntity<?> obtenerBoleta(@PathVariable Long folio) {
        return pagoService.obtenerBoletaPorFolio(folio)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Boleta no encontrada con folio: " + folio));
    }

    @Operation(summary = "Boletas por pedido", description = "Obtiene la boleta emitida para un pedido")
    @ApiResponse(responseCode = "200", description = "Lista de boletas obtenida")
    @GetMapping("/boletas/pedido/{pedidoId}")
    public ResponseEntity<List<Boleta>> obtenerBoletasPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.obtenerBoletasPorPedido(pedidoId));
    }
}
