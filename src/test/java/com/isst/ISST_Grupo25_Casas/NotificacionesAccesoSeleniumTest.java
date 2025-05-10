package com.isst.ISST_Grupo25_Casas;

import com.isst.ISST_Grupo25_Casas.models.*;
import com.isst.ISST_Grupo25_Casas.services.*;
import com.isst.ISST_Grupo25_Casas.repository.*;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NotificacionesAccesoSeleniumTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    @Autowired private AccesoService accesoService;
    @Autowired private ReservaService reservaService;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private HuespedService huespedService;
    @Autowired private CerraduraService cerraduraService;
    @Autowired private GestorService gestorService;

    private String gestorEmail = "selenium_gestor2@gmail.com";
    private String gestorPassword = "clave123";
    private Gestor gestor;
    private Huesped huesped;
    private Reserva reserva;

    @BeforeAll
    void prepararDatos() {
        // Crear gestor de prueba
        gestor = gestorService.registerGestor("Gestor Selenium", gestorEmail, gestorPassword, "666666666");
        // Crear cerradura
        Cerradura c = cerraduraService.guardarCerradura("Casa Test", "TOKEN123", gestor.getId());
        // Crear huésped
        huesped = huespedService.registerHuesped("Huesped Selenium", "huespedSelenium@gmail.com", "clave123");
        // Crear reserva
        reserva = new Reserva();
        reserva.setCerradura(c);
        reserva.setGestor(gestor);
        reserva.setFechainicio(Date.valueOf(LocalDate.now()));
        reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
        reserva.setPin("654321");
        reserva = reservaRepository.save(reserva);
        reservaService.asociarHuesped(reserva.getId(), huesped.getId());
        // Un acceso inicial
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
    }

    @BeforeEach
    void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedriver.exe");

        ChromeOptions opts = new ChromeOptions();
        driver = new ChromeDriver(opts);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }


    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterAll
    void limpiarDatos() {
        reservaService.eliminarReservaYHuespedes(reserva.getId());
        cerraduraService.eliminarCerradura(reserva.getCerradura().getId());
        huespedService.eliminarHuesped(huesped.getId());
        gestorService.eliminarGestor(gestor.getId());
    }

    private void loginComoGestor() {
        driver.get(APP_URL + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
        wait.until(ExpectedConditions.urlContains("/login"));
        driver.findElement(By.id("email")).sendKeys(gestorEmail);
        driver.findElement(By.id("password")).sendKeys(gestorPassword);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/calendar"));
    }

    private void irANotificaciones() {
        WebElement menu = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menu);
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
    void testNuevaNotificacionAparece() {
        pause(3000);
        loginComoGestor();
        accesoService.marcarTodosLeidos(
        accesoService.obtenerAccesosNoLeidos(
                reservaService.obtenerReservasPorGestor(gestor.getId())
            )
        );
        pause(4000);
        irANotificaciones();
        pause(3000);
        // contamos las que hay
        int antes = driver.findElements(By.cssSelector(".notification")).size();
        // generamos un nuevo acceso
        accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);
        pause(500);
        // refrescamos
        driver.navigate().refresh();
        pause(3000);
        irANotificaciones();
        pause(3000);
        int despues = driver.findElements(By.cssSelector(".notification")).size();
        pause(500);
        assertEquals(antes + 1, despues, "Debe aparecer una nueva notificación");
    }

    @Test
    void testDescartarUno() {
        pause(3000);
        loginComoGestor();
        pause(3000);
        for (int i = 0; i < 3; i++) {
            accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);
        }
        irANotificaciones();
        pause(3000);
        List<WebElement> cards = driver.findElements(By.cssSelector(".notification"));
        assertFalse(cards.isEmpty(), "Debe haber al menos una notificación");
        int inicial = cards.size();
        pause(3000);
        // descartar la primera
        WebElement btn = cards.get(0).findElement(By.cssSelector(".discard-btn"));
        btn.click();
        pause(3000);
        // espera que baje en 1
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".notification"), inicial - 1));
        
        List<WebElement> tras = driver.findElements(By.cssSelector(".notification"));
        assertEquals(inicial - 1, tras.size());
        pause(2000);
    }

    @Test
    void testDescartarTodos() {
        pause(3000);
        loginComoGestor();
        pause(3000);
        // añadimos un par más para asegurar
        for (int i = 0; i < 5; i++) {
            accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);
        }

        pause(1000);
        irANotificaciones();
        pause(3000);
        List<WebElement> cards = driver.findElements(By.cssSelector(".notification"));
        assertTrue(cards.size() >= 5, "Debe haber al menos 5 notificaciones");
        WebElement allBtn = driver.findElement(By.id("clear-all-btn"));
        allBtn.click();
        pause(3000);
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".notification"), 0));
        assertTrue(driver.findElements(By.cssSelector(".notification")).isEmpty());
        pause(3000);
    }


}
