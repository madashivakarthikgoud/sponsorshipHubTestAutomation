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
import org.openqa.selenium.support.ui.Wait;

import java.util.List;

public class CampaignsPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(CampaignsPage.class);

    public CampaignsPage(WebDriver driver, Wait<WebDriver> wait) {
        super(driver, wait);
    }

    @FindBy(xpath = "//button//span[normalize-space()='Create Campaign']")
    private WebElement createCampaignBtn;

    private final By searchInputLocator = By.xpath(
        "//input[@placeholder='Search campaigns' or @placeholder='Search by name']"
    );

    @FindBy(xpath = "//mat-select[@formcontrolname='filterPlatform' or contains(@class,'platform')]")
    private WebElement platformFilterDD;

    @FindBy(xpath = "//mat-select[@formcontrolname='filterStatus' or contains(@class,'status')]")
    private WebElement statusFilterDD;

    private final By campaignCardLocator = By.cssSelector(
        "mat-card, .campaign-card, app-campaign-list mat-list-item"
    );

    private final String editBtnXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]//button[contains(@class,'edit') or .//mat-icon[text()='edit']]";

    private final String deleteBtnXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]//button[contains(@class,'delete') or .//mat-icon[text()='delete']]";

    private final String viewCardXpathTemplate =
        "//mat-card[.//mat-card-title[contains(normalize-space(),'%s')]]";

    @FindBy(xpath = "(//mat-card//mat-card-title)[1]")
    private WebElement firstCampaignTitle;

    // ── Actions ─────────────────────────────────────────────────────────────

    public void clickCreateCampaignBtn() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(createCampaignBtn));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        logger.info("Clicked Create Campaign button");
    }

    public void waitForListToLoad() {
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(campaignCardLocator),
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".empty-state, .no-campaigns"))
        ));
        logger.info("Campaign list has loaded");
    }

    public int getCampaignCount() {
        List<WebElement> cards = driver.findElements(campaignCardLocator);
        int count = cards.size();
        logger.info("Campaign count on list page: {}", count);
        return count;
    }

    public String getFirstCampaignTitle() {
        String text = wait.until(ExpectedConditions.visibilityOf(firstCampaignTitle)).getText().trim();
        logger.info("First campaign title: {}", text);
        return text;
    }

    public void searchByName(String name) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
        searchInput.clear();
        searchInput.sendKeys(name);
        searchInput.sendKeys(Keys.TAB);
        logger.info("Searched for campaign: {}", name);
    }

    public void clearSearch() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
        searchInput.clear();
        searchInput.sendKeys(Keys.TAB);
        logger.info("Cleared search input");
    }

    public void filterByPlatform(String platform) {
        wait.until(ExpectedConditions.elementToBeClickable(platformFilterDD)).click();
        String optionXpath = String.format("//mat-option[normalize-space()='%s']", platform);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXpath))).click();
        logger.info("Filtered by platform: {}", platform);
    }

    public void clickEditCampaign(String campaignName) {
        String xpath = String.format(editBtnXpathTemplate, campaignName);
        WebElement editBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
        logger.info("Clicked Edit for campaign: {}", campaignName);
    }

    public void clickDeleteCampaign(String campaignName) {
        String xpath = String.format(deleteBtnXpathTemplate, campaignName);
        WebElement deleteBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn);
        logger.info("Clicked Delete for campaign: {}", campaignName);
    }

    public void clickViewCampaign(String campaignName) {
        String xpath = String.format(viewCardXpathTemplate, campaignName);
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
        logger.info("Clicked campaign card to view detail: {}", campaignName);
    }

    public boolean isCampaignVisible(String campaignName) {
        try {
            String xpath = String.format(viewCardXpathTemplate, campaignName);
            List<WebElement> cards = driver.findElements(By.xpath(xpath));
            return !cards.isEmpty() && cards.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Blocks until the named campaign card is visible on screen.
     * Replaces manual Thread.sleep polling loops in tests.
     */
    public void waitForCampaignVisible(String campaignName) {
        String xpath = String.format(viewCardXpathTemplate, campaignName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        logger.info("Campaign '{}' is now visible in the list", campaignName);
    }

    /**
     * Blocks until the named campaign card is no longer present/visible.
     * Replaces manual Thread.sleep polling loops in deletion tests.
     */
    public void waitForCampaignToDisappear(String campaignName) {
        String xpath = String.format(viewCardXpathTemplate, campaignName);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
        logger.info("Campaign '{}' has been removed from the list", campaignName);
    }
}
