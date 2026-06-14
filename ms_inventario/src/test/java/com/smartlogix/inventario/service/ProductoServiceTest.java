package com.smartlogix.inventario.service;

import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.repository.ProductoRepository;
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

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto();
        productoMock.setIdProducto(1L);
        // Ajusta los campos según lo que tenga tu entidad Producto
    }

    @Test
    void listarTodos_DebeRetornarListaDeProductos() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoMock));

        List<Producto> resultado = productoService.listarTodos();

        assertEquals(1, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void guardar_DebeRetornarProductoGuardado() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        Producto resultado = productoService.guardar(productoMock);

        assertNotNull(resultado);
        verify(productoRepository).save(productoMock);
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarProducto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeRetornarNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.buscarPorId(99L);

        assertNull(resultado);
    }

    @Test
    void eliminar_DebeLlamarAlMetodoDeleteDelRepositorio() {
        // En métodos void, usamos doNothing o simplemente la llamada
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminar(1L);

        verify(productoRepository, times(1)).deleteById(1L);
    }
}