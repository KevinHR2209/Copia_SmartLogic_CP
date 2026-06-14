package com.smartlogix.msventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.msventas.model.Pedido;
import com.smartlogix.msventas.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 1. Cargamos SOLO el contexto web para este controlador, es súper rápido.
@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    // 2. MockMvc nos permite simular peticiones HTTP sin levantar un servidor real (como Tomcat).
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper sirve para convertir Objetos Java a JSON y viceversa.
    @Autowired
    private ObjectMapper objectMapper;

    // 3. @MockBean es la versión de Spring para agregar un mock de Mockito al contexto de la app.
    @MockitoBean
    private PedidoService pedidoService;

    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        pedidoMock = new Pedido();
        pedidoMock.setIdPedido(1L);
        pedidoMock.setEstadoPedido("PENDIENTE");
    }

    @Test
    void listar_DebeRetornarEstado200YListaDePedidos() throws Exception {
        // Arrange: Le decimos al servicio qué responder
        when(pedidoService.listar()).thenReturn(Arrays.asList(pedidoMock));

        // Act & Assert: Simulamos la petición GET y verificamos la respuesta
        mockMvc.perform(get("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica HTTP 200
                .andExpect(jsonPath("$[0].id").value(1L)) // $. representa la raíz del JSON
                .andExpect(jsonPath("$[0].estadoPedido").value("PENDIENTE"));
    }

    @Test
    void obtener_DebeRetornarEstado200YPedido() throws Exception {
        // Arrange
        when(pedidoService.buscarPorId(1L)).thenReturn(pedidoMock);

        // Act & Assert
        mockMvc.perform(get("/api/pedidos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estadoPedido").value("PENDIENTE"));
    }

    @Test
    void crear_DebeRetornarEstado201YPedidoCreado() throws Exception {
        // Arrange
        when(pedidoService.crear(any(Pedido.class))).thenReturn(pedidoMock);

        // Convertimos el objeto Java a JSON String para enviarlo en el Body
        String pedidoJson = objectMapper.writeValueAsString(pedidoMock);

        // Act & Assert
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson)) // Metemos el JSON en el cuerpo de la petición
                .andExpect(status().isCreated()) // Verifica HTTP 201 (Creado)
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void cambiarEstado_DebeRetornarEstado200YPedidoActualizado() throws Exception {
        // Arrange
        Pedido pedidoActualizado = new Pedido();
        pedidoActualizado.setIdPedido(1L);
        pedidoActualizado.setEstadoPedido("ENVIADO");

        when(pedidoService.cambiarEstado(eq(1L), eq("ENVIADO"))).thenReturn(pedidoActualizado);

        // Act & Assert
        // Aquí pasamos el estado como un RequestParam (?estado=ENVIADO)
        mockMvc.perform(put("/api/pedidos/{id}/estado", 1L)
                        .param("estado", "ENVIADO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPedido").value("ENVIADO"));
    }
}