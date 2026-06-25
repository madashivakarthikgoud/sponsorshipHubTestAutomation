package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class CampaignsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(CampaignsPage.class);

    public CampaignsPage(WebDriver driver) {
        super(driver);
    }

    // ─── Toolbar / Header Actions ───────────────────────────────────────────────

    /** "Create Campaign" button visible to Brand users on the list page */
    @FindBy(xpath = "//button//span[normalize-space()='Create Campaign']")
    WebElement createCampaignBtn;

    // ─── Search & Filter Controls ───────────────────────────────────────────────

    /** Name search input (Angular Material input inside the filter bar) */
    private final By searchInputLocator = By.xpath(
        "//input[@placeholder='Search campaigns' or @placeholder='Search by name']"
    );

    /** Platform filter dropdown (mat-select) */
    @FindBy(xpath = "//mat-select[@formcontrolname='filterPlatform' or contains(@class,'platform')]")
    WebElement platformFilterDD;

    /** Status filter dropdown (mat-select) */
    @FindBy(xpath = "//mat-select[@formcontrolname='filterStatus' or contains(@class,'status')]")
    WebElement statusFilterDD;

    // ─── Campaign Card / Row Locators ───────────────────────────────────────────

    /** All campaign card containers rendered in the list */
    private final By campaignCardLocator = By.cssSelector(
        "mat-card, .campaign-card, app-campaign-list mat-list-item"
    );

    /** Edit button for a campaign — takes campaign name as parameter */
    private final String editBtnXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]//button[contains(@class,'edit') or .//mat-icon[text()='edit']]";

    /** Delete button for a campaign — takes campaign name as parameter */
    private final String deleteBtnXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]//button[contains(@class,'delete') or .//mat-icon[text()='delete']]";

    /** View / title click for a campaign — takes campaign name as parameter */
    private final String viewCardXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]";

    /** First campaign card title on the list */
    @FindBy(xpath = "(//mat-card//mat-card-title)[1]")
    WebElement firstCampaignTitle;

    // ─── Page State ─────────────────────────────────────────────────────────────

    /** Loading spinner */
    @FindBy(css = "mat-progress-spinner, .loading-spinner")
    WebElement loadingSpinner;

    // ─── Public API ─────────────────────────────────────────────────────────────

    /**
     * Clicks the "Create Campaign" button to navigate to the campaign form.
     */
    public void clickCreateCampaignBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(createCampaignBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Clicked Create Campaign button");
    }

    /**
     * Waits for the campaign list to finish loading (spinner disappears or cards appear).
     */
    public void waitForListToLoad() {
        // Wait until at least one mat-card or the empty-state message is visible
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(campaignCardLocator),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".empty-state, .no-campaigns"))
        ));
        logger.info("Campaign list has loaded");
    }

    /**
     * Returns the count of campaign cards currently visible on the page.
     */
    public int getCampaignCount() {
        List<WebElement> cards = driver.findElements(campaignCardLocator);
        int count = cards.size();
        logger.info("Campaign count on list page: {}", count);
        return count;
    }

    /**
     * Returns the text of the first campaign's title card.
     * Used to capture a campaign name for subsequent edit/delete operations.
     */
    public String getFirstCampaignTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOf(firstCampaignTitle));
        String text = title.getText().trim();
        logger.info("First campaign title: {}", text);
        return text;
    }

    /**
     * Types into the search input to filter campaigns by name.
     */
    public void searchByName(String name) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
        searchInput.clear();
        searchInput.sendKeys(name);
        searchInput.sendKeys(Keys.TAB); // trigger Angular change detection
        logger.info("Searched for campaign: {}", name);
    }

    /**
     * Clears the search input.
     */
    public void clearSearch() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
        searchInput.clear();
        searchInput.sendKeys(Keys.TAB);
        logger.info("Cleared search input");
    }

    /**
     * Selects a platform from the platform filter dropdown.
     * @param platform e.g. "Instagram", "YouTube"
     */
    public void filterByPlatform(String platform) {
        wait.until(ExpectedConditions.elementToBeClickable(platformFilterDD)).click();
        String optionXpath = String.format("//mat-option[normalize-space()='%s']", platform);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXpath))).click();
        logger.info("Filtered by platform: {}", platform);
    }

    /**
     * Clicks the Edit icon button on the campaign card matching the given name.
     */
    public void clickEditCampaign(String campaignName) {
        String xpath = String.format(editBtnXpathTemplate, campaignName);
        WebElement editBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
        logger.info("Clicked Edit for campaign: {}", campaignName);
    }

    /**
     * Clicks the Delete icon button on the campaign card matching the given name.
     */
    public void clickDeleteCampaign(String campaignName) {
        String xpath = String.format(deleteBtnXpathTemplate, campaignName);
        WebElement deleteBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
        logger.info("Clicked Delete for campaign: {}", campaignName);
    }

    /**
     * Clicks the campaign card itself to navigate to the detail view.
     */
    public void clickViewCampaign(String campaignName) {
        String xpath = String.format(viewCardXpathTemplate, campaignName);
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
        logger.info("Clicked campaign card to view detail: {}", campaignName);
    }

    /**
     * Checks whether a campaign with the given name is visible in the list.
     */
    public boolean isCampaignVisible(String campaignName) {
        try {
            String xpath = String.format(viewCardXpathTemplate, campaignName);
            java.util.List<WebElement> cards = driver.findElements(By.xpath(xpath));
            return !cards.isEmpty() && cards.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
