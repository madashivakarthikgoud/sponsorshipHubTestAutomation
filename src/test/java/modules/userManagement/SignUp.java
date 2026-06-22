package modules.userManagement;

import base.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import pages.SignUpPage;

public class SignUp extends BaseTest {

    @Test(description = "TC001 - Verify user signup functionality")
    public void signUp(){
        SignUpPage signUpPage = new SignUpPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        loginPage.clickSignupBtn();
        signUpPage.setUserName(randomUserName());
        signUpPage.setUserEmail(randomUserEmail());
        signUpPage.setUserPasswordInput(randomUserPassword());
        signUpPage.setRoleSelectInput(randomUserRole());
        signUpPage.clickCreateAccountBtn();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOf(dashboardPage.getRegistrationSuccessfulPopUp())).getText().trim().contains("Registration successful!"));
        dashboardPage.clickMenuBtn();
        dashboardPage.clickLogout();
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
}
