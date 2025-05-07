// package com.isst.ISST_Grupo25_Casas;

// import com.isst.ISST_Grupo25_Casas.models.*;
// import com.isst.ISST_Grupo25_Casas.services.*;
// import com.isst.ISST_Grupo25_Casas.repository.*;

// import org.junit.jupiter.api.*;
// import org.openqa.selenium.*;

// import io.github.bonigarcia.wdm.WebDriverManager;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeOptions;

// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.openqa.selenium.safari.SafariDriver;

// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;



// import java.sql.Date;

// import java.time.Duration;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.io.File;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class MonitorizarAccesoViviendaSeleniumTest {
//     private static final Logger log = LoggerFactory.getLogger(MonitorizarAccesoViviendaSeleniumTest.class);

//     private WebDriver driver;
//     private WebDriverWait wait;
//     private static final String APP_URL = "http://localhost:8080";

//     @Autowired
//     private AccesoService accesoService;

//     @Autowired
//     private ReservaService reservaService;

//     @Autowired
//     private ReservaRepository reservaRepository;

//     @Autowired
//     private HuespedService huespedService;

//     @Autowired
//     private CerraduraService cerraduraService;

//     @Autowired
//     private GestorService gestorService;

//     private Huesped huesped;
//     private Reserva reserva;
//     private Cerradura cerradura;
//     private String gestorEmail= "inesvalcarcel03@gmail.com";
//     private String gestorPassword = "hola";

//     @BeforeAll
//     void prepararDatos() {
//         // 1. Obtener un huésped válido
//         List<Huesped> huespedes = huespedService.obtenerTodosLosHuespedes();
//         assertFalse(huespedes.isEmpty(), "Debe haber al menos un huésped para test");
//         huesped = huespedes.get(0);
    
//         // 2. Crear o buscar cerradura gestionada por un gestor válido
//         Gestor gestor = gestorService.findByEmail(gestorEmail).orElseThrow(() -> new IllegalArgumentException("Gestor no encontrado con el email: " + gestorEmail)); /* gestor de test existente */;
//         String ubicacion = "Casa Test Monitorización";
//         String token = "TOKEN123";
//         Long gestorId = gestor.getId(); // ID del gestor existente

//         cerradura = cerraduraService.guardarCerradura(ubicacion,token, gestorId ); // Usa tu servicio
    
//         // 3. Crear reserva activa desde hoy a mañana
//         reserva = new Reserva();
//         reserva.setCerradura(cerradura);
//         reserva.setFechainicio(Date.valueOf(LocalDate.now()));
//         reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
//         reserva.setPin("654321");
//         reserva.setGestor(gestor);
//         reserva = reservaRepository.save(reserva); // Usa tu servicio
    
//         // 4. Asociar huésped a la reserva
//         reservaService.asociarHuesped(reserva.getId(), huesped.getId()); // método personalizado
    
//         // 5. Insertar acceso válido hoy
//         accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
//     }

//     @BeforeEach
//     void setUp() {
//         // Intentar usar chromedriver local si existe
//         String localDriverPath = "C:\\Users\\inesv\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
//         File localDriver = new File(localDriverPath);

//         ChromeOptions options = new ChromeOptions();

//         if (localDriver.exists()) {
//             System.out.println("🔧 Usando chromedriver local: " + localDriverPath);
//             System.setProperty("webdriver.chrome.driver", localDriverPath);
//         } else {
//             System.out.println("🌐 Usando WebDriverManager (puede fallar si no encuentra versión correcta)");
//             WebDriverManager.chromedriver().setup();
//         }

//         driver = new ChromeDriver(options);
//         driver.manage().window().setSize(new Dimension(1280, 900));
//         driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
//         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//     }


//     @AfterEach
//     void tearDown() {
//         if (driver != null) driver.quit();
//     }

