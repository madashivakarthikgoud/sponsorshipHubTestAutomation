package modules.userManagement;

import base.BaseTest;
import dataProviders.TestDataProviders;
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

    @Test(
            priority = 1,
            description = "TestCase 01 : Verify user signup functionality",
            dataProvider = "signUpData",
            dataProviderClass = TestDataProviders.class
    )
    public void signUp(String testCaseID, String userName, String email, String password, String role, String expectedResult, String expectedMessage) {
        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));

        logger.info("===== Starting signup verification for " + testCaseID + " =====");

        String uniqueID = String.valueOf(System.currentTimeMillis());

        if (expectedResult.equalsIgnoreCase("Success")) {
            userName = userName + uniqueID;
            if (email != null && email.contains("@")) {
                email = email.replace("@", "+" + uniqueID + "@");
            }
        } else {
            if (expectedMessage.contains("Email already registered")) {
                userName = "AutoUser" + uniqueID;
            }

            if (expectedMessage.contains("Username already exists")) {
                if (email != null && email.contains("@")) {
                    email = email.replace("@", "+" + uniqueID + "@");
                }
            }
        }

        SignUpPage signUpPage = new SignUpPage(driver);
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        logger.info("clicking signup button");
        loginPage.clickSignupBtn();
        wait.until(ExpectedConditions.urlContains("signup"));

        logger.info("set user name: " + userName);
        signUpPage.setUserName(userName);

        logger.info("set user email: " + email);
        signUpPage.setUserEmail(email);

        logger.info("set user password");
        signUpPage.setUserPasswordInput(password);

        logger.info("set user role: " + role);
        signUpPage.setRoleSelectInput(role);

        logger.info("click create account button");
        signUpPage.clickCreateAccountBtn();

        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            String dashboard = driver.getCurrentUrl();
            Assert.assertTrue(dashboard.contains("dashboard"), "user navigated to wrong URL, actual URL: " + dashboard);

            String message = wait.until(ExpectedConditions.visibilityOf(dashboardPage.getRegistrationSuccessfulPopUp())).getText().trim();
            Assert.assertTrue(message.contains(expectedMessage), "expected login success popup, but got: " + message);
            logger.info("popup message: " + message);

            logger.info("clicking menu button");
            dashboardPage.clickMenuBtn();

            logger.info("clicking logout button");
            dashboardPage.clickLogout();

            String loginUrl = driver.getCurrentUrl();
            Assert.assertTrue(loginUrl.contains("login"), "expected login page on logout but actual page is " + loginUrl);
            logger.info("URL after logout: " + loginUrl);
        } else {
            String errorMessage = signUpPage.getErrorMessage();
            Assert.assertTrue(errorMessage.contains(expectedMessage), "Expected error: " + expectedMessage + ", but got: " + errorMessage);
        }
        logger.info("===== Signup test completed successfully =====");
    }


    @Test(
            priority = 2,
            description="LOGIN: Verify user login functionality",
            dataProvider = "loginData",
            dataProviderClass = TestDataProviders.class
    )
    public void login(String testCaseID, String email, String password, String expectedResult, String expectedMessage) {
        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));

        logger.info("===== Starting login verification for " + testCaseID + " =====");

        DashboardPage dashboardPage = new DashboardPage(driver);
        LoginPage loginPage = new LoginPage(driver);

        logger.info("Entering email: " + email);
        loginPage.setUserEmail(email);

        logger.info("Entering password");
        loginPage.setUserPasswordInput(password);

        logger.info("Clicking login button");
        loginPage.clickLoginBtn();

        if (expectedResult.equalsIgnoreCase("Success")) {
            logger.info("Waiting for dashboard URL");
            wait.until(ExpectedConditions.urlContains("dashboard"));

            String currentUrl = driver.getCurrentUrl();
            logger.info("Current URL: " + currentUrl);
            Assert.assertTrue(currentUrl.contains("dashboard"), "User didn't navigate to dashboard. Actual URL: " + currentUrl);

            logger.info("Validating login success popup");
            String message = wait.until(ExpectedConditions.visibilityOf(dashboardPage.getLoginSuccessfulPopUp())).getText().trim();
            logger.info("Popup message: " + message);
            Assert.assertTrue(message.contains(expectedMessage), "Expected login success popup, but got: " + message);

            logger.info("Clicking menu button");
            dashboardPage.clickMenuBtn();

            logger.info("Clicking logout button");
            dashboardPage.clickLogout();

            String logoutUrl = driver.getCurrentUrl();
            logger.info("URL after logout: " + logoutUrl);
            Assert.assertTrue(logoutUrl.contains("login"), "Logout failed. Current URL: " + logoutUrl);
        } else {
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertTrue(errorMessage.contains(expectedMessage), "Expected error: " + expectedMessage + ", but got: " + errorMessage);
        }
        logger.info("===== Login test completed successfully =====");
    }
}