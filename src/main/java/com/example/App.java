package com.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        try {
            String urlString = "https://api.cuvora.com/car/partner/cricket-data";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apiKey", "test-creds@2320");

            int statusCode = conn.getResponseCode();
            if (statusCode == 200) { 
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("data")) {
                    JSONArray matches = jsonResponse.getJSONArray("data");

                    int highestScore = 0;
                    String highestScoringTeam = "";
                    int count300Plus = 0;

                    for (int i = 0; i < matches.length(); i++) {
                        JSONObject match = matches.getJSONObject(i);

                        String t1sStr = match.optString("t1s", "0/0 (0)");
                        String t2sStr = match.optString("t2s", "0/0 (0)");

                        int t1Score = Integer.parseInt(t1sStr.split("/")[0].trim());
                        int t2Score = Integer.parseInt(t2sStr.split("/")[0].trim());

                        int maxScore = Math.max(t1Score, t2Score);
                        if (maxScore > highestScore) {
                            highestScore = maxScore;
                            highestScoringTeam = maxScore == t1Score ? match.optString("t1") : match.optString("t2");
                        }

                        if ((t1Score + t2Score) > 300) {
                            count300Plus++;
                        }
                    }
                    
                    System.out.println("Highest Score: " + highestScore + " and Team Name is: " + highestScoringTeam);
                    System.out.println("Number Of Matches with total 300 Plus Score: " + count300Plus);
                } else {
                    System.out.println("The 'data' key was not found in the API response.");
                }
            } else {
                System.out.println("Error: API request failed with status code " + statusCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
