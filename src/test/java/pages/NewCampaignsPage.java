package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

import java.util.List;
import java.util.stream.Collectors;

public class NewCampaignsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(NewCampaignsPage.class);

    public NewCampaignsPage(WebDriver driver, Wait<WebDriver> wait) {
        super(driver, wait);
    }

    // ── Locators ─────────────────────────────────────────────────────────────

    @FindBy(xpath = "//input[@placeholder='Enter campaign name']")
    private WebElement campaignName;

    @FindBy(xpath = "//textarea[@placeholder='Describe your campaign']")
    private WebElement campaignDescription;

    private final By campaignStartDateLocator = By.xpath(
        "//mat-form-field[.//label[contains(., 'Start Date')] or .//mat-label[contains(., 'Start Date')]]//input"
    );

    private final By campaignEndDateLocator = By.xpath(
        "//mat-form-field[.//label[contains(., 'End Date')] or .//mat-label[contains(., 'End Date')]]//input"
    );

    @FindBy(xpath = "//mat-select[@formcontrolname='platform']")
    private WebElement campaignPlatformDD;

    @FindBy(xpath = "//input[@placeholder='Enter budget']")
    private WebElement campaignBudget;

    private final By campaignEligibilityLocator = By.xpath("//textarea[@formcontrolname='eligibility']");

    @FindBy(xpath = "//button[@type='submit' or .//span[normalize-space()='Create Campaign' or normalize-space()='Update Campaign']]")
    private WebElement submitBtn;

    @FindBy(xpath = "//button[normalize-space()='Cancel']")
    private WebElement cancelBtn;

    private final By snackBarLocator = By.cssSelector(".mdc-snackbar__label");
    private final By matErrorLocator = By.cssSelector("mat-error");

    // ── Field setters ────────────────────────────────────────────────────────

    public void setCampaignName(String name) {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignName));
        field.clear();
        field.sendKeys(name);
        logger.info("Set campaign name: {}", name);
    }

    /**
     * Blocks until the campaign name field contains a non-empty value.
     * Use after navigating to an edit form where Angular pre-fills existing data asynchronously.
     */
    public void waitForFormToPopulate() {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignName));
        wait.until(d -> !field.getAttribute("value").trim().isEmpty());
        logger.info("Edit form populated. Campaign name: '{}'", field.getAttribute("value"));
    }

    public void setCampaignDescription(String description) {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignDescription));
        field.clear();
        field.sendKeys(description);
        logger.info("Set campaign description");
    }

    /**
     * Types a date into a datepicker input, then waits for Angular's two-way binding
     * to reflect the value back — replacing the fragile Thread.sleep that was here before.
     */
    public void setCampaignStartDate(String date) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(campaignStartDateLocator));
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        field.sendKeys(date);
        field.sendKeys(Keys.TAB);
        wait.until(d -> !field.getAttribute("value").isEmpty());
        logger.info("Set start date: {}", date);
    }

    public void setCampaignEndDate(String date) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(campaignEndDateLocator));
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        field.sendKeys(date);
        field.sendKeys(Keys.TAB);
        wait.until(d -> !field.getAttribute("value").isEmpty());
        logger.info("Set end date: {}", date);
    }

    public void setCampaignPlatform(String platform) {
        // Click the mat-select directly — no need to hunt for an inner trigger sub-element.
        wait.until(ExpectedConditions.elementToBeClickable(campaignPlatformDD)).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option[normalize-space()='" + platform + "']"))).click();
        logger.info("Selected platform: {}", platform);
    }

    public void setCampaignBudget(String budget) {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignBudget));
        field.clear();
        field.sendKeys(budget);
        logger.info("Set budget: {}", budget);
    }

    public void setCampaignEligibilityReq(String req) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(campaignEligibilityLocator));
        field.clear();
        field.sendKeys(req);
        logger.info("Set eligibility requirements");
    }

    // ── Submit / Cancel ──────────────────────────────────────────────────────

    /**
     * Attempts to click the submit button, waiting up to 2 seconds for it to become enabled.
     * If the button remains disabled (e.g. form validation errors), it skips the click cleanly.
     */
    public void clickCreateCampaignSubmit() {
        clickSubmitOrSkipIfDisabled(submitBtn, "Create Campaign submit button");
    }

    /**
     * Returns {@code true} if the submit button is currently visible AND enabled.
     * Performs an immediate state check — does NOT wait for it to become enabled.
     */
    public boolean isSubmitEnabled() {
        try {
            return submitBtn.isDisplayed() && submitBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickCancelBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelBtn));
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            logger.warn("Click intercepted on Cancel — falling back to JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        logger.info("Clicked Cancel button");
    }

    // ── Assertion helpers ────────────────────────────────────────────────────

    public String getSnackBarMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(snackBarLocator))
                       .getText().trim();
        } catch (Exception e) {
            logger.error("Snack-bar did not appear within timeout.");
            return "";
        }
    }

    /**
     * Returns all currently visible Angular Material inline validation errors,
     * joined by " | ".  Returns an empty string if none are present.
     */
    public String getFormValidationErrors() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(matErrorLocator));
            List<WebElement> errors = driver.findElements(matErrorLocator);
            return errors.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.joining(" | "));
        } catch (Exception e) {
            logger.error("No mat-error elements found within timeout.");
            return "";
        }
    }

    // ── Composite helper ─────────────────────────────────────────────────────

    /**
     * Fills every field in the campaign form and submits.
     * {@code eligibility} may be {@code null} or blank — it will be skipped.
     *
     * <p>Internally calls {@link #clickCreateCampaignSubmit()} which waits for the button
     * to become enabled before clicking — safe for positive test scenarios.
     */
    public void fillAndSubmitCampaign(String name, String description, String startDate,
                                      String endDate, String platform, String budget,
                                      String eligibility) {
        setCampaignName(name);
        setCampaignDescription(description);
        setCampaignStartDate(startDate);
        setCampaignEndDate(endDate);
        setCampaignPlatform(platform);
        setCampaignBudget(budget);
        if (eligibility != null && !eligibility.isBlank()) {
            setCampaignEligibilityReq(eligibility);
        }
        clickCreateCampaignSubmit();
    }
}