package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.By;

public class SignUpPage extends BasePage {
    public SignUpPage(WebDriver driver){
        super(driver);
    }

    @FindBy(xpath="//input[@id='mat-input-2' or @placeholder='Enter a unique username']") WebElement userNameInput;

    @FindBy(xpath="//input[@id='mat-input-3' or @type='email']") WebElement userEmailInput;

    @FindBy(xpath="//input[@placeholder='Create a password']") WebElement userPasswordInput;

    @FindBy(id="mat-select-0") WebElement roleSelectInput;

    @FindBy(xpath="//button[normalize-space()='Create Account']") WebElement createAccountBtn;

    public void setUserName(String username){
        wait.until(ExpectedConditions.visibilityOf(userNameInput));
        userNameInput.sendKeys(username);
    }

    public void setUserEmail(String email){
        wait.until(ExpectedConditions.visibilityOf(userEmailInput));
        userEmailInput.sendKeys(email);
    }

    public void setUserPasswordInput(String password){
        wait.until(ExpectedConditions.visibilityOf(userPasswordInput));
        userPasswordInput.sendKeys(password);
    }

    public void setRoleSelectInput(String role){
        wait.until(ExpectedConditions.elementToBeClickable(roleSelectInput)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option//span[normalize-space()='"+role+"']"))).click();
    }

    public void clickCreateAccountBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(createAccountBtn)).click();
    }
}
