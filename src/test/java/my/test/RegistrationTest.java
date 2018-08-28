package my.test;

import libs.ActionWithOurElements;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.TempMail;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class RegistrationTest {

    WebDriver webDriver;
    Logger logger =Logger.getLogger(getClass());
    Date date = new Date();
    Random random = new Random();
    WebDriverWait wait;
    Actions action;

    public TempMail tempMail;


    String userName = "Kristopher"+random.nextInt();
    String email;
    String password = "Test123456";
    String url = "https://www.goodreads.com";
    String searchWord = "Best crime and mystery books";


    @Before
    public void setUp(){
        File file = new File("./src/drivers/chromedriver");
        System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
        webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        wait = new WebDriverWait(webDriver, 15);
        tempMail = new TempMail(webDriver);
        action = new Actions(webDriver);

    }

    @Test
    public void userRegistrationWithValidCredentials() {
        webDriver.manage().window().maximize();

        //Get Temporary email from TempMail site
      email = tempMail.getEmailFromTempMail();

        //Register new user
        webDriver.get(url);
        logger.info(url +" was opened");

        webDriver.findElement(By.id("user_first_name")).clear();
        webDriver.findElement(By.id("user_first_name")).sendKeys(userName);
        logger.info(userName+ " was entered to UserName input");

        webDriver.findElement(By.id("user_email")).clear();
        webDriver.findElement(By.id("user_email")).sendKeys(email);
        logger.info(email+ " was entered to Email input");


        webDriver.findElement(By.id("user_password_signup")).clear();
        webDriver.findElement(By.id("user_password_signup")).sendKeys(password);
        logger.info(password+ " was entered to Password input");

        webDriver.findElement(By.xpath(".//*[@id='userSignupForm']/div[4]/input[2]")).click();
        logger.info("Credentials was submited --> wait for validation mail");


        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//a[contains(text(),'Skip')]")));
        webDriver.findElement(By.xpath(".//a[contains(text(),'Skip')]")).click();
        logger.info("First registration page quiz was skipped");

        webDriver.findElement(By.xpath(".//a[contains(text(),'skip')]")).click();
        logger.info("Second registration page quiz was skipped");

        webDriver.findElement(By.xpath(".//*[@id='favorites_Art']")).click();
        action.moveToElement(webDriver.findElement(By.xpath(".//input[@type='submit']")));
        webDriver.findElement(By.xpath(".//input[@type='submit']")).click();
        logger.info("Favourites was selected and submitted");

        webDriver.findElement(By.xpath(".//a[contains(text(),'finished')]")).click();
        logger.info("Registration was finished");

        //Click verify email button
        webDriver.manage().deleteAllCookies();
        tempMail.clickVerifyEmailButtonFromTempMail();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        //Get to BookStore tab -->
        for(String winHandle : webDriver.getWindowHandles()){
            webDriver.switchTo().window(winHandle);
            logger.info("Focus switched to new Tab");
        }

        //Login with invalid credentials
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("user_email")));
        webDriver.findElement(By.id("user_email")).clear();
        webDriver.findElement(By.id("user_email")).sendKeys("wrongMail"+email);
        webDriver.findElement(By.id("user_password")).sendKeys("wrong"+password);
        webDriver.findElement(By.xpath(".//*[@type='submit']")).click();

        //Validating of error presence
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@class='flash error']")));
        logger.info("Error message is present on page");

        //Login with valid credentials
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("user_email")));
        webDriver.findElement(By.id("user_email")).clear();
        webDriver.findElement(By.id("user_email")).sendKeys(email);
        webDriver.findElement(By.id("user_password")).sendKeys(password);
        webDriver.findElement(By.xpath(".//*[@type='submit']")).click();

        //Validating login
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//div[@class='siteHeader__personal']")));
        logger.info("Login was successful");

