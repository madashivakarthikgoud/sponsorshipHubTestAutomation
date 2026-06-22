package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.awt.*;

public class DashboardPage extends BasePage {
    public DashboardPage(WebDriver drive){
        super(drive);
    }
    @FindBy(xpath="//mat-snack-bar-container//div[normalize-space()='Registration successful!']")
    WebElement registrationSuccessfulPopUp;

    @FindBy(xpath="//mat-snack-bar-container//div[normalize-space()='Login successful!']")
    WebElement loginSuccessfulPopUp;

    @FindBy(xpath="//button[contains(@class,'user-menu-btn')]")
    WebElement menueBtn;

    @FindBy(xpath="//button//span[contains(text(),'Logout')]")
    WebElement logoutBtn;

    public void clickLogout(){
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }

    public void clickMenuBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(menueBtn)).click();
    }
    public WebElement getRegistrationSuccessfulPopUp(){
        return registrationSuccessfulPopUp;
    }
    public WebElement getLoginSuccessfulPopUp(){
        return loginSuccessfulPopUp;
    }
}
