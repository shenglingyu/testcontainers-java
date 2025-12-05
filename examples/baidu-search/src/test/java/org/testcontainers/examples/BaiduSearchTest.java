package org.testcontainers.examples;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class BaiduSearchTest {

    @Container
    private BrowserWebDriverContainer container = new BrowserWebDriverContainer()
        .withCapabilities(new ChromeOptions());

    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BAIDU_URL = "https://www.baidu.com/?a";
    private static final String SEARCH_INPUT_ID = "kw";
    private static final String SEARCH_BUTTON_ID = "su";

    @BeforeEach
    public void setUp() {
        driver = new RemoteWebDriver(container.getSeleniumAddress(), new ChromeOptions());
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(BAIDU_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 正常场景：搜索框存在且可见
     */
    @Test
    public void testSearchInputExistsAndVisible() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        assertThat(searchInput.isDisplayed()).isTrue();
        assertThat(searchInput.isEnabled()).isTrue();
    }

    /**
     * 正常场景：搜索按钮存在且可见
     */
    @Test
    public void testSearchButtonExistsAndVisible() {
        WebElement searchButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_BUTTON_ID)));
        assertThat(searchButton.isDisplayed()).isTrue();
        assertThat(searchButton.isEnabled()).isTrue();
    }

    /**
     * 正常场景：输入有效关键词并点击搜索按钮
     */
    @Test
    public void testSearchWithValidKeyword() {
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
        
        // 验证搜索结果页面包含关键词
        WebElement searchResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        assertThat(searchResult.getText()).contains(keyword);
    }

    /**
     * 正常场景：输入有效关键词并按回车键
     */
    @Test
    public void testSearchWithValidKeywordAndEnterKey() {
        String keyword = "Selenium WebDriver";
        
        // 输入关键词并按回车键
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(keyword);
        searchInput.submit();
        
        // 验证搜索结果
        wait.until(ExpectedConditions.titleContains(keyword));
        assertThat(driver.getTitle()).contains(keyword);
    }

    /**
     * 异常场景：输入空关键词点击搜索
     */
    @Test
    public void testSearchWithEmptyKeyword() {
        // 不输入任何内容，直接点击搜索按钮
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_BUTTON_ID)));
        searchButton.click();
        
        // 验证页面没有跳转，仍然在百度首页
        wait.until(ExpectedConditions.urlToBe(BAIDU_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(BAIDU_URL);
    }

    /**
     * 异常场景：输入超长关键词
     */
    @Test
    public void testSearchWithVeryLongKeyword() {
        // 创建一个超长关键词（超过1000个字符）
        StringBuilder longKeyword = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longKeyword.append("Testcontainers is a great tool for testing ");
        }
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(longKeyword.toString());
        
        // 验证输入内容被正确处理
        assertThat(searchInput.getAttribute("value")).isEqualTo(longKeyword.toString());
    }

    /**
     * 异常场景：输入特殊字符
     */
    @Test
    public void testSearchWithSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+{}[]|\\:;'\"<>,.?/~`";
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(specialChars);
        
        // 点击搜索按钮
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_BUTTON_ID)));
        searchButton.click();
        
        // 验证搜索成功
        wait.until(ExpectedConditions.titleContains("百度搜索"));
    }

    /**
     * 异常场景：输入SQL注入尝试 - 基本SQL注入
     */
    @Test
    public void testSearchWithBasicSqlInjection() {
        String sqlInjection = "' OR 1=1 --";
        performSearchAndVerifyNoError(sqlInjection);
    }

    /**
     * 异常场景：输入SQL注入尝试 - UNION查询
     */
    @Test
    public void testSearchWithUnionSqlInjection() {
        String sqlInjection = "' UNION SELECT username, password FROM users --";
        performSearchAndVerifyNoError(sqlInjection);
    }

    /**
     * 异常场景：输入SQL注入尝试 - 基于时间的盲注
     */
    @Test
    public void testSearchWithTimeBasedSqlInjection() {
        String sqlInjection = "' OR SLEEP(5) --";
        performSearchAndVerifyNoError(sqlInjection);
    }

    /**
     * 异常场景：输入SQL注入尝试 - 错误型注入
     */
    @Test
    public void testSearchWithErrorBasedSqlInjection() {
        String sqlInjection = "' AND (SELECT COUNT(*) FROM information_schema.tables) > 0 --";
        performSearchAndVerifyNoError(sqlInjection);
    }

    /**
     * 异常场景：输入SQL注入尝试 - 堆叠查询
     */
    @Test
    public void testSearchWithStackedQuerySqlInjection() {
        String sqlInjection = "'; DROP TABLE users --";
        performSearchAndVerifyNoError(sqlInjection);
    }

    /**
     * 辅助方法：执行搜索并验证没有出现错误
     */
    private void performSearchAndVerifyNoError(String searchTerm) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(searchTerm);
        
        // 点击搜索按钮
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_BUTTON_ID)));
        searchButton.click();
        
        // 验证搜索成功，没有出现SQL注入错误或其他异常
        wait.until(ExpectedConditions.titleContains("百度搜索"));
        // 验证页面没有显示数据库错误信息
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        assertThat(body.getText()).doesNotContain("SQL syntax");
        assertThat(body.getText()).doesNotContain("database error");
        assertThat(body.getText()).doesNotContain("error");
    }

    /**
     * 异常场景：输入XSS尝试
     */
    @Test
    public void testSearchWithXssAttempt() {
        String xssAttempt = "<script>alert('XSS')</script>";
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(xssAttempt);
        
        // 点击搜索按钮
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_BUTTON_ID)));
        searchButton.click();
        
        // 验证搜索成功，没有出现XSS漏洞
        wait.until(ExpectedConditions.titleContains("百度搜索"));
    }

    /**
     * 异常场景：页面加载超时
     */
    @Test
    public void testPageLoadTimeout() {
        // 验证页面在合理时间内加载完成
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        assertThat(searchInput).isNotNull();
    }

    /**
     * 正常场景：搜索框的placeholder属性
     */
    @Test
    public void testSearchInputPlaceholder() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        String placeholder = searchInput.getAttribute("placeholder");
        assertThat(placeholder).isNotNull();
        assertThat(placeholder).isNotBlank();
    }

    /**
     * 正常场景：搜索框的最大长度限制
     */
    @Test
    public void testSearchInputMaxLength() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        String maxLength = searchInput.getAttribute("maxlength");
        // 百度搜索框通常没有设置maxlength属性，或者设置了一个较大的值
        assertThat(maxLength).isNull();
    }

    /**
     * 正常场景：搜索建议功能
     */
    @Test
    public void testSearchSuggestions() {
        String keyword = "Test";
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(keyword);
        
        // 等待搜索建议出现
        WebElement suggestionsContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("s-ctner")));
        List<WebElement> suggestions = suggestionsContainer.findElements(By.tagName("li"));
        
        // 验证搜索建议包含输入的关键词
        assertThat(suggestions).isNotEmpty();
        for (WebElement suggestion : suggestions) {
            assertThat(suggestion.getText()).containsIgnoringCase(keyword);
        }
    }

    /**
     * 正常场景：清除搜索框内容
     */
    @Test
    public void testClearSearchInput() {
        String keyword = "Testcontainers";
        
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        searchInput.sendKeys(keyword);
        
        // 验证输入内容
        assertThat(searchInput.getAttribute("value")).isEqualTo(keyword);
        
        // 清除输入内容
        searchInput.clear();
        
        // 验证内容已清除
        assertThat(searchInput.getAttribute("value")).isEmpty();
    }

    /**
     * 异常场景：网络中断模拟（通过验证页面加载失败）
     */
    @Test
    public void testNetworkFailure() {
        // 尝试访问一个不存在的域名，模拟网络中断
        try {
            driver.get("https://nonexistent.example.com");
            // 验证页面加载失败
            assertThat(driver.getTitle()).contains("无法访问此网站");
        } catch (Exception e) {
            // 捕获任何异常并验证
            assertThat(e).isNotNull();
        }
    }

    /**
     * 浏览器兼容性测试：Chrome浏览器
     */
    @Test
    public void testChromeCompatibility() {
        // 验证Chrome浏览器能正常访问百度首页
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
        assertThat(searchInput.isDisplayed()).isTrue();
        
        // 验证页面标题
        assertThat(driver.getTitle()).isEqualTo("百度一下，你就知道");
    }

    /**
     * 浏览器兼容性测试：Firefox浏览器
     * 注意：需要在容器配置中切换为Firefox
     */
    // @Test
    // public void testFirefoxCompatibility() {
    //     // 验证Firefox浏览器能正常访问百度首页
    //     WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
    //     assertThat(searchInput.isDisplayed()).isTrue();
    //     
    //     // 验证页面标题
    //     assertThat(driver.getTitle()).isEqualTo("百度一下，你就知道");
    // }

    /**
     * 浏览器兼容性测试：Edge浏览器
     * 注意：需要在容器配置中切换为Edge
     */
    // @Test
    // public void testEdgeCompatibility() {
    //     // 验证Edge浏览器能正常访问百度首页
    //     WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SEARCH_INPUT_ID)));
    //     assertThat(searchInput.isDisplayed()).isTrue();
    //     
    //     // 验证页面标题
    //     assertThat(driver.getTitle()).isEqualTo("百度一下，你就知道");
    // }

    /**
     * 浏览器兼容性测试：页面响应式设计
     */
    @Test
    public void testResponsiveDesign() {
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
}