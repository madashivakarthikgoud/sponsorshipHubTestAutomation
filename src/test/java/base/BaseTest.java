package base;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.BeforeTest;

import java.time.Duration;

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
}
