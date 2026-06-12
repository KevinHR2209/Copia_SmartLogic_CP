package com.smartlogix.msclientes.service;

import com.smartlogix.msclientes.model.Cliente;
import com.smartlogix.msclientes.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith habilita las anotaciones de Mockito (@Mock, @InjectMocks)
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    // 1. Mockeamos la dependencia (ClienteRepository)
    @Mock
    private ClienteRepository repository;

    // 2. Inyectamos los mocks en el servicio que vamos a probar
    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteMock;

    // 3. Preparamos datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        clienteMock = new Cliente();
        clienteMock.setIdCliente(1L);
        clienteMock.setRut("12345678-9");
        clienteMock.setNombre("Juan");
        clienteMock.setApellidoPaterno("Pérez");
        clienteMock.setCorreo("juan@test.com");
    }

    @Test
    void listar_DebeRetornarListaDeClientes() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(clienteMock));

        // Act
        List<Cliente> resultado = clienteService.listar();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll(); // Verifica que el método fue llamado 1 vez
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarCliente() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(clienteMock));

        // Act
        Cliente resultado = clienteService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        // Es buena práctica probar las excepciones (Caminos negativos)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorId(99L);
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
    }

    @Test
    void crear_DebeGuardarYRetornarCliente() {
        // Arrange
        when(repository.save(any(Cliente.class))).thenReturn(clienteMock);

        // Act
        Cliente resultado = clienteService.crear(clienteMock);

        // Assert
        assertNotNull(resultado);
        assertEquals("12345678-9", resultado.getRut());
        verify(repository).save(clienteMock);
    }

    @Test
    void actualizar_CuandoExiste_DebeActualizarYRetornarCliente() {
        // Arrange
        Cliente datosActualizados = new Cliente();
        datosActualizados.setNombre("Pedro");
        datosActualizados.setRut("87654321-0");

        // Simulamos que primero lo encuentra y luego lo guarda
        when(repository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(repository.save(any(Cliente.class))).thenReturn(clienteMock); // save() usa la misma instancia modificada

        // Act
        Cliente resultado = clienteService.actualizar(1L, datosActualizados);

        // Assert
        assertEquals("Pedro", resultado.getNombre()); // Verificamos que se actualizaron los campos
        assertEquals("87654321-0", resultado.getRut());
        verify(repository).findById(1L);
        verify(repository).save(clienteMock);
    }

    @Test
    void eliminar_DebeLlamarAlRepositorio() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        clienteService.eliminar(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }
}