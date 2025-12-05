package org.testcontainers.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class BaiduBrowserCompatibilityTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BAIDU_URL = "https://www.baidu.com/?a";
    private static final String SEARCH_INPUT_ID = "kw";
    private static final String SEARCH_BUTTON_ID = "su";

    // 定义不同浏览器的容器配置
    @Container
    private BrowserWebDriverContainer chromeContainer = new BrowserWebDriverContainer()
        .withCapabilities(new ChromeOptions());

    @Container
    private BrowserWebDriverContainer firefoxContainer = new BrowserWebDriverContainer()
        .withCapabilities(new FirefoxOptions());

    @Container
    private BrowserWebDriverContainer edgeContainer = new BrowserWebDriverContainer()
        .withCapabilities(new EdgeOptions());

    // 提供浏览器驱动源
    static Stream<WebDriver> browserDrivers() {
        return Stream.of(
            new RemoteWebDriver(chromeContainer.getSeleniumAddress(), new ChromeOptions()),
            new RemoteWebDriver(firefoxContainer.getSeleniumAddress(), new FirefoxOptions()),
            new RemoteWebDriver(edgeContainer.getSeleniumAddress(), new EdgeOptions())
        );
    }

    @BeforeEach
    public void setUp(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BAIDU_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 浏览器兼容性测试：验证不同浏览器能正常访问百度首页
     */
    @ParameterizedTest
    @MethodSource("browserDrivers")
    public void testBrowserCompatibility(WebDriver driver) {
        // 验证搜索输入框可见
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        assertThat(searchInput.isDisplayed()).isTrue();
        assertThat(searchInput.isEnabled()).isTrue();
        
        // 验证搜索按钮可见
        WebElement searchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_BUTTON_ID)));
        assertThat(searchButton.isDisplayed()).isTrue();
        assertThat(searchButton.isEnabled()).isTrue();
        
        // 验证页面标题
        assertThat(driver.getTitle()).isEqualTo("百度一下，你就知道");
    }

    /**
     * 浏览器兼容性测试：验证不同浏览器能正常执行搜索功能
     */
    @ParameterizedTest
    @MethodSource("browserDrivers")
    public void testSearchFunctionalityAcrossBrowsers(WebDriver driver) {
        String keyword = "Testcontainers";
        
        // 输入关键词
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(keyword);
        
        // 点击搜索按钮
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_BUTTON_ID)));
        searchButton.click();
        
        // 验证搜索结果
        wait.until(ExpectedConditions.titleContains(keyword));
        assertThat(driver.getTitle()).contains(keyword);
    }

    /**
     * 浏览器兼容性测试：验证不同浏览器的响应式设计
     */
    @ParameterizedTest
    @MethodSource("browserDrivers")
    public void testResponsiveDesignAcrossBrowsers(WebDriver driver) {
        // 测试不同窗口尺寸下的页面显示
        int[] widths = {320, 768, 1024, 1440};
        
        for (int width : widths) {
            driver.manage().window().setSize(new Dimension(width, 800));
            
            // 验证搜索输入框在不同尺寸下都可见
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
            assertThat(searchInput.isDisplayed()).isTrue();
            
            // 验证搜索按钮在不同尺寸下都可见
            WebElement searchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_BUTTON_ID)));
            assertThat(searchButton.isDisplayed()).isTrue();
        }
    }

    /**
     * 浏览器兼容性测试：验证不同浏览器的搜索建议功能
     */
    @ParameterizedTest
    @MethodSource("browserDrivers")
    public void testSearchSuggestionsAcrossBrowsers(WebDriver driver) {
        String keyword = "Test";
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(keyword);
        
        // 等待搜索建议出现
        WebElement suggestionsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("s-ctner")));
        assertThat(suggestionsContainer.isDisplayed()).isTrue();
    }
}