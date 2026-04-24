package com.quizleaderboard;

import com.google.gson.Gson;
import com.quizleaderboard.model.LeaderboardEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class QuizSubmitter {

    private static final String SUBMIT_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit";
    private static final String REG_NO = "RA2311026020010";

    private final Gson gson = new Gson();

    public String submit(List<LeaderboardEntry> leaderboard) {
        // build payload as a simple map — no need for a dedicated class here
        List<Map<String, Object>> leaderboardPayload = leaderboard.stream()
                .map(e -> Map.<String, Object>of(
                        "participant", e.getParticipant(),
                        "totalScore", e.getTotalScore()
                ))
                .toList();

        Map<String, Object> payload = Map.of(
                "regNo", REG_NO,
                "leaderboard", leaderboardPayload
        );

        String requestBody = gson.toJson(payload);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(SUBMIT_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }

            int status = conn.getResponseCode();
            // read error stream if non-2xx so we don't miss the response body
            // getErrorStream() can return null if the server sent no body with the error
            InputStream stream = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : (conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder sb = new StringBuilder();
            try (reader) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String responseBody = sb.toString();
            System.out.println("Submit response (HTTP " + status + "): " + responseBody);

            conn.disconnect();
            return responseBody;

        } catch (IOException e) {
            throw new RuntimeException("Submission failed: " + e.getMessage(), e);
        }
    }
}
