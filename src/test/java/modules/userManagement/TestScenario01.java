package modules.userManagement;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import pages.SignUpPage;

public class TestScenario01 extends BaseTest {
    private static final Logger logger = LogManager.getLogger(TestScenario01.class);


    @Test(description = "TestCase 01 : Verify user signup functionality")
    public void signUp(){
        logger.info("===== Starting signup verification =====");
        SignUpPage signUpPage = new SignUpPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        logger.info("clicking signup button");
        loginPage.clickSignupBtn();
        wait.until(ExpectedConditions.urlContains("signup"));
        logger.info("set user name");
        signUpPage.setUserName(randomUserName());
        logger.info("set user email");
        signUpPage.setUserEmail(randomUserEmail());
        logger.info("set user password");
        signUpPage.setUserPasswordInput(randomUserPassword());
        logger.info("set user role");
        signUpPage.setRoleSelectInput(randomUserRole());
        logger.info("click create account button");
        signUpPage.clickCreateAccountBtn();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        String dashboard = driver.getCurrentUrl();
        Assert.assertTrue(dashboard.contains("dashboard"),"user navigated to wrong URL, actual URL: "+dashboard);
        String message=wait.until(ExpectedConditions.visibilityOf(dashboardPage.getRegistrationSuccessfulPopUp())).getText().trim();
        Assert.assertTrue(message.contains("Registration successful!"),"expected login success popup, but got: " + message);
        logger.info("popup message: "+message);
        logger.info("clicking menu button");
        dashboardPage.clickMenuBtn();
        logger.info("clicking logout button");
        dashboardPage.clickLogout();
        String loginUrl=driver.getCurrentUrl();
        Assert.assertTrue(loginUrl.contains("login"),"expected login page on logout but actual page is "+loginUrl);
        logger.info("URL after logout: "+loginUrl);
        logger.info("===== Signup test completed successfully =====");
    }




    @Test(description="LOGIN: Verify user login functionality")
    public void login(){
        String email = "Test1@gmail.com";
        String password = "Test@1";
        logger.info("===== Starting login verification =====");
        wait.until(ExpectedConditions.urlContains("login"));
        DashboardPage dashboardPage= new DashboardPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        logger.info("Entering email");
        loginPage.setUserEmail(email);
        logger.info("Entering password");
        loginPage.setUserPasswordInput(password);
        logger.info("Clicking login button");
        loginPage.clickLoginBtn();
        logger.info("Waiting for dashboard URL");
        wait.until(ExpectedConditions.urlContains("dashboard"));
        String currentUrl = driver.getCurrentUrl();
        logger.info("Current URL: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("dashboard"),"User didn't navigate to dashboard. Actual URL: " + currentUrl);
        logger.info("Validating login success popup");
        String message = wait.until(ExpectedConditions.visibilityOf(dashboardPage.getLoginSuccessfulPopUp())).getText().trim();
        logger.info("Popup message: " + message);
        Assert.assertTrue(message.contains("Login successful"),"Expected login success popup, but got: " + message);
        logger.info("Clicking menu button");
        dashboardPage.clickMenuBtn();
        logger.info("Clicking logout button");
        dashboardPage.clickLogout();
        String logoutUrl = driver.getCurrentUrl();
        logger.info("URL after logout: " + logoutUrl);
        Assert.assertTrue(logoutUrl.contains("login"),"Logout failed. Current URL: " + logoutUrl);
        logger.info("===== Login test completed successfully =====");
    }
}
