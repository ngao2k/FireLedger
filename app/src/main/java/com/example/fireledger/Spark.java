package com.example.fireledger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * This class provides functionality to send messages to a specified HTTP service
 * and handle the JSON responses.
 */
public class Spark {
    /**
     * The API password used for authentication.
     */
    private static final String API_Password = "OyvFcmgBUONPxaUcwpOd:dmckcJLjUDcGMTzAddVB";

    /**
     * The URL of the Spark API endpoint.
     */
    private static final String Spark_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";

    /**
     * Sends a request to the Spark API with the given user message.
     *
     * @param userMessage The message to be sent to the Spark API.
     * @return The content field from the JSON response if the request is successful, otherwise null.
     * @throws IOException If an I/O error occurs while sending or receiving data.
     * @throws JSONException If there is an error parsing the JSON response.
     */
    public String request(String userMessage) throws IOException, JSONException {
        // Create URL object and open connection
        URL url = new URL(Spark_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST"); // Set request method to POST
        urlConnection.setRequestProperty("Authorization", "Bearer " + API_Password); // Set authorization header
        urlConnection.setRequestProperty("Content-Type", "application/json"); // Set content type to JSON
        urlConnection.setDoOutput(true); // Allow output

        // Build request body
        String jsonInputString = "{\n"
                + "    \"model\": \"lite\",\n"
                + "    \"messages\": [\n"
                + "        {\n"
                + "            \"role\": \"user\",\n"
                + "            \"content\": \"" + userMessage + "\"\n"
                + "        }\n"
                + "    ],\n"
                + "    \"stream\": false\n"
                + "}";

        // Write request body to output stream
        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Handle response
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Request successful");

            // Read response and extract content
            try (InputStream is = urlConnection.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse JSON response and extract "content" field
                JSONObject jsonResponse = new JSONObject(response.toString());
                String content = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return content;
            }
        } else {
            System.out.println("Request failed, response code: " + responseCode);
            return null;
        }
    }
}