package modules.userManagement;

import base.AppConstants;
import base.BaseTest;
import dataProviders.TestDataProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import pages.SignUpPage;
import utils.JsonUtils;

/**
 * Test Scenario 01 — User Management
 *
 * <p>Covers:
 * <ul>
 *   <li>UM_TC_01–08 : Sign-up (positive and negative)</li>
 *   <li>UM_TC_09–14 : Login (positive and negative)</li>
 *   <li>UM_TC_15    : Logout redirect</li>
 * </ul>
 *
 * <p>Page objects are created once in {@link #initPages()} which runs after the parent
 * {@code BaseTest.initializeDriver()} — TestNG guarantees parent {@code @BeforeClass}
 * executes before child {@code @BeforeClass}.
 */
public class TestScenario01 extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TestScenario01.class);

    // ── Page objects (initialized once per test class, not per test method) ──

    private LoginPage     loginPage;
    private SignUpPage    signUpPage;
    private DashboardPage dashboardPage;

    // ── Test data loaded once from JSON ──────────────────────────────────────

    private String logoutEmail;
    private String logoutPassword;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        loginPage     = new LoginPage(driver, wait);
        signUpPage    = new SignUpPage(driver, wait);
        dashboardPage = new DashboardPage(driver, wait);

        // Credentials for the logout test — loaded once, reused in the test method.
        var logoutData = JsonUtils.loadJson("testdata/campaign_data.json").get("logoutTest");
        logoutEmail    = logoutData.get("email").asText();
        logoutPassword = logoutData.get("password").asText();
    }

    // ── UM_TC_01–08: Sign-up ─────────────────────────────────────────────────

    @Test(
        priority = 1,
        description = "SIGNUP: Verify signup functionality for all scenarios (positive + negative)",
        dataProvider = "signUpData",
        dataProviderClass = TestDataProviders.class
    )
    public void signUp(String testCaseID, String userName, String email,
                       String password, String role,
                       String expectedResult, String expectedMessage) {

        driver.get(baseUrl + AppConstants.PATH_LOGIN);
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
        logger.info("===== [{}] START: Signup test =====", testCaseID);

        /*
         * Uniquify credentials so repeated runs never collide on a live server.
         *
         * Success cases      → uniquify BOTH username and email.
         * "Email already registered" → keep email fixed (that's the point), uniquify username.
         * "Username already exists"  → keep username fixed (that's the point), uniquify email.
         * All other failures → leave data exactly as supplied in JSON.
         */
        String uniqueID = String.valueOf(System.currentTimeMillis());

        if (expectedResult.equalsIgnoreCase("Success")) {
            userName = userName + uniqueID;
            if (email != null && email.contains("@")) {
                email = email.replace("@", "+" + uniqueID + "@");
            }
        } else if (expectedMessage.contains("Email already registered")) {
            userName = "AutoUser" + uniqueID;
        } else if (expectedMessage.contains("Username already exists") && email != null && email.contains("@")) {
            email = email.replace("@", "+" + uniqueID + "@");
        }

        loginPage.clickSignupBtn();
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_SIGNUP));
        logger.info("[{}] On signup page", testCaseID);

        /*
         * Always call text-field setters even when the value is an empty string.
         * Sending an empty string followed by TAB "touches" the field in Angular's
         * reactive form model, which triggers inline required/format validators
         * and displays the mat-error messages we assert against.
         * Skipping the call leaves the field untouched → no error → assertion fails.
         *
         * Exception: the role mat-select dropdown — passing an empty string to
         * setRoleSelectInput would try to find a blank <mat-option>, which doesn't
         * exist and would throw a TimeoutException.
         */
        if (userName != null) {
            logger.info("[{}] Setting username: '{}'", testCaseID, userName);
            signUpPage.setUserName(userName);
        }
        if (email != null) {
            logger.info("[{}] Setting email: '{}'", testCaseID, email);
            signUpPage.setUserEmail(email);
        }
        if (password != null) {
            logger.info("[{}] Setting password", testCaseID);
            signUpPage.setUserPasswordInput(password);
        }
        if (role != null && !role.isEmpty()) {
            logger.info("[{}] Selecting role: {}", testCaseID, role);
            signUpPage.setRoleSelectInput(role);
        }

        if (signUpPage.isCreateAccountEnabled()) {
            signUpPage.clickCreateAccountBtn();
        } else {
            logger.info("[{}] Create Account button is disabled. Skipping click to read errors.", testCaseID);
        }

        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains(AppConstants.PATH_DASHBOARD));
            Assert.assertTrue(driver.getCurrentUrl().contains(AppConstants.PATH_DASHBOARD),
                "[" + testCaseID + "] Expected dashboard URL but got: " + driver.getCurrentUrl());

            String popupMsg = dashboardPage.getRegistrationSuccessText();
            Assert.assertTrue(popupMsg.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: '" + popupMsg + "'");
            logger.info("[{}] Registration success popup: '{}'", testCaseID, popupMsg);

            dashboardPage.clickMenuBtn();
            dashboardPage.clickLogout();
            wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
            logger.info("[{}] Logged out after successful signup", testCaseID);

        } else {
            String errorMessage = signUpPage.getErrorMessage();
            logger.info("[{}] Captured error: '{}'", testCaseID, errorMessage);
            Assert.assertTrue(errorMessage.contains(expectedMessage),
                "[" + testCaseID + "] Expected error '" + expectedMessage + "' but got: '" + errorMessage + "'");
        }

        logger.info("===== [{}] END: Signup — PASSED =====", testCaseID);
    }

    // ── UM_TC_09–14: Login ───────────────────────────────────────────────────

    @Test(
        priority = 2,
        description = "LOGIN: Verify login functionality for all scenarios (positive + negative)",
        dataProvider = "loginData",
        dataProviderClass = TestDataProviders.class
    )
    public void login(String testCaseID, String email, String password,
                      String expectedResult, String expectedMessage) {

        driver.get(baseUrl + AppConstants.PATH_LOGIN);
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
        logger.info("===== [{}] START: Login test =====", testCaseID);

        // Same reasoning as signup: always touch fields even when empty so Angular
        // shows inline required errors. See signup test for the full explanation.
        if (email != null) {
            logger.info("[{}] Setting email: '{}'", testCaseID, email);
            loginPage.setUserEmail(email);
        }
        if (password != null) {
            logger.info("[{}] Setting password", testCaseID);
            loginPage.setUserPasswordInput(password);
        }

        if (loginPage.isLoginEnabled()) {
            loginPage.clickLoginBtn();
        } else {
            logger.info("[{}] Login button is disabled. Skipping click to read errors.", testCaseID);
        }

        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains(AppConstants.PATH_DASHBOARD));
            Assert.assertTrue(driver.getCurrentUrl().contains(AppConstants.PATH_DASHBOARD),
                "[" + testCaseID + "] Expected dashboard URL but got: " + driver.getCurrentUrl());

            String popupMsg = dashboardPage.getLoginSuccessText();
            Assert.assertTrue(popupMsg.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: '" + popupMsg + "'");
            logger.info("[{}] Login success popup: '{}'", testCaseID, popupMsg);

            dashboardPage.clickMenuBtn();
            dashboardPage.clickLogout();
            wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
            logger.info("[{}] Logged out successfully", testCaseID);

        } else {
            String errorMessage = loginPage.getErrorMessage();
            logger.info("[{}] Captured error: '{}'", testCaseID, errorMessage);
            Assert.assertTrue(errorMessage.contains(expectedMessage),
                "[" + testCaseID + "] Expected error '" + expectedMessage + "' but got: '" + errorMessage + "'");
        }

        logger.info("===== [{}] END: Login — PASSED =====", testCaseID);
    }

    // ── UM_TC_15: Logout ─────────────────────────────────────────────────────

    @Test(
        priority = 3,
        description = "UM_TC_15: Verify Logout redirects user back to the login page"
    )
    public void verifyLogoutRedirectsToLogin() {
        logger.info("===== [UM_TC_15] START: Logout redirect test =====");

        driver.get(baseUrl + AppConstants.PATH_LOGIN);
        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));

        loginPage.setUserEmail(logoutEmail);
        loginPage.setUserPasswordInput(logoutPassword);
        loginPage.clickLoginBtn();

        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_DASHBOARD));
        Assert.assertTrue(driver.getCurrentUrl().contains(AppConstants.PATH_DASHBOARD),
            "[UM_TC_15] Login failed — did not reach dashboard.");

        dashboardPage.clickMenuBtn();
        dashboardPage.clickLogout();

        wait.until(ExpectedConditions.urlContains(AppConstants.PATH_LOGIN));
        String finalUrl = driver.getCurrentUrl();
        Assert.assertTrue(finalUrl.contains(AppConstants.PATH_LOGIN),
            "[UM_TC_15] Expected redirect to /login after logout but got: " + finalUrl);

        logger.info("[UM_TC_15] Redirected to: {}", finalUrl);
        logger.info("===== [UM_TC_15] END: Logout redirect — PASSED =====");
    }
}