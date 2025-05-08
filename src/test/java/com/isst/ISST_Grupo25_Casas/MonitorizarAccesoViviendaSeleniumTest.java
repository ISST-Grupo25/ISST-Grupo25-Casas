package com.isst.ISST_Grupo25_Casas;

import com.isst.ISST_Grupo25_Casas.models.*;
import com.isst.ISST_Grupo25_Casas.services.*;
import com.isst.ISST_Grupo25_Casas.repository.*;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.openqa.selenium.safari.SafariDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.sql.Date;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MonitorizarAccesoViviendaSeleniumTest {
    private static final Logger log = LoggerFactory.getLogger(MonitorizarAccesoViviendaSeleniumTest.class);

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    @Autowired
    private AccesoService accesoService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HuespedService huespedService;

    @Autowired
    private CerraduraService cerraduraService;

    @Autowired
    private GestorService gestorService;

    private Huesped huesped;
    private Reserva reserva;
    private Huesped segundoHuesped;
    private Reserva reserva2;
    private Cerradura cerradura;
    private Cerradura cerradura2;
    private String gestorEmail = "selenium_gestor2@gmail.com";
    private String gestorPassword = "clave123";

    @BeforeAll
    void prepararDatos() {
        // Crear gestor de prueba
        //Gestor gestor = new Gestor();
        String nombreG= "Gestor Selenium";
        String telefonoG = "666666666";
        Gestor gestor = gestorService.registerGestor(nombreG, gestorEmail, gestorPassword, telefonoG);

        // Crear cerradura para el gestor
        cerradura = cerraduraService.guardarCerradura("Casa Test Monitorización", "TOKEN123", gestor.getId());

        // Crear huésped
        huesped = huespedService.registerHuesped("Huesped Selenium", "huespedSelenium@gmail.com", "clave123");

        // Crear reserva activa
        reserva = new Reserva();
        reserva.setCerradura(cerradura);
        reserva.setFechainicio(Date.valueOf(LocalDate.now()));
        reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
        reserva.setPin("654321");
        reserva.setGestor(gestor);
        reserva = reservaRepository.save(reserva);

        reservaService.asociarHuesped(reserva.getId(), huesped.getId());

        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
    }

    @BeforeEach
    void setUp() {
        // Intentar usar chromedriver local si existe
        String localDriverPath = "C:\\Users\\inesv\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
        File localDriver = new File(localDriverPath);

        ChromeOptions options = new ChromeOptions();

        if (localDriver.exists()) {
            System.out.println("🔧 Usando chromedriver local: " + localDriverPath);
            System.setProperty("webdriver.chrome.driver", localDriverPath);
        } else {
            System.out.println("🌐 Usando WebDriverManager (puede fallar si no encuentra versión correcta)");
            WebDriverManager.chromedriver().setup();
        }

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    void loginComoGestor() {
        driver.get(APP_URL + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
        driver.findElement(By.id("password")).sendKeys(gestorPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/calendar"));
    }

    void abrirVistaMonitor() {
        WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);
    
        WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Historial de Acessos")));
        monitorLink.click();
        wait.until(ExpectedConditions.urlContains("/monitor"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
    }
    

    @AfterAll
    void limpiarDatos() {
        try {
            // Reservas (y sus accesos y enlaces intermedios)
        if (reserva2 != null && reserva2.getId() != null) {
            reservaService.eliminarReservaYHuespedes(reserva2.getId());
            System.out.println("🧹 Segunda reserva eliminada");
        }
        if (reserva != null && reserva.getId() != null) {
            reservaService.eliminarReservaYHuespedes(reserva.getId());
            System.out.println("🧹 Reserva eliminada");
        }

        // Cerraduras (tras eliminar reservas asociadas)
        if (cerradura2 != null && cerradura2.getId() != null) {
            cerraduraService.eliminarCerradura(cerradura2.getId());
            System.out.println("🧹 Segunda cerradura eliminada");
        }
        if (cerradura != null && cerradura.getId() != null) {
            cerraduraService.eliminarCerradura(cerradura.getId());
            System.out.println("🧹 Cerradura eliminada");
        }

        // Huéspedes
        if (segundoHuesped != null && segundoHuesped.getId() != null) {
            huespedService.eliminarHuesped(segundoHuesped.getId());
            System.out.println("🧹 Segundo huésped eliminado");
        }
        if (huesped != null && huesped.getId() != null) {
            huespedService.eliminarHuesped(huesped.getId());
            System.out.println("🧹 Huésped eliminado");
        }

        // Gestor (al final)
        gestorService.findByEmail(gestorEmail).ifPresent(gestor -> {
            gestorService.eliminarGestor(gestor.getId());
            System.out.println("🧹 Gestor eliminado");
        });


            
            //borrar los accesos de prueba


        } catch (Exception e) {
            System.err.println("❌ Error al limpiar datos de prueba: " + e.getMessage());
        }
    }

    @Test
    void testAccesoVisibleParaReservaActiva() throws InterruptedException {
        log.info("✅ Test iniciado correctamente");
        // 1. Ir a la home
        driver.get(APP_URL + "/");
        driver.manage().window().setSize(new Dimension(1280, 900));

        // 2. Iniciar sesión
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
        wait.until(ExpectedConditions.urlContains("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
        driver.findElement(By.id("password")).sendKeys(gestorPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // 3. Asegurar redirección correcta y apertura de menú
        wait.until(ExpectedConditions.urlContains("/calendar"));
        WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);

        // 4. Ir a monitorización
        WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Historial de Acessos")));
        monitorLink.click();
        wait.until(ExpectedConditions.urlContains("/monitor"));

        // 5. Ver tarjetas y hacer clic en la primera
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
        List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
        assertFalse(tarjetas.isEmpty(), "❌ No se muestran tarjetas de reservas");
        tarjetas.get(0).click();

        // 6. Esperar detalles y verificar accesos renderizados
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
        Thread.sleep(1000);

        WebElement lista = driver.findElement(By.id("accessList"));
        String contenido = lista.getText();

        System.out.println("🧾 Accesos visibles:");
        System.out.println(contenido);
        log.info("🧾 Contenido mostrado: {}", lista.getText());

        // ✅ Verificaciones reales
        assertTrue(contenido.contains("Éxito"), "❌ No se muestra 'Éxito' en el resultado del acceso");
        assertTrue(contenido.contains(huesped.getNombre()), "❌ No aparece el nombre del huésped esperado: " + huesped.getNombre());
        assertTrue(contenido.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), "❌ La fecha de hoy no aparece en el acceso");
    }

    @Test
    void testReservaConAccesoFallido() throws InterruptedException {
        // 🔁 Simulamos acceso fallido
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);

        // 🔐 Login y navegación
        loginComoGestor();

        // 📊 Entrar en la vista de monitorización
        abrirVistaMonitor();

        // 🃏 Selección de tarjeta de reserva
        List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
        assertFalse(tarjetas.isEmpty(), "❌ No hay tarjetas de reservas visibles");
        tarjetas.get(0).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
        Thread.sleep(1000); // espera al fetch

        WebElement lista = driver.findElement(By.id("accessList"));
        String contenido = lista.getText();

        // ✅ Verificaciones
        assertTrue(contenido.contains("Fallo"), "❌ No se muestra un acceso con resultado 'Fallo'");
    }

    @Test
    void testConteoAccesosExitosos() throws InterruptedException {
        // ✚ Insertar 3 accesos exitosos
        
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);

        // 🔐 Login y navegación
        loginComoGestor();
        abrirVistaMonitor();

        // 🃏 Selección de tarjeta
        List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
        assertFalse(tarjetas.isEmpty(), "❌ No hay tarjetas disponibles");
        tarjetas.get(0).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
        Thread.sleep(1000);

        WebElement lista = driver.findElement(By.id("accessList"));
        long numExitos = lista.getText().lines().filter(l -> l.contains("Éxito")).count();

        assertTrue(numExitos >= 6, "❌ Se esperaban al menos 6 accesos exitosos, pero hay: " + numExitos);
    }

    @Test
    void testAccesoDeVariosHuespedes() throws InterruptedException {
        // Crear segundo huésped
        segundoHuesped = huespedService.registerHuesped("Huesped Secundario", "huespedSecundario@gmail.com", "clave456");
        

        // Crear segunda cerradura (opcional, pero para evitar conflictos)
        cerradura2 = cerraduraService.guardarCerradura("Casa 2 del Test","TOKEN456", gestorService.findByEmail(gestorEmail).get().getId());

        // Crear segunda reserva (distinta cerradura)
        reserva2 = new Reserva();
        reserva2.setCerradura(cerradura2);
        reserva2.setFechainicio(Date.valueOf(LocalDate.now()));
        reserva2.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
        reserva2.setPin("987654");
        reserva2.setGestor(cerradura2.getGestor());
        reserva2 = reservaRepository.save(reserva2);
        reservaService.asociarHuesped(reserva2.getId(), segundoHuesped.getId());
    
        // 🔐 Login y navegación inicial
        loginComoGestor();
        abrirVistaMonitor();
    
        // Comprobar que hay al menos dos tarjetas
        List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
        assertTrue(tarjetas.size() >= 2, "❌ No hay al menos dos tarjetas de reservas");
    
        // Buscar la tarjeta de la nueva reserva
        WebElement tarjetaNueva = tarjetas.get(1); // suponiendo que se añade como segunda
        tarjetaNueva.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
        Thread.sleep(1000);
    
        WebElement lista = driver.findElement(By.id("accessList"));
        String contenidoAntes = lista.getText();
        assertTrue(contenidoAntes.contains("No hay accesos") || contenidoAntes.isBlank(),
                   "❌ Se esperaban 0 accesos al inicio para la nueva reserva");
    
        // ✅ Registrar acceso del segundo huésped
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, segundoHuesped, reserva2);
    
        // 🔄 Refrescar monitorización
        driver.navigate().refresh();
        abrirVistaMonitor();
        tarjetas = driver.findElements(By.cssSelector(".access-card"));
        tarjetaNueva = tarjetas.get(1); // asegurar que es la correcta
        tarjetaNueva.click();
    
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
        Thread.sleep(1000);
        WebElement lista2 = driver.findElement(By.id("accessList"));
        String contenidoDespues = lista2.getText();
    
        assertTrue(contenidoDespues.contains(segundoHuesped.getNombre()), "❌ No se muestra el acceso del segundo huésped");
        assertTrue(contenidoDespues.contains("Éxito"), "❌ No se muestra el resultado del acceso");
    }

}