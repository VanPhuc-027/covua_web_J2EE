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
public class RegisterTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8080/register";

    private static String lastCreatedUser;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    @DisplayName("1. Đăng ký thành công và lưu lại Username")
    void testRegisterSuccess() {
        driver.get(BASE_URL);
        lastCreatedUser = "player_" + System.currentTimeMillis(); // Tạo tên ngẫu nhiên

        driver.findElement(By.id("regUsername")).sendKeys(lastCreatedUser);
        driver.findElement(By.name("email")).sendKeys(lastCreatedUser + "@test.com");
        driver.findElement(By.id("regPassword")).sendKeys("Abc12345");
        driver.findElement(By.id("confirmPassword")).sendKeys("Abc12345");
        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.urlContains("/login"));
        System.out.println("--- PASS: Đã tạo thành công user: " + lastCreatedUser);
    }

    @Test
    @Order(2)
    @DisplayName("2. Kiểm tra trùng Username (Dùng lại user vừa tạo)")
    void testDuplicateUsername() {
        driver.get(BASE_URL);

        driver.findElement(By.id("regUsername")).sendKeys(lastCreatedUser);
        driver.findElement(By.name("email")).sendKeys("another_email@test.com");
        driver.findElement(By.id("regPassword")).sendKeys("Abc12345");
        driver.findElement(By.id("confirmPassword")).sendKeys("Abc12345");
        driver.findElement(By.className("btn-submit")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Username đã được sử dụng')]")
        ));
        assertTrue(error.isDisplayed());
        System.out.println("--- PASS: Đã chặn thành công khi dùng lại tên: " + lastCreatedUser);
    }

    @Test
    @Order(3)
    @DisplayName("3. Kiểm tra lỗi độ dài mật khẩu (Bug 7 ký tự)")
    void testPasswordLengthBug() {
        driver.get(BASE_URL);
        driver.findElement(By.id("regUsername")).sendKeys("test_pass_bug");
        driver.findElement(By.name("email")).sendKeys("bug_hunter@test.com");

        driver.findElement(By.id("regPassword")).sendKeys("Abc1234"); // 7 ký tự
        driver.findElement(By.id("confirmPassword")).sendKeys("Abc1234");
        driver.findElement(By.className("btn-submit")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'ít nhất 8 ký tự')]")
        ));
        assertTrue(error.isDisplayed());
        System.out.println("--- PASS: Đã bắt được bug độ dài mật khẩu! ---");
    }

    @Test
    @Order(4)
    @DisplayName("4. Kiểm tra xác nhận mật khẩu sai (Xử lý Alert JS)")
    void testPasswordMismatch() {
        driver.get(BASE_URL);
        driver.findElement(By.id("regUsername")).sendKeys("test_mismatch");
        driver.findElement(By.name("email")).sendKeys("mismatch@test.com");

        driver.findElement(By.id("regPassword")).sendKeys("Abc12345");
        driver.findElement(By.id("confirmPassword")).sendKeys("Wrong123");
        driver.findElement(By.className("btn-submit")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().contains("không khớp"), "Nội dung Alert không đúng!");
        alert.accept();
        System.out.println("--- PASS: Đã bắt được Alert xác nhận mật khẩu không khớp! ---");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}
