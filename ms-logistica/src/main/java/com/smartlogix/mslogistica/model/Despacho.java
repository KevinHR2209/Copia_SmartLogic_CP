package com.smartlogix.mslogistica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "despacho")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDespacho;

    private Long idPedido; // referencia logica a ms-ventas

    private String direccionEntrega;

    private String comunaEntrega;

    private String estadoDespacho; // PENDIENTE, EN_RUTA, ENTREGADO, CANCELADO

    private OffsetDateTime fechaCreacion;

    private OffsetDateTime fechaEntregaEstimada;

    @ManyToOne
    @JoinColumn(name = "id_transportista")
    private Transportista transportista;
}