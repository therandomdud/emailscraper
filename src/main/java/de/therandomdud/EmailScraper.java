package de.therandomdud;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
public class EmailScraper {

    public EmailScraper(String iservURL, String username, String password) {

        if (iservURL.charAt(iservURL.length() - 1) == '/') {
            this.iservURL = iservURL.substring(0,iservURL.length() - 1);
        }else {
            this.iservURL = iservURL;
        }

        this.username = username;
        this.password = password;
    }
    private String iservURL;
    private String username;

    private String password;

    private WebDriver driver = new ChromeDriver();

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

    private ArrayList<Integer> findInboxIDs() {
        ArrayList<Integer> emailIDs = new ArrayList<>();
        ArrayList<Integer> inboxSiteIDs = new ArrayList<>();
        List<WebElement> options = driver.findElement(By.id("mail-page")).findElements(By.cssSelector("option"));

        for (int i = 0; i < options.size(); i++) {
            WebElement current = options.get(i);
            inboxSiteIDs.add(Integer.valueOf(current.getAttribute("value")));
        }
        inboxSiteIDs.remove(0);

        for (int i = 0; i < inboxSiteIDs.size(); i++) {
            driver.get(iservURL + "/iserv/mail?path=INBOX&start=" + inboxSiteIDs.get(i));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

            List<WebElement> emailElements = driver.findElements(By.cssSelector("tr[data-message-id]"));
            for (int j = 0; j < emailElements.size();j++) {
                WebElement current = emailElements.get(j);
                emailIDs.add(Integer.valueOf(current.getAttribute("data-message-id")));
            }
        }
        return emailIDs;
    }
    private ArrayList<Integer> findSendIDs() {
        ArrayList<Integer> emailIDS = new ArrayList<>();
        ArrayList<Integer> sendSiteIDs = new ArrayList<>();
        List<WebElement> options = driver.findElement(By.id("mail-page")).findElements(By.cssSelector("option"));

        System.out.println(options.size());

        for (int i = 0; i < options.size(); i++) {
            WebElement current = options.get(i);
            sendSiteIDs.add(Integer.valueOf(current.getAttribute("value")));
        }
        sendSiteIDs.remove(0);

        for (int i = 0; i < sendSiteIDs.size(); i++) {
            driver.get(iservURL + "/iserv/mail?path=INBOX%252FSent&start=" + sendSiteIDs.get(i));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            List<WebElement> emailElements = driver.findElements(By.cssSelector("tr[data-message-id]"));

            for (int j = 0; j < emailElements.size(); j++) {
                WebElement current = emailElements.get(j);
                emailIDS.add(Integer.valueOf(current.getAttribute("data-message-id")));
            }

        }

        return emailIDS;
    }

    private Email scrapeMail(String emailUrl) {
        driver.get(emailUrl);

        String sender = "";
        String recipient = "";
        String subject = "";
        String date = "";
        String content = "";

        try {
            WebElement senderElement = driver.findElement(By.id("message-from")).findElement(By.cssSelector("span[data-addr]"));
            sender = senderElement.getAttribute("data-addr");
        }catch (Exception e){
            sender = "";
        }

        try {
            WebElement recipientElement = driver.findElement(By.id("message-to")).findElement(By.cssSelector("span[data-addr]"));
            recipient = recipientElement.getAttribute("data-addr");

        }catch (Exception e){
            recipient = "";
        }

        try {
            WebElement subjectElement = driver.findElement(By.className("message-subject")).findElement(By.cssSelector("span"));
            subject = subjectElement.getText();
        }catch (Exception e){
            subject = "";
        }

        try {
            WebElement dateElement = driver.findElement(By.className("message-date"));
            date = dateElement.getText();
        }catch (Exception e) {
            date = "";
        }


        try{
            WebElement contentElement = driver.findElement(By.className("content-plain"));
            content = contentElement.getText();
        }catch (Exception ignored) {
            content = "";
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        try {
            Email email = new Email(sender,recipient,subject,date,content);
            return email;
        }catch (Exception Ig){
            return null;
        }


    }

    public ArrayList<Email> scrapeInboxMails() {
        driver.get(iservURL + "/iserv/mail?path=INBOX");
        ArrayList<Integer> emailIDs = findInboxIDs();
        ArrayList<Email> emails = new ArrayList<>();


        for (int i = 0; i < emailIDs.size(); i++) {
            String url = iservURL + "/iserv/mail?path=INBOX&msg=" + emailIDs.get(i);
            emails.add(scrapeMail(url));
        }

        return emails;
    }

    public ArrayList<Email> scrapeSendMails() {
        driver.get(iservURL + "/iserv/mail?path=INBOX%252FSent");
        ArrayList<Integer> sendIDs = findSendIDs();
        ArrayList<Email> emails = new ArrayList<>();

        for (int i = 0; i < sendIDs.size(); i++) {
            String url = iservURL + "/iserv/mail?path=INBOX%2FSent&msg=" + sendIDs.get(i);
            emails.add(scrapeMail(url));
        }

        return emails;
    }
}