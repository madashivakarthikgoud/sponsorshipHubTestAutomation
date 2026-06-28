package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

public class DashboardPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    public DashboardPage(WebDriver driver, Wait<WebDriver> wait) {
        super(driver, wait);
    }

    private final By snackBarLabelLocator   = By.cssSelector(".mdc-snackbar__label");
    private final By logoutBtnLocator       = By.xpath("//button//span[contains(text(),'Logout')]");

    @FindBy(xpath = "//button[contains(@class,'user-menu-btn')]")
    private WebElement menuBtn;

    @FindBy(xpath = "//button//span[contains(text(),'Logout')]")
    private WebElement logoutBtn;

    @FindBy(xpath = "//button[@routerlink='/campaigns']")
    private WebElement campaignsBtn;

    public void clickCampaignsBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(campaignsBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Clicked Campaigns navigation button");
    }

    public void clickMenuBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(menuBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        // Wait for the dropdown menu to appear — no Thread.sleep needed.
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtnLocator));
    }

    public void clickLogout() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(logoutBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Logged out");
    }

    /**
     * Waits for the "Registration successful!" snack-bar and returns its text.
     * Returns an empty string if it does not appear within the wait timeout.
     */
    public String getRegistrationSuccessText() {
        try {
            By locator = By.xpath(
                "//mat-snack-bar-container//div[normalize-space()='Registration successful!']");
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
            logger.info("Registration success popup: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Registration success popup did not appear within timeout.");
            return "";
        }
    }

    /**
     * Waits for the "Login successful!" snack-bar and returns its text.
     * Returns an empty string if it does not appear within the wait timeout.
     */
    public String getLoginSuccessText() {
        try {
            By locator = By.xpath(
                "//mat-snack-bar-container//div[normalize-space()='Login successful!']");
            String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
            logger.info("Login success popup: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Login success popup did not appear within timeout.");
            return "";
        }
    }

    public String getSnackBarMessage() {
        try {
            String message = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(snackBarLabelLocator))
                    .getText().trim();
            logger.info("Snack-bar message: {}", message);
            return message;
        } catch (Exception e) {
            logger.error("Snack-bar did not appear within timeout.");
            return "";
        }
    }
}