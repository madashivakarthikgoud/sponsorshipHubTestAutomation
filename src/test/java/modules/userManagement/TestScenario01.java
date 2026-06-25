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

/**
 * TestScenario01 — User Management Module
 *
 * Covers 15 test cases:
 *   UM_TC_01  Valid signup as Brand
 *   UM_TC_02  Valid signup as Influencer
 *   UM_TC_03  Signup with duplicate email
 *   UM_TC_04  Signup with duplicate username
 *   UM_TC_05  Signup with invalid email format
 *   UM_TC_06  Signup with weak password (no @, no digit)
 *   UM_TC_07  Signup with username < 3 chars
 *   UM_TC_08  Signup with all fields empty
 *   UM_TC_09  Valid login with Brand credentials
 *   UM_TC_10  Valid login with Influencer credentials
 *   UM_TC_11  Login with wrong password
 *   UM_TC_12  Login with unregistered email
 *   UM_TC_13  Login with empty email field
 *   UM_TC_14  Login with empty password field
 *   UM_TC_15  Logout redirects to login page
 */
public class TestScenario01 extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TestScenario01.class);

    // ══════════════════════════════════════════════════════════════════════════════
    // SIGNUP TESTS  (UM_TC_01 → UM_TC_08)  —  data-driven via Excel
    // ══════════════════════════════════════════════════════════════════════════════

    /**
     * TC_GROUP: SIGNUP
     * Reads test rows from "SignUp_Data" sheet in TestData.xlsx.
     * Columns (0-indexed):
     *   0=testCaseID | 1=userName | 2=email | 3=password | 4=role | 5=expectedResult | 6=expectedMessage
     *
     * Positive rows  → expects dashboard navigation + "Registration successful!" popup + logout.
     * Negative rows  → expects error message in mat-error or snack-bar.
     */
    @Test(
        priority = 1,
        description = "SIGNUP: Verify signup functionality for all scenarios (positive + negative)",
        dataProvider = "signUpData",
        dataProviderClass = TestDataProviders.class
    )
    public void signUp(String testCaseID, String userName, String email,
                       String password, String role,
                       String expectedResult, String expectedMessage) {

        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));
        logger.info("===== [{}] START: Signup test =====", testCaseID);

        // ── Uniquify credentials for success-path rows to avoid conflicts ──
        String uniqueID = String.valueOf(System.currentTimeMillis());

        if (expectedResult.equalsIgnoreCase("Success")) {
            userName = userName + uniqueID;
            if (email != null && email.contains("@")) {
                email = email.replace("@", "+" + uniqueID + "@");
            }
        } else {
            // Negative: duplicate-email scenario — use a fresh username so username
            // uniqueness doesn't accidentally block the duplicate-email assertion.
            if (expectedMessage.contains("Email already registered")) {
                userName = "AutoUser" + uniqueID;
            }
            // Negative: duplicate-username scenario — use a fresh email so email
            // uniqueness doesn't block the duplicate-username assertion.
            if (expectedMessage.contains("Username already exists")) {
                if (email != null && email.contains("@")) {
                    email = email.replace("@", "+" + uniqueID + "@");
                }
            }
        }

        SignUpPage   signUpPage   = new SignUpPage(driver);
        LoginPage    loginPage    = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        // Navigate to signup
        logger.info("[{}] Clicking Sign Up link", testCaseID);
        loginPage.clickSignupBtn();
        wait.until(ExpectedConditions.urlContains("signup"));

        // Fill the form (empty strings are deliberately sent for negative empty-field tests)
        if (userName != null) {
            logger.info("[{}] Setting username: {}", testCaseID, userName);
            signUpPage.setUserName(userName);
        }

        if (email != null) {
            logger.info("[{}] Setting email: {}", testCaseID, email);
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

        logger.info("[{}] Clicking Create Account button", testCaseID);
        signUpPage.clickCreateAccountBtn();

        // ── Assert based on expected outcome ──
        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            String dashboardUrl = driver.getCurrentUrl();
            Assert.assertTrue(dashboardUrl.contains("dashboard"),
                "[" + testCaseID + "] Expected dashboard URL but got: " + dashboardUrl);
            logger.info("[{}] Navigated to dashboard: {}", testCaseID, dashboardUrl);

            String popupMsg = wait.until(
                ExpectedConditions.visibilityOf(dashboardPage.getRegistrationSuccessfulPopUp())
            ).getText().trim();
            Assert.assertTrue(popupMsg.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: " + popupMsg);
            logger.info("[{}] Signup success popup: {}", testCaseID, popupMsg);

            // Logout after successful signup to return to clean state
            dashboardPage.clickMenuBtn();
            dashboardPage.clickLogout();
            wait.until(ExpectedConditions.urlContains("login"));
            logger.info("[{}] Logged out after successful signup", testCaseID);

        } else {
            // Negative — collect error from mat-error OR snack-bar
            String errorMessage = signUpPage.getErrorMessage();
            logger.info("[{}] Captured error message: {}", testCaseID, errorMessage);
            Assert.assertTrue(
                errorMessage.contains(expectedMessage),
                "[" + testCaseID + "] Expected error '" + expectedMessage + "' but got: '" + errorMessage + "'"
            );
        }

        logger.info("===== [{}] END: Signup test — PASSED =====", testCaseID);
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // LOGIN TESTS  (UM_TC_09 → UM_TC_14)  —  data-driven via Excel
    // ══════════════════════════════════════════════════════════════════════════════

    /**
     * TC_GROUP: LOGIN
     * Reads test rows from "Login_Data" sheet in TestData.xlsx.
     * Columns (0-indexed):
     *   0=testCaseID | 1=email | 2=password | 3=expectedResult | 4=expectedMessage
     *
     * Positive rows  → expects dashboard navigation + "Login successful!" popup + logout.
     * Negative rows  → expects error in mat-error or snack-bar.
     */
    @Test(
        priority = 2,
        description = "LOGIN: Verify login functionality for all scenarios (positive + negative)",
        dataProvider = "loginData",
        dataProviderClass = TestDataProviders.class
    )
    public void login(String testCaseID, String email, String password,
                      String expectedResult, String expectedMessage) {

        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));
        logger.info("===== [{}] START: Login test =====", testCaseID);

        LoginPage     loginPage     = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        // Enter credentials (may be empty for negative empty-field tests)
        if (email != null) {
            logger.info("[{}] Entering email: {}", testCaseID, email);
            loginPage.setUserEmail(email);
        }

        if (password != null) {
            logger.info("[{}] Entering password", testCaseID);
            loginPage.setUserPasswordInput(password);
        }

        logger.info("[{}] Clicking Sign In button", testCaseID);
        loginPage.clickLoginBtn();

        // ── Assert based on expected outcome ──
        if (expectedResult.equalsIgnoreCase("Success")) {
            wait.until(ExpectedConditions.urlContains("dashboard"));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("dashboard"),
                "[" + testCaseID + "] Expected dashboard URL but got: " + currentUrl);
            logger.info("[{}] Navigated to dashboard: {}", testCaseID, currentUrl);

            String popupMsg = wait.until(
                ExpectedConditions.visibilityOf(dashboardPage.getLoginSuccessfulPopUp())
            ).getText().trim();
            Assert.assertTrue(popupMsg.contains(expectedMessage),
                "[" + testCaseID + "] Expected popup '" + expectedMessage + "' but got: " + popupMsg);
            logger.info("[{}] Login success popup: {}", testCaseID, popupMsg);

            // Logout to restore clean state
            dashboardPage.clickMenuBtn();
            dashboardPage.clickLogout();
            wait.until(ExpectedConditions.urlContains("login"));
            logger.info("[{}] Logged out after successful login", testCaseID);

        } else {
            String errorMessage = loginPage.getErrorMessage();
            logger.info("[{}] Captured error message: {}", testCaseID, errorMessage);
            Assert.assertTrue(
                errorMessage.contains(expectedMessage),
                "[" + testCaseID + "] Expected error '" + expectedMessage + "' but got: '" + errorMessage + "'"
            );
        }

        logger.info("===== [{}] END: Login test — PASSED =====", testCaseID);
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // LOGOUT TEST  (UM_TC_15)  —  inline (uses a known account)
    // ══════════════════════════════════════════════════════════════════════════════

    /**
     * UM_TC_15: Verify that clicking Logout from an authenticated session
     * correctly redirects the user back to the /login page.
     *
     * Uses the seeded brand account (brand@gmail.com / brand@123) — the same
     * account used throughout Campaign Management tests, so it is guaranteed to exist.
     */
    @Test(
        priority = 3,
        description = "UM_TC_15: Verify Logout redirects user back to the login page"
    )
    public void verifyLogoutRedirectsToLogin() {
        logger.info("===== [UM_TC_15] START: Logout redirect test =====");

        driver.get("https://sponsorship-front.netlify.app/login");
        wait.until(ExpectedConditions.urlContains("login"));

        LoginPage     loginPage     = new LoginPage(driver);
        DashboardPage dashboardPage = new DashboardPage(driver);

        // ── Step 1: Login with known brand account ──
        logger.info("[UM_TC_15] Logging in with brand credentials");
        loginPage.setUserEmail("brand@gmail.com");
        loginPage.setUserPasswordInput("brand@123");
        loginPage.clickLoginBtn();

        wait.until(ExpectedConditions.urlContains("dashboard"));
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"),
            "[UM_TC_15] Login failed — did not reach dashboard");

        // ── Step 2: Open menu and click Logout ──
        logger.info("[UM_TC_15] Opening user menu");
        dashboardPage.clickMenuBtn();

        logger.info("[UM_TC_15] Clicking Logout");
        dashboardPage.clickLogout();

        // ── Step 3: Assert redirect to /login ──
        wait.until(ExpectedConditions.urlContains("login"));
        String finalUrl = driver.getCurrentUrl();
        Assert.assertTrue(finalUrl.contains("login"),
            "[UM_TC_15] Expected redirect to /login after logout but got: " + finalUrl);

        logger.info("[UM_TC_15] Successfully redirected to: {}", finalUrl);
        logger.info("===== [UM_TC_15] END: Logout redirect test — PASSED =====");
    }
}