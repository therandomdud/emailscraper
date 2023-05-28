package de.therandomdud;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EmailScraper scraper = new EmailScraper("test","test","test");
        scraper.login();
        ArrayList<Email> emailsReceive = scraper.scrapeInboxMails();
        System.out.println(emailsReceive);
        saveEmails(emailsReceive);
    }
    public static void saveEmails(ArrayList<Email> emails) {
        Gson gson = new Gson();
        String jsonObject = gson.toJson(emails);
        try {
            FileWriter file = new FileWriter("src/main/resources/emails.json");
            System.out.println("fuck you");
            file.write(jsonObject);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}