//        //TempLogin
//        webDriver.get("https://www.goodreads.com/user/sign_in");
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("user_email")));
//        webDriver.findElement(By.id("user_email")).clear();
//        webDriver.findElement(By.id("user_email")).sendKeys("jovova@hurify1.com");
//        webDriver.findElement(By.id("user_password")).sendKeys(password);
//        webDriver.findElement(By.xpath(".//*[@type='submit']")).click();

        //Search for “Best crime and mystery books”
        webDriver.findElement(By.xpath(".//div[@class='siteHeader__contents']/div/form/input")).sendKeys(searchWord);
        webDriver.findElement(By.xpath(".//div[@class='siteHeader__contents']/div/form/button")).click();
        List<WebElement> searchResults = webDriver.findElements(By.xpath(".//table/tbody/tr"));
        logger.info("Total search results on page: "+searchResults.size());



        //Mark top 3 books as “Want to read”
        for (int x = 0; x < 3; x++) {
            searchResults.get(x).findElement(By.xpath("./td/div/div[1]")).click();
            logger.info("Element "+x+ " was clicked");
        }


        //Mark as read
        for (int x = 0; x < 3; x++) {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//td[3]/div/div[2]/button")));
            searchResults.get(x).findElement(By.xpath(".//td[3]/div/div[2]/button")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[1]/li[1]/button")));
            webDriver.findElement(By.xpath("//ul[1]/li[1]/button")).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//*[@class='reviewLightBox']")));

            //Rate and leave feedback for them
            List<WebElement> stars = webDriver.findElements(By.xpath(".//div[@id='box']//div/div/div/a[@class='star off']"));
            stars.get(random.nextInt(4)).click();
            logger.info("Book was rated");

        webDriver.findElement(By.id("review_review_usertext")).sendKeys("My best review ever )))) ");
        logger.info("Review was entered");

            webDriver.findElement(By.id("readingSessionAddLink")).click(); //Adding different dates
            waitABit();
            logger.info("Reading date was added");

            List<WebElement> dateStrings = webDriver.findElements(By.xpath(".//table[@id='rereadingTable']/tbody/tr[contains(@id,'readingSessionEntry')]"));
            logger.info("Count of dates strings: "+dateStrings.size());

            for (int i = 0; i < dateStrings.size(); i++) {
                WebElement temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker smallPicker startYear']"));
                Select startYearSelect = new Select(temp);
                startYearSelect.selectByVisibleText("2016");
                logger.info("Start year was entered");

                temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker largePicker startMonth']"));
                Select startMounthSelect = new Select(temp);
                startMounthSelect.selectByValue(String.valueOf(random.nextInt(5)+1));
                logger.info("Start month was entered");


                temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker smallPicker startDay']"));
                Select startDaySelect = new Select(temp);
                startDaySelect.selectByVisibleText(String.valueOf(random.nextInt(28)));
                logger.info("Start date was entered");


                //End dates

                temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker smallPicker endYear']"));
                Select endYearSelect = new Select(temp);
                endYearSelect.selectByVisibleText("2017");
                logger.info("End year was entered");

                temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker largePicker endMonth']"));
                Select endMounthSelect = new Select(temp);
                endMounthSelect.selectByValue(String.valueOf(random.nextInt(11)+1));
                logger.info("End month was entered");

                temp = dateStrings.get(i).findElement(By.xpath(".//select[@class='rereadDatePicker smallPicker endDay']"));
                Select endDaySelect = new Select(temp);
                endDaySelect.selectByVisibleText(String.valueOf(random.nextInt(28)));
                logger.info("End date was entered");
            }
            webDriver.findElement(By.xpath(".//*[@value='Save']")).click();
            logger.info("Button Save was clicked");
            waitABit();
        }


        //Logout
        webDriver.findElement(By.xpath(".//*[contains(@class, 'dropdown__trigger--profileMenu')]")).click();
        webDriver.findElement(By.xpath(".//*[contains(text(),'Sign out')]")).click();
        logger.info("Logging out");
    }

    private void waitABit() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }
}
