// package com.isst.ISST_Grupo25_Casas;
 
// import com.isst.ISST_Grupo25_Casas.models.*;
// import com.isst.ISST_Grupo25_Casas.repository.*;
// import com.isst.ISST_Grupo25_Casas.services.*;
 
// import org.junit.jupiter.api.*;
// import org.openqa.selenium.*;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
 
// import java.sql.Date;
// import java.time.Duration;
// import java.time.LocalDate;
 
// import static org.junit.jupiter.api.Assertions.*;
 
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class AccesoViviendaSeleniumTests {
 
//     private WebDriver driver;
//     private WebDriverWait wait;
//     private static final String APP_URL = "http://localhost:8080";
 
//     @Autowired
//     private HuespedService huespedService;
 
//     @Autowired
//     private CerraduraService cerraduraService;
 
//     @Autowired
//     private ReservaRepository reservaRepository;
 
//     @Autowired
//     private ReservaService reservaService;
 
//     @Autowired
//     private GestorService gestorService;
 
//     private Huesped huesped;
//     private Cerradura cerradura;
//     private Reserva reserva;
//     private String gestorEmail = "gestor1@gmail.com";
 
//     @BeforeAll
//     void prepararDatos() {
//         Gestor gestor = gestorService.registerGestor("Gestor Test", gestorEmail, "clave123", "123456789");
 
//         cerradura = cerraduraService.guardarCerradura("Casa Selenium", "TOKEN123", gestor.getId());
 
//         huesped = huespedService.registerHuesped("Selenium Tester", "selenium_test_huesped@gmail.com", "seleniumabc1");
 
//         // Reserva ACTUAL (válida para testAccesoExitoso)
//         reserva = new Reserva();
//         reserva.setFechainicio(Date.valueOf(LocalDate.now()));
//         reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
//         reserva.setPin("654322");
//         reserva.setCerradura(cerradura);
//         reserva.setGestor(gestor);
//         reserva = reservaRepository.save(reserva);
 
//         reservaService.asociarHuesped(reserva.getId(), huesped.getId());
 
//         // Reserva ANTIGUA (válida para testAccesoFueraHorario)
//         Reserva reservaAntigua = new Reserva();
//         reservaAntigua.setFechainicio(Date.valueOf(LocalDate.now().minusDays(3)));
//         reservaAntigua.setFechafin(Date.valueOf(LocalDate.now().minusDays(1)));
//         reservaAntigua.setPin("987654");
//         reservaAntigua.setCerradura(cerradura);
//         reservaAntigua.setGestor(gestor);
//         reservaAntigua = reservaRepository.save(reservaAntigua);
 
//         reservaService.asociarHuesped(reservaAntigua.getId(), huesped.getId());
//     }
 
//     private void pause(long millis) {
//          try {
//             Thread.sleep(millis);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }
 
//     @BeforeEach
//     void setUp() {
//         ChromeOptions options = new ChromeOptions();
//         options.addArguments("--remote-allow-origins=*");
//         options.addArguments("--start-maximized");
 
//         driver = new ChromeDriver(options);
//         driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
//         driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
//         wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//     }
 
//     @AfterEach
//     void tearDown() {
//         if (driver != null) {
//             try {
//                 closeAlertsSafely();
//                 ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
//                 driver.quit();
//             } catch (Exception ignored) {
//             } finally {
//                 driver = null;
//                 System.gc();
//             }
//         }
//     }
 
//     private void closeAlertsSafely() {
//         try {
//             if (driver != null) {
//                 driver.switchTo().alert().dismiss();
//             }
//         } catch (NoAlertPresentException | NoSuchWindowException ignored) {}
//     }
 
//     private void login(String email, String password) {
//         driver.get(APP_URL + "/login");
//         wait.until(ExpectedConditions.urlContains("login"));
 
//         WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         emailField.sendKeys(email);
 
//         WebElement passwordField = driver.findElement(By.id("password"));
//         passwordField.sendKeys(password);
 
//         WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-primary")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
//     }
 
//     private void enterPinSafely(String pin) {
//         WebElement pinDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pin-dialog")));
//         wait.until(ExpectedConditions.attributeContains(pinDialog, "style", "display: flex"));
 
//         for (char digit : pin.toCharArray()) {
//             WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
//                 By.cssSelector(".pin-btn[data-value='" + digit + "']:not(:disabled)")
//             ));
//             ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
//         }
 
//         WebElement pinInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pin-input")));
//         wait.until(d -> pinInput.getAttribute("value").length() == pin.length());
//     }
 
//     private void clickAcceptButton() {
//         WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("accept-btn")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", acceptBtn);
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptBtn);
 
