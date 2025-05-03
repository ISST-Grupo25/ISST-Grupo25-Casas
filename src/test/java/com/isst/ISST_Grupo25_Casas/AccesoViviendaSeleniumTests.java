package com.isst.ISST_Grupo25_Casas;

/**
 * Pruebas Selenium para el caso de uso "Acceder a la vivienda".
 *
 * Instrucciones de uso:
 * - Si usas **Chrome**, comenta la línea de SafariDriver e
 *   descomenta las líneas de WebDriverManager + ChromeDriver.
 * - Si usas **Safari** (macOS), comenta las líneas de ChromeDriver
 *   y deja activada la línea de SafariDriver.
 * - Cambia el usuario y contraseña (m1@gmail.com / 1234) por los
 *   que tengas registrados en tu base de datos de tu aplicación.
 * - Usa como PIN de la cerradura **654321** (modifica si tu BD tiene otro).
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AccesoViviendaSeleniumTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL    = "http://localhost:8080";
    private static final String LOCKER_URL = "http://localhost:8090";

    @BeforeEach
    void setUp() {
        // Inicia una nueva sesión de navegador para cada test:
        driver = new SafariDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        /*
         * Si prefieres usar Chrome:
         * WebDriverManager.chromedriver().setup();
         * driver = new ChromeDriver();
         * driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
         * wait = new WebDriverWait(driver, Duration.ofSeconds(10));
         */
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFlujoAccesoExitoso() {
        driver.get(APP_URL + "/login");

        // Login
        WebElement emailInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys("m1@gmail.com");
        WebElement passInput = driver.findElement(By.id("password"));
        passInput.clear();
        passInput.sendKeys("1234");
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        assertEquals(APP_URL + "/dashboard", driver.getCurrentUrl());

        // PIN y desbloqueo en home-access (PIN 654321)
        driver.findElement(By.id("pinInput")).sendKeys("654321");
        driver.findElement(By.id("submitPinBtn")).click();
        WebElement pinOk = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pinOkMsg"))
        );
        assertTrue(pinOk.isDisplayed());

        // Cerradura simulada
        driver.get(LOCKER_URL + "/lock-service");
        driver.findElement(By.id("checkProximityBtn")).click();
        WebElement proxOk = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("proximityOkMsg"))
        );
        assertTrue(proxOk.isDisplayed());
        driver.findElement(By.id("openLockBtn")).click();
        WebElement lockOpened = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("lockOpenedMsg"))
        );
        assertTrue(lockOpened.isDisplayed());

        // Verificación final en dashboard
        driver.get(APP_URL + "/dashboard");
        WebElement finalMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("accessResult"))
        );
        assertEquals("Acceso completado: puerta abierta", finalMsg.getText());
    }

    @Test
    void testCredencialesInvalidas() {
        driver.get(APP_URL + "/login");

        WebElement emailInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys("m1@gmail.com");
        WebElement passInput = driver.findElement(By.id("password"));
        passInput.clear();
        passInput.sendKeys("wrongpass");
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        WebElement errorMsg = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(),'Usuario o contraseña incorrectos')]")
            )
        );
        assertTrue(errorMsg.isDisplayed());
    }

    @Test
    void testAccesoFueraHorario() {
        driver.get(APP_URL + "/login");

        WebElement emailInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys("m1@gmail.com");
        WebElement passInput = driver.findElement(By.id("password"));
        passInput.clear();
        passInput.sendKeys("1234");
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        // Ahora estamos en home-access: espera al botón key-btn
        WebElement keyBtn = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.key-btn"))
        );
        assertTrue(keyBtn.isDisplayed());

        // Simula clic en la cerradura y rechaza fuera de horario usando PIN 654321
        keyBtn.click();
        WebElement pinInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-input"))
        );
        pinInput.sendKeys("654321");
        driver.findElement(By.id("accept-btn")).click();

        WebElement timeError = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("timeError"))
        );
        assertTrue(timeError.isDisplayed());
        assertEquals("Acceso denegado: fuera del horario permitido.", timeError.getText());
    }
}
