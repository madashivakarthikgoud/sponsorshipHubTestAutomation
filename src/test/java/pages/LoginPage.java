package pages;

import base.BasePage;
import modules.userManagement.TestScenario01;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver){
        super(driver);
    }

    private static final Logger logger = LogManager.getLogger(TestScenario01.class);

    @FindBy(xpath = "//a[@routerlink='/signup' and text()='Sign up']")
    WebElement signupBtn;

    @FindBy(xpath = "//button//span[text()='Sign In']")
    WebElement loginbtn;

    By userEmailInput = By.xpath("//input[@type='email' or @placeholder='name@gmail.com']");
    By userPasswordInput = By.xpath("//input[@placeholder='Enter your password' and @type='password']");

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

    public void setUserEmail(String email){
        WebElement mail = wait.until(ExpectedConditions.visibilityOfElementLocated(userEmailInput));
        mail.clear();
        mail.sendKeys(email);
        mail.sendKeys(Keys.TAB);
    }

    public void setUserPasswordInput(String password){
        WebElement pass = wait.until(ExpectedConditions.visibilityOfElementLocated(userPasswordInput));
        pass.clear();
        pass.sendKeys(password);
        pass.sendKeys(Keys.TAB);
    }

    public void clickLoginBtn(){
        WebElement btn = wait.until(ExpectedConditions.visibilityOf(loginbtn));

        if (btn.isEnabled() && btn.getAttribute("disabled") == null) {
            try {
                btn.click();
            } catch (Exception e) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", btn);
            }
        } else {
            logger.info("Login button is disabled due to invalid form data. Skipping click to immediately read form errors.");
        }
    }

    public void clickSignupBtn(){
        wait.until(ExpectedConditions.elementToBeClickable(signupBtn)).click();
    }
}