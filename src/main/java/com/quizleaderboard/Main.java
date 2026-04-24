package com.quizleaderboard;

import com.quizleaderboard.model.LeaderboardEntry;
import com.quizleaderboard.model.PollResponse;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting quiz leaderboard system...");

        List<PollResponse> responses = new QuizPoller().pollAll();

        LeaderboardBuilder builder = new LeaderboardBuilder();
        List<LeaderboardEntry> leaderboard = builder.buildLeaderboard(responses);

        System.out.println("\n--- Leaderboard ---");
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            System.out.println("Rank " + (i + 1) + ": " + entry.getParticipant() + " — " + entry.getTotalScore() + " points");
        }
        System.out.println("Grand total: " + builder.getGrandTotal() + " points");

        System.out.println("\nSubmitting leaderboard...");
        String response = new QuizSubmitter().submit(leaderboard);
        System.out.println("Response: " + response);

        System.out.println("Done.");
    }
}
