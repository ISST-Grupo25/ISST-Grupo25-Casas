// package com.isst.ISST_Grupo25_Casas;

// import com.isst.ISST_Grupo25_Casas.models.*;
// import com.isst.ISST_Grupo25_Casas.services.*;
// import com.isst.ISST_Grupo25_Casas.repository.*;

// import org.checkerframework.checker.units.qual.C;
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
// import java.util.stream.Collectors;

// import static java.util.Collections.singletonList;
// import java.io.File;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
// public class MonitorizarAccesoViviendaSeleniumTest {
// 	private static final Logger log = LoggerFactory.getLogger(MonitorizarAccesoViviendaSeleniumTest.class);

// 	private WebDriver driver;
// 	private WebDriverWait wait;
// 	private static final String APP_URL = "http://localhost:8080";

// 	@Autowired
// 	private AccesoService accesoService;

// 	@Autowired
// 	private ReservaService reservaService;

// 	@Autowired
// 	private ReservaRepository reservaRepository;

// 	@Autowired
// 	private HuespedService huespedService;

// 	@Autowired
// 	private CerraduraService cerraduraService;

// 	@Autowired
// 	private GestorService gestorService;

// 	private Huesped huesped;
// 	private Reserva reserva;
// 	private Huesped segundoHuesped;
// 	private Reserva reserva2;
// 	private Cerradura cerradura;
// 	private Cerradura cerradura2;
// 	private String gestorEmail = "selenium_gestor2@gmail.com";
// 	private String gestorPassword = "clave123";

// 	@BeforeAll
// 	void prepararDatos() {
// 		// Crear gestor de prueba
// 		// Gestor gestor = new Gestor();
// 		String nombreG = "Gestor Selenium";
// 		String telefonoG = "666666666";
// 		Gestor gestor = gestorService.registerGestor(nombreG, gestorEmail, gestorPassword, telefonoG);

// 		// Crear cerradura para el gestor
// 		cerradura = cerraduraService.guardarCerradura("Casa Test Monitorización", "TOKEN123", gestor.getId());

// 		// Crear huésped
// 		huesped = huespedService.registerHuesped("SeleniumHuesped", "huespedSelenium@gmail.com", "clave123");

// 		// Crear segundo huésped
// 		segundoHuesped = huespedService.registerHuesped("Huesped Secundario", "huespedSecundario@gmail.com", "clave456");


		
// 		//añadir huesped y segundo huesped a la reserva
// 		//List<Huesped> huespedes =	List.of(huesped, segundoHuesped);

// 	// 1. Crear la reserva sin huéspedes
// reserva = new Reserva();
// reserva.setFechainicio(Date.valueOf(LocalDate.now()));
// reserva.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
// reserva.setCerradura(cerradura);
// reserva.setPin("654321");
// reserva.setGestor(gestor);
// reserva = reservaRepository.save(reserva);

// // 2. Asociar huéspedes como lo hace el controlador
// reservaService.asociarHuesped(reserva.getId(), huesped.getId());
// reservaService.asociarHuesped(reserva.getId(), segundoHuesped.getId());


// 		accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, huesped, reserva);
// 	}

// 	@BeforeEach
// 	void setUp() {
// 		// Intentar usar chromedriver local si existe
// 		String localDriverPath = "C:\\Users\\inesv\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
// 		File localDriver = new File(localDriverPath);

// 		ChromeOptions options = new ChromeOptions();
// 		//options.addArguments("--force-device-scale-factor=0.5");
		

// 		if (localDriver.exists()) {
// 			System.out.println("🔧 Usando chromedriver local: " + localDriverPath);
// 			System.setProperty("webdriver.chrome.driver", localDriverPath);
// 		} else {
// 			System.out.println("🌐 Usando WebDriverManager (puede fallar si no encuentra versión correcta)");
// 			WebDriverManager.chromedriver().setup();
// 		}

		
		
// 		driver = new ChromeDriver(options);
// 		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
// 		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
// 	}

