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

public class Spark {
    private static final String API_Password = "OyvFcmgBUONPxaUcwpOd:dmckcJLjUDcGMTzAddVB";
    private static final String Spark_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";

    // 发送到HTTP Spark
    public String request(String userMessage) throws IOException, JSONException {
        // 构建请求头
        URL url = new URL(Spark_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Bearer " + API_Password);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setDoOutput(true);

        // 构建请求体
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

        // 将请求体写入输出流
        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 处理响应
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("请求成功");

            // 读取响应并提取内容
            try (InputStream is = urlConnection.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // 解析 JSON 响应并提取 "content" 字段
                JSONObject jsonResponse = new JSONObject(response.toString());
                String content = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return content;
            }
        } else {
            System.out.println("请求失败，响应码：" + responseCode);
            return null;
        }
    }
}
