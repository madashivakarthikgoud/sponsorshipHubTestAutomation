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

import java.util.List;
import java.util.stream.Collectors;

public class NewCampaignsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(NewCampaignsPage.class);

    public NewCampaignsPage(WebDriver driver) {
        super(driver);
    }

    // ─── Form Fields ───────────────────────────────────────────────────────────

    @FindBy(xpath = "//input[@placeholder='Enter campaign name']")
    WebElement campaignName;

    @FindBy(xpath = "//textarea[@placeholder='Describe your campaign']")
    WebElement campaignDescription;

    /**
     * Stable XPath for startDate — uses formcontrolname attribute which is
     * part of the Angular reactive form definition and does NOT change on reload.
     * Falls back to a position-based input selector inside the start-date form field.
     */
    private final By campaignStartDateLocator = By.xpath(
        "//mat-form-field[.//label[contains(., 'Start Date')] or .//mat-label[contains(., 'Start Date')]]//input"
    );

    /**
     * Stable XPath for endDate — same stable formcontrolname strategy.
     */
    private final By campaignEndDateLocator = By.xpath(
        "//mat-form-field[.//label[contains(., 'End Date')] or .//mat-label[contains(., 'End Date')]]//input"
    );

    @FindBy(xpath = "//mat-select[@formcontrolname='platform']")
    WebElement campaignPlatformDD;

    @FindBy(xpath = "//input[@placeholder='Enter budget']")
    WebElement campaignBudget;

    /**
     * Stable XPath for eligibility textarea — uses formcontrolname attribute.
     */
    private final By campaignEligibilityLocator = By.xpath(
        "//textarea[@formcontrolname='eligibility']"
    );

    // ─── Buttons ───────────────────────────────────────────────────────────────

    @FindBy(xpath = "//button[@type='submit' or .//span[normalize-space()='Create Campaign' or normalize-space()='Update Campaign']]")
    WebElement createCampaignSubmitBtn;

    @FindBy(xpath = "//button[normalize-space()='Cancel']")
    WebElement campaignCancelBtn;

    // ─── Snack-bar ─────────────────────────────────────────────────────────────

    private final By snackBarLocator = By.cssSelector(".mdc-snackbar__label");

    // ─── Form Validation Error ─────────────────────────────────────────────────

    private final By matErrorLocator = By.cssSelector("mat-error");

    // ─── Public API ────────────────────────────────────────────────────────────

    public void setCampaignName(String name) {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignName));
        field.clear();
        field.sendKeys(name);
        logger.info("Set campaign name: {}", name);
    }

    public void waitForFormToPopulate() {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignName));
        wait.until(driver -> !field.getAttribute("value").trim().isEmpty());
        logger.info("Form has finished loading. Campaign name field populated with: {}", field.getAttribute("value"));
    }

    public void setCampaignDescription(String description) {
        WebElement field = wait.until(ExpectedConditions.visibilityOf(campaignDescription));
        field.clear();
        field.sendKeys(description);
        logger.info("Set campaign description");
    }

    public void setCampaignStartDate(String date) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(campaignStartDateLocator));
        field.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        field.sendKeys(date);
        field.sendKeys(org.openqa.selenium.Keys.TAB);
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("Set start date: {}", date);
    }

    public void setCampaignEndDate(String date) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(campaignEndDateLocator));
        field.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        field.sendKeys(date);
        field.sendKeys(org.openqa.selenium.Keys.TAB);
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("Set end date: {}", date);
    }

    public void setCampaignPlatform(String platform) {
        WebElement dd = wait.until(ExpectedConditions.visibilityOf(campaignPlatformDD));
        WebElement trigger = dd.findElement(By.xpath(".//div[contains(@class,'select-trigger') or contains(@class,'select-arrow-wrapper')]"));
        wait.until(ExpectedConditions.elementToBeClickable(trigger)).click();
        String optionXpath = String.format("//mat-option[normalize-space()='%s']", platform);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXpath))).click();
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

    public void clickCreateCampaignSubmit() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(createCampaignSubmitBtn));
        if (btn.isEnabled() && btn.getAttribute("disabled") == null) {
            try {
                btn.click();
            } catch (Exception e) {
                logger.warn("Normal click intercepted on submit, using JS click.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            }
            logger.info("Clicked Create Campaign submit button");
        } else {
            logger.info("Create Campaign submit button is disabled due to invalid form data. Skipping click.");
        }
    }

    public void clickCancelBtn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(campaignCancelBtn));
        try {
            btn.click();
        } catch (Exception e) {
            logger.warn("Normal click intercepted on cancel, using JS click.");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        logger.info("Clicked Cancel button");
    }

    /**
     * Returns the text from the Material snack-bar popup.
     * Used to verify success/failure messages after form submission.
     */
    public String getSnackBarMessage() {
        try {
            WebElement snackBar = wait.until(ExpectedConditions.visibilityOfElementLocated(snackBarLocator));
            return snackBar.getText().trim();
        } catch (Exception e) {
            logger.error("Snack-bar did not appear within timeout.");
            return "";
        }
    }

    /**
     * Returns all visible mat-error validation messages joined by " | ".
     * Used for negative test cases where form submission is blocked.
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

    /**
     * Convenience method: fills all required fields and submits the campaign form.
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
        if (eligibility != null && !eligibility.isEmpty()) {
            setCampaignEligibilityReq(eligibility);
        }
        clickCreateCampaignSubmit();
    }
}