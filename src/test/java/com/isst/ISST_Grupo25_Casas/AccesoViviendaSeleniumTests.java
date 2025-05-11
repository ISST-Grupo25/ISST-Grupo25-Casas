package com.isst.ISST_Grupo25_Casas;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    void setUp() {
        // Si necesitas indicar la ruta del ChromeDriver manualmente, descomenta:
        // System.setProperty("webdriver.chrome.driver", "/ruta/a/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--start-maximized");
        // options.addArguments("--headless"); // Opcional, para modo sin interfaz

        driver = new ChromeDriver(options);

        driver.manage().timeouts()
            .pageLoadTimeout(Duration.ofSeconds(10))
            .implicitlyWait(Duration.ofSeconds(3));

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                closeAlertsSafely();
                ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
                driver.quit();
            } catch (Exception ignored) {
            } finally {
                driver = null;
                System.gc();
            }
        }
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

        WebElement pinInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-input"))
        );
        wait.until(d -> pinInput.getAttribute("value").length() == pin.length());
    }

    private void clickAcceptButton() {
        WebElement acceptBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("accept-btn"))
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", acceptBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptBtn);

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
    }

    @Test
    @Order(1)
    void testAccesoFueraHorario() {
        login("m1@gmail.com", "1234");

        WebElement historyButton = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("mostrarAntiguasBtn"))
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", historyButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", historyButton);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("#antiguasSection[style*='display: block']")
        ));

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

        WebElement activeKey = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector(".house:not(.reserva-inactiva) .key-btn:not(:disabled)")
            )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeKey);

        WebElement pinDialog = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("pin-dialog"))
        );
        wait.until(ExpectedConditions.attributeContains(pinDialog, "style", "display: flex"));

        enterPinSafely("0000");
        clickAcceptButton();

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

        // Esperar la alerta de éxito
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        alert.accept(); // cerrar el alert

        // Verificamos que el texto sea el esperado
        Assertions.assertEquals("✅ PIN válido, acérquese a la cerradura", alertText);
    }
    @Test
    @Order(4)
    void testLoginConCredencialesIncorrectas() {
        driver.get(APP_URL + "/login");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailField.sendKeys("usuario@falso.com");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("contrasenaFalsa");

        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-primary")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);

        // Verificamos que el mensaje de error aparece tras login fallido
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'Usuario o contraseña incorrectos')]")
        ));

        assertTrue(errorMessage.isDisplayed(), "Debería mostrarse el mensaje de error por login fallido");
    }


}