// 	@AfterEach
// 	void tearDown() {
// 		if (driver != null)
// 			driver.quit();
// 	}

// 	void loginComoGestor() {
// 		driver.get(APP_URL + "/");
// wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));  //  espera a que la página cargue

// ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='70%'");
// 		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
// 		wait.until(ExpectedConditions.urlContains("/login"));
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
// 		driver.findElement(By.id("password")).sendKeys(gestorPassword);
// 		driver.findElement(By.cssSelector("button[type='submit']")).click();
// 		wait.until(ExpectedConditions.urlContains("/calendar"));
// 	}

// 	void abrirVistaMonitor() {
// 		WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
// 		((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);

// 		WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Historial de Acessos")));
// 		monitorLink.click();
// 		wait.until(ExpectedConditions.urlContains("/monitor"));
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
// 	}

// 	private void pause(long millis) {
// 		try {
// 			Thread.sleep(millis);
// 		} catch (InterruptedException e) {
// 			Thread.currentThread().interrupt();
// 		}
// 	}

// 	@AfterAll
// 	void limpiarDatos() {
// 		try {
// 			// Reservas (y sus accesos y enlaces intermedios)
// 			if (reserva2 != null && reserva2.getId() != null) {
// 				reservaService.eliminarReservaYHuespedes(reserva2.getId());
// 				System.out.println("🧹 Segunda reserva eliminada");
// 			}
// 			if (reserva != null && reserva.getId() != null) {
// 				reservaService.eliminarReservaYHuespedes(reserva.getId());
// 				System.out.println("🧹 Reserva eliminada");
// 			}

// 			// Cerraduras (tras eliminar reservas asociadas)
// 			if (cerradura2 != null && cerradura2.getId() != null) {
// 				cerraduraService.eliminarCerradura(cerradura2.getId());
// 				System.out.println("🧹 Segunda cerradura eliminada");
// 			}
// 			if (cerradura != null && cerradura.getId() != null) {
// 				cerraduraService.eliminarCerradura(cerradura.getId());
// 				System.out.println("🧹 Cerradura eliminada");
// 			}

// 			// Huéspedes
// 			if (segundoHuesped != null && segundoHuesped.getId() != null) {
// 				huespedService.eliminarHuesped(segundoHuesped.getId());
// 				System.out.println("🧹 Segundo huésped eliminado");
// 			}
// 			if (huesped != null && huesped.getId() != null) {
// 				huespedService.eliminarHuesped(huesped.getId());
// 				System.out.println("🧹 Huésped eliminado");
// 			}

// 			// Gestor (al final)
// 			gestorService.findByEmail(gestorEmail).ifPresent(gestor -> {
// 				gestorService.eliminarGestor(gestor.getId());
// 				System.out.println("🧹 Gestor eliminado");
// 			});

// 			// borrar los accesos de prueba

// 		} catch (Exception e) {
// 			System.err.println("❌ Error al limpiar datos de prueba: " + e.getMessage());
// 		}
// 	}

// 	@Test
// 	@Order(1)
// 	void testAccesoVisibleParaReservaActiva() throws InterruptedException {
// 		log.info("✅ Test iniciado correctamente");
// 		// 1. Ir a la home
// 		driver.get(APP_URL + "/");
// 		((JavascriptExecutor) driver).executeScript("document.body.style.zoom='70%'");

// 		// 2. Iniciar sesión
// 		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-login"))).click();
// 		wait.until(ExpectedConditions.urlContains("/login"));
// 		pause(2000);
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email"))).sendKeys(gestorEmail);
// 		driver.findElement(By.id("password")).sendKeys(gestorPassword);
// 		pause(2000);
// 		driver.findElement(By.cssSelector("button[type='submit']")).click();

// 		// 3. Asegurar redirección correcta y apertura de menú
// 		wait.until(ExpectedConditions.urlContains("/calendar"));
// 		pause(2000);
// 		WebElement menuBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuButton")));
// 		((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuBtn);
// 		pause(2000);

