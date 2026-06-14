package com.smartlogix.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.inventario.entity.Producto;
import com.smartlogix.inventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Usamos la nueva anotación que ya comprobamos que funciona en tu versión
    @MockitoBean
    private ProductoService productoService;

    private Producto productoMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto();
        // Basado en tu método actualizar(), asumo que el setter es setIdProducto()
        productoMock.setIdProducto(1L);
        productoMock.setNombre("Bebida Pepsi");
        productoMock.setPrecioActual(150000);
    }

    @Test
    void crear_DebeRetornarEstado200YProductoCreado() throws Exception {
        // Arrange
        when(productoService.guardar(any(Producto.class))).thenReturn(productoMock);
        String productoJson = objectMapper.writeValueAsString(productoMock);

        // Act & Assert
        // Nota: Como no usas ResponseEntity.status(201), Spring por defecto retorna 200 OK
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Bebida Pepsi"));
    }

    @Test
    void listar_DebeRetornarEstado200YListaDeProductos() throws Exception {
        // Arrange
        when(productoService.listarTodos()).thenReturn(Arrays.asList(productoMock));

        // Act & Assert
        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Bebida Pepsi"));
    }

    @Test
    void obtener_DebeRetornarEstado200YProducto() throws Exception {
        // Arrange
        when(productoService.buscarPorId(1L)).thenReturn(productoMock);

        // Act & Assert
        mockMvc.perform(get("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L));
    }

    @Test
    void actualizar_DebeRetornarEstado200YProductoActualizado() throws Exception {
        // Arrange
        Producto productoActualizado = new Producto();
        productoActualizado.setIdProducto(1L);
        productoActualizado.setNombre("Switch Palo Alto");

        when(productoService.guardar(any(Producto.class))).thenReturn(productoActualizado);
        String productoJson = objectMapper.writeValueAsString(productoActualizado);

        // Act & Assert
        mockMvc.perform(put("/api/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Switch Palo Alto")); // Comprobamos que cambió
    }

    @Test
    void eliminar_DebeRetornarEstado200() throws Exception {
        // Arrange
        doNothing().when(productoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/productos/{id}", 1L))
                .andExpect(status().isOk());
    }
}