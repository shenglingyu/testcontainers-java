# Testcontainers for Java

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/testcontainers/testcontainers-java/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/testcontainers/testcontainers-java/tree/main)
[![Maven Central](https://img.shields.io/maven-central/v/org.testcontainers/testcontainers.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.testcontainers%22%20AND%20a:%22testcontainers%22)
[![Slack](https://img.shields.io/badge/slack-join%20chat-4A154B.svg?logo=slack)](https://slack.testcontainers.org)
[![Javadocs](https://javadoc.io/badge/org.testcontainers/testcontainers.svg)](https://javadoc.io/doc/org.testcontainers/testcontainers)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-testcontainers-%23F48024)](https://stackoverflow.com/questions/tagged/testcontainers)

Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

## 百度搜索自动化测试

本项目包含一个百度搜索输入框的自动化测试示例，展示了如何使用Testcontainers结合Selenium进行Web应用测试。

### 测试文件

- `BaiduSearchTest.java` - 百度搜索输入框自动化测试类，覆盖25个测试场景
- `pom.xml` - Maven配置文件，包含所有必要的依赖

### 测试场景

#### 基础功能测试（25个）
1. **页面加载测试** - 验证百度首页是否成功加载并显示标题
2. **元素存在性测试** - 验证搜索输入框和搜索按钮是否存在且可见
3. **正常关键词搜索** - 输入"自动化测试"进行搜索并验证结果
4. **特殊字符搜索** - 输入"!@#$%^&*()"进行搜索并验证结果
5. **数字搜索** - 输入"123456"进行搜索并验证结果
6. **英文搜索** - 输入"Automation Testing"进行搜索并验证结果
7. **长文本搜索** - 输入超过100个字符的长文本进行搜索
8. **空格搜索** - 输入多个空格进行搜索
9. **URL搜索** - 输入"https://www.baidu.com"进行搜索
10. **HTML标签搜索** - 输入"<html><body>测试</body></html>"进行搜索
11. **回车键搜索** - 输入关键词后按回车键进行搜索
12. **搜索建议功能** - 输入关键词后验证是否显示搜索建议
13. **搜索建议点击** - 点击搜索建议项进行搜索
14. **输入框清除功能** - 验证搜索输入框的清除功能
15. **多次搜索功能** - 连续进行多次搜索验证功能稳定性
16. **搜索结果翻页** - 验证搜索结果页面的翻页功能
17. **搜索结果准确性** - 验证搜索结果是否与关键词相关
18. **自动补全功能** - 验证输入部分关键词时的自动补全功能
19. **搜索历史记录** - 验证是否显示搜索历史记录
20. **最大长度限制** - 验证搜索输入框的最大长度限制
21. **Placeholder文本** - 验证输入框的占位符文本是否正确
22. **搜索按钮Hover效果** - 验证鼠标悬停在搜索按钮上的视觉反馈
23. **键盘快捷键** - 验证Ctrl+A全选输入框内容的功能
24. **搜索结果加载时间** - 验证搜索结果页面的加载时间
25. **空搜索** - 点击搜索按钮而不输入任何内容

#### 浏览器兼容性测试（6个）
26. Chrome浏览器兼容性测试 - 验证百度搜索在Chrome浏览器中的基本功能
27. Firefox浏览器兼容性测试 - 验证百度搜索在Firefox浏览器中的基本功能
28. Edge浏览器兼容性测试 - 验证百度搜索在Edge浏览器中的基本功能
29. 多浏览器兼容性测试（参数化测试） - 使用参数化测试验证百度搜索在不同浏览器中的基本功能
30. 浏览器窗口大小兼容性测试 - 验证百度搜索在不同窗口大小下的显示和功能
31. 浏览器缩放级别兼容性测试 - 验证百度搜索在不同缩放级别下的显示和功能

### 运行百度搜索测试

```bash
mvn test -Dtest=BaiduSearchTest
```

## Testcontainers makes the following kinds of tests easier:

* **Data access layer integration tests**: use a containerized instance of a MySQL, PostgreSQL or Oracle database to test your data access layer code without requiring complex setup on developers' machines and safe in the knowledge that your tests will always start with a known DB state.

* **Application integration tests**: for running your application in a short-lived test mode with dependencies, such as databases, message queues or web servers.

* **UI/Acceptance tests**: use containerized web browsers (Chrome, Firefox, Safari) in conjunction with Selenium WebDriver to run your UI tests. Each test can get a fresh instance of the browser, with no browser state, plugin variations or automated browser upgrades to worry about.

## Usage

For more information, please see the [Testcontainers Quickstart Guide](https://testcontainers.com/quickstart/junit-5/).

### Example: Running a database migration with Flyway

```java
@Testcontainers
class FlywayContainerTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"))
        .withDatabaseName("db")
        .withUsername("user")
        .withPassword("password");

    @Test
    void testMigration() {
        Flyway flyway = Flyway.configure()
            .dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
            )
            .load();

        MigrationInfo migrationInfo = flyway.info().current();

        assertNull(migrationInfo);

        flyway.migrate();

        migrationInfo = flyway.info().current();

        assertEquals("1", migrationInfo.getVersion().getVersion());
    }
}
```

### Example: Testing a Spring Boot application with Testcontainers

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SpringBootContainerTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"))
        .withDatabaseName("db")
        .withUsername("user")
        .withPassword("password");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void testSomething(@Autowired RestTemplate restTemplate) {
        // ... test using restTemplate ...
    }
}
```

### Example: Running Selenium tests with Testcontainers

```java
@Testcontainers
class SeleniumContainerTest {

    @Container
    private static final BrowserWebDriverContainer<?> browserContainer = new BrowserWebDriverContainer<>()
        .withCapabilities(new ChromeOptions());

    @Test
    void testTitle() {
        WebDriver driver = browserContainer.getWebDriver();
        driver.get("https://example.com");
        assertEquals("Example Domain", driver.getTitle());
    }
}
```

## Documentation

The full documentation is available at [testcontainers.com](https://testcontainers.com/).

## Modules

Testcontainers is distributed as separate modules:

* **Core module**: The main Testcontainers library, including the `GenericContainer` class and support for JUnit 4 and JUnit 5.
* **Database modules**: Support for specific databases, such as PostgreSQL, MySQL, Oracle, and more.
* **Selenium module**: Support for running Selenium tests with containerized browsers.
* **Message queue modules**: Support for message queues, such as Kafka, RabbitMQ, and more.
* **Cloud modules**: Support for cloud services, such as AWS S3, Azure Blob Storage, and more.

For a full list of modules, please see the [Testcontainers documentation](https://testcontainers.com/modules/).

## Contributing

We welcome contributions! Please see the [Contributing Guide](CONTRIBUTING.md) for more information.

## License

Testcontainers is licensed under the [MIT License](LICENSE).

## Support

If you need help with Testcontainers, please check out the following resources:

* [Stack Overflow](https://stackoverflow.com/questions/tagged/testcontainers)
* [Slack](https://slack.testcontainers.org)
* [GitHub Issues](https://github.com/testcontainers/testcontainers-java/issues)