// 		// 4. Ir a monitorización
// 		WebElement monitorLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Historial de Acessos")));
// 		monitorLink.click();
// 		wait.until(ExpectedConditions.urlContains("/monitor"));
// 		pause(2000);
// 		((JavascriptExecutor) driver).executeScript("document.body.style.zoom='70%'");

// 		// 5. Ver tarjetas y hacer clic en la primera
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessCards")));
// 		pause(2000);
// 		List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
// 		System.out.println("🧾 Tarjetas visibles:");
// 		assertFalse(tarjetas.isEmpty(), "❌ No se muestran tarjetas de reservas");
// 		tarjetas.get(0).click();

// 		// 6. Esperar detalles y verificar accesos renderizados
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
// 		Thread.sleep(1000);

// 		WebElement lista = driver.findElement(By.id("accessList"));
// 		String contenido = lista.getText();
// 		pause(2000);

// 		System.out.println("🧾 Accesos visibles:");
// 		System.out.println(contenido);
// 		log.info("🧾 Contenido mostrado: {}", lista.getText());

// 		// ✅ Verificaciones reales
// 		assertTrue(contenido.contains("Éxito"), "❌ No se muestra 'Éxito' en el resultado del acceso");
// 		assertTrue(contenido.contains(huesped.getNombre()),
// 				"❌ No aparece el nombre del huésped esperado: " + huesped.getNombre());
// 				assertFalse(contenido.contains(segundoHuesped.getNombre()),
// 				"❌ Aparece el nombre del huésped inesperado: " + segundoHuesped.getNombre());
// 		assertTrue(contenido.contains(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
// 				"❌ La fecha de hoy no aparece en el acceso");
// 	}

// 	@Test
// 	@Order(3)
// 	void testAccesoDeVariosHuespedes() throws InterruptedException {

// 		// Crear segunda cerradura (opcional, pero para evitar conflictos)
// 		cerradura2 = cerraduraService.guardarCerradura("Casa 2 del Test", "TOKEN456",
// 				gestorService.findByEmail(gestorEmail).get().getId());

// 		// Crear segunda reserva (distinta cerradura)
// 		reserva2 = new Reserva();
// 		reserva2.setCerradura(cerradura2);
// 		reserva2.setFechainicio(Date.valueOf(LocalDate.now()));
// 		reserva2.setFechafin(Date.valueOf(LocalDate.now().plusDays(1)));
// 		reserva2.setPin("987654");
// 		reserva2.setGestor(cerradura2.getGestor());
// 		reserva2 = reservaRepository.save(reserva2);
// 		reservaService.asociarHuesped(reserva2.getId(), segundoHuesped.getId());

// 		// 🔐 Login y navegación inicial
// 		loginComoGestor();
// 		pause(2000);
// 		abrirVistaMonitor();
// 		pause(2000);

// 		// Comprobar que hay al menos dos tarjetas
// 		List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
// 		pause(2000);
// 		assertTrue(tarjetas.size() >= 2, "❌ No hay al menos dos tarjetas de reservas");

// 		// Buscar la tarjeta de la nueva reserva
// 		WebElement tarjetaNueva = tarjetas.get(1); // suponiendo que se añade como segunda
// 		tarjetaNueva.click();
// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
// 		Thread.sleep(1000);

// 		WebElement lista = driver.findElement(By.id("accessList"));
// 		pause(2000);

// 		String contenidoAntes = lista.getText();
// 		assertTrue(contenidoAntes.contains("No hay accesos") || contenidoAntes.isBlank(),
// 				"❌ Se esperaban 0 accesos al inicio para la nueva reserva");

// 				pause(2000);
// 		// ✅ Registrar acceso del segundo huésped
// 		accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), true, segundoHuesped, reserva2);
		
