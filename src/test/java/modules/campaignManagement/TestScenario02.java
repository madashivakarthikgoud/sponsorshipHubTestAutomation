package modules.campaignManagement;

import base.AppConstants;
import base.BaseTest;
import dataProviders.TestDataProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CampaignsPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.NewCampaignsPage;
import utils.JsonUtils;

/**
 * Test Scenario 02 — Campaign Management
 *
 * <p>Covers:
 * <ul>
 *   <li>CM_TC_01 : Brand login (prerequisite for all campaign operations)</li>
 *   <li>CM_TC_02–04 : Create campaigns (Instagram, YouTube, TikTok)</li>
 *   <li>CM_TC_05–06 : Negative campaign creation (invalid data)</li>
 *   <li>CM_TC_07 : Cancel campaign creation</li>
 *   <li>CM_TC_08 : Campaign list loads with at least one entry</li>
 *   <li>CM_TC_09 : Search campaign by name (partial match)</li>
 *   <li>CM_TC_10 : View campaign detail page</li>
 *   <li>CM_TC_11 : Edit an existing campaign</li>
 *   <li>CM_TC_12 : Delete a campaign and verify removal</li>
 * </ul>
 *
 * <p><strong>Session strategy:</strong> The brand logs in once in {@code brandLogin} and the
 * session is maintained throughout the class.  Every test navigates to its own start URL so
 * the execution order can be reasoned about independently.
 *
 * <p><strong>Dependency chain:</strong>
 * <pre>
 *   brandLogin → createCampaign → verifyCampaignListLoads
 *                               → searchCampaignByName
 *                               → viewCampaignDetail
 *                               → editExistingCampaign
 *                               → deleteCampaign
 *             → createCampaignNegative
 *             → cancelCampaignCreation
 * </pre>
 */
