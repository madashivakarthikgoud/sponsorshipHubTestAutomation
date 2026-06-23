package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(DashboardPage.class);

    public DashboardPage(WebDriver driver){
        super(driver);
    }

    @FindBy(xpath="//mat-snack-bar-container//div[normalize-space()='Registration successful!']")
    WebElement registrationSuccessfulPopUp;

    @FindBy(xpath="//mat-snack-bar-container//div[normalize-space()='Login successful!']")
    WebElement loginSuccessfulPopUp;

    @FindBy(xpath="//button[contains(@class,'user-menu-btn')]")
    WebElement menuBtn;

    @FindBy(xpath="//button//span[contains(text(),'Logout')]")
    WebElement logoutBtn;

    @FindBy(xpath = "//button[@routerlink='/campaigns']")
    WebElement campaignsBtn;

    public void clickCampaignsBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(campaignsBtn)).click();
    }


    public void clickMenuBtn(){
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(menuBtn));
        wait.until(ExpectedConditions.elementToBeClickable(btn));

        try {
            btn.click();
        } catch (Exception e) {
            logger.info("Normal menu click intercepted, using JS click.");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", btn);
        }
    }

    public void clickLogout(){
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(logoutBtn));
        wait.until(ExpectedConditions.elementToBeClickable(btn));

        try {
            btn.click();
        } catch (Exception e) {
            logger.info("Normal logout click intercepted, using JS click.");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", btn);
        }
    }

    public WebElement getRegistrationSuccessfulPopUp(){
        return registrationSuccessfulPopUp;
    }

    public WebElement getLoginSuccessfulPopUp(){
        return loginSuccessfulPopUp;
    }
}