//     // void login(String email, String password) {
//     //     driver.get(APP_URL + "/login");
//     //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(email);
//     //     driver.findElement(By.id("password")).sendKeys(password);
//     //     driver.findElement(By.cssSelector("button[type='submit']")).click();
//     //     wait.until(ExpectedConditions.urlContains("/home-access")); // o la URL tras login
//     // }

//     void loginComoGestor() {
//         driver.get(APP_URL + "/");
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
//         wait.until(ExpectedConditions.urlContains("/login"));
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
//         driver.findElement(By.id("password")).sendKeys("hola");
//         driver.findElement(By.cssSelector("button[type='submit']")).click();
//         wait.until(ExpectedConditions.urlContains("/calendar"));
//     }

//     void abrirVistaMonitor() {
//         WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);
    
//         WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Monitorizar Acessos")));
//         monitorLink.click();
//         wait.until(ExpectedConditions.urlContains("/monitor"));
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
//     }
    

//     @AfterAll
//     void limpiarDatos() {
//         try {
//             if (reserva != null && reserva.getId() != null) {
//                 reservaService.eliminarReservaYHuespedes(reserva.getId());
//                 System.out.println("🧹 Reserva eliminada");
//             }

//             if (cerradura != null && cerradura.getId() != null) {
//                 cerraduraService.eliminarCerradura(cerradura.getId());
//                 System.out.println("🧹 Cerradura eliminada");
//             }

//             // Si creaste accesos manualmente y quieres borrarlos, añade aquí:
//             // accesoService.eliminarAccesosPorReserva(reserva.getId()); (si tienes ese método)
//         } catch (Exception e) {
//             System.err.println("❌ Error al limpiar datos de prueba: " + e.getMessage());
//         }
//     }

//     @Test
//     void testAccesoVisibleParaReservaActiva() throws InterruptedException {
//         log.info("✅ Test iniciado correctamente");
//         // 1. Ir a la home
//         driver.get(APP_URL + "/");
//         driver.manage().window().setSize(new Dimension(1280, 900));

//         // 2. Iniciar sesión
//         wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
//         wait.until(ExpectedConditions.urlContains("/login"));
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
//         driver.findElement(By.id("password")).sendKeys(gestorPassword);
//         driver.findElement(By.cssSelector("button[type='submit']")).click();

//         // 3. Asegurar redirección correcta y apertura de menú
//         wait.until(ExpectedConditions.urlContains("/calendar"));
//         WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
//         ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);

//         // 4. Ir a monitorización
//         WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Monitorizar Acessos")));
//         monitorLink.click();
//         wait.until(ExpectedConditions.urlContains("/monitor"));

//         // 5. Ver tarjetas y hacer clic en la primera
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
//         List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
//         assertFalse(tarjetas.isEmpty(), "❌ No se muestran tarjetas de reservas");
//         tarjetas.get(0).click();

//         // 6. Esperar detalles y verificar accesos renderizados
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
//         Thread.sleep(1000);

//         WebElement lista = driver.findElement(By.id("accessList"));
//         String contenido = lista.getText();

//         System.out.println("🧾 Accesos visibles:");
//         System.out.println(contenido);
//         log.info("🧾 Contenido mostrado: {}", lista.getText());

//         // ✅ Verificaciones reales
//         assertTrue(contenido.contains("Éxito"), "❌ No se muestra 'Éxito' en el resultado del acceso");
//         assertTrue(contenido.contains(huesped.getNombre()), "❌ No aparece el nombre del huésped esperado: " + huesped.getNombre());
//         assertTrue(contenido.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), "❌ La fecha de hoy no aparece en el acceso");
//     }

//     //@Test
//     // void testReservaSinAccesosMuestraMensaje() throws InterruptedException {
//     //     log.info("🧪 Ejecutando test: Reserva activa sin accesos");

//     //     // Crear una reserva activa SIN accesos
//     //     Reserva reservaSinAccesos = new Reserva();
//     //     reservaSinAccesos.setCerradura(cerradura); // misma cerradura del gestor
//     //     reservaSinAccesos.setFechainicio(Date.valueOf(LocalDate.now()));
//     //     reservaSinAccesos.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
//     //     reservaSinAccesos.setPin("111111");
//     //     reservaSinAccesos.setGestor(cerradura.getGestor());
//     //     reservaSinAccesos = reservaRepository.save(reservaSinAccesos);