public class TestScenario02 extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TestScenario02.class);

    // ── Page objects (single instance per test class) ─────────────────────────

    private LoginPage       loginPage;
    private DashboardPage   dashboardPage;
    private CampaignsPage   campaignsPage;
    private NewCampaignsPage newCampaignsPage;

    // ── Test data resolved once from JSON ─────────────────────────────────────

    /** Name of the Instagram campaign created in CM_TC_02, used by search/view/edit tests. */
    private String campaignNameInstagram;

    /** Name of the TikTok campaign created in CM_TC_04, targeted for deletion in CM_TC_12. */
    private String campaignNameTikTok;

    /** Cancel-test details (CM_TC_07). */
    private String cancelTestCaseID;
    private String cancelCampaignName;

    /** Edit-test patch data (CM_TC_11). */
    private String editNameSuffix;
    private String editDescription;

    /**
     * Runs after the parent {@code BaseTest.initializeDriver()} (TestNG guarantees
     * parent {@code @BeforeClass} runs first).
     */
    @BeforeClass(alwaysRun = true)
    public void initPagesAndData() {
        // ── Page objects ──────────────────────────────────────────────────────
        loginPage        = new LoginPage(driver, wait);
        dashboardPage    = new DashboardPage(driver, wait);
        campaignsPage    = new CampaignsPage(driver, wait);
        newCampaignsPage = new NewCampaignsPage(driver, wait);

        // ── Campaign test data ────────────────────────────────────────────────
        var root      = JsonUtils.loadJson("testdata/campaign_data.json");
        var campaigns = root.get("createCampaigns");

        for (var c : campaigns) {
            String platform = c.get("platform").asText();
            if ("Instagram".equalsIgnoreCase(platform)) {
                campaignNameInstagram = c.get("name").asText();
            } else if ("TikTok".equalsIgnoreCase(platform)) {
                campaignNameTikTok = c.get("name").asText();
            }
        }

        var cancelNode  = root.get("cancelCampaign");
        cancelTestCaseID    = cancelNode.get("testCaseID").asText();
        cancelCampaignName  = cancelNode.get("name").asText();

        var editNode    = root.get("editCampaign");
        editNameSuffix  = editNode.get("nameSuffix").asText();
        editDescription = editNode.get("description").asText();
    }

    // ── CM_TC_01: Brand login ─────────────────────────────────────────────────

    @Test(
        priority = 4,
        description = "CM_TC_01: Brand user login — prerequisite for all campaign tests",
        dataProvider = "brandLoginData",
        dataProviderClass = TestDataProviders.class
    )
    public void brandLogin(String testCaseID, String email, String password,
                           String expectedResult, String expectedMessage) {

        driver.get(baseUrl + AppConstants.PATH_LOGIN);
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
        logger.info("===== [{}] START: Brand login =====", testCaseID);

        loginPage.setUserEmail(email);
        loginPage.setUserPasswordInput(password);
        loginPage.clickLoginBtn();

        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains(AppConstants.PATH_DASHBOARD));
            Assert.assertTrue(driver.getCurrentUrl().contains(AppConstants.PATH_DASHBOARD),
                "[" + testCaseID + "] Expected dashboard, got: " + driver.getCurrentUrl());

            String popup = dashboardPage.getLoginSuccessText();
            Assert.assertTrue(popup.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: '" + popup + "'");
            logger.info("[{}] Brand logged in. Current URL: {}", testCaseID, driver.getCurrentUrl());
        } else {
            // brandLogin is only called with Success data; any failure here is a setup problem.
            Assert.fail("[" + testCaseID + "] Brand login failed unexpectedly. Error: "
                    + loginPage.getErrorMessage());
        }

        logger.info("===== [{}] END: Brand login — PASSED =====", testCaseID);
    }

    // ── CM_TC_02/03/04: Create campaigns (positive) ───────────────────────────

    @Test(
        priority = 5,
        description = "CM_TC_02/03/04: Create campaigns for Instagram, YouTube, and TikTok",
        dataProvider = "createCampaignData",
        dataProviderClass = TestDataProviders.class,
        dependsOnMethods = {"brandLogin"}
    )
    public void createCampaign(String testCaseID, String name, String description,
                               String startDate, String endDate, String platform,
                               String budget, String eligibility) {

        logger.info("===== [{}] START: Create Campaign — {} =====", testCaseID, platform);

        navigateToCampaignsList(testCaseID);

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_NEW_CAMPAIGN));

        // Uniquify the campaign name to prevent collisions with duplicate stale data
        String uniqueID = String.valueOf(System.currentTimeMillis());
        String uniqueName = name + "_" + uniqueID;

        if ("Instagram".equalsIgnoreCase(platform)) {
            campaignNameInstagram = uniqueName;
        } else if ("TikTok".equalsIgnoreCase(platform)) {
            campaignNameTikTok = uniqueName;
        }

        logger.info("[{}] Filling and submitting campaign form with name: '{}'", testCaseID, uniqueName);
        newCampaignsPage.fillAndSubmitCampaign(uniqueName, description, startDate, endDate,
                                               platform, budget, eligibility);

        wait.until(ExpectedConditions.urlMatches(AppConstants.URL_REGEX_CAMPAIGNS_LIST));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/" + AppConstants.PATH_CAMPAIGNS),
            "[" + testCaseID + "] Expected /campaigns list after creation, got: "
                + driver.getCurrentUrl());

        logger.info("===== [{}] END: Create Campaign ({}) — PASSED =====", testCaseID, platform);
    }

    // ── CM_TC_05/06: Create campaigns (negative) ──────────────────────────────

    @Test(
        priority = 6,
        description = "CM_TC_05/06: Negative campaign creation — missing name and zero budget",
        dataProvider = "negativeCampaignData",
        dataProviderClass = TestDataProviders.class,
        dependsOnMethods = {"brandLogin"}
    )
    public void createCampaignNegative(String testCaseID, String name, String description,
                                       String startDate, String endDate, String platform,
                                       String budget, String eligibility) {

        logger.info("===== [{}] START: Create Campaign Negative =====", testCaseID);
        logger.info("[{}] name='{}', budget='{}'", testCaseID, name, budget);

        navigateToCampaignsList(testCaseID);

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_NEW_CAMPAIGN));

        // Always call setters to touch fields so Angular reactive form validation triggers.
        newCampaignsPage.setCampaignName(name);
        newCampaignsPage.setCampaignDescription(description);
        newCampaignsPage.setCampaignStartDate(startDate);
        newCampaignsPage.setCampaignEndDate(endDate);
        if (platform != null && !platform.isEmpty()) {
            newCampaignsPage.setCampaignPlatform(platform);
        }
        newCampaignsPage.setCampaignBudget(budget);

        newCampaignsPage.clickCreateCampaignSubmit();

        String errors       = newCampaignsPage.getFormValidationErrors();
        boolean staysOnForm = driver.getCurrentUrl().contains(AppConstants.PATH_NEW_CAMPAIGN);

        Assert.assertTrue(!errors.isEmpty() || staysOnForm,
            "[" + testCaseID + "] Expected validation error or form to remain open, but got URL: "
                + driver.getCurrentUrl() + " with no errors.");

        logger.info("[{}] Validation blocked submission. errors='{}', staysOnForm={}",
            testCaseID, errors, staysOnForm);
        logger.info("===== [{}] END: Negative campaign test — PASSED =====", testCaseID);
    }

    // ── CM_TC_07: Cancel ──────────────────────────────────────────────────────

    @Test(
        priority = 7,
        description = "CM_TC_07: Clicking Cancel on the campaign form returns user to campaigns list",
        dependsOnMethods = {"brandLogin"}
    )
    public void cancelCampaignCreation() {
        logger.info("===== [{}] START: Cancel Campaign Creation =====", cancelTestCaseID);

        navigateToCampaignsList(cancelTestCaseID);

        campaignsPage.clickCreateCampaignBtn();
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_NEW_CAMPAIGN));

        newCampaignsPage.setCampaignName(cancelCampaignName);

        logger.info("[{}] Clicking Cancel", cancelTestCaseID);
        newCampaignsPage.clickCancelBtn();

        wait.until(ExpectedConditions.urlMatches(AppConstants.URL_REGEX_CAMPAIGNS_LIST));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.endsWith("/" + AppConstants.PATH_CAMPAIGNS),
            "[" + cancelTestCaseID + "] Expected redirect to /campaigns after cancel, got: " + url);

        logger.info("[{}] Redirected to: {}", cancelTestCaseID, url);
        logger.info("===== [{}] END: Cancel Campaign — PASSED =====", cancelTestCaseID);
    }

    // ── CM_TC_08: Campaign list loads ─────────────────────────────────────────

    @Test(
        priority = 8,
        description = "CM_TC_08: Verify campaign list loads and displays campaigns owned by the Brand",
        dependsOnMethods = {"createCampaign"}
    )
    public void verifyCampaignListLoads() {
        logger.info("===== [CM_TC_08] START: Verify Campaign List Loads =====");

        navigateToCampaignsList("CM_TC_08");

        int count = campaignsPage.getCampaignCount();
        logger.info("[CM_TC_08] Campaigns visible: {}", count);
        Assert.assertTrue(count > 0,
            "[CM_TC_08] Expected at least one campaign in the Brand's list, but found: " + count);

        logger.info("===== [CM_TC_08] END: Campaign List Loads — PASSED =====");
    }

    // ── CM_TC_09: Search by name ──────────────────────────────────────────────

    @Test(
        priority = 9,
        description = "CM_TC_09: Search for a campaign by partial name and verify it appears in results",
        dependsOnMethods = {"createCampaign"}
    )
    public void searchCampaignByName() {
        logger.info("===== [CM_TC_09] START: Search Campaign by Name =====");

        navigateToCampaignsList("CM_TC_09");

        // Use a prefix to validate substring / partial-match search behaviour.
        String searchTerm = campaignNameInstagram.length() > AppConstants.SEARCH_TERM_PREFIX_LENGTH
                ? campaignNameInstagram.substring(0, AppConstants.SEARCH_TERM_PREFIX_LENGTH)
                : campaignNameInstagram;

        logger.info("[CM_TC_09] Searching with prefix: '{}'", searchTerm);
        campaignsPage.searchByName(searchTerm);

        // Wait via FluentWait rather than a Thread.sleep loop.
        campaignsPage.waitForCampaignVisible(campaignNameInstagram);
        Assert.assertTrue(campaignsPage.isCampaignVisible(campaignNameInstagram),
            "[CM_TC_09] Campaign '" + campaignNameInstagram + "' was not found in search results.");

        logger.info("[CM_TC_09] Campaign '{}' found", campaignNameInstagram);
        logger.info("===== [CM_TC_09] END: Search Campaign — PASSED =====");
    }

    // ── CM_TC_10: View detail ─────────────────────────────────────────────────

    @Test(
        priority = 10,
        description = "CM_TC_10: Click a campaign card to navigate to its detail page",
        dependsOnMethods = {"createCampaign"}
    )
    public void viewCampaignDetail() {
        logger.info("===== [CM_TC_10] START: View Campaign Detail =====");

        navigateToCampaignsList("CM_TC_10");

        logger.info("[CM_TC_10] Clicking campaign card: '{}'", campaignNameInstagram);
        campaignsPage.clickViewCampaign(campaignNameInstagram);

        wait.until(ExpectedConditions.urlMatches(AppConstants.URL_REGEX_CAMPAIGN_DETAIL));
        String detailUrl = driver.getCurrentUrl();
        Assert.assertTrue(detailUrl.matches(AppConstants.URL_REGEX_CAMPAIGN_DETAIL),
            "[CM_TC_10] Expected campaign detail URL (/campaigns/{id}) but got: " + detailUrl);

        logger.info("[CM_TC_10] Navigated to detail: {}", detailUrl);
        logger.info("===== [CM_TC_10] END: View Campaign Detail — PASSED =====");
    }

    // ── CM_TC_11: Edit ────────────────────────────────────────────────────────

    @Test(
        priority = 11,
        description = "CM_TC_11: Edit an existing campaign — update name and description",
        dependsOnMethods = {"createCampaign"}
    )
    public void editExistingCampaign() {
        logger.info("===== [CM_TC_11] START: Edit Campaign =====");

        navigateToCampaignsList("CM_TC_11");

        logger.info("[CM_TC_11] Clicking Edit on: '{}'", campaignNameInstagram);
        campaignsPage.clickEditCampaign(campaignNameInstagram);

        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_EDIT_CAMPAIGN));
        Assert.assertTrue(driver.getCurrentUrl().contains(AppConstants.PATH_EDIT_CAMPAIGN),
            "[CM_TC_11] Expected edit URL but got: " + driver.getCurrentUrl());

        newCampaignsPage.waitForFormToPopulate();

        String updatedName = campaignNameInstagram + editNameSuffix;
        logger.info("[CM_TC_11] Updating name to: '{}'", updatedName);
        newCampaignsPage.setCampaignName(updatedName);
        newCampaignsPage.setCampaignDescription(editDescription);

        newCampaignsPage.clickCreateCampaignSubmit();

        wait.until(ExpectedConditions.urlMatches(AppConstants.URL_REGEX_CAMPAIGNS_LIST));
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/" + AppConstants.PATH_CAMPAIGNS),
            "[CM_TC_11] Expected to return to /campaigns after edit, got: " + driver.getCurrentUrl());

        // Reflect the rename so any future reference uses the correct name.
        campaignNameInstagram = updatedName;
        logger.info("[CM_TC_11] Instagram campaign is now named: '{}'", campaignNameInstagram);

        logger.info("===== [CM_TC_11] END: Edit Campaign — PASSED =====");
    }

    // ── CM_TC_12: Delete ─────────────────────────────────────────────────────

    @Test(
        priority = 12,
        description = "CM_TC_12: Delete a campaign and verify it is removed from the list",
        dependsOnMethods = {"createCampaign"}
    )
    public void deleteCampaign() {
        logger.info("===== [CM_TC_12] START: Delete Campaign =====");

        navigateToCampaignsList("CM_TC_12");

        /*
         * Override the browser's confirm() so it always returns true.
         * The app uses a native browser dialog for delete confirmation.
         * This must be done BEFORE clicking delete — the override does not persist
         * across navigation since the driver.get() above has already completed.
         */
        ((JavascriptExecutor) driver).executeScript("window.confirm = function() { return true; };");
        logger.info("[CM_TC_12] Browser confirm() overridden to auto-accept");

        logger.info("[CM_TC_12] Clicking Delete on: '{}'", campaignNameTikTok);
        campaignsPage.clickDeleteCampaign(campaignNameTikTok);

        // Wait via FluentWait instead of a Thread.sleep polling loop.
        campaignsPage.waitForCampaignToDisappear(campaignNameTikTok);
        Assert.assertFalse(campaignsPage.isCampaignVisible(campaignNameTikTok),
            "[CM_TC_12] Campaign '" + campaignNameTikTok + "' should have been deleted but is still visible.");

        logger.info("[CM_TC_12] Campaign '{}' successfully deleted", campaignNameTikTok);
        logger.info("===== [CM_TC_12] END: Delete Campaign — PASSED =====");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Navigates to the campaigns list and waits for it to fully load.
     * Every campaign test starts here to ensure a known page state.
     *
     * @param callerTestID test case ID used in log messages
     */
    private void navigateToCampaignsList(String callerTestID) {
        dashboardPage.clickCampaignsBtn();
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_CAMPAIGNS));
        campaignsPage.waitForListToLoad();
        logger.info("[{}] Campaigns list loaded", callerTestID);
    }
}