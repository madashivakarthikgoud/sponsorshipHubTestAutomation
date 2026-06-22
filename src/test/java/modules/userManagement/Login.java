package modules.userManagement;

import base.BaseTest;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;

public class Login extends BaseTest {
    static String email = "Test1@gmail.com";
    static String password = "Test@1";
    @Test(description = "TC002 - Verify user login functionality")
    public void login(){
        DashboardPage dashboardPage= new DashboardPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setUserEmail(email);
        loginPage.setUserPasswordInput(password);
        loginPage.clickLoginBtn();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOf(dashboardPage.getLoginSuccessfulPopUp())).getText().trim().contains("Login successful!"));
        dashboardPage.clickMenuBtn();
        dashboardPage.clickLogout();
        Assert.assertTrue(driver.getCurrentUrl().contains("login"));
    }
}
