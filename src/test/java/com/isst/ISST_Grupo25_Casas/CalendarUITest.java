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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalendarUITest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    private final String gestorEmail = "gestor.calendar@test.com";
    private final String gestorPassword = "clave123";
    private Gestor gestor;
    private Cerradura cerradura;
    private Reserva reserva;

    @Autowired private GestorService gestorService;
    @Autowired private CerraduraService cerraduraService;
    @Autowired private ReservaService reservaService;
    @Autowired private ReservaRepository reservaRepository;

    @BeforeAll
    void prepararDatos() {
        gestor = gestorService.registerGestor("Gestor Calendario", gestorEmail, gestorPassword, "612345678");
        cerradura = cerraduraService.guardarCerradura("Casa Calendario", "TOKENCAL", gestor.getId());
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

    // Esperar a que cargue el calendario
    WebElement calendario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendar")));
    assertNotNull(calendario, "❌ No se encontró el calendario");

    // Abrir el modal de nueva reserva
    wait.until(ExpectedConditions.elementToBeClickable(By.id("addEvent"))).click();

    // Rellenar el formulario
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessForm")));
    driver.findElement(By.id("fechaInicio")).sendKeys(LocalDate.now().toString());
    driver.findElement(By.id("fechaFin")).sendKeys(LocalDate.now().plusDays(1).toString());

    // Seleccionar cerradura
    WebElement select = driver.findElement(By.id("casa"));
    select.findElement(By.cssSelector("option[value='" + cerradura.getId() + "']")).click();

    // Enviar formulario
    driver.findElement(By.cssSelector("form#accessForm button[type='submit']")).click();

    // Esperar redirección y evento en calendario
    wait.until(ExpectedConditions.urlContains("/calendar"));
    Thread.sleep(1000);
    assertTrue(driver.findElement(By.tagName("body")).getText().contains("Casa Calendario"), "❌ La reserva no se muestra en el calendario");

    // Obtener la reserva guardada
    reserva = reservaRepository.findAll().stream()
        .filter(r -> r.getCerradura().getId().equals(cerradura.getId()))
        .max((r1, r2) -> r1.getId().compareTo(r2.getId())).orElse(null);
    assertNotNull(reserva);

    // Hacer clic en el evento del calendario
    WebElement evento = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fc-event")));
    evento.click();

    // Esperar modal y hacer clic en "Modificar"
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventModal")));
    driver.findElement(By.id("editEventBtn")).click();

    // Esperar el formulario de edición y cambiar fechas
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editForm")));
    WebElement fechaFinInput = driver.findElement(By.id("editFechaFin"));
    fechaFinInput.clear();
    fechaFinInput.sendKeys(LocalDate.now().plusDays(2).toString());

    // Enviar cambios
    driver.findElement(By.cssSelector("#editForm button[type='submit']")).click();

    // Verificar que se guardó
    Thread.sleep(1000);
    Reserva editada = reservaRepository.findById(reserva.getId()).orElse(null);
    assertNotNull(editada);
    assertEquals(Date.valueOf(LocalDate.now().plusDays(2)), editada.getFechafin(), "❌ La fecha de fin no se actualizó");
}
}
