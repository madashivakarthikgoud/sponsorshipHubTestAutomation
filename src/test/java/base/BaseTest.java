package base;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.BeforeTest;

import java.time.Duration;
import java.util.Random;

public class BaseTest{
    public WebDriver driver;
    public Wait<WebDriver> wait;
    @BeforeTest
    public void initializer(){
        driver = new ChromeDriver();
        wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(30)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
        driver.get("https://sponsorship-front.netlify.app/login");
        driver.manage().window().maximize();
    }

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
