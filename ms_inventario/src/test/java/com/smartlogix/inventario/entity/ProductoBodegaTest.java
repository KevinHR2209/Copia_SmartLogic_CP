package com.smartlogix.inventario.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoBodegaTest {

    private ProductoBodega productoBodega;

    @BeforeEach
    void setUp() {
        productoBodega = new ProductoBodega();
        productoBodega.setStockDisponible(100);
        productoBodega.setStockReservado(10);
    }

    @Test
    void reservarStock_ConCantidadValida_DebeMoverStock() {
        // Act
        productoBodega.reservarStock(20);

        // Assert
        assertEquals(80, productoBodega.getStockDisponible(), "El stock disponible debió disminuir en 20");
        assertEquals(30, productoBodega.getStockReservado(), "El stock reservado debió aumentar en 20");
    }

    @Test
    void reservarStock_ConStockInsuficiente_DebeLanzarExcepcion() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productoBodega.reservarStock(150);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        // Verificamos que los valores originales no se corrompieron por el fallo
        assertEquals(100, productoBodega.getStockDisponible());
        assertEquals(10, productoBodega.getStockReservado());
    }

    @Test
    void reservarStock_ConCantidadNegativa_DebeLanzarExcepcion() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productoBodega.reservarStock(-5);
        });
    }

    @Test
    void liberarReserva_ConCantidadValida_DebeMoverStock() {
        // Act
        productoBodega.liberarReserva(5);

        // Assert
        assertEquals(105, productoBodega.getStockDisponible(), "El stock disponible debió recuperar 5 unidades");
        assertEquals(5, productoBodega.getStockReservado(), "El stock reservado debió disminuir en 5");
    }

    @Test
    void liberarReserva_ConExcesoDeCantidad_DebeLanzarExcepcion() {
        // Intentar liberar 20 cuando solo hay 10 reservados
        assertThrows(IllegalStateException.class, () -> {
            productoBodega.liberarReserva(20);
        });
    }
}