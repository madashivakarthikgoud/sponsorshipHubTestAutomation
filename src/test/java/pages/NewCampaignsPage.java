package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NewCampaignsPage extends BasePage {
    public NewCampaignsPage(WebDriver driver){
        super(driver);
    }

    @FindBy(xpath = "//input[@placeholder='Enter campaign name']")
    WebElement campaignName;

    @FindBy(xpath = "//textarea[@placeholder='Describe your campaign']")
    WebElement campaignDescription;

    // Note: Angular 'mat-input' IDs are often dynamic and can change on reload.
    // Consider using formcontrolname or placeholders instead if these become flaky.
    @FindBy(xpath = "//input[@id='mat-input-37']")
    WebElement campaignStartDate;

    // Fixed missing '@' in the xpath
    @FindBy(xpath = "//input[@id='mat-input-52']")
    WebElement campaignEndDate;

    @FindBy(xpath = "//mat-select[@formcontrolname='platform']")
    WebElement campaignPlatformDD;

    @FindBy(xpath = "//input[@placeholder='Enter budget']")
    WebElement campaignBudget;

    @FindBy(xpath = "//textarea[@id='mat-input-39']")
    WebElement campaignEligibilityReq;

    @FindBy(xpath = "//button/span[normalize-space()='Create Campaign']")
    WebElement createCampaignBtn;

    @FindBy(xpath = "//button[normalize-space()='Cancel']")
    WebElement campaignCancel;

    public void setCampaignName(String name){
        WebElement campName = wait.until(ExpectedConditions.visibilityOf(campaignName));
        campName.clear();
        campName.sendKeys(name);
    }

    public void setCampaignDescription(String description){
        // Fixed: was pointing to campaignName instead of campaignDescription
        WebElement campDescription = wait.until(ExpectedConditions.visibilityOf(campaignDescription));
        campDescription.clear();
        campDescription.sendKeys(description);
    }

    public void setCampaignPlatform(String platform) {
        WebElement campPlatform = wait.until(ExpectedConditions.elementToBeClickable(campaignPlatformDD));
        campPlatform.click();

        String dynamicXpath = String.format("//mat-option[normalize-space()='%s']", platform);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath))).click();
    }

    public void setCampaignBudget(String budget){
        // Implemented the empty method
        WebElement campBudget = wait.until(ExpectedConditions.visibilityOf(campaignBudget));
        campBudget.clear();
        campBudget.sendKeys(budget);
    }

    public void setCampaignStartDate(String date){
        WebElement campStartDate = wait.until(ExpectedConditions.visibilityOf(campaignStartDate));
        campStartDate.clear();
        campStartDate.sendKeys(date);
    }

    public void setCampaignEndDate(String date){
        WebElement campEndDate = wait.until(ExpectedConditions.visibilityOf(campaignEndDate));
        campEndDate.clear();
        campEndDate.sendKeys(date);
    }

    public void setCampaignEligibilityReq(String req){
        WebElement campElig = wait.until(ExpectedConditions.visibilityOf(campaignEligibilityReq));
        campElig.clear();
        campElig.sendKeys(req);
    }

    public void clickCreateCampaignSubmit(){
        wait.until(ExpectedConditions.elementToBeClickable(createCampaignBtn)).click();
    }
}