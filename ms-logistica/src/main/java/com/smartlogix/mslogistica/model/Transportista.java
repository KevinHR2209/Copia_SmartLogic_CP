package com.smartlogix.mslogistica.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transportista")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransportista;

    private String nombreCompleto;

    private String patenteVehiculo;

    private String telefonoContacto;

    private String estado; // ACTIVO, INACTIVO
}