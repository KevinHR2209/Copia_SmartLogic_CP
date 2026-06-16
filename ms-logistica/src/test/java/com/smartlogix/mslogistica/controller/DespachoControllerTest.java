package com.smartlogix.mslogistica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.mslogistica.model.Despacho;
import com.smartlogix.mslogistica.service.DespachoService;
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

@WebMvcTest(DespachoController.class)
class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DespachoService despachoService;

    private Despacho despachoBase;

    @BeforeEach
    void setUp() {
        despachoBase = new Despacho();
        despachoBase.setIdDespacho(1L);
        despachoBase.setEstadoDespacho("PENDIENTE");
    }

    @Test
    void listar_DebeRetornarStatus200YLista() throws Exception {
        when(despachoService.listar()).thenReturn(Arrays.asList(despachoBase));

        mockMvc.perform(get("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // CAMBIO: id -> idDespacho
                .andExpect(jsonPath("$[0].idDespacho").value(1))
                .andExpect(jsonPath("$[0].estadoDespacho").value("PENDIENTE"));
    }

    @Test
    void obtener_CuandoExiste_DebeRetornarStatus200() throws Exception {
        when(despachoService.buscarPorId(1L)).thenReturn(despachoBase);

        mockMvc.perform(get("/api/despachos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // CAMBIO: id -> idDespacho
                .andExpect(jsonPath("$.idDespacho").value(1))
                .andExpect(jsonPath("$.estadoDespacho").value("PENDIENTE"));
    }

    @Test
    void crear_DebeRetornarStatus201YDespachoCreado() throws Exception {
        when(despachoService.crear(any(Despacho.class))).thenReturn(despachoBase);

        mockMvc.perform(post("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(despachoBase)))
                .andExpect(status().isCreated())
                // CAMBIO: id -> idDespacho
                .andExpect(jsonPath("$.idDespacho").value(1));
    }

    @Test
    void cambiarEstado_DebeRetornarStatus200YDespachoActualizado() throws Exception {
        Despacho despachoActualizado = new Despacho();
        despachoActualizado.setIdDespacho(1L);
        despachoActualizado.setEstadoDespacho("ENTREGADO");

        when(despachoService.cambiarEstado(eq(1L), eq("ENTREGADO"))).thenReturn(despachoActualizado);

        mockMvc.perform(put("/api/despachos/{id}/estado", 1L)
                        .param("estado", "ENTREGADO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDespacho").value("ENTREGADO"));
    }

    @Test
    void asignarTransportista_DebeRetornarStatus200() throws Exception {
        when(despachoService.asignarTransportista(1L, 100L)).thenReturn(despachoBase);

        mockMvc.perform(put("/api/despachos/{id}/transportista/{idTransportista}", 1L, 100L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // CAMBIO: id -> idDespacho
                .andExpect(jsonPath("$.idDespacho").value(1));
    }
}