//         try {
//             Thread.sleep(500);
//         } catch (InterruptedException ignored) {}
//     }
 
//     @Test
//     @Order(1)
//     void testAccesoFueraHorario() {
//         login("selenium_test_huesped@gmail.com", "seleniumabc1");
//         pause(2000);
        
//         WebElement historyButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("mostrarAntiguasBtn")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", historyButton);
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", historyButton);
 
//         wait.until(ExpectedConditions.visibilityOfElementLocated(
//             By.cssSelector("#antiguasSection[style*='display: block']")
//         ));
 
//         WebElement oldKeyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
//             By.cssSelector("#antiguasSection .key-btn[disabled]")
//         ));
//         pause(2000);
 
//         assertAll("Validación cerradura antigua",
//             () -> assertTrue(oldKeyButton.isDisplayed(), "El botón debería estar visible"),
//             () -> assertFalse(oldKeyButton.isEnabled(), "El botón debería estar deshabilitado")
//         );
//         pause(2000);
//     }
 
//     @Test
//     @Order(2)
//     void testPinIncorrecto() {
//         login("selenium_test_huesped@gmail.com", "seleniumabc1");
//         pause(2000);
 
//         WebElement activeKey = wait.until(ExpectedConditions.elementToBeClickable(
//             By.cssSelector(".house:not(.reserva-inactiva) .key-btn:not(:disabled)")
//         ));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeKey);
 
//         enterPinSafely("0000");
//         clickAcceptButton();
//         pause(2000);
 
//         Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//         String alertText = alert.getText();
//         alert.accept();
 
//         if (!alertText.equals("❌ PIN incorrecto")) {
//             alert = wait.until(ExpectedConditions.alertIsPresent());
//             alertText = alert.getText();
//             alert.accept();
//         }
 
//         assertEquals("❌ PIN incorrecto", alertText, "Mensaje de error incorrecto");
//         pause(2000);
//     }
 
//     @Test
//     @Order(3)
//     void testAccesoExitoso() {
//         login("selenium_test_huesped@gmail.com", "seleniumabc1");
//         pause(2000);
 
//         WebElement activeKey = wait.until(ExpectedConditions.elementToBeClickable(
//             By.cssSelector(".house:not(.reserva-inactiva) .key-btn:not(:disabled)")
//         ));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", activeKey);
 
//         enterPinSafely("654322");
//         clickAcceptButton();
//         pause(2000);
        
//         Alert alert = wait.until(ExpectedConditions.alertIsPresent());
//         String alertText = alert.getText();
//         alert.accept();
 
//         assertEquals("✅ PIN válido, acérquese a la cerradura", alertText);
//         pause(2000);
//     }
 
//     @Test
//     @Order(4)
//     void testLoginConCredencialesIncorrectas() {
//         driver.get(APP_URL + "/login");
 
//         WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
//         emailField.sendKeys("usuario@falso.com");
 
//         WebElement passwordField = driver.findElement(By.id("password"));
//         passwordField.sendKeys("contrasenaFalsa");
        
//         pause(2000);
//         WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn-primary")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
 
//         WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
//             By.xpath("//*[contains(text(),'Usuario o contraseña incorrectos')]")
//         ));
 
//         assertTrue(errorMessage.isDisplayed(), "Debería mostrarse el mensaje de error por login fallido");
//         pause(2000);
//     }
 
//     @AfterAll
//     void limpiarDatos() {
//         try {
//             // Eliminar todas las reservas asociadas al huésped
//             if (huesped != null && huesped.getId() != null) {
//                 var reservas = reservaRepository.findByHuespedesId(huesped.getId());
//                 for (Reserva r : reservas) {
//                     reservaService.eliminarReservaYHuespedes(r.getId());
//                     System.out.println("🧹 Reserva eliminada (ID: " + r.getId() + ")");
//                 }
//             }
 
//             if (cerradura != null && cerradura.getId() != null) {
//                 cerraduraService.eliminarCerradura(cerradura.getId());
//                 System.out.println("🧹 Cerradura eliminada");
//             }
 
//             if (huesped != null && huesped.getId() != null) {
//                 huespedService.eliminarHuesped(huesped.getId());
//                 System.out.println("🧹 Huésped eliminado");
//             }
 
//             gestorService.findByEmail(gestorEmail).ifPresent(gestor -> {
//                 gestorService.eliminarGestor(gestor.getId());
//                 System.out.println("🧹 Gestor eliminado");
//             });
 
//         } catch (Exception e) {
//             System.err.println("❌ Error al limpiar datos de prueba: " + e.getMessage());
//         }
//     }
 
// }
 