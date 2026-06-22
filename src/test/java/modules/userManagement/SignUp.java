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

public class SignUp extends BaseTest {
    private static final Logger logger = LogManager.getLogger(SignUp.class);
    @Test(description = "SIGNUP: Verify user signup functionality")
    public void signUp(){
        logger.info("===== Starting signup verification =====");
        SignUpPage signUpPage = new SignUpPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        logger.info("clicking signup button");
        loginPage.clickSignupBtn();
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
}
