package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for all Page Objects.
 * Holds the shared driver + wait references and common page utilities.
 */
public class BasePage {

    public final WebDriver driver;
    public final Wait<WebDriver> wait;

    private static final Logger logger = LogManager.getLogger(BasePage.class);

    /**
     * Selector that covers Angular Material inline validation errors and snack-bar labels.
     * Used by {@link #getErrorMessage()} so every page gets this behavior for free.
     */
    private static final By ERROR_LOCATOR = By.cssSelector("mat-error, .mdc-snackbar__label");

    public BasePage(WebDriver driver, Wait<WebDriver> wait) {
        this.driver = driver;
        this.wait = wait;
        PageFactory.initElements(driver, this);
    }

    /**
     * Waits for one or more visible error/snack-bar messages and returns them
     * joined by " | ".  Returns an empty string if none appear within the wait timeout.
     */
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_LOCATOR));
            List<WebElement> errorElements = driver.findElements(ERROR_LOCATOR);
            return errorElements.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .map(String::trim)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.joining(" | "));
        } catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            logger.error("No error messages appeared within the timeout period.");
            return "";
        }
    }

    /**
     * Attempts to click a button, waiting up to 2 seconds for it to become enabled
     * (allowing Angular validation and binding to stabilize).
     * If the button is disabled, the method skips the click cleanly to allow validation reading.
     */
    protected void clickSubmitOrSkipIfDisabled(WebElement button, String buttonName) {
        boolean isEnabled = false;
        try {
            org.openqa.selenium.support.ui.WebDriverWait shortWait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(2));
            isEnabled = shortWait.until(d -> button.isDisplayed() && button.isEnabled());
        } catch (Exception e) {
            // timed out waiting to become enabled - button is disabled
        }

        if (isEnabled) {
            try {
                button.click();
                logger.info("Clicked " + buttonName);
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                logger.warn("Click intercepted on " + buttonName + " — falling back to JS click.");
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            }
        } else {
            logger.info(buttonName + " is disabled. Skipping click.");
        }
    }
}
