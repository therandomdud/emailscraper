package de.therandomdud;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EmailScraper implements AutoCloseable {

    private static final String MAIL_PAGE_ID = "mail-page";
    private static final String MESSAGE_FROM_ID = "message-from";
    private static final String MESSAGE_TO_ID = "message-to";

    private String iservURL;
    private String username;
    private String password;
    private WebDriver driver;

    public EmailScraper(String iservURL, String username, String password) {
        this.iservURL = iservURL.endsWith("/") ? iservURL.substring(0, iservURL.length() - 1) : iservURL;
        this.username = username;
        this.password = password;
        this.driver = new ChromeDriver();
    }

    public void login() {
        driver.get(iservURL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        WebElement usernameInput = driver.findElement(By.cssSelector("input[name='_username']"));
        WebElement passwordInput = driver.findElement(By.cssSelector("input[name='_password']"));
        WebElement loginButton = driver.findElement(By.cssSelector("button"));

        usernameInput.clear();
        usernameInput.sendKeys(this.username);

        passwordInput.clear();
        passwordInput.sendKeys(this.password);

        loginButton.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private List<Integer> findEmailIDs(String mailPath) {
        List<Integer> emailIDs = new ArrayList<>();
        List<WebElement> options = driver.findElement(By.id(MAIL_PAGE_ID)).findElements(By.cssSelector("option"));

        for (WebElement option : options) {
            emailIDs.add(Integer.parseInt(option.getAttribute("value")));
        }
        emailIDs.remove(0);

        for (Integer siteID : emailIDs) {
            driver.get(iservURL + "/iserv/mail?path=" + mailPath + "&start=" + siteID);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

            List<WebElement> emailElements = driver.findElements(By.cssSelector("tr[data-message-id]"));
            for (WebElement emailElement : emailElements) {
                emailIDs.add(Integer.parseInt(emailElement.getAttribute("data-message-id")));
            }
        }
        return emailIDs;
    }

    private Email scrapeMail(String emailUrl) {
        driver.get(emailUrl);

        String sender = getElementText("message-from", "span[data-addr]");
        String recipient = getElementText("message-to", "span[data-addr]");
        String subject = getElementText("message-subject", "span");
        String date = getElementText("message-date");
        String content = getElementText("content-plain");

        return new Email(sender, recipient, subject, date, content);
    }

    private String getElementText(String elementId, String cssSelector) {
        try {
            WebElement element = driver.findElement(By.id(elementId)).findElement(By.cssSelector(cssSelector));
            return element.getAttribute("data-addr");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String getElementText(String elementClassName) {
        try {
            WebElement element = driver.findElement(By.className(elementClassName));
            return element.getText();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public List<Email> scrapeMails(String mailPath) {
        driver.get(iservURL + "/iserv/mail?path=" + mailPath);
        List<Integer> emailIDs = findEmailIDs(mailPath);
        List<Email> emails = new ArrayList<>();

        for (Integer emailID : emailIDs) {
            String url = iservURL + "/iserv/mail?path=" + mailPath + "&msg=" + emailID;
            emails.add(scrapeMail(url));
        }

        return emails;
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
