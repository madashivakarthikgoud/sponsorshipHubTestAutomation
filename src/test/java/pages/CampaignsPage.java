package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CampaignsPage extends BasePage {
    public CampaignsPage(WebDriver driver){
        super(driver);
    }

    @FindBy(xpath = "//button//span[normalize-space()='Create Campaign']")
    WebElement createCampaignBtn;



    public void clickCreateCampaignBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(createCampaignBtn)).click();
    }

}
