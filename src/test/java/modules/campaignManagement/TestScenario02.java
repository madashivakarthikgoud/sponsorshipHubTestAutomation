package modules.campaignManagement;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CampaignsPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.NewCampaignsPage;

/**
 * TestScenario02 — Campaign Management Module
 *
 * Covers 12 test cases (all under Brand role):
 *   CM_TC_01  Brand login prerequisite
 *   CM_TC_02  Create campaign — Instagram platform (positive)
 *   CM_TC_03  Create campaign — YouTube platform (positive)
 *   CM_TC_04  Create campaign — TikTok platform (positive)
 *   CM_TC_05  Attempt create campaign with empty name (negative)
 *   CM_TC_06  Attempt create campaign with zero budget (negative)
 *   CM_TC_07  Cancel campaign creation returns to list (positive)
 *   CM_TC_08  Campaign list loads — Brand sees own campaigns (positive)
 *   CM_TC_09  Search campaign by name (positive)
 *   CM_TC_10  View campaign detail page (positive)
 *   CM_TC_11  Edit existing campaign — update name + description (positive)
 *   CM_TC_12  Delete campaign — confirm dialog + removal from list (positive)
 *
 * Test dependency chain:
 *   brandLogin → all campaign tests (login is a prerequisite for all)
 */
public class TestScenario02 extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TestScenario02.class);

    /** Name used in CM_TC_02 — also referenced by search, view, edit, delete tests */
    private static final String CAMPAIGN_NAME_INSTAGRAM = "Summer Vibes IG 2026";
    /** Name used in CM_TC_03 */
    private static final String CAMPAIGN_NAME_YOUTUBE   = "Product Launch YT 2026";
    /** Name used in CM_TC_04 */
    private static final String CAMPAIGN_NAME_TIKTOK    = "Viral Trend TT 2026";
    /** Shared eligibility text */
    private static final String ELIGIBILITY             = "Minimum 5k followers, engagement rate > 2%.";
    /** Common start/end dates — future dates relative to the test run */
    private static final String START_DATE              = "08/01/2026";
    private static final String END_DATE                = "08/31/2026";

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_01 — Brand Login Prerequisite
    // ══════════════════════════════════════════════════════════════════════════════

    @DataProvider(name = "brandLoginData")
    public Object[][] getBrandLoginData() {
        return new Object[][] {
            // testCaseID  | email            | password    | expectedResult | expectedMessage
            { "CM_TC_01",  "brand@gmail.com", "brand@123", "Success",       "Login successful!" }
        };
    }

    /**
     * CM_TC_01: Login as a Brand user.
     * All subsequent campaign tests depend on this method having been executed
     * successfully (browser stays authenticated after this test).
     */
    @Test(
        priority = 4,
        description = "CM_TC_01: Brand user login — prerequisite for all campaign tests",
        dataProvider = "brandLoginData"
    )
    public void brandLogin(String testCaseID, String email, String password,
                           String expectedResult, String expectedMessage) {

        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));
        logger.info("===== [{}] START: Brand login =====", testCaseID);

        LoginPage     loginPage     = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        loginPage.setUserEmail(email);
        loginPage.setUserPasswordInput(password);
        loginPage.clickLoginBtn();

        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("dashboard"),
                "[" + testCaseID + "] Expected dashboard, got: " + currentUrl);

            String popup = wait.until(
                ExpectedConditions.visibilityOf(dashboardPage.getLoginSuccessfulPopUp())
            ).getText().trim();
            Assert.assertTrue(popup.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: " + popup);
            logger.info("[{}] Brand logged in. URL: {}", testCaseID, currentUrl);
        } else {
            String error = loginPage.getErrorMessage();
            Assert.fail("[" + testCaseID + "] Login failed unexpectedly. Error: " + error);
        }

        logger.info("===== [{}] END: Brand login — PASSED =====", testCaseID);
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_02 — Create Campaign: Instagram Platform
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 5,
        description = "CM_TC_02: Create a new campaign with Instagram platform (positive)",
        dependsOnMethods = { "brandLogin" }
    )
    public void createCampaignInstagram() {
        logger.info("===== [CM_TC_02] START: Create Campaign — Instagram =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        logger.info("[CM_TC_02] Navigating to Campaigns tab");
        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        logger.info("[CM_TC_02] Clicking Create Campaign");
        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));

        logger.info("[CM_TC_02] Filling campaign form");
        newCampaignsPage.fillAndSubmitCampaign(
            CAMPAIGN_NAME_INSTAGRAM,
            "Promoting our premium summer clothing line on Instagram.",
            START_DATE, END_DATE,
            "Instagram",
            "7500",
            ELIGIBILITY
        );

        logger.info("[CM_TC_02] Verifying redirect back to campaigns list");
        wait.until(ExpectedConditions.urlMatches(".*/campaigns$"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/campaigns"),
            "[CM_TC_02] Expected to return to /campaigns list, got: " + driver.getCurrentUrl());

        logger.info("===== [CM_TC_02] END: Create Campaign Instagram — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_03 — Create Campaign: YouTube Platform
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 6,
        description = "CM_TC_03: Create a new campaign with YouTube platform (positive)",
        dependsOnMethods = { "brandLogin" }
    )
    public void createCampaignYouTube() {
        logger.info("===== [CM_TC_03] START: Create Campaign — YouTube =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));

        newCampaignsPage.fillAndSubmitCampaign(
            CAMPAIGN_NAME_YOUTUBE,
            "Launching our new product with YouTube video reviews.",
            "09/01/2026", "09/30/2026",
            "YouTube",
            "12000",
            "YouTube channel with 10k+ subscribers required."
        );

        wait.until(ExpectedConditions.urlMatches(".*/campaigns$"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/campaigns"),
            "[CM_TC_03] Expected to return to /campaigns list");

        logger.info("===== [CM_TC_03] END: Create Campaign YouTube — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_04 — Create Campaign: TikTok Platform
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 7,
        description = "CM_TC_04: Create a new campaign with TikTok platform (positive)",
        dependsOnMethods = { "brandLogin" }
    )
    public void createCampaignTikTok() {
        logger.info("===== [CM_TC_04] START: Create Campaign — TikTok =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));

        newCampaignsPage.fillAndSubmitCampaign(
            CAMPAIGN_NAME_TIKTOK,
            "Viral marketing campaign targeting Gen-Z on TikTok.",
            "10/01/2026", "10/31/2026",
            "TikTok",
            "5000",
            ELIGIBILITY
        );

        wait.until(ExpectedConditions.urlMatches(".*/campaigns$"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/campaigns"),
            "[CM_TC_04] Expected to return to /campaigns list");

        logger.info("===== [CM_TC_04] END: Create Campaign TikTok — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_05 — Create Campaign: Empty Name (Negative)
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 8,
        description = "CM_TC_05: Attempt to create campaign with empty campaign name (negative)",
        dependsOnMethods = { "brandLogin" }
    )
    public void createCampaignWithEmptyName() {
        logger.info("===== [CM_TC_05] START: Create Campaign — Empty Name (Negative) =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));

        // Fill all fields EXCEPT name, then submit
        logger.info("[CM_TC_05] Submitting form without campaign name");
        newCampaignsPage.setCampaignDescription("Description without name.");
        newCampaignsPage.setCampaignStartDate(START_DATE);
        newCampaignsPage.setCampaignEndDate(END_DATE);
        newCampaignsPage.setCampaignPlatform("Instagram");
        newCampaignsPage.setCampaignBudget("1000");
        newCampaignsPage.clickCreateCampaignSubmit();

        // Should remain on the form page (URL still contains 'new') OR show a validation error
        String errors = newCampaignsPage.getFormValidationErrors();
        boolean staysOnPage = driver.getCurrentUrl().contains("campaigns/new");

        Assert.assertTrue(
            !errors.isEmpty() || staysOnPage,
            "[CM_TC_05] Expected validation error or form to stay open, but got URL: "
                + driver.getCurrentUrl() + " and errors: '" + errors + "'"
        );

        logger.info("[CM_TC_05] Validation blocked submission. Errors: '{}'. URL: {}",
            errors, driver.getCurrentUrl());
        logger.info("===== [CM_TC_05] END: Empty Name negative test — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_06 — Create Campaign: Zero Budget (Negative)
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 9,
        description = "CM_TC_06: Attempt to create campaign with budget = 0 (negative)",
        dependsOnMethods = { "brandLogin" }
    )
    public void createCampaignWithZeroBudget() {
        logger.info("===== [CM_TC_06] START: Create Campaign — Zero Budget (Negative) =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));

        logger.info("[CM_TC_06] Submitting form with budget = 0");
        newCampaignsPage.setCampaignName("Zero Budget Campaign");
        newCampaignsPage.setCampaignDescription("Testing zero budget validation scenario.");
        newCampaignsPage.setCampaignStartDate(START_DATE);
        newCampaignsPage.setCampaignEndDate(END_DATE);
        newCampaignsPage.setCampaignPlatform("Facebook");
        newCampaignsPage.setCampaignBudget("0");
        newCampaignsPage.clickCreateCampaignSubmit();

        String errors = newCampaignsPage.getFormValidationErrors();
        boolean staysOnPage = driver.getCurrentUrl().contains("campaigns/new");

        Assert.assertTrue(
            !errors.isEmpty() || staysOnPage,
            "[CM_TC_06] Expected validation error for zero budget, but form was submitted. URL: "
                + driver.getCurrentUrl()
        );

        logger.info("[CM_TC_06] Validation blocked zero-budget submission. Errors: '{}'", errors);
        logger.info("===== [CM_TC_06] END: Zero Budget negative test — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_07 — Cancel Campaign Creation Returns to List
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 10,
        description = "CM_TC_07: Clicking Cancel on campaign form returns user to campaigns list",
        dependsOnMethods = { "brandLogin" }
    )
    public void cancelCampaignCreation() {
        logger.info("===== [CM_TC_07] START: Cancel Campaign Creation =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage newCampaignsPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains("campaigns/new"));
        logger.info("[CM_TC_07] On new campaign form. Clicking Cancel.");

        // Partially fill the form to ensure cancel ignores entered data
        newCampaignsPage.setCampaignName("Campaign to be cancelled");
        newCampaignsPage.clickCancelBtn();

        wait.until(ExpectedConditions.urlMatches(".*/campaigns$"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.endsWith("/campaigns"),
            "[CM_TC_07] Expected redirect to /campaigns after cancel, got: " + url);

        logger.info("[CM_TC_07] Redirected back to /campaigns: {}", url);
        logger.info("===== [CM_TC_07] END: Cancel Campaign — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_08 — Campaign List Loads for Brand
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 11,
        description = "CM_TC_08: Verify campaign list loads and shows campaigns owned by the Brand",
        dependsOnMethods = { "createCampaignInstagram" }
    )
    public void verifyCampaignListLoads() {
        logger.info("===== [CM_TC_08] START: Verify Campaign List Loads =====");

        DashboardPage dashboardPage = new DashboardPage(driver);
        CampaignsPage campaignsPage = new CampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));

        logger.info("[CM_TC_08] Waiting for campaign list to load");
        campaignsPage.waitForListToLoad();

        int count = campaignsPage.getCampaignCount();
        logger.info("[CM_TC_08] Campaigns visible on list: {}", count);
        Assert.assertTrue(count > 0,
            "[CM_TC_08] Expected at least one campaign in the Brand's list, but found: " + count);

        logger.info("===== [CM_TC_08] END: Campaign List Loads — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_09 — Search Campaign by Name
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 12,
        description = "CM_TC_09: Search for a campaign by name using the search bar",
        dependsOnMethods = { "createCampaignInstagram" }
    )
    public void searchCampaignByName() {
        logger.info("===== [CM_TC_09] START: Search Campaign by Name =====");

        DashboardPage dashboardPage = new DashboardPage(driver);
        CampaignsPage campaignsPage = new CampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        // Search using the first 10 characters of the known campaign name
        String searchTerm = CAMPAIGN_NAME_INSTAGRAM.substring(0, 10);
        logger.info("[CM_TC_09] Searching for: '{}'", searchTerm);
        campaignsPage.searchByName(searchTerm);

        // Wait for the searched campaign to appear in the filtered results (up to 5 seconds)
        boolean visible = false;
        for (int i = 0; i < 5; i++) {
            if (campaignsPage.isCampaignVisible(CAMPAIGN_NAME_INSTAGRAM)) {
                visible = true;
                break;
            }
            logger.info("[CM_TC_09] Campaign not visible yet in search results. Waiting... (Attempt {})", i + 1);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        Assert.assertTrue(visible,
            "[CM_TC_09] Expected campaign '" + CAMPAIGN_NAME_INSTAGRAM
                + "' to be visible after search, but it was not found.");

        logger.info("[CM_TC_09] Campaign '{}' found in search results", CAMPAIGN_NAME_INSTAGRAM);
        logger.info("===== [CM_TC_09] END: Search Campaign — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_10 — View Campaign Detail Page
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 13,
        description = "CM_TC_10: Click a campaign card to navigate to its detail page",
        dependsOnMethods = { "createCampaignInstagram" }
    )
    public void viewCampaignDetail() {
        logger.info("===== [CM_TC_10] START: View Campaign Detail =====");

        DashboardPage dashboardPage = new DashboardPage(driver);
        CampaignsPage campaignsPage = new CampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        logger.info("[CM_TC_10] Clicking on campaign card: '{}'", CAMPAIGN_NAME_INSTAGRAM);
        campaignsPage.clickViewCampaign(CAMPAIGN_NAME_INSTAGRAM);

        // URL should be /campaigns/{id}
        wait.until(ExpectedConditions.urlMatches(".*/campaigns/\\d+$"));
        String detailUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            detailUrl.matches(".*/campaigns/\\d+$"),
            "[CM_TC_10] Expected campaign detail URL (/campaigns/{id}) but got: " + detailUrl
        );

        logger.info("[CM_TC_10] Navigated to campaign detail: {}", detailUrl);
        logger.info("===== [CM_TC_10] END: View Campaign Detail — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_11 — Edit Existing Campaign
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 14,
        description = "CM_TC_11: Edit an existing campaign — update name and description",
        dependsOnMethods = { "createCampaignInstagram" }
    )
    public void editExistingCampaign() {
        logger.info("===== [CM_TC_11] START: Edit Campaign =====");

        DashboardPage    dashboardPage    = new DashboardPage(driver);
        CampaignsPage    campaignsPage    = new CampaignsPage(driver);
        NewCampaignsPage editCampaignPage = new NewCampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        logger.info("[CM_TC_11] Clicking Edit on campaign: '{}'", CAMPAIGN_NAME_INSTAGRAM);
        campaignsPage.clickEditCampaign(CAMPAIGN_NAME_INSTAGRAM);

        // URL should be /campaigns/edit/{id}
        wait.until(ExpectedConditions.urlContains("campaigns/edit"));
        Assert.assertTrue(driver.getCurrentUrl().contains("campaigns/edit"),
            "[CM_TC_11] Expected edit URL but got: " + driver.getCurrentUrl());

        // Wait for Angular form to fetch and populate existing values from the backend API
        editCampaignPage.waitForFormToPopulate();

        String updatedName = CAMPAIGN_NAME_INSTAGRAM + " [EDITED]";
        logger.info("[CM_TC_11] Updating campaign name to: '{}'", updatedName);
        editCampaignPage.setCampaignName(updatedName);
        editCampaignPage.setCampaignDescription("Updated description for the edited Instagram campaign.");

        logger.info("[CM_TC_11] Submitting the edit");
        editCampaignPage.clickCreateCampaignSubmit();

        // On success the app routes back to /campaigns
        wait.until(ExpectedConditions.urlMatches(".*/campaigns$"));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/campaigns"),
            "[CM_TC_11] Expected to return to /campaigns after edit, got: " + driver.getCurrentUrl());

        logger.info("===== [CM_TC_11] END: Edit Campaign — PASSED =====");
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // CM_TC_12 — Delete Campaign
    // ══════════════════════════════════════════════════════════════════════════════

    @Test(
        priority = 15,
        description = "CM_TC_12: Delete a campaign — confirm dialog and verify removal from list",
        dependsOnMethods = { "createCampaignTikTok" }
    )
    public void deleteCampaign() {
        logger.info("===== [CM_TC_12] START: Delete Campaign =====");

        DashboardPage dashboardPage = new DashboardPage(driver);
        CampaignsPage campaignsPage = new CampaignsPage(driver);

        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains("campaigns"));
        campaignsPage.waitForListToLoad();

        logger.info("[CM_TC_12] Overriding window.confirm to auto-accept delete confirmation");
        ((JavascriptExecutor) driver).executeScript("window.confirm = function() { return true; };");

        logger.info("[CM_TC_12] Clicking Delete on campaign: '{}'", CAMPAIGN_NAME_TIKTOK);
        campaignsPage.clickDeleteCampaign(CAMPAIGN_NAME_TIKTOK);

        // Wait for the campaign to disappear from the list (up to 10 seconds)
        boolean deleted = false;
        for (int i = 0; i < 10; i++) {
            if (!campaignsPage.isCampaignVisible(CAMPAIGN_NAME_TIKTOK)) {
                deleted = true;
                break;
            }
            logger.info("[CM_TC_12] Campaign is still visible. Waiting for refresh... (Attempt {})", i + 1);
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
        Assert.assertTrue(deleted,
            "[CM_TC_12] Campaign '" + CAMPAIGN_NAME_TIKTOK
                + "' should have been deleted but is still visible in the list.");

        logger.info("[CM_TC_12] Campaign '{}' successfully deleted", CAMPAIGN_NAME_TIKTOK);
        logger.info("===== [CM_TC_12] END: Delete Campaign — PASSED =====");
    }
}