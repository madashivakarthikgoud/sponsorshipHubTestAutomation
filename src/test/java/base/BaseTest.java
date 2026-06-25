package base;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Parameters;

import java.time.Duration;
import java.util.Random;

public class BaseTest{
    public WebDriver driver;
    public Wait<WebDriver> wait;
    @BeforeClass
    @Parameters("browser")
    public void initializeDriver(String browser){
        if(browser.equals("chrome")){
            org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
//            options.addArguments("--headless=new");
            options.addArguments("--disable-features=PasswordLeakDetection,PasswordLeakToggleMove");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-default-apps");
            options.addArguments("--disable-extensions");
            java.util.Map<String, Object> prefs = new java.util.HashMap<>();
            prefs.put("profile.password_manager_enabled", false);
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_leak_detection", false);
            prefs.put("profile.password_manager.leak_detection", false);
            prefs.put("password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);
            driver = new org.openqa.selenium.chrome.ChromeDriver(options);
        }else if(browser.equals("edge")){
            driver = new EdgeDriver();
        }
        wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
        driver.manage().deleteAllCookies();
        driver.get("https://sponsorship-front.netlify.app/login");
        driver.manage().window().maximize();
    }
//
//    @AfterClass
//    public void closeDriver(){
//        if(driver!=null){
//            driver.quit();
//        }
//    }

    public String randomUserName(){
        return RandomStringUtils.randomAlphabetic(5);
    }
    public String randomUserEmail(){
        return (RandomStringUtils.randomAlphabetic(5) + "@gmail.com");
    }
    public String randomUserPassword(){
        return (RandomStringUtils.randomAlphabetic(4)+"@"+RandomStringUtils.randomNumeric(5));
    }
    public String randomUserRole(){
        String[] roles = {"Brand","Influencer"};
        return (roles[new Random().nextInt(roles.length)]);
    }
}
