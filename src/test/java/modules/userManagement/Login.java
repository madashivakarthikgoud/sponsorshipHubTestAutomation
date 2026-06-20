package modules.userManagement;

import base.BaseTest;
import org.testng.annotations.Test;
import pages.LoginPage;

public class Login extends BaseTest {
    static String email = "Test1@gmail.com";
    static String password = "Test@1";
    @Test(description = "TC002 - Verify user login functionality")
    public void Login(){
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setUserEmail(email);
        loginPage.setUserPasswordInput(password);
        loginPage.clickLoginBtn();
    }
}
