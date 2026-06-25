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

public class DashboardPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    // ─── Snack-bar Pop-ups ──────────────────────────────────────────────────────

    @FindBy(xpath = "//mat-snack-bar-container//div[normalize-space()='Registration successful!']")
    WebElement registrationSuccessfulPopUp;

    @FindBy(xpath = "//mat-snack-bar-container//div[normalize-space()='Login successful!']")
    WebElement loginSuccessfulPopUp;

    /** Generic snack-bar label — used when the exact message is dynamic */
    private final By snackBarLabelLocator = By.cssSelector(".mdc-snackbar__label");

    // ─── Navigation ─────────────────────────────────────────────────────────────

    @FindBy(xpath = "//button[contains(@class,'user-menu-btn')]")
    WebElement menuBtn;

    @FindBy(xpath = "//button//span[contains(text(),'Logout')]")
    WebElement logoutBtn;

    @FindBy(xpath = "//button[@routerlink='/campaigns']")
    WebElement campaignsBtn;

    // ─── Public API ─────────────────────────────────────────────────────────────

    public void clickCampaignsBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(campaignsBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Clicked Campaigns navigation button");
    }

    public void clickMenuBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(menuBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clickLogout() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(logoutBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Logged out");
    }

    // ─── Pop-up Accessors ───────────────────────────────────────────────────────

    public WebElement getRegistrationSuccessfulPopUp() {
        return registrationSuccessfulPopUp;
    }

    public WebElement getLoginSuccessfulPopUp() {
        return loginSuccessfulPopUp;
    }

    /**
     * Waits for and returns the text of the Material snack-bar.
     * Works for any snack-bar message — registration, login, campaign success, etc.
     */
    public String getSnackBarMessage() {
        try {
            WebElement snackBar = wait.until(
                ExpectedConditions.visibilityOfElementLocated(snackBarLabelLocator)
            );
            String message = snackBar.getText().trim();
            logger.info("Snack-bar message: {}", message);
            return message;
        } catch (Exception e) {
            logger.error("Snack-bar did not appear within timeout.");
            return "";
        }
    }
}