//     //     // Asociar huésped
//     //     reservaService.asociarHuesped(reservaSinAccesos.getId(), huesped.getId());

//     //     // 🔑 Login como gestor
//     //     driver.get(APP_URL + "/login");
//     //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
//     //     driver.findElement(By.id("password")).sendKeys(gestorPassword);
//     //     driver.findElement(By.cssSelector("button[type='submit']")).click();
//     //     wait.until(ExpectedConditions.urlContains("/calendar"));

//     //     // Menú lateral
//     //     WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
//     //     ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);

//     //     // Ir a monitorización
//     //     wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Monitorizar Acessos"))).click();
//     //     wait.until(ExpectedConditions.urlContains("/monitor"));
//     //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));

//     //     // Buscar tarjeta de esta reserva
//     //     List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
//     //     WebElement tarjeta = tarjetas.stream()
//     //         .filter(card -> card.getText().contains("Casa Test Monitorización"))
//     //         .findFirst()
//     //         .orElseThrow(() -> new AssertionError("❌ No se encontró la tarjeta de la reserva sin accesos"));

//     //     tarjeta.click();

//     //     // Comprobar mensaje "No hay accesos"
//     //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
//     //     Thread.sleep(1000); // esperar JS

//     //     WebElement lista = driver.findElement(By.id("accessList"));
//     //     assertTrue(lista.getText().contains("No hay accesos"), "❌ No se muestra el mensaje de 'No hay accesos'");

//     //     // Limpieza individual si quieres:
//     //     reservaRepository.deleteById(reservaSinAccesos.getId());
//     // }

//     @Test
//     void testReservaConAccesoFallido() throws InterruptedException {
//         // 🔁 Simulamos acceso fallido
//         accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);

//         // 🔐 Login y navegación
//         loginComoGestor();

//         // 📊 Entrar en la vista de monitorización
//         abrirVistaMonitor();

//         // 🃏 Selección de tarjeta de reserva
//         List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
//         assertFalse(tarjetas.isEmpty(), "❌ No hay tarjetas de reservas visibles");
//         tarjetas.get(0).click();

//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
//         Thread.sleep(1000); // espera al fetch

//         WebElement lista = driver.findElement(By.id("accessList"));
//         String contenido = lista.getText();

//         // ✅ Verificaciones
//         assertTrue(contenido.contains("Fallo"), "❌ No se muestra un acceso con resultado 'Fallo'");
//     }

//     @Test
//     void testConteoAccesosExitosos() throws InterruptedException {
//         // ✚ Insertar 3 accesos exitosos
//         accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
//         accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
//         accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);

//         // 🔐 Login y navegación
//         loginComoGestor();
//         abrirVistaMonitor();

//         // 🃏 Selección de tarjeta
//         List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
//         assertFalse(tarjetas.isEmpty(), "❌ No hay tarjetas disponibles");
//         tarjetas.get(0).click();

//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
//         Thread.sleep(1000);

//         WebElement lista = driver.findElement(By.id("accessList"));
//         long numExitos = lista.getText().lines().filter(l -> l.contains("Éxito")).count();

//         assertTrue(numExitos >= 3, "❌ Se esperaban al menos 3 accesos exitosos, pero hay: " + numExitos);
//     }
//     // @Test
//     // void testMonitorizacion_GestorVeAccesosCorrectos() { ... }

//     // @Test
//     // void testMonitorizacion_ReservaSinAccesos() { ... }

//     // @Test
//     // void testMonitorizacion_ReservaNoActiva() { ... }

//     // @Test
//     // void testMonitorizacion_HuespedNoVeCasaAjena() { ... }

//     // @Test
//     // void testMonitorizacion_ContadoresCorrectos() { ... }




// }