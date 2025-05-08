package com.isst.ISST_Grupo25_Casas;

import com.isst.ISST_Grupo25_Casas.models.*;
import com.isst.ISST_Grupo25_Casas.repository.*;
import com.isst.ISST_Grupo25_Casas.services.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProgramarReservas {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    private final String gestorEmail = "gestor.calendar@test.com";
    private final String gestorPassword = "clave123";
    private Gestor gestor;
    private Cerradura cerradura;
    private Reserva reserva;
    private Huesped huesped;

    @Autowired private GestorService gestorService;
    @Autowired private CerraduraService cerraduraService;
    @Autowired private HuespedService huespedService;
    @Autowired private ReservaService reservaService;
    @Autowired private ReservaRepository reservaRepository;

    @BeforeAll
    void prepararDatos() {
        gestor = gestorService.registerGestor("Gestor Calendario", gestorEmail, gestorPassword, "612345678");
        cerradura = cerraduraService.guardarCerradura("Casa Calendario", "TOKENCAL", gestor.getId());
        huesped = huespedService.registerHuesped("Huésped Prueba", "huesped.prueba@calendario.com", "123456");

    }

    @BeforeEach
    void setUp() {
        String localDriverPath = "/usr/local/bin/chromedriver";
        File localDriver = new File(localDriverPath);
        ChromeOptions options = new ChromeOptions();

        if (localDriver.exists()) {
            System.setProperty("webdriver.chrome.driver", localDriverPath);
        } else {
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

    @AfterAll
    void limpiarDatos() {
        if (reserva != null) reservaService.eliminarReservaYHuespedes(reserva.getId());
        if (cerradura != null) cerraduraService.eliminarCerradura(cerradura.getId());
        if (gestor != null) gestorService.eliminarGestor(gestor.getId());
        if (huesped != null) huespedService.eliminarHuesped(huesped.getId());
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

    @Test
    void testAñadirYEditarReservaEnCalendario() throws InterruptedException {
        loginComoGestor();

        // 📅 Esperar calendario
        WebElement calendario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendar")));
        assertNotNull(calendario, "❌ No se encontró el calendario");

        // ➕ Abrir modal
        wait.until(ExpectedConditions.elementToBeClickable(By.id("addEvent"))).click();

        // 🔄 Forzar visibilidad por si acaso
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('accessModal').style.display = 'block';");
        js.executeScript("document.getElementById('modalOverlay').classList.add('show');");

        // Esperar formulario y checkbox
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessForm")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#huespedes-container input[type='checkbox']")));

        // ✅ Seleccionar datos de nueva reserva
        WebElement checkboxHuesped = driver.findElement(By.cssSelector("#huespedes-container input[type='checkbox']"));
        js.executeScript("arguments[0].scrollIntoView(true);", checkboxHuesped);
        if (!checkboxHuesped.isSelected()) {
            checkboxHuesped.click();
        }

        LocalDate hoy = LocalDate.now();
        LocalDate mañana = hoy.plusDays(1);
        
        js.executeScript("document.getElementById('fechaInicio').value = arguments[0];", hoy.toString());
        js.executeScript("document.getElementById('fechaFin').value = arguments[0];", mañana.toString());
        

        WebElement select = driver.findElement(By.id("casa"));
        select.findElement(By.cssSelector("option[value='" + cerradura.getId() + "']")).click();


        driver.findElement(By.cssSelector("#accessForm button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/calendar"));

        // Esperar explícitamente que aparezca al menos un evento en el DOM
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".fc-event")));
        
        List<WebElement> eventos = driver.findElements(By.cssSelector(".fc-event"));
        assertTrue(eventos.size() > 0, "❌ No se encontró evento creado");        
        

        // 💾 Guardar la reserva desde la BDD
        reserva = reservaRepository.findAll().stream()
            .filter(r -> r.getCerradura().getId().equals(cerradura.getId()))
            .max((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .orElse(null);

        assertNotNull(reserva, "❌ La reserva no fue persistida correctamente");

       // ✏️ Editar la reserva
        eventos.get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventModal")));
        driver.findElement(By.id("editEventBtn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editForm")));

        // 🔄 Seleccionar huésped en formulario de edición
        WebElement checkboxEditHuesped = driver.findElement(By.cssSelector("#edit-huespedes-container input[type='checkbox']"));
        if (!checkboxEditHuesped.isSelected()) {
            checkboxEditHuesped.click();
        }

        // 🗓️ Cambiar fecha de fin
        LocalDate diaRandom = hoy.plusDays(6);
        js.executeScript("document.getElementById('fechaFin').value = arguments[0];", diaRandom.toString());

        // 💾 Enviar formulario
        driver.findElement(By.cssSelector("#editForm button[type='submit']")).click();

        // 📅 Verificar edición en BDD
        Thread.sleep(1000);
        Reserva editada = reservaRepository.findById(reserva.getId()).orElse(null);
        assertNotNull(editada);
        assertEquals(Date.valueOf(LocalDate.now().plusDays(2)), editada.getFechafin(), "❌ La fecha de fin no se actualizó");
}
    
}
