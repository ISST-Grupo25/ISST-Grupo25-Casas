package com.isst.ISST_Grupo25_Casas;

import com.isst.ISST_Grupo25_Casas.models.*;
import com.isst.ISST_Grupo25_Casas.services.*;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificacionesBateriaBajaSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    @Autowired private GestorService gestorService;
    @Autowired private CerraduraService cerraduraService;
    @Autowired private HuespedService huespedService;
    @Autowired private ReservaService reservaService;
    @Autowired private AccesoService accesoService;

    private String gestorEmail = "bateria_gestor_" + System.currentTimeMillis() + "@test.com";
    private String gestorPassword = "clave123";

    private String huespedEmail = "bateria_huesped_" + System.currentTimeMillis() + "@test.com";
    private String huespedPassword = "clave123";

    private Gestor gestor;
    private Huesped huesped;
    private Cerradura cerradura;
    private Reserva reserva;

    @BeforeAll
    void prepararDatos() {
        gestor = gestorService.registerGestor("Gestor Batería", gestorEmail, gestorPassword, "600600600");
        cerradura = cerraduraService.guardarCerradura("Casa Batería", "BAT123", gestor.getId());
        cerradura.setBateria(10); // batería baja
        cerraduraService.guardar(cerradura);

        huesped = huespedService.registerHuesped("Huesped Prueba", huespedEmail, huespedPassword);

        reserva = new Reserva();
        reserva.setGestor(gestor);
        reserva.setCerradura(cerradura);
        reserva.setFechainicio(Date.valueOf(LocalDate.now()));
        reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
        reserva.setPin("123456");
        reserva = reservaService.guardar(reserva);
        reservaService.asociarHuesped(reserva.getId(), huesped.getId());

        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva); // Para mezclar notificaciones
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    void limpiarDatos() {
        reservaService.eliminarReservaYHuespedes(reserva.getId());
        cerraduraService.eliminarCerradura(cerradura.getId());
        huespedService.eliminarHuesped(huesped.getId());
        gestorService.eliminarGestor(gestor.getId());
    }

    private void loginComoGestor() {
        driver.get(APP_URL + "/login");
        pause(1000);
        driver.findElement(By.id("email")).sendKeys(gestorEmail);
        driver.findElement(By.id("password")).sendKeys(gestorPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/calendar"));
    }

    private void irANotificaciones() {
        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
        pause(500);
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.nav-notifications")));
        link.click();
        wait.until(ExpectedConditions.urlContains("/notifications"));
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testAlertaBateriaBajaApareceYCorrecta() {
        loginComoGestor();
        irANotificaciones();
        pause(1000);

        WebElement alerta = driver.findElement(By.cssSelector(".notification.warning"));
        assertTrue(alerta.getText().contains("batería baja"), "Debe aparecer texto de batería baja");
        assertTrue(alerta.getText().contains("10%"), "Debe mostrar el porcentaje de batería correcto");
    }

    @Test
    void testDescartarTodoQuitaAlertaYAccesos() {
        loginComoGestor();
        irANotificaciones();
        pause(1000);

        List<WebElement> notificaciones = driver.findElements(By.cssSelector(".notification"));
        assertTrue(notificaciones.size() >= 1, "Debe haber al menos una notificación");

        WebElement btn = driver.findElement(By.id("clear-all-btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        btn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".notification")));
        List<WebElement> restantes = driver.findElements(By.cssSelector(".notification"));
        assertEquals(0, restantes.size(), "No deben quedar notificaciones");

        WebElement vacio = driver.findElement(By.id("empty-msg"));
        assertTrue(vacio.isDisplayed(), "Debe mostrarse el mensaje de notificaciones vacías");
    }
}
