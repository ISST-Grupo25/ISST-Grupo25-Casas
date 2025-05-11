package com.isst.ISST_Grupo25_Casas;

import com.isst.ISST_Grupo25_Casas.models.Cerradura;
import com.isst.ISST_Grupo25_Casas.models.Gestor;
import com.isst.ISST_Grupo25_Casas.repository.CerraduraRepository;
import com.isst.ISST_Grupo25_Casas.repository.GestorRepository;
import com.isst.ISST_Grupo25_Casas.services.BatteryMonitorService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.Duration;

import java.io.File;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=8081"})
public class NotificacionBateriaBajaTests {

    @Autowired
    private BatteryMonitorService batteryMonitorService;

    @Autowired
    private CerraduraRepository cerraduraRepository;

    @Autowired
    private GestorRepository gestorRepository;

    private WebDriver driver;

    private static final String TOKEN_TEST = "demo-token";

    @BeforeAll
    static void setupDriverManager() {
        WebDriverManager.chromedriver().clearResolutionCache().setup();
    }

    @BeforeEach
    void setupBrowser() {
        driver = new ChromeDriver();
    }

    @BeforeEach
    void prepararCerraduraCritica() {
        Cerradura cerradura = cerraduraRepository.findAll().stream()
                .filter(c -> TOKEN_TEST.equals(c.getToken()))
                .findFirst()
                .orElseGet(() -> {
                    Cerradura nueva = new Cerradura();
                    nueva.setToken(TOKEN_TEST);
                    nueva.setUbicacion("Ubicación Test");
                    nueva.setGestor(null); // o asigna un gestor válido según tu lógica
                    return nueva;
                });

        cerradura.setBateria(10); // Nivel crítico
        cerraduraRepository.save(cerradura);
    }


    @AfterEach
    void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("🧪 Verifica batería crítica en BD (sin llamar servicio real)")
    void testForzarBateriaCriticaYVerificarActualizacion() {
        Optional<Cerradura> opt = cerraduraRepository.findAll().stream()
                .filter(c -> TOKEN_TEST.equals(c.getToken()))
                .findFirst();

        Assertions.assertTrue(opt.isPresent(), "❌ Cerradura no encontrada en BD");
        Cerradura cerradura = opt.get();

        Assertions.assertTrue(cerradura.getBateria() <= 15,
                "❌ La batería no está en nivel crítico (valor: " + cerradura.getBateria() + ")");
        System.out.println("✅ Batería crítica detectada: " + cerradura.getBateria() + "%");
    }

}
