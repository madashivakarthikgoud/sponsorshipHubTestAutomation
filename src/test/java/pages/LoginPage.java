package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage{
    public LoginPage(WebDriver driver){
        super(driver);
    }
    @FindBy(xpath = "//a[@routerlink='/signup' and text()='Sign up']")
    WebElement signupBtn;

    @FindBy(xpath = "//button//span[text()='Sign In']")
    WebElement loginbtn;

    By userEmailInput = By.xpath("//input[@type='email' or @placeholder='name@gmail.com']");
    By userPasswordInput = By.xpath("//input[@placeholder='Enter your password' and @type='password']");


    public void setUserEmail(String email){
        wait.until(ExpectedConditions.visibilityOfElementLocated(userEmailInput)).sendKeys(email);
    }

    public void setUserPasswordInput(String password){
        wait.until(ExpectedConditions.visibilityOfElementLocated(userPasswordInput)).sendKeys(password);
    }

    public void clickLoginBtn(){
        loginbtn.click();
    }
    public void clickSignupBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(signupBtn)).click();
    }
}
