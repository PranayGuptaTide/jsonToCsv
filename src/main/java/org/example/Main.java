package org.example;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
         String csvFilePath = "file.csv";

        String url = "https://api.tideplatform.in/api/v4/hackathon/registrations/applications";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("api-key","a0cbcce0-9349-438f-ac63-5a21daa6253b")
                .build();


        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error in fetching data from API", e);
        }


        try {

            JSONObject jsonData = new JSONObject(httpResponse.body());
            JSONArray applications = jsonData.getJSONObject("data").getJSONArray("applications");
            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeNext(new String[] { "Name", "Email", "Contact Number", "Topic","Details","linkedinUrl","githubUrl","Team Size","Experience","Date of application" });
            List<String[]> rows = new ArrayList<>();
            for (int i = 0; i < applications.length(); i++) {

                JSONObject application = applications.getJSONObject(i);

                writer.writeNext(new String[] {
                        application.getString("name"),
                        application.getString("email"),
                        application.getString("contactNo"),
                        application.getString("topic"),
                        application.getString("details"),
                        application.getString("linkedinUrl"),
                        application.getString("githubUrl"),
                        String.valueOf(application.getInt("teamSize")),
                        String.valueOf(application.getString("experience"))
                                .replace("ZERO_TO_ONE", "0-1")
                                .replace("ONE_TO_FIVE", "1-5")
                                .replace("FIVE_PLUS", "5+"),
                        LocalDate.parse(application.getString("createdOn").substring(0, 10)).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                });

            }

            writer.close();
            System.out.println("Data has been fetched and dumped to a CSV file at " + new Date());

        } catch ( IOException e) {
            throw new RuntimeException("Error converting JSON to CSV", e);
        }
    }
}
