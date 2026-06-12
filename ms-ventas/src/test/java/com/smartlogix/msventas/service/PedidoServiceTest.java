package com.smartlogix.msventas.service;

import com.smartlogix.msventas.model.DetallePedido;
import com.smartlogix.msventas.model.Pedido;
import com.smartlogix.msventas.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoMock;
    private DetallePedido detalleMock;

    @BeforeEach
    void setUp() {
        // Inicializamos los mocks antes de cada test
        pedidoMock = new Pedido();
        pedidoMock.setIdPedido(1L);
        pedidoMock.setEstadoPedido("PENDIENTE");

        detalleMock = new DetallePedido();
        detalleMock.setIdDetalle(10L);

        List<DetallePedido> detalles = new ArrayList<>();
        detalles.add(detalleMock);
        pedidoMock.setDetalles(detalles);
    }

    // ==========================================
    // TESTS PARA LISTAR
    // ==========================================

    @Test
    void listar_DebeRetornarListaDePedidos() {
        // Arrange
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoMock));

        // Act
        List<Pedido> resultado = pedidoService.listar();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void listar_CuandoNoHayPedidos_DebeRetornarListaVacia() {
        // Arrange (Caso Borde)
        when(pedidoRepository.findAll()).thenReturn(List.of());

        // Act
        List<Pedido> resultado = pedidoService.listar();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==========================================
    // TESTS PARA BUSCAR POR ID
    // ==========================================

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarPedido() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido resultado = pedidoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPedido());
        assertEquals("PENDIENTE", resultado.getEstadoPedido());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        // Arrange (Sad Path)
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.buscarPorId(99L);
        });

        assertEquals("Pedido no encontrado", exception.getMessage());
    }

    // ==========================================
    // TESTS PARA CREAR (Lógica de Negocio Central)
    // ==========================================

    @Test
    void crear_ConFechaNula_DebeAsignarFechaYGuardar() {
        // Arrange
        pedidoMock.setFechaCreacion(null); // Aseguramos que venga nula
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoMock);

        // Act
        Pedido resultado = pedidoService.crear(pedidoMock);

        // Assert
        assertNotNull(resultado.getFechaCreacion(), "La fecha de creación no debería ser nula después de crear");
        verify(pedidoRepository).save(pedidoMock);
    }

    @Test
    void crear_ConDetalles_DebeVincularDetallesAlPedido() {
        // Arrange
        // (En el setUp ya le agregamos un detalle sin pedido asociado)
        assertNull(detalleMock.getPedido(), "El detalle no debería tener pedido antes de crear");
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoMock);

        // Act
        pedidoService.crear(pedidoMock);

        // Assert
        // Verificamos que el servicio inyectó el 'this' (el pedido) dentro del detalle
        assertNotNull(detalleMock.getPedido(), "El detalle debe tener el pedido asignado");
        assertEquals(1L, detalleMock.getPedido().getIdPedido(), "El id del pedido en el detalle debe coincidir");
    }

    // ==========================================
    // TESTS PARA CAMBIAR ESTADO
    // ==========================================

    @Test
    void cambiarEstado_CuandoExiste_DebeActualizarYGuardar() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoMock);

        // Act
        Pedido resultado = pedidoService.cambiarEstado(1L, "ENVIADO");

        // Assert
        assertEquals("ENVIADO", resultado.getEstadoPedido(), "El estado debe haber cambiado");
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(pedidoMock);
    }

    @Test
    void cambiarEstado_CuandoNoExiste_DebeLanzarExcepcion() {
        // Arrange (Sad Path)
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pedidoService.cambiarEstado(99L, "ENVIADO");
        });

        // Verificamos que NUNCA se haya llamado al método save() si el pedido no existía
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}