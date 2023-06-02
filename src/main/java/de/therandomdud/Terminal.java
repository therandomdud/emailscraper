package de.therandomdud;

import java.util.Scanner;

public class Terminal {
    public static void terminal() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("iservURL: ");
        String iservURL = scanner.next();

        System.out.println("username: ");
        String username = scanner.next();

        System.out.println("password: ");
        String password = scanner.next();

        EmailScraper scraper = new EmailScraper(iservURL,username,password);

        System.out.println("1. for scraping receive mails");
        System.out.println("2. for scraping send mails");
        System.out.println("3. for scraping receive and send mails \n");
        int chooser;

        do {
            chooser = scanner.nextInt();
        }while (chooser < 1 || chooser > 2);

        scraper.login();

        switch (chooser) {
            case 1:
                JSONUtils.saveEmails(scraper.scrapeInboxMails());
                break;
            case 2:
                JSONUtils.saveEmails(scraper.scrapeSendMails());
                break;

        }
    }
}
