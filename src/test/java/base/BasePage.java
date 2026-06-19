package base;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

public class BasePage {
    public WebDriver driver;
    public Wait<WebDriver> wait;
    public BasePage(WebDriver driver){
        this.driver=driver;
        wait=new FluentWait<>(driver).withTimeout(Duration.ofSeconds(15)).pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchElementException.class);
        PageFactory.initElements(driver,this);
    }
}
