package modules.userManagement;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;

public class Login extends BaseTest {
    private static final Logger logger = LogManager.getLogger(Login.class);
    static String email = "Test1@gmail.com";
    static String password = "Test@1";
    @Test(description="LOGIN: Verify user login functionality")
    public void login(){
        logger.info("===== Starting login verification =====");
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
