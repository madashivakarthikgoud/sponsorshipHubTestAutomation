package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

public class LoginPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    public LoginPage(WebDriver driver, Wait<WebDriver> wait) {
        super(driver, wait);
    }

    @FindBy(xpath = "//a[@routerlink='/signup' and text()='Sign up']")
    private WebElement signupBtn;

    @FindBy(xpath = "//button//span[text()='Sign In']")
    private WebElement loginBtn;

    private final By userEmailInput    = By.xpath("//input[@type='email' or @placeholder='name@gmail.com']");
    private final By userPasswordInput = By.xpath("//input[@placeholder='Enter your password' and @type='password']");

    public void setUserEmail(String email) {
        WebElement mail = wait.until(ExpectedConditions.visibilityOfElementLocated(userEmailInput));
        mail.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        mail.sendKeys(email);
        mail.sendKeys(Keys.TAB);
    }

    public void setUserPasswordInput(String password) {
        WebElement pass = wait.until(ExpectedConditions.visibilityOfElementLocated(userPasswordInput));
        pass.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        pass.sendKeys(password);
        pass.sendKeys(Keys.TAB);
    }

    /**
     * Attempts to click the Sign In button, waiting up to 2 seconds for it to become enabled.
     * If the button remains disabled (e.g. form validation errors), it skips the click cleanly.
     */
    public void clickLoginBtn() {
        clickSubmitOrSkipIfDisabled(loginBtn, "Login button");
    }

    /**
     * Returns {@code true} if the Sign In button is currently visible AND enabled.
     * Performs an immediate state check — does NOT wait for it to become enabled.
     */
    public boolean isLoginEnabled() {
        try {
            return loginBtn.isDisplayed() && loginBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickSignupBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(signupBtn)).click();
    }

    // getErrorMessage() is inherited from BasePage.
}