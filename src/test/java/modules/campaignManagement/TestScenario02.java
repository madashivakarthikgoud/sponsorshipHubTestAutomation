package modules.campaignManagement;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CampaignsPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.NewCampaignsPage;

public class TestScenario02 extends BaseTest {
    private static final Logger logger = LogManager.getLogger(TestScenario02.class);

    // 1. Create the DataProvider
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
                // testCaseID, email, password, expectedResult, expectedMessage
                {"TC_02_01", "brand@gmail.com", "brand@123", "Success", "Login successful!"}
        };
    }

    // 2. Link the DataProvider to your Test
    @Test(
            priority = 3,
            description = "TestCase 02 : Verify Brand Login",
            dataProvider = "loginData"
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
        } else {
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertTrue(errorMessage.contains(expectedMessage), "Expected error: " + expectedMessage + ", but got: " + errorMessage);
        }
        logger.info("===== Login test completed successfully =====");
    }

    @Test(
            priority = 4,
            description = "TestCase 02 : Create Campaign",
            dependsOnMethods = {"login"}
    )
    public void createCampaign(){
        logger.info("===== Starting Campaign Creation =====");

        CampaignsPage campaignsPage = new CampaignsPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        logger.info("Navigating to Campaigns tab");
        dashboardPage.clickCampaignsBtn();

        logger.info("Clicking Create Campaign");
        campaignsPage.clickCreateCampaignBtn();

        logger.info("Filling out Campaign Form");
        newCampaignsPage.setCampaignName("Summer Launch 2026");
        newCampaignsPage.setCampaignDescription("Promoting our new line of summer apparel.");
        newCampaignsPage.setCampaignStartDate("07/01/2026");
        newCampaignsPage.setCampaignEndDate("07/31/2026");
        newCampaignsPage.setCampaignPlatform("Instagram");
        newCampaignsPage.setCampaignBudget("5000");
        newCampaignsPage.setCampaignEligibilityReq("Must have at least 10k followers and high engagement.");

        logger.info("Submitting new campaign");
        newCampaignsPage.clickCreateCampaignSubmit();

        logger.info("===== Campaign Creation Completed =====");
    }
}