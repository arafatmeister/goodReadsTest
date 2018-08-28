package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TempMail extends ParentPage{

    public String tempMail = null;
    public String validationUrl = null;
    WebDriverWait wait = new WebDriverWait(webDriver,15);

    @FindBy(id = ("mail"))
    private WebElement tempMailInput;

    @FindBy(xpath = ".//a[contains(text(), 'Goodreads - Verify')]")
    private WebElement subjectTextLink;

    @FindBy(xpath = ".//a[contains(text(),'Verify Email')]")
    private WebElement verificationLink;

    public TempMail(WebDriver webDriver) {
        super(webDriver);
    }

    public String getTempMail(){
        return tempMailInput.getAttribute("value");
    }

    public void clickOnMailWithSubject(){
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(".//a[contains(text(), 'Goodreads - Verify')]")));
        actionWithOurElements.clickOnElement(subjectTextLink);
        logger.info("Mail with particular subject was clicked");
    }

    public void clickOnValidationLink(){
        actionWithOurElements.clickOnElement(verificationLink);
    }

    public void openTempMail(){
        String tempMailUrl = "https://temp-mail.org";
        try {
            webDriver.get(tempMailUrl);
            logger.info("Temp Mail was opened");
        } catch (Exception e) {
            logger.error("Can't open page");
        }

    }

    public String getValidationUrl() {
        String vl = verificationLink.getAttribute("href");
        logger.info("Validation link is: " + vl);
        return vl;
    }

    public String getValidationUrlFromTempMail(){
       openTempMail();
       clickOnMailWithSubject();
       validationUrl = getValidationUrl();
       logger.info("Validation URL is: "+validationUrl);
       return validationUrl;
    }

    public String getEmailFromTempMail(){
        openTempMail();
        wait.until(ExpectedConditions.attributeToBeNotEmpty(tempMailInput,"value"));
        tempMail = getTempMail();
        logger.info("Temp mail is: "+tempMail);
        return tempMail;
    }

    public void clickVerifyEmailButtonFromTempMail(){
        openTempMail();
        clickOnMailWithSubject();
        verificationLink.click();
    }


}
