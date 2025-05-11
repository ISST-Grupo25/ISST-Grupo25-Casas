package com.isst.ISST_Grupo25_Casas;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccesoViviendaSeleniumTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String APP_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() throws InterruptedException {
        // Limpieza agresiva de procesos
        killSafariProcesses();
        Thread.sleep(2000); // Mayor tiempo de espera

        // Configuración de SafariOptions
        SafariOptions options = new SafariOptions();
        options.setCapability("safari:automaticInspection", true);
        options.setCapability("safari:automaticProfiling", true);
        options.setCapability("safari:useTechnologyPreview", false);
        options.setCapability("safari:noCache", true);

        // Inicialización con reintentos
        int attempts = 0;
        while (attempts < 3) {
            try {
                driver = new SafariDriver(options);
                break;
            } catch (SessionNotCreatedException e) {
                killSafariProcesses();
                Thread.sleep(2000);
                attempts++;
                if (attempts == 3) throw new RuntimeException("No se pudo iniciar SafariDriver después de 3 intentos");
            }
        }

        // Configuración de tiempos
        driver.manage().timeouts()
            .pageLoadTimeout(Duration.ofSeconds(10))
            .implicitlyWait(Duration.ofSeconds(3));
            
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (driver != null) {
            try {
                // 1. Cerrar alerts primero
                closeAlertsSafely();
                
                // 2. Limpiar almacenamiento local ANTES de cerrar
                ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
                
                // 3. Cerrar driver
                driver.quit();
                
            } catch (Exception e) {
                // Manejar excepciones silenciosamente
            } finally {
                // 4. Limpieza de procesos
                killSafariProcesses();
                Thread.sleep(500);
                driver = null;
                System.gc();
            }
        }
    }

    private void killSafariProcesses() {
        try {
            new ProcessBuilder("pkill", "-9", "-f", "safaridriver").start();
            new ProcessBuilder("pkill", "-9", "-f", "Safari").start();
            new ProcessBuilder("killall", "-9", "Safari").start();
        } catch (Exception ignored) {}
    }

    private void closeAlertsSafely() {
        try {
            if (driver != null) {
                driver.switchTo().alert().dismiss();
            }
        } catch (NoAlertPresentException | NoSuchWindowException ignored) {
        }
    }

    private void login(String email, String password) {
        driver.get(APP_URL + "/login");
        wait.until(ExpectedConditions.urlContains("login"));

        WebElement emailField = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("email"))
        );
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);

        WebElement loginBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-primary"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
    }

    private void enterPinSafely(String pin) {
        // Verificar que el modal está completamente visible
        WebElement pinDialog = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-dialog"))
        );
        wait.until(ExpectedConditions.attributeContains(pinDialog, "style", "display: flex"));
    
        for (char digit : pin.toCharArray()) {
            WebElement btn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".pin-btn[data-value='" + digit + "']:not(:disabled)")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    
        // Verificación más robusta del input
        WebElement pinInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-input"))
        );
        wait.until(d -> pinInput.getAttribute("value").length() == pin.length());
    }

    private void clickAcceptButton() {
        WebElement acceptBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("accept-btn"))
        );
        
        // Intento de clic con JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", acceptBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptBtn);
        
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    @Test
    @Order(1)
    void testAccesoFueraHorario() {
        login("m1@gmail.com", "1234");

        // 1. Esperar a que el botón de antiguas sea clickable
        WebElement historyButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("mostrarAntiguasBtn"))
        );
        
        // 2. Hacer scroll y click con JavaScript
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", historyButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", historyButton);

        // 3. Esperar a que la sección sea visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#antiguasSection[style*='display: block']")
        ));

        // 4. Buscar botón deshabilitado dentro del contexto correcto
        WebElement oldKeyButton = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#antiguasSection .key-btn[disabled]")
            )
        );

        assertAll("Validación cerradura antigua",
            () -> assertTrue(oldKeyButton.isDisplayed(), "El botón debería estar visible"),
            () -> assertFalse(oldKeyButton.isEnabled(), "El botón debería estar deshabilitado")
        );
    }

    @Test
    @Order(2)
    void testPinIncorrecto() {
        login("m1@gmail.com", "1234");

        // 1. Esperar a que el botón esté realmente activo
        WebElement activeKey = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".house:not(.reserva-inactiva) .key-btn:not(:disabled)")
            )
        );
        
        // 2. Click con JavaScript para evitar overlays
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeKey);

        // 3. Esperar a que el modal esté completamente visible
        WebElement pinDialog = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-dialog"))
        );
        wait.until(ExpectedConditions.attributeContains(pinDialog, "style", "display: flex"));

        enterPinSafely("0000");
        clickAcceptButton();

        // 4. Manejar posibles múltiples alerts
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept();
        
        if (!alertText.equals("❌ PIN incorrecto")) {
            alert = wait.until(ExpectedConditions.alertIsPresent());
            alertText = alert.getText();
            alert.accept();
        }
        
        assertEquals("❌ PIN incorrecto", alertText, "Mensaje de error incorrecto");
    }

    @Test
    @Order(3)
    void testAccesoExitoso() {
        login("m1@gmail.com", "1234");

        WebElement activeKey = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".house:not(.reserva-inactiva) .key-btn:not(:disabled)")
            )
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeKey);

        enterPinSafely("654321");
        clickAcceptButton();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("✅ PIN válido, acérquese a la cerradura", alert.getText(), "Mensaje de éxito incorrecto");
        alert.accept();
    }
}