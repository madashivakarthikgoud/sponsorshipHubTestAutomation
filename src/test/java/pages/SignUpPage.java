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

public class SignUpPage extends BasePage {
    public SignUpPage(WebDriver driver){
        super(driver);
    }
    private static final Logger logger = LogManager.getLogger(SignUpPage.class);

    @FindBy(xpath="//input[@placeholder='Enter a unique username']")
    WebElement userNameInput;

    @FindBy(xpath="//input[@type='email']")
    WebElement userEmailInput;

    @FindBy(xpath="//input[@placeholder='Create a password']")
    WebElement userPasswordInput;

    @FindBy(tagName="mat-select")
    WebElement roleSelectInput;

    @FindBy(xpath="//button[normalize-space()='Create Account']")
    WebElement createAccountBtn;

    public String getErrorMessage() {
        try {
            By errorLocator = By.cssSelector("mat-error, .mdc-snackbar__label");
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            List<WebElement> errorElements = driver.findElements(errorLocator);

            return errorElements.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .map(String::trim)
                    .filter(text -> !text.isEmpty())
                    .collect(Collectors.joining(" | "));
        } catch (Exception e) {
            logger.error("No error messages appeared within the timeout period.");
            return "";
        }
    }

    public void setUserName(String username){
        wait.until(ExpectedConditions.visibilityOf(userNameInput));
        userNameInput.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        userNameInput.sendKeys(username);
        userNameInput.sendKeys(org.openqa.selenium.Keys.TAB);
    }

    public void setUserEmail(String email){
        wait.until(ExpectedConditions.visibilityOf(userEmailInput));
        userEmailInput.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        userEmailInput.sendKeys(email);
        userEmailInput.sendKeys(org.openqa.selenium.Keys.TAB);
    }

    public void setUserPasswordInput(String password){
        wait.until(ExpectedConditions.visibilityOf(userPasswordInput));
        userPasswordInput.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        userPasswordInput.sendKeys(password);
        userPasswordInput.sendKeys(org.openqa.selenium.Keys.TAB);
    }

    public void setRoleSelectInput(String role){
        wait.until(ExpectedConditions.elementToBeClickable(roleSelectInput)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//mat-option//span[normalize-space()='"+role+"']"))).click();
    }

    public void clickCreateAccountBtn(){
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(createAccountBtn));
        if (btn.isEnabled() && btn.getAttribute("disabled") == null) {
            try {
                btn.click();
            } catch (Exception e) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", btn);
            }
        } else {
            logger.info("Create Account button is disabled. Skipping click to immediately read form errors.");
        }
    }
}