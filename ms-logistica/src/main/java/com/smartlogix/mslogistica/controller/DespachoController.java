package com.smartlogix.mslogistica.controller;

import com.smartlogix.mslogistica.model.Despacho;
import com.smartlogix.mslogistica.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService service;

    @GetMapping
    public List<Despacho> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despacho> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Despacho> crear(@RequestBody Despacho despacho) {
        Despacho creado = service.crear(despacho);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Despacho> cambiarEstado(@PathVariable Long id,
                                                  @RequestParam String estado) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado));
    }

    @PutMapping("/{id}/transportista/{idTransportista}")
    public ResponseEntity<Despacho> asignarTransportista(@PathVariable Long id,
                                                         @PathVariable Long idTransportista) {
        return ResponseEntity.ok(service.asignarTransportista(id, idTransportista));
    }
}