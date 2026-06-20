package com.smartlogix.msclientes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")   // ← ¿está esta línea?
class MsClientesApplicationTests {

    @Test
    void contextLoads() {
    }
}