package com.smartlogix.msclientes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.msclientes.model.Cliente;
import com.smartlogix.msclientes.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Anotación moderna
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // Spring Boot 3.5.x compatible
    private ClienteService clienteService;

    private Cliente clienteBase;

    @BeforeEach
    void setUp() {
        clienteBase = new Cliente();
        clienteBase.setIdCliente(1L);
        clienteBase.setRut("11222333-4");
        clienteBase.setNombre("Juan");
        clienteBase.setApellidoPaterno("Perez");
        clienteBase.setCorreo("juan.perez@correo.com");
    }

    @Test
    void listar_DebeRetornarStatus200YLista() throws Exception {
        when(clienteService.listar()).thenReturn(Arrays.asList(clienteBase));

        mockMvc.perform(get("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(1))
                .andExpect(jsonPath("$[0].rut").value("11222333-4"))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarStatus200() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(clienteBase);

        mockMvc.perform(get("/api/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void crear_DebeRetornarStatus201YClienteCreado() throws Exception {
        when(clienteService.crear(any(Cliente.class))).thenReturn(clienteBase);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteBase)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(1));
    }

    @Test
    void actualizar_DebeRetornarStatus200YClienteActualizado() throws Exception {
        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setIdCliente(1L);
        clienteActualizado.setNombre("Pedro"); // Simulamos un cambio de nombre

        when(clienteService.actualizar(eq(1L), any(Cliente.class))).thenReturn(clienteActualizado);

        mockMvc.perform(put("/api/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pedro"));
    }

    @Test
    void eliminar_DebeRetornarStatus204() throws Exception {
        // En los métodos void (como eliminar), usamos doNothing() en Mockito
        doNothing().when(clienteService).eliminar(1L);

        mockMvc.perform(delete("/api/clientes/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}