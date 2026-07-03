package com.tp.githubapi.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Automatiza, contra el Swagger UI desplegado localmente (docker-compose.yml, puerto 8081),
 * los incisos 1, 2 y 3 del TP:
 *   1) Autenticacion via Bearer token (boton "Authorize").
 *   2) Listado de repositorios propios (GET /user/repos, "Try it out" -> "Execute").
 *   3) Creacion de un repositorio (POST /user/repos, "Try it out" -> "Execute").
 *
 * El token se toma de la variable de entorno GITHUB_TOKEN, nunca se hardcodea.
 * Si no esta definida, los tests se saltean (no fallan) via Assumptions.
 *
 * Requisitos para correrlo:
 *   - docker compose up -d   (Swagger UI en http://localhost:8081)
 *   - export GITHUB_TOKEN=ghp_xxxxx
 *   - Google Chrome / Chromium instalado (WebDriverManager descarga el chromedriver acorde)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SwaggerUiSeleniumTest {

    private static final String SWAGGER_UI_URL =
            System.getProperty("swagger.ui.url", "http://localhost:8081/");
    private static final String TOKEN = System.getenv("GITHUB_TOKEN");

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setUp() {
        assumeTrue(TOKEN != null && !TOKEN.isBlank(),
                "GITHUB_TOKEN no esta definido: se saltea el test de Selenium");

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1600,1200");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(SWAGGER_UI_URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("swagger-ui")));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void inciso1_autenticacionConBearerToken() {
        WebElement authorizeButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.authorize")));
        authorizeButton.click();

        // OJO: no usar el selector generico input[type='text']: la barra "Explore" de arriba
        // (#download-url-input) tambien matchea y queda antes en el DOM que el campo real del
        // token (#auth-bearer-value), por lo que hay que apuntar al id especifico.
        WebElement tokenInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("auth-bearer-value")));
        tokenInput.clear();
        tokenInput.sendKeys(TOKEN);

        driver.findElement(By.cssSelector(".auth-btn-wrapper .authorize")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".auth-btn-wrapper .btn-done")))
                .click();

        // El modal de autorizacion debe cerrarse una vez cargado el token (confirma que se acepto)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("dialog-ux")));
    }

    @Test
    @Order(2)
    void inciso2_listadoDeRepositoriosPropios() {
        String operationId = "operations-Repositorios-listMyRepositories";
        expandirYEjecutar(operationId);

        String responseCode = textoDeCodigoDeRespuesta(operationId);
        assertTrue(responseCode.startsWith("200"),
                "Se esperaba 200 en GET /user/repos pero se obtuvo: " + responseCode);
    }

    @Test
    @Order(3)
    void inciso3_creacionDeRepositorio() {
        String operationId = "operations-Repositorios-createRepository";
        expandir(operationId);

        WebElement tryItOut = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='" + operationId + "']//button[contains(., 'Try it out')]")));
        tryItOut.click();

        WebElement bodyTextArea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='" + operationId + "']//textarea")));

        String nombreRepo = "tp-selenium-" + System.currentTimeMillis();
        String body = """
                {
                  "name": "%s",
                  "description": "Repositorio creado automaticamente por el test de Selenium del TP",
                  "private": false
                }
                """.formatted(nombreRepo);

        bodyTextArea.clear();
        bodyTextArea.sendKeys(body);

        WebElement executeButton = driver.findElement(
                By.xpath("//*[@id='" + operationId + "']//button[contains(., 'Execute')]"));
        executeButton.click();

        String responseCode = textoDeCodigoDeRespuesta(operationId);
        assertTrue(responseCode.startsWith("201"),
                "Se esperaba 201 en POST /user/repos pero se obtuvo: " + responseCode
                        + " (revisar si el nombre '" + nombreRepo + "' ya existia)");
    }

    private void expandir(String operationId) {
        WebElement summary = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='" + operationId + "']//span[contains(@class,'opblock-summary-path')]")));
        summary.click();
    }

    private void expandirYEjecutar(String operationId) {
        expandir(operationId);

        WebElement tryItOut = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='" + operationId + "']//button[contains(., 'Try it out')]")));
        tryItOut.click();

        WebElement executeButton = driver.findElement(
                By.xpath("//*[@id='" + operationId + "']//button[contains(., 'Execute')]"));
        executeButton.click();
    }

    /**
     * La tabla de respuesta tiene una fila de encabezado (td.col_header.response-col_status con texto "Code")
     * y una fila de datos (tr.response > td.response-col_status con el codigo real), ambas comparten clase
     * "response-col_status". Hay que apuntar puntualmente a la fila de datos dentro del operationId indicado.
     */
    private String textoDeCodigoDeRespuesta(String operationId) {
        WebElement codeCell = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                "//*[@id='" + operationId + "']//table[contains(@class,'live-responses-table')]"
                        + "//tr[@class='response']/td[@class='response-col_status']")));
        return codeCell.getText().trim();
    }
}
