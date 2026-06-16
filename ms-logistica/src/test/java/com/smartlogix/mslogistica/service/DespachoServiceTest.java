package com.smartlogix.mslogistica.service;

import com.smartlogix.mslogistica.model.Despacho;
import com.smartlogix.mslogistica.model.Transportista;
import com.smartlogix.mslogistica.repository.DespachoRepository;
import com.smartlogix.mslogistica.repository.TransportistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private DespachoService despachoService;

    private Despacho despachoBase;
    private Transportista transportistaBase;

    @BeforeEach
    void setUp() {
        despachoBase = new Despacho();
        despachoBase.setIdDespacho(1L);
        despachoBase.setEstadoDespacho("PENDIENTE");

        transportistaBase = new Transportista();
        transportistaBase.setIdTransportista(100L);
        // Asume que Transportista tiene un método setNombre, ajusta si es necesario
        // transportistaBase.setNombre("Transportes Express");
    }

    @Test
    void listar_DebeRetornarListaDeDespachos() {
        // Arrange
        when(despachoRepository.findAll()).thenReturn(Arrays.asList(despachoBase));

        // Act
        List<Despacho> resultado = despachoService.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(despachoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarDespacho() {
        // Arrange
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBase));

        // Act
        Despacho resultado = despachoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdDespacho());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            despachoService.buscarPorId(99L);
        });
        assertEquals("Despacho no encontrado", excepcion.getMessage());
    }

    @Test
    void crear_CuandoFechaEsNula_DebeAsignarFechaYGuardar() {
        // Arrange
        Despacho nuevoDespacho = new Despacho();
        nuevoDespacho.setEstadoDespacho("NUEVO");

        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Despacho resultado = despachoService.crear(nuevoDespacho);

        // Assert
        assertNotNull(resultado.getFechaCreacion());
        assertEquals("NUEVO", resultado.getEstadoDespacho());
        verify(despachoRepository, times(1)).save(any(Despacho.class));
    }

    @Test
    void asignarTransportista_CuandoAmbosExisten_DebeAsignarYGuardar() {
        // Arrange
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBase));
        when(transportistaRepository.findById(100L)).thenReturn(Optional.of(transportistaBase));
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Despacho resultado = despachoService.asignarTransportista(1L, 100L);

        // Assert
        assertNotNull(resultado.getTransportista());
        assertEquals(100L, resultado.getTransportista().getIdTransportista());
        verify(despachoRepository, times(1)).save(despachoBase);
    }

    @Test
    void cambiarEstado_CuandoExiste_DebeActualizarEstadoYGuardar() {
        // Arrange
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBase));
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Despacho resultado = despachoService.cambiarEstado(1L, "EN_RUTA");

        // Assert
        assertEquals("EN_RUTA", resultado.getEstadoDespacho());
        verify(despachoRepository, times(1)).save(despachoBase);
    }
}