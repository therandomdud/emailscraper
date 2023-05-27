package de.therandomdud;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.v85.layertree.model.Layer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class EmailScraper {
    private String iservURL;
    private String username;
    private String password;

    private WebDriver driver = new ChromeDriver();
    public EmailScraper(String iservURL, String username, String password) {
        this.iservURL = iservURL;
        this.username = username;
        this.password = password;
    }

    public void login(){
        driver.get(iservURL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));

        WebElement username = driver.findElement(By.cssSelector("input[name='_username']"));
        WebElement password = driver.findElement(By.cssSelector("input[name='_password']"));
        WebElement button = driver.findElement(By.cssSelector("button"));

        username.clear();
        username.sendKeys(this.username);

        password.clear();
        password.sendKeys(this.password);

        button.click();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    private ArrayList<Integer> findIds() {
        List<WebElement> emailElements = driver.findElements(By.cssSelector("tr[data-message-id]"));
        ArrayList<Integer> emailIDs = new ArrayList<>();

        for (int i = 0; i < emailElements.size();i++) {
            WebElement current = emailElements.get(i);
            emailIDs.add(Integer.valueOf(current.getAttribute("data-message-id")));
        }

        return emailIDs;
    }

    public ArrayList<Email> scrapeInboxMails() {
        driver.get(iservURL + "/iserv/mail?path=INBOX");
        
        ArrayList<Integer> emailIDs = findIds();
        for (int i = 0; i < emailIDs.size(); i++) {
            driver.get(iservURL + "/iserv/mail?path=INBOX&msg=" + emailIDs.get(i));


            WebElement senderElement = driver.findElement(By.id("message-from")).findElement(By.cssSelector("span[data-addr]"));
            String sender = senderElement.getAttribute("data-addr");

            WebElement recipientElement = driver.findElement(By.id("message-to")).findElement(By.cssSelector("span[data-addr]"));
            String recipient = recipientElement.getAttribute("data-addr");

            WebElement subjectElement = driver.findElement(By.className("message-subject")).findElement(By.cssSelector("span"));
            String subject = subjectElement.getText();

            WebElement dateElement = driver.findElement(By.className("message-date"));
            String date = dateElement.getText();

            WebElement contentElement = driver.findElement(By.className("content-plain"));
            System.out.println(contentElement.getText());
        }

        return null;
    }

    public ArrayList<Email> scrapeSendMails() {


        return null;
    }
}