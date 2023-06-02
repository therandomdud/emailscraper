package de.therandomdud;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class JSONUtils {
    public static void saveEmails(ArrayList<Email> emails, String fileName) {
        Gson gson = new Gson();
        String jsonObject = gson.toJson(emails);
        try {
            FileWriter file = new FileWriter("src/main/resources/" + fileName);
            file.write(jsonObject);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveEmails(ArrayList<Email> emails) {
        saveEmails(emails, "emails.json");
    }
}
