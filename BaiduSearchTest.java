import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 百度搜索输入框自动化测试
 * 覆盖所有正常和异常场景
 */
public class BaiduSearchTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BAIDU_URL = "https://www.baidu.com/?a";
    private static final String SEARCH_INPUT_ID = "kw";
    private static final String SEARCH_BUTTON_ID = "su";
    private static final String SEARCH_RESULT_STATS_ID = "tsn_inner";
    private static final String SUGGESTION_DIV_CLASS = "bdsug";

    /**
     * 浏览器类型枚举
     */
    public enum BrowserType {
        CHROME,
        FIREFOX,
        EDGE
    }

    /**
     * 根据浏览器类型初始化WebDriver
     * @param browserType 浏览器类型
     * @return WebDriver实例
     */
    private WebDriver initDriver(BrowserType browserType) {
        switch (browserType) {
            case CHROME:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless"); // 无头模式运行
                chromeOptions.addArguments("--disable-gpu"); // 禁用GPU加速
                chromeOptions.addArguments("--window-size=1920,1080"); // 设置窗口大小
                chromeOptions.addArguments("--no-sandbox"); // 禁用沙盒模式
                chromeOptions.addArguments("--disable-dev-shm-usage"); // 禁用/dev/shm使用
                return new ChromeDriver(chromeOptions);
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless"); // 无头模式运行
                firefoxOptions.addArguments("--window-size=1920,1080"); // 设置窗口大小
                return new FirefoxDriver(firefoxOptions);
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless"); // 无头模式运行
                edgeOptions.addArguments("--disable-gpu"); // 禁用GPU加速
                edgeOptions.addArguments("--window-size=1920,1080"); // 设置窗口大小
                edgeOptions.addArguments("--no-sandbox"); // 禁用沙盒模式
                return new EdgeDriver(edgeOptions);
            default:
                throw new IllegalArgumentException("不支持的浏览器类型: " + browserType);
        }
    }

    /**
     * 测试前的准备工作
     * 初始化Chrome浏览器驱动和等待对象
     */
    @BeforeEach
    public void setUp() {
        // 默认使用Chrome浏览器
        driver = initDriver(BrowserType.CHROME);
        // 设置隐式等待时间
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        // 初始化显式等待对象
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // 导航到百度首页
        driver.get(BAIDU_URL);
    }

    /**
     * 测试后清理工作
     * 关闭浏览器驱动
     */
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 测试场景1：页面成功加载并显示标题
     */
    @Test
    public void testPageLoadSuccess() {
        // 验证页面标题是否正确
        assertEquals("百度一下，你就知道", driver.getTitle());
        // 验证页面是否包含百度Logo
        assertTrue(driver.findElement(By.id("lg")).isDisplayed());
    }

    /**
     * 测试场景2：搜索输入框和搜索按钮存在且可见
     */
    @Test
    public void testSearchElementsExist() {
        // 验证搜索输入框存在且可见
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        assertTrue(searchInput.isDisplayed());
        assertTrue(searchInput.isEnabled());

        // 验证搜索按钮存在且可见
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        assertTrue(searchButton.isDisplayed());
        assertTrue(searchButton.isEnabled());
        assertEquals("百度一下", searchButton.getAttribute("value"));
    }

    /**
     * 测试场景3：输入正常关键词进行搜索
     * 输入"自动化测试"并验证搜索结果
     */
    @Test
    public void testNormalSearch() {
        String searchKeyword = "自动化测试";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));

        // 验证搜索结果统计信息存在
        WebElement resultStats = driver.findElement(By.id(SEARCH_RESULT_STATS_ID));
        assertTrue(resultStats.isDisplayed());
        assertTrue(resultStats.getText().contains("找到相关结果"));

        // 验证搜索结果列表非空
        List<WebElement> searchResults = driver.findElements(By.cssSelector("h3.t a"));
        assertFalse(searchResults.isEmpty());
        assertTrue(searchResults.size() > 0);
    }

    /**
     * 测试场景4：输入特殊字符进行搜索
     * 输入"!@#$%^&*()"并验证搜索结果
     */
    @Test
    public void testSpecialCharactersSearch() {
        String searchKeyword = "!@#$%^&*()";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));
    }

    /**
     * 测试场景5：输入数字进行搜索
     * 输入"123456"并验证搜索结果
     */
    @Test
    public void testNumericSearch() {
        String searchKeyword = "123456";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));
    }

    /**
     * 测试场景6：输入英文进行搜索
     * 输入"Automation Testing"并验证搜索结果
     */
    @Test
    public void testEnglishSearch() {
        String searchKeyword = "Automation Testing";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));
    }

    /**
     * 测试场景7：输入长文本进行搜索
     * 输入超过100个字符的长文本并验证搜索结果
     */
    @Test
    public void testLongTextSearch() {
        String searchKeyword = "这是一个非常长的测试文本，用于测试百度搜索输入框对长文本的处理能力。" +
                "这个文本包含了多个句子和标点符号，目的是验证搜索功能在处理大段文本时的表现。" +
                "我们希望百度能够正确处理这种长文本输入，并返回相关的搜索结果。";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含部分搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword.substring(0, 20)));
    }

    /**
     * 测试场景8：输入空格进行搜索
     * 输入多个空格并验证搜索结果
     */
    @Test
    public void testSpaceSearch() {
        String searchKeyword = "   ";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待页面响应
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 验证是否仍在百度首页或显示搜索结果
        assertTrue(driver.getTitle().contains("百度一下，你就知道") || 
                   driver.getTitle().contains("搜索"));
    }

    /**
     * 测试场景9：输入URL进行搜索
     * 输入"https://www.baidu.com"并验证搜索结果
     */
    @Test
    public void testUrlSearch() {
        String searchKeyword = "https://www.baidu.com";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));
    }

    /**
     * 测试场景10：输入HTML标签进行搜索
     * 输入"<html><body>测试</body></html>"并验证搜索结果
     */
    @Test
    public void testHtmlTagSearch() {
        String searchKeyword = "<html><body>测试</body></html>";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含部分搜索关键词
        assertTrue(driver.getTitle().contains("测试"));
    }

    /**
     * 测试场景11：点击搜索按钮而不输入任何内容
     * 验证是否显示提示信息或返回搜索结果
     */
    @Test
    public void testEmptySearch() {
        // 直接点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待页面响应
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 验证是否仍在百度首页或显示搜索结果
        assertTrue(driver.getTitle().contains("百度一下，你就知道") || 
                   driver.getTitle().contains("搜索"));
    }

    /**
     * 测试场景12：使用回车键进行搜索
     * 输入关键词后按回车键验证搜索功能
     */
    @Test
    public void testEnterKeySearch() {
        String searchKeyword = "回车键搜索测试";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);
        // 按回车键
        searchInput.sendKeys(Keys.ENTER);

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证搜索结果页面标题包含搜索关键词
        assertTrue(driver.getTitle().contains(searchKeyword));
    }

    /**
     * 测试场景13：搜索建议功能
     * 输入关键词后验证是否显示搜索建议
     */
    @Test
    public void testSearchSuggestions() {
        String searchKeyword = "自动化";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 等待搜索建议出现
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(SUGGESTION_DIV_CLASS)));

        // 验证搜索建议框可见
        WebElement suggestionDiv = driver.findElement(By.className(SUGGESTION_DIV_CLASS));
        assertTrue(suggestionDiv.isDisplayed());

        // 验证搜索建议列表非空
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".bdsug li"));
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.size() > 0);

        // 验证搜索建议包含输入的关键词
        for (WebElement suggestion : suggestions) {
            assertTrue(suggestion.getText().contains(searchKeyword));
        }
    }

    /**
     * 测试场景14：搜索建议点击功能
     * 输入关键词后点击搜索建议项验证搜索功能
     */
    @Test
    public void testSuggestionClick() {
        String searchKeyword = "自动化";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 等待搜索建议出现
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(SUGGESTION_DIV_CLASS)));

        // 获取第一个搜索建议项
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".bdsug li"));
        if (!suggestions.isEmpty()) {
            String firstSuggestionText = suggestions.get(0).getText();
            // 点击第一个搜索建议项
            suggestions.get(0).click();

            // 等待搜索结果加载完成
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

            // 验证搜索结果页面标题包含搜索建议文本
            assertTrue(driver.getTitle().contains(firstSuggestionText));
        }
    }

    /**
     * 测试场景15：搜索输入框的清除功能
     * 输入关键词后点击清除按钮验证输入框是否清空
     */
    @Test
    public void testSearchInputClear() {
        String searchKeyword = "测试清除功能";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 验证输入框内容
        assertEquals(searchKeyword, searchInput.getAttribute("value"));

        // 点击清除按钮（如果存在）
        try {
            WebElement clearButton = driver.findElement(By.cssSelector("#kw_clear"));
            if (clearButton.isDisplayed()) {
                clearButton.click();
                // 验证输入框是否清空
                assertEquals("", searchInput.getAttribute("value"));
            }
        } catch (NoSuchElementException e) {
            // 如果清除按钮不存在，使用Backspace键清除
            searchInput.sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
            // 验证输入框是否清空
            assertEquals("", searchInput.getAttribute("value"));
        }
    }

    /**
     * 测试场景16：多次搜索功能
     * 连续进行多次搜索验证功能稳定性
     */
    @Test
    public void testMultipleSearches() {
        String[] searchKeywords = {"第一次搜索", "第二次搜索", "第三次搜索"};

        for (String keyword : searchKeywords) {
            // 输入搜索关键词
            WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
            searchInput.clear();
            searchInput.sendKeys(keyword);

            // 点击搜索按钮
            WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
            searchButton.click();

            // 等待搜索结果加载完成
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

            // 验证搜索结果页面标题包含搜索关键词
            assertTrue(driver.getTitle().contains(keyword));
        }
    }

    /**
     * 测试场景17：搜索结果页面的翻页功能
     * 验证搜索结果页面是否可以翻页
     */
    @Test
    public void testSearchResultPagination() {
        String searchKeyword = "自动化测试";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证下一页按钮存在且可见
        try {
            WebElement nextPageButton = driver.findElement(By.cssSelector(".n a"));
            assertTrue(nextPageButton.isDisplayed());
            assertTrue(nextPageButton.isEnabled());
            assertEquals("下一页>", nextPageButton.getText());
        } catch (NoSuchElementException e) {
            // 如果只有一页结果，下一页按钮可能不存在，这是正常情况
            System.out.println("搜索结果只有一页，下一页按钮不存在");
        }
    }

    /**
     * 测试场景18：搜索结果的准确性
     * 验证搜索结果是否与输入的关键词相关
     */
    @Test
    public void testSearchResultAccuracy() {
        String searchKeyword = "百度搜索测试";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 点击搜索按钮
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 验证前几个搜索结果标题包含搜索关键词
        List<WebElement> searchResults = driver.findElements(By.cssSelector("h3.t a"));
        if (searchResults.size() > 0) {
            // 验证前5个搜索结果（如果有）
            int resultsToCheck = Math.min(5, searchResults.size());
            for (int i = 0; i < resultsToCheck; i++) {
                String resultTitle = searchResults.get(i).getText();
                // 验证结果标题包含搜索关键词或相关词汇
                assertTrue(resultTitle.contains("百度") || resultTitle.contains("搜索") || resultTitle.contains("测试"));
            }
        }
    }

    /**
     * 测试场景19：搜索输入框的自动补全功能
     * 输入部分关键词验证自动补全功能
     */
    @Test
    public void testAutoComplete() {
        String searchKeyword = "自动";
        String expectedAutoComplete = "自动化";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 等待搜索建议出现
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className(SUGGESTION_DIV_CLASS)));

        // 获取搜索建议列表
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".bdsug li"));
        if (!suggestions.isEmpty()) {
            // 验证第一个搜索建议是否包含预期的自动补全内容
            assertTrue(suggestions.get(0).getText().contains(expectedAutoComplete));
        }
    }

    /**
     * 测试场景20：搜索历史记录功能
     * 验证是否显示搜索历史记录
     */
    @Test
    public void testSearchHistory() {
        String searchKeyword = "测试搜索历史";

        // 第一次搜索
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 返回首页
        driver.get(BAIDU_URL);

        // 点击搜索输入框
        searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.click();

        // 等待搜索历史出现
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".s-history-list")));
            // 验证搜索历史包含刚才搜索的关键词
            WebElement historyList = driver.findElement(By.cssSelector(".s-history-list"));
            assertTrue(historyList.getText().contains(searchKeyword));
        } catch (TimeoutException e) {
            // 如果搜索历史功能未启用或未显示，这是正常情况
            System.out.println("搜索历史功能未显示");
        }
    }

    /**
     * 测试场景21：搜索输入框的最大长度限制
     * 验证输入框是否有最大长度限制
     */
    @Test
    public void testMaxLengthLimit() {
        // 获取搜索输入框的maxlength属性
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        String maxLength = searchInput.getAttribute("maxlength");

        if (maxLength != null) {
            int maxLengthValue = Integer.parseInt(maxLength);
            System.out.println("搜索输入框的最大长度限制为: " + maxLengthValue);

            // 输入超过最大长度的文本
            String longText = "a".repeat(maxLengthValue + 10);
            searchInput.sendKeys(longText);

            // 验证输入框实际内容长度不超过最大长度
            String actualText = searchInput.getAttribute("value");
            assertTrue(actualText.length() <= maxLengthValue);
        } else {
            System.out.println("搜索输入框没有设置最大长度限制");
        }
    }

    /**
     * 测试场景22：搜索输入框的 placeholder 属性
     * 验证输入框是否显示正确的占位符文本
     */
    @Test
    public void testPlaceholderText() {
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        String placeholder = searchInput.getAttribute("placeholder");
        // 验证占位符文本是否正确
        assertEquals("请输入搜索关键词", placeholder);
    }

    /**
     * 测试场景23：搜索按钮的 hover 效果
     * 验证鼠标悬停在搜索按钮上时是否有视觉反馈
     */
    @Test
    public void testSearchButtonHoverEffect() {
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));

        // 使用JavaScript获取按钮的背景颜色
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalColor = (String) js.executeScript("return window.getComputedStyle(arguments[0]).backgroundColor", searchButton);

        // 模拟鼠标悬停
        Actions actions = new Actions(driver);
        actions.moveToElement(searchButton).perform();

        // 等待一下确保hover效果生效
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 获取hover后的背景颜色
        String hoverColor = (String) js.executeScript("return window.getComputedStyle(arguments[0]).backgroundColor", searchButton);

        // 验证背景颜色是否发生变化
        assertNotEquals(originalColor, hoverColor);
    }

    /**
     * 测试场景24：搜索输入框的键盘快捷键
     * 验证Ctrl+A是否可以全选输入框内容
     */
    @Test
    public void testKeyboardShortcuts() {
        String searchKeyword = "测试键盘快捷键";

        // 输入搜索关键词
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);

        // 使用Ctrl+A全选内容
        searchInput.sendKeys(Keys.CONTROL + "a");

        // 等待一下确保全选生效
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 使用JavaScript验证是否全选
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean isSelected = (boolean) js.executeScript("return arguments[0].selectionStart === 0 && arguments[0].selectionEnd === arguments[0].value.length", searchInput);

        // 验证内容是否被全选
        assertTrue(isSelected);
    }

    /**
     * 测试场景25：搜索结果页面的加载时间
     * 验证搜索结果页面的加载时间是否在可接受范围内
     */
    @Test
    public void testSearchResultLoadTime() {
        String searchKeyword = "测试搜索结果加载时间";

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 输入搜索关键词并点击搜索按钮
        WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
        searchInput.sendKeys(searchKeyword);
        WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
        searchButton.click();

        // 等待搜索结果加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

        // 记录结束时间
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        // 验证加载时间是否在5秒内（可根据实际情况调整）
        assertTrue(loadTime < 5000, "搜索结果页面加载时间过长: " + loadTime + "ms");
        System.out.println("搜索结果页面加载时间: " + loadTime + "ms");
    }

    // ==================== 浏览器兼容性测试场景 ====================

    /**
     * 测试场景26：Chrome浏览器兼容性测试
     * 验证百度搜索在Chrome浏览器中的基本功能
     */
    @Test
    public void testChromeBrowserCompatibility() {
        runBrowserCompatibilityTest(BrowserType.CHROME);
    }

    /**
     * 测试场景27：Firefox浏览器兼容性测试
     * 验证百度搜索在Firefox浏览器中的基本功能
     */
    @Test
    public void testFirefoxBrowserCompatibility() {
        runBrowserCompatibilityTest(BrowserType.FIREFOX);
    }

    /**
     * 测试场景28：Edge浏览器兼容性测试
     * 验证百度搜索在Edge浏览器中的基本功能
     */
    @Test
    public void testEdgeBrowserCompatibility() {
        runBrowserCompatibilityTest(BrowserType.EDGE);
    }

    /**
     * 测试场景29：多浏览器兼容性测试（参数化测试）
     * 使用参数化测试验证百度搜索在不同浏览器中的基本功能
     * @param browserType 浏览器类型
     */
    @ParameterizedTest
    @EnumSource(BrowserType.class)
    public void testMultiBrowserCompatibility(BrowserType browserType) {
        runBrowserCompatibilityTest(browserType);
    }

    /**
     * 执行浏览器兼容性测试的通用方法
     * @param browserType 浏览器类型
     */
    private void runBrowserCompatibilityTest(BrowserType browserType) {
        System.out.println("开始测试浏览器: " + browserType);

        // 关闭当前driver（如果存在）
        if (driver != null) {
            driver.quit();
        }

        // 初始化指定浏览器的driver
        driver = initDriver(browserType);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // 导航到百度首页
            driver.get(BAIDU_URL);

            // 验证页面标题
            assertEquals("百度一下，你就知道", driver.getTitle());

            // 验证搜索输入框和搜索按钮存在
            WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
            assertTrue(searchInput.isDisplayed());
            assertTrue(searchInput.isEnabled());

            WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
            assertTrue(searchButton.isDisplayed());
            assertTrue(searchButton.isEnabled());

            // 执行简单搜索
            String searchKeyword = "浏览器兼容性测试";
            searchInput.sendKeys(searchKeyword);
            searchButton.click();

            // 等待搜索结果加载完成
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(SEARCH_RESULT_STATS_ID)));

            // 验证搜索结果页面标题包含搜索关键词
            assertTrue(driver.getTitle().contains(searchKeyword));

            // 验证搜索结果统计信息存在
            WebElement resultStats = driver.findElement(By.id(SEARCH_RESULT_STATS_ID));
            assertTrue(resultStats.isDisplayed());

            System.out.println("浏览器 " + browserType + " 测试通过");
        } finally {
            // 确保driver被关闭
            if (driver != null) {
                driver.quit();
            }
        }
    }

    /**
     * 测试场景30：浏览器窗口大小兼容性测试
     * 验证百度搜索在不同窗口大小下的显示和功能
     */
    @Test
    public void testWindowSizeCompatibility() {
        // 测试不同的窗口大小
        int[][] windowSizes = {
            {1920, 1080}, // 全高清
            {1366, 768},  // 常见笔记本分辨率
            {1024, 768},  // 旧版分辨率
            {768, 1024},  // 平板竖屏
            {375, 667}    // 手机竖屏
        };

        for (int[] size : windowSizes) {
            int width = size[0];
            int height = size[1];
            System.out.println("测试窗口大小: " + width + "x" + height);

            // 设置窗口大小
            driver.manage().window().setSize(new Dimension(width, height));

            // 刷新页面
            driver.navigate().refresh();

            // 验证页面标题
            assertEquals("百度一下，你就知道", driver.getTitle());

            // 验证搜索输入框和搜索按钮存在且可见
            WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
            assertTrue(searchInput.isDisplayed());
            assertTrue(searchInput.isEnabled());

            WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
            assertTrue(searchButton.isDisplayed());
            assertTrue(searchButton.isEnabled());

            // 验证搜索输入框可以输入内容
            String testText = "窗口大小测试";
            searchInput.clear();
            searchInput.sendKeys(testText);
            assertEquals(testText, searchInput.getAttribute("value"));

            System.out.println("窗口大小 " + width + "x" + height + " 测试通过");
        }
    }

    /**
     * 测试场景31：浏览器缩放级别兼容性测试
     * 验证百度搜索在不同缩放级别下的显示和功能
     */
    @Test
    public void testZoomLevelCompatibility() {
        // 测试不同的缩放级别（100%, 125%, 150%, 75%, 50%）
        int[] zoomLevels = {100, 125, 150, 75, 50};

        for (int zoomLevel : zoomLevels) {
            System.out.println("测试缩放级别: " + zoomLevel + "%");

            // 使用JavaScript设置浏览器缩放级别
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.body.style.zoom = arguments[0]", zoomLevel / 100.0);

            // 等待页面调整
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 验证搜索输入框和搜索按钮存在且可见
            WebElement searchInput = driver.findElement(By.id(SEARCH_INPUT_ID));
            assertTrue(searchInput.isDisplayed());
            assertTrue(searchInput.isEnabled());

            WebElement searchButton = driver.findElement(By.id(SEARCH_BUTTON_ID));
            assertTrue(searchButton.isDisplayed());
            assertTrue(searchButton.isEnabled());

            System.out.println("缩放级别 " + zoomLevel + "% 测试通过");
        }
    }
}
