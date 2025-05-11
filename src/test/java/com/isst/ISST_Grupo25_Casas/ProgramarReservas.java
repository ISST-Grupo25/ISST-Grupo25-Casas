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
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProgramarReservas {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    private final String gestorEmail = "ioh_isst_test_reservas@gmail.com";
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
        gestor = gestorService.registerGestor("Gestor Reservas test", gestorEmail, gestorPassword, "612345678");
        cerradura = cerraduraService.guardarCerradura("Casa Calendario test reservas", "TOKEN1323423", gestor.getId());
        huesped = huespedService.registerHuesped("Huésped prueba test", "huesped_test@calendario.com", "123456");
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
        Cerradura cerraduraNueva = cerraduraService.obtenerCerraduraPorUbicacion("Dirección casa nueva Test");
        if (cerraduraNueva != null) cerraduraService.eliminarCerradura(cerraduraNueva.getId());
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
        Thread.sleep(1000);

        WebElement calendario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("calendar")));
        assertNotNull(calendario, "❌ No se encontró el calendario");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("addEvent"))).click();
        Thread.sleep(1000);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.getElementById('accessModal').style.display = 'block';");
        js.executeScript("document.getElementById('modalOverlay').classList.add('show');");
        Thread.sleep(1000);

        driver.findElement(By.id("addCasa")).click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newCasaInputs")));
        Thread.sleep(1000);

        String ubicacionNueva = "Dirección casa nueva Test";
        String tokenNuevo = "TOKEN-NUEVA";

        driver.findElement(By.id("ubicacion")).sendKeys(ubicacionNueva);
        Thread.sleep(1000);
        driver.findElement(By.id("token")).sendKeys(tokenNuevo);
        Thread.sleep(1000);

        driver.findElement(By.cssSelector(".btn-confirmar-cerradura")).click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("newCasaInputs")));
        Thread.sleep(1000);

        WebElement selectCasa = driver.findElement(By.id("casa"));
        List<WebElement> opciones = selectCasa.findElements(By.tagName("option"));

        boolean nuevaCerraduraPresente = opciones.stream()
            .anyMatch(opt -> opt.getText().equals(ubicacionNueva));

        assertTrue(nuevaCerraduraPresente, "❌ La nueva cerradura no apareció en el selector");

        WebElement nuevaOpcion = opciones.stream()
            .filter(opt -> opt.getText().equals(ubicacionNueva))
            .findFirst()
            .orElseThrow();

        nuevaOpcion.click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessForm")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#huespedes-container input[type='checkbox']")));
        Thread.sleep(1000);

        WebElement checkboxHuesped = driver.findElement(By.cssSelector("#huespedes-container input[type='checkbox']"));
        js.executeScript("arguments[0].scrollIntoView(true);", checkboxHuesped);
        if (!checkboxHuesped.isSelected()) {
            checkboxHuesped.click();
        }
        Thread.sleep(1000);

        LocalDate hoy = LocalDate.now();
        LocalDate mañana = hoy.plusDays(1);
        js.executeScript("document.getElementById('fechaInicio').value = arguments[0];", hoy.toString());
        js.executeScript("document.getElementById('fechaFin').value = arguments[0];", mañana.toString());
        Thread.sleep(1000);

        WebElement select = driver.findElement(By.id("casa"));
        select.findElement(By.cssSelector("option[value='" + cerradura.getId() + "']")).click();
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("#accessForm button[type='submit']")).click();
        Thread.sleep(1500);

        wait.until(ExpectedConditions.urlContains("/calendar"));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".fc-event")));
        Thread.sleep(1000);

        List<WebElement> eventos = driver.findElements(By.cssSelector(".fc-event"));
        assertTrue(eventos.size() > 0, "❌ No se encontró evento creado");

        reserva = reservaRepository.findAll().stream()
            .filter(r -> r.getCerradura().getId().equals(cerradura.getId()))
            .max((r1, r2) -> r1.getId().compareTo(r2.getId()))
            .orElse(null);

        assertNotNull(reserva, "❌ La reserva no fue persistida correctamente");
        Thread.sleep(1500);

        eventos.get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventModal")));
        Thread.sleep(1000);

        driver.findElement(By.id("editEventBtn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editForm")));
        Thread.sleep(1000);

        WebElement checkboxEditHuesped = driver.findElement(By.cssSelector("#edit-huespedes-container input[type='checkbox']"));
        if (!checkboxEditHuesped.isSelected()) {
            checkboxEditHuesped.click();
        }
        Thread.sleep(1000);

        LocalDate diaRandom = hoy.plusDays(6);
        js.executeScript("document.getElementById('fechaFin').value = arguments[0];", diaRandom.toString());
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("#editForm button[type='submit']")).click();
        Thread.sleep(1500);

        Reserva editada = reservaRepository.findById(reserva.getId()).orElse(null);
        assertNotNull(editada);
        assertEquals(Date.valueOf(LocalDate.now().plusDays(2)), editada.getFechafin(), "❌ La fecha de fin no se actualizó");
        Thread.sleep(1000);

        // // 🔁 Pulsar botón de sincronizar con Google Calendar
        // WebElement botonSincronizar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("form[action='/calendar/sincronizar'] button[type='submit']")));
        // botonSincronizar.click();

        // // 🕐 Esperar redirección (sincronización exitosa)
        // wait.until(ExpectedConditions.urlContains("syncSuccess"));

        // // ⏳ Pequeña pausa para confirmar guardado
        // Thread.sleep(1000);

        // Reserva sincronizada = reservaRepository.findById(reserva.getId()).orElse(null);
        // assertNotNull(sincronizada, "❌ No se encontró la reserva tras sincronizar");
        // // assertNotNull(sincronizada.getEventId(), "❌ La reserva no tiene eventId después de sincronizar");
        // // assertFalse(sincronizada.getEventId().isBlank(), "❌ El eventId está vacío");
    
    }

}