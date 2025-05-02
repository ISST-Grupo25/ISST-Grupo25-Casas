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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccesoViviendaSeleniumTests {

    private WebDriver driver;
    private static final String APP_URL    = "http://localhost:8080";
    private static final String LOCKER_URL = "http://localhost:8090";

    @BeforeEach
    public void setUp() {
        // Usa Safari por defecto en macOS:
        driver = new SafariDriver();
        driver.manage().window().maximize();
        // Limpiar cookies para cada test y asegurar sesión limpia
        driver.manage().deleteAllCookies();

        /*
         * Si no estás usando macOS y prefieres Chrome:
         * comenta SafariDriver y descomenta estas líneas:
         *
         * WebDriverManager.chromedriver().setup();
         * driver = new ChromeDriver();
         * driver.manage().deleteAllCookies();
         */
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testFlujoAccesoExitoso() {
        driver.get(APP_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Credenciales: reemplaza con las tuyas en la BD
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameInput.sendKeys("m1@gmail.com");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.id("loginBtn")).click();

        assertEquals(APP_URL + "/dashboard", driver.getCurrentUrl());

        // Introducción de PIN y desbloqueo
        driver.findElement(By.id("pinInput")).sendKeys("1234");
        driver.findElement(By.id("submitPinBtn")).click();
        WebElement pinOk = driver.findElement(By.id("pinOkMsg"));
        assertTrue(pinOk.isDisplayed());

        // Flujo con el servicio de cerradura simulado
        driver.get(LOCKER_URL + "/lock-service");
        driver.findElement(By.id("checkProximityBtn")).click();
        WebElement proxOk = driver.findElement(By.id("proximityOkMsg"));
        assertTrue(proxOk.isDisplayed());
        driver.findElement(By.id("openLockBtn")).click();
        WebElement lockOpened = driver.findElement(By.id("lockOpenedMsg"));
        assertTrue(lockOpened.isDisplayed());

        // Verificación final en la aplicación principal
        driver.get(APP_URL + "/dashboard");
        WebElement finalMsg = driver.findElement(By.id("accessResult"));
        assertEquals("Acceso completado: puerta abierta", finalMsg.getText());
    }

    @Test
    void testCredencialesInvalidas() {
        driver.get(APP_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameInput.sendKeys("m1@gmail.com");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        driver.findElement(By.id("loginBtn")).click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginError")));
        assertTrue(errorMsg.isDisplayed());
        assertEquals("Usuario o clave incorrectos.", errorMsg.getText());
    }

    @Test
    void testAccesoFueraHorario() {
        driver.get(APP_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        usernameInput.sendKeys("m1@gmail.com");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.id("loginBtn")).click();

        WebElement lockStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lockStatus")));
        assertTrue(lockStatus.isDisplayed());

        driver.findElement(By.id("pinInput")).sendKeys("1234");
        driver.findElement(By.id("unlockBtn")).click();

        WebElement timeError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("timeError")));
        assertTrue(timeError.isDisplayed());
        assertEquals("Acceso denegado: fuera del horario permitido.", timeError.getText());
    }
}
