package com.isst.ISST_Grupo25_Casas.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalendarUITest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void testLoginYVisualizaCalendario() {
        driver.get("http://localhost:8080/login");

        // Suponiendo que tienes login por email y contraseña
        driver.findElement(By.name("email")).sendKeys("natalia.burguillo@alumnos.upm.es");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("form button[type=submit]")).click();

        // Esperamos a que la URL cambie
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/calendar"),
            ExpectedConditions.urlContains("/login?error")
        ));

        System.out.println("URL final: " + driver.getCurrentUrl());

        // Asegurarse de estar en /calendar
        Assertions.assertTrue(driver.getCurrentUrl().contains("/calendar"));
        Assertions.assertTrue(driver.getPageSource().contains("Reservas"));
    }
}
