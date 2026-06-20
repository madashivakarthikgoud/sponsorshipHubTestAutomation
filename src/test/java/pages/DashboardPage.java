package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends BasePage {
    public DashboardPage(WebDriver drive){
        super(drive);
    }
    @FindBy(xpath="//mat-snack-bar-container//div[normalize-space()='Registration successful!']")
    WebElement registrationSuccessfulPopUp;

    public WebElement registrationSuccessfulPopUpText(){
        return registrationSuccessfulPopUp;
    }
}
