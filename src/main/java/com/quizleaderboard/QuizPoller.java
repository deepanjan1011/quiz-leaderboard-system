package com.quizleaderboard;

import com.google.gson.Gson;
import com.quizleaderboard.model.PollResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class QuizPoller {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String REG_NO = "RA2311026020010";
    private static final int TOTAL_POLLS = 10;
    private static final int DELAY_MS = 5000;

    private final Gson gson = new Gson();

    public List<PollResponse> pollAll() {
        List<PollResponse> responses = new ArrayList<>();

        for (int i = 0; i < TOTAL_POLLS; i++) {
            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + i;

            try {
                String body = get(url);
                PollResponse response = gson.fromJson(body, PollResponse.class);
                int eventCount = response.getEvents() != null ? response.getEvents().size() : 0;
                System.out.println("Polling [" + (i + 1) + "/" + TOTAL_POLLS + "]... got " + eventCount + " events");
                responses.add(response);
            } catch (IOException e) {
                throw new RuntimeException("Poll " + i + " failed: " + e.getMessage(), e);
            }

            // mandatory 5s delay between polls (except after the last one)
            if (i < TOTAL_POLLS - 1) {
                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Polling interrupted at poll " + i, e);
                }
            }
        }

        return responses;
    }

    private String get(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setReadTimeout(10_000);
        conn.setConnectTimeout(10_000);

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new IOException("Unexpected HTTP status " + status + " for " + urlStr);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }
}
