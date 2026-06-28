package pages;

import base.BasePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

public class SignUpPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(SignUpPage.class);

    public SignUpPage(WebDriver driver, Wait<WebDriver> wait) {
        super(driver, wait);
    }

    @FindBy(xpath = "//input[@placeholder='Enter a unique username']")
    private WebElement userNameInput;

    @FindBy(xpath = "//input[@type='email']")
    private WebElement userEmailInput;

    @FindBy(xpath = "//input[@placeholder='Create a password']")
    private WebElement userPasswordInput;

    @FindBy(tagName = "mat-select")
    private WebElement roleSelectInput;

    @FindBy(xpath = "//button[normalize-space()='Create Account']")
    private WebElement createAccountBtn;

    public void setUserName(String username) {
        wait.until(ExpectedConditions.visibilityOf(userNameInput));
        userNameInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        userNameInput.sendKeys(username);
        userNameInput.sendKeys(Keys.TAB);
    }

    public void setUserEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(userEmailInput));
        userEmailInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        userEmailInput.sendKeys(email);
        userEmailInput.sendKeys(Keys.TAB);
    }

    public void setUserPasswordInput(String password) {
        wait.until(ExpectedConditions.visibilityOf(userPasswordInput));
        userPasswordInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        userPasswordInput.sendKeys(password);
        userPasswordInput.sendKeys(Keys.TAB);
    }

    public void setRoleSelectInput(String role) {
        wait.until(ExpectedConditions.elementToBeClickable(roleSelectInput)).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option//span[normalize-space()='" + role + "']"))).click();
    }

    /**
     * Attempts to click the Create Account button, waiting up to 2 seconds for it to become enabled.
     * If the button remains disabled (e.g. form validation errors), it skips the click cleanly.
     */
    public void clickCreateAccountBtn() {
        clickSubmitOrSkipIfDisabled(createAccountBtn, "Create Account button");
    }

    /**
     * Returns {@code true} if the Create Account button is currently visible AND enabled.
     * Performs an immediate state check — does NOT wait for it to become enabled.
     */
    public boolean isCreateAccountEnabled() {
        try {
            return createAccountBtn.isDisplayed() && createAccountBtn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // getErrorMessage() is inherited from BasePage.
}