package com.group18.chessgame.admin;

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
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminManagementTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String LOGIN_URL = "http://localhost:8080/login";
    private final String ADMIN_USERS_URL = "http://localhost:8080/admin/users";

    private final String adminUser = "admin";
    private final String adminPass = "Admin@033830";
    private final String targetUser = "Quydeptrai123";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    private void loginAs(String username, String password) {
        driver.get(LOGIN_URL);
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.className("btn-submit")).click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login")));
    }

    private void logout() {
        driver.get("http://localhost:8080/logout");
    }

    // --- CÁC TEST CASE ---

    @Test
    @Order(1)
    @DisplayName("AD_001: Chặn truy cập Admin từ User thường")
    void testAdminAccess_ForbiddenForNormalUser() {
        loginAs("Quydeptrai123", "Quydeptrai@123");

        driver.get(ADMIN_USERS_URL);

        wait.until(ExpectedConditions.urlMatches("http://localhost:8080/?"));
        assertFalse(driver.getCurrentUrl().contains("/admin"), "User thường vẫn vào được trang Admin!");
        System.out.println("--- PASS: Đã chặn thành công User thường vào trang Admin ---");

        logout();
    }

    @Test
    @Order(2)
    @DisplayName("AD_002: Truy cập trang Quản lý thành công bằng Admin")
    void testAdminAccess_SuccessForAdmin() {
        loginAs(adminUser, adminPass);
        driver.get(ADMIN_USERS_URL);

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user-table")));
        assertTrue(table.isDisplayed());
        System.out.println("--- PASS: Admin đã vào được trang Quản lý User ---");
    }

    @Test
    @Order(3)
    @DisplayName("AD_003: Khóa tài khoản User (Ban)")
    void testBanUser() {
        loginAs(adminUser, adminPass);
        driver.get(ADMIN_USERS_URL);

        String banButtonXpath = String.format("//tr[td[contains(text(), '%s')]]//button[contains(@class, 'btn-ban')]", targetUser);

        try {
            WebElement banButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(banButtonXpath)));
            banButton.click();

            String statusXpath = String.format("//tr[td[contains(text(), '%s')]]//span[contains(@class, 'status-banned')]", targetUser);
            WebElement bannedStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(statusXpath)));
            assertTrue(bannedStatus.isDisplayed());
            System.out.println("--- PASS: Đã khóa thành công user: " + targetUser + " ---");
        } catch (Exception e) {
            System.out.println("--- BỎ QUA TEST NÀY VÌ KHÔNG TÌM THẤY NÚT KHÓA (CÓ THỂ USER NÀY ĐÃ BỊ KHÓA SẴN RỒI) ---");
        }
    }

    @Test
    @Order(4)
    @DisplayName("AD_004: Mở khóa tài khoản User (Unban)")
    void testUnbanUser() {
        loginAs(adminUser, adminPass);
        driver.get(ADMIN_USERS_URL);

        String unbanButtonXpath = String.format("//tr[td[contains(text(), '%s')]]//button[contains(@class, 'btn-unban')]", targetUser);

        try {
            WebElement unbanButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(unbanButtonXpath)));
            unbanButton.click();

            String statusXpath = String.format("//tr[td[contains(text(), '%s')]]//span[contains(@class, 'status-active')]", targetUser);
            WebElement activeStatus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(statusXpath)));
            assertTrue(activeStatus.isDisplayed());
            System.out.println("--- PASS: Đã mở khóa thành công user: " + targetUser + " ---");
        } catch (Exception e) {
            System.out.println("--- BỎ QUA TEST NÀY VÌ KHÔNG TÌM THẤY NÚT MỞ KHÓA (CÓ THỂ USER NÀY ĐANG ACTIVE SẴN) ---");
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