// 		// 🔄 Refrescar monitorización
// 		driver.navigate().refresh();
// 		pause(2000);
// 		//abrirVistaMonitor();
// 		tarjetas = driver.findElements(By.cssSelector(".access-card"));
// 		pause(2000);
// 		tarjetaNueva = tarjetas.get(1); // asegurar que es la correcta
// 		tarjetaNueva.click();

// 		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
// 		Thread.sleep(1000);
// 		pause(2000);
// 		WebElement lista2 = driver.findElement(By.id("accessList"));
// 		String contenidoDespues = lista2.getText();

// 		assertTrue(contenidoDespues.contains(segundoHuesped.getNombre()), "❌ No se muestra el acceso del segundo huésped");
// 		assertTrue(contenidoDespues.contains("Éxito"), "❌ No se muestra el resultado del acceso");
// 	}

// 	@Test
// 	@Order(5)
// 	void testFiltroPorNombreHuesped() throws InterruptedException {
// 					loginComoGestor();
// 					pause(2000);
// 					abrirVistaMonitor();
	
// 					// Esperar que haya tarjetas cargadas
// 					wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".access-card")));
// 					pause(2000);
	
// 					WebElement searchInput = driver.findElement(By.id("searchInput"));
// 					searchInput.clear();
// 					pause(2000);
// 					searchInput.sendKeys(huesped.getNombre());
	
// 					pause(2000); // Esperar que el filtro actúe visualmente
	
// 					List<WebElement> visibles = driver.findElements(By.cssSelector(".access-card"));
// 					pause(2000);
// 					long visiblesFiltradas = visibles.stream()
// 									.filter(card -> card.isDisplayed())
// 									.count();
	
// 					assertTrue(visiblesFiltradas == 1, "❌ No se filtró correctamente por nombre de huésped");
// 	}
	
	
// 	@Test
// 	@Order(2) 
// 	void testFiltroPorUbicacionCerradura() throws InterruptedException {
// 					loginComoGestor();
// 					pause(2000);
// 					abrirVistaMonitor();
	
// 					WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
// 					pause(2000);
// 					searchInput.clear();
// 					pause(2000);
// 					searchInput.sendKeys("Casa Test Monitorización");
	
// 					pause(2000); // esperar a que se apliquen los filtros JS
	
// 					List<WebElement> visibles = driver.findElements(By.cssSelector(".access-card"))
// 									.stream()
// 									.filter(WebElement::isDisplayed)
// 									.toList();
// 						pause(2000);
// 					assertEquals(1, visibles.size(), "❌ No se filtró correctamente por ubicación de la cerradura");
// 	}
	
// 	@Test
// 	@Order(4)
// 	void testReservaConAccesoFallido() throws InterruptedException {
// 		//  Simulamos acceso fallido
// 		accesoService.guardarAcceso(Date.valueOf(LocalDate.now()), false, huesped, reserva);

// 		//  Login y navegación
// 		loginComoGestor();
// 		pause(2000);

// 		//  Entrar en la vista de monitorización
// 		abrirVistaMonitor();
// 		pause(2000);
// // Buscar la tarjeta de la reserva con ubicación específica
// String ubicacionEsperada = reserva.getCerradura().getUbicacion(); // debería ser "Casa Test Monitorización"

// List<WebElement> tarjetas = driver.findElements(By.cssSelector(".access-card"));
// pause(2000);

// WebElement tarjetaCorrecta = tarjetas.stream()
// 	.filter(card -> card.getText().contains(ubicacionEsperada))
// 	.findFirst()
// 	.orElseThrow(() -> new AssertionError("❌ No se encontró la tarjeta de la reserva con ubicación: " + ubicacionEsperada));
// 	pause(2000);
// tarjetaCorrecta.click();
// pause(2000);
// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accessDetails")));
// pause(2000); // mejor que Thread.sleep

// WebElement lista = driver.findElement(By.id("accessList"));
// pause(2000);
// String contenido = lista.getText();

// // ✅ Verificaciones
// assertTrue(contenido.contains("Fallo"), "❌ No se muestra un acceso con resultado 'Fallo'");
// }
	
// }

