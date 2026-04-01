package com.group18.chessgame.auth;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.jupiter.api.*;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8080/login";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("1. Đăng nhập thất bại - Sai mật khẩu")
    void testLogin_WrongPassword() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("MatKhauTaDao123");
        driver.findElement(By.className("btn-submit")).click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'không đúng') or contains(text(), 'Sai')]")
        ));
        assertTrue(errorMsg.isDisplayed());
        System.out.println("--- PASS: Đã chặn đăng nhập khi sai mật khẩu ---");
    }

    @Test
    @Order(2)
    @DisplayName("2. Đăng nhập thất bại - Tài khoản không tồn tại")
    void testLogin_GhostUser() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("user_khong_ton_tai");
        driver.findElement(By.id("password")).sendKeys("Abc12345");
        driver.findElement(By.className("btn-submit")).click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'không đúng') or contains(text(), 'Sai')]")
        ));
        assertTrue(errorMsg.isDisplayed());
        System.out.println("--- PASS: Đã chặn đăng nhập user lạ ---");
    }

    @Test
    @Order(3)
    @DisplayName("3. Đăng nhập - Test SQL Injection")
    void testLogin_SqlInjection() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("' OR 1=1--");
        driver.findElement(By.id("password")).sendKeys("Quydeptrai@123");
        driver.findElement(By.className("btn-submit")).click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'không đúng') or contains(text(), 'Sai')]")
        ));
        assertTrue(errorMsg.isDisplayed());
        System.out.println("--- PASS: Hệ thống an toàn trước SQL Injection cơ bản ---");
    }

    @Test
    @Order(4)
    @DisplayName("4. Đăng nhập thất bại - Tài khoản bị khóa (Banned)")
    void testLogin_BannedUser() {
        driver.get(BASE_URL + "?error=banned");

        WebElement bannedMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'bị khóa')]")
        ));
        assertTrue(bannedMsg.isDisplayed());
        System.out.println("--- PASS: Đã hiển thị đúng cảnh báo tài khoản bị khóa! ---");
    }

    @Test
    @Order(5)
    @DisplayName("5. Đăng nhập thành công - Happy Path")
    void testLogin_Success() {
        driver.get(BASE_URL);
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin@033830");
        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
        System.out.println("--- PASS: Đăng nhập thành công và chuyển hướng! ---");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
