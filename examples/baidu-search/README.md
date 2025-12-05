# 百度搜索自动化测试

这是一个使用 Testcontainers 和 Selenium WebDriver 实现的百度搜索自动化测试项目，涵盖了各种正常和异常场景的测试，包括安全测试和浏览器兼容性测试。

## 测试场景

### 正常场景测试
- 搜索框存在且可见
- 搜索按钮存在且可见
- 输入有效关键词并点击搜索按钮
- 输入有效关键词并按回车键
- 搜索框的 placeholder 属性
- 搜索框的最大长度限制
- 搜索建议功能
- 清除搜索框内容

### 异常场景测试
- 输入空关键词点击搜索
- 输入超长关键词（超过1000个字符）
- 输入特殊字符（!@#$%^&*()_+{}[]|\\:;'\"<>,.?/~`）
- 页面加载超时
- 网络中断模拟

### 安全测试场景
- 基本 SQL 注入尝试（' OR 1=1 --）
- UNION 查询 SQL 注入尝试（' UNION SELECT username, password FROM users --）
- 基于时间的盲注尝试（' OR SLEEP(5) --）
- 错误型 SQL 注入尝试（' AND (SELECT COUNT(*) FROM information_schema.tables) > 0 --）
- 堆叠查询 SQL 注入尝试（'; DROP TABLE users --）
- XSS 攻击尝试（<script>alert('XSS')</script>）

### 浏览器兼容性测试
- Chrome 浏览器兼容性
- Firefox 浏览器兼容性
- Edge 浏览器兼容性
- 页面响应式设计测试（不同窗口尺寸）

## 技术栈

- **Java 11**: 编程语言
- **Testcontainers**: 用于启动浏览器容器
- **Selenium WebDriver**: 用于浏览器自动化
- **JUnit 5**: 测试框架
- **AssertJ**: 断言库
- **Gradle**: 构建工具

## 环境准备

1. **安装 Java 11 或更高版本**
   - 下载地址：https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
   - 配置 JAVA_HOME 环境变量

2. **安装 Docker**
   - 下载地址：https://www.docker.com/get-started
   - 确保 Docker 服务正在运行

3. **安装 Gradle**（可选，项目包含 Gradle Wrapper）
   - 下载地址：https://gradle.org/install/

## 项目结构

```
baidu-search/
├─ build.gradle          # Gradle 构建配置文件
├─ README.md            # 项目说明文档
└─ src/
   └─ test/
      └─ java/
         └─ org/
            └─ testcontainers/
               └─ examples/
                  ├─ BaiduSearchTest.java               # 主要测试类
                  └─ BaiduBrowserCompatibilityTest.java # 浏览器兼容性测试类
```

## 依赖安装

项目包含 Gradle Wrapper，无需手动安装 Gradle。在项目根目录下执行以下命令安装依赖：

```bash
./gradlew build --no-tests
```

或者在 Windows 系统上：

```cmd
gradlew.bat build --no-tests
```

## 执行测试

### 执行所有测试

```bash
./gradlew test
```

或者在 Windows 系统上：

```cmd
gradlew.bat test
```

### 执行特定测试类

执行主要测试类：

```bash
./gradlew test --tests "org.testcontainers.examples.BaiduSearchTest"
```

执行浏览器兼容性测试类：

```bash
./gradlew test --tests "org.testcontainers.examples.BaiduBrowserCompatibilityTest"
```

### 执行特定测试方法

```bash
./gradlew test --tests "org.testcontainers.examples.BaiduSearchTest.testSearchWithValidKeyword"
```

## 测试报告

测试执行完成后，可以在以下位置查看测试报告：

```
build/reports/tests/test/index.html
```

用浏览器打开该文件即可查看详细的测试结果。

## 注意事项

1. **Docker 资源配置**：确保 Docker 有足够的资源（至少 2GB 内存）来运行浏览器容器。

2. **网络连接**：测试需要访问百度网站，请确保网络连接正常。

3. **浏览器镜像下载**：首次运行测试时，Testcontainers 会自动下载所需的浏览器镜像（如 Chrome、Firefox、Edge），这可能需要一些时间。

4. **测试执行时间**：由于涉及多个浏览器和多个测试场景，完整测试可能需要较长时间。

5. **浏览器兼容性测试**：BaiduBrowserCompatibilityTest 类同时启动了 Chrome、Firefox 和 Edge 三个浏览器容器，需要较多的系统资源。如果资源不足，可以注释掉不需要的浏览器测试。

## 扩展测试

可以根据需要扩展更多的测试场景：

- 测试不同的搜索关键词类型（中文、英文、数字、混合）
- 测试搜索结果的排序和准确性
- 测试搜索历史功能
- 测试百度高级搜索功能
- 测试百度图片搜索、新闻搜索等其他搜索类型

## 许可证

MIT License