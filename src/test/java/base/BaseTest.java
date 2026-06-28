package base;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * Base class for all test classes.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Bootstrap the WebDriver instance (Chrome / Edge) with consistent options.</li>
 *   <li>Create and expose a single {@link Wait} that is shared with every page object.</li>
 *   <li>Read configuration from {@code config.properties} (baseUrl, timeouts).</li>
 *   <li>Provide random data helpers for registration tests.</li>
 * </ul>
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    /** Shared across helper methods — no need to re-instantiate on every call. */
    private static final Random RANDOM = new Random();

    public WebDriver driver;
    public Wait<WebDriver> wait;
    public String baseUrl;

    @BeforeClass
    @Parameters("browser")
    public void initializeDriver(String browser) {
        Properties config = new Properties();
        try (InputStream input = BaseTest.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                config.load(input);
            } else {
                logger.warn("config.properties not found on classpath — all defaults will be used.");
            }
        } catch (IOException e) {
            logger.warn("Failed to read config.properties — all defaults will be used.", e);
        }

        baseUrl = config.getProperty("baseUrl", AppConstants.DEFAULT_BASE_URL);
        int waitTimeoutSeconds  = parseIntProperty(config, "wait.timeout.seconds",
                                                    AppConstants.DEFAULT_WAIT_TIMEOUT_SECONDS);
        int pollIntervalSeconds = parseIntProperty(config, "wait.poll.seconds",
                                                    AppConstants.DEFAULT_POLL_INTERVAL_SECONDS);

        logger.info("Base URL        : {}", baseUrl);
        logger.info("Wait timeout    : {}s  |  Poll interval: {}s", waitTimeoutSeconds, pollIntervalSeconds);

        driver = createDriver(browser);

        wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(waitTimeoutSeconds))
                .pollingEvery(Duration.ofSeconds(pollIntervalSeconds))
                .ignoring(NoSuchElementException.class);

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        // Individual test methods navigate to their own start URL.
        // No blanket driver.get() here — that would be a wasted round-trip for every test class.
    }

    @AfterClass
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Random data helpers ───────────────────────────────────────────────────

    public String randomUserName() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String randomUserEmail() {
        return RandomStringUtils.randomAlphabetic(5) + "@gmail.com";
    }

    public String randomUserPassword() {
        return RandomStringUtils.randomAlphabetic(4) + "@" + RandomStringUtils.randomNumeric(5);
    }

    public String randomUserRole() {
        String[] roles = {"Brand", "Influencer"};
        return roles[RANDOM.nextInt(roles.length)];
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private WebDriver createDriver(String browser) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments(
                        "--disable-features=PasswordLeakDetection,PasswordLeakToggleMove",
                        "--disable-popup-blocking",
                        "--disable-default-apps",
                        "--disable-extensions"
                );
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_leak_detection", false);
                prefs.put("profile.password_manager.leak_detection", false);
                prefs.put("password_manager_enabled", false);
                opts.setExperimentalOption("prefs", prefs);
                yield new ChromeDriver(opts);
            }
            case "edge" -> {
                EdgeOptions opts = new EdgeOptions();
                opts.addArguments(
                        "--disable-features=PasswordLeakDetection,PasswordLeakToggleMove",
                        "--disable-popup-blocking",
                        "--disable-default-apps",
                        "--disable-extensions"
                );
                yield new EdgeDriver(opts);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported browser: \"" + browser + "\". Supported values: chrome, edge.");
        };
    }

    /**
     * Reads an integer property from the given {@link Properties} object.
     * Falls back to {@code defaultValue} if the key is missing or the value cannot be parsed.
     */
    private int parseIntProperty(Properties props, String key, int defaultValue) {
        String raw = props.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for config key '{}': '{}'. Using default: {}.", key, raw, defaultValue);
            return defaultValue;
        }
    }
}
