package DJ.TIENDA.ms_pedidos.dto;

import lombok.Data;

// Solicitud y respuesta del pago en ms-pagos via Feign
@Data
public class PagoDTO {

    // Lo que se envia
    private Long pedidoId;
    private Long usuarioId;
    private Double monto;
    private String medio;
    private boolean simularRechazo;

    // Lo que responde ms-pagos
    private Long pagoId;
    private String estado;  // APROBADO o RECHAZADO
    private String detalle; // Motivo cuando es RECHAZADO
    private Boleta boleta;  // Solo cuando es APROBADO

    @Data
    public static class Boleta {
        private Long folio;
        private Double neto;
        private Double iva;
        private Double total;
    }
}
