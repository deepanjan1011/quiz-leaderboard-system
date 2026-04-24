package com.quizleaderboard;

import com.quizleaderboard.model.LeaderboardEntry;
import com.quizleaderboard.model.PollResponse;
import com.quizleaderboard.model.QuizEvent;

import java.util.*;

public class LeaderboardBuilder {

    private int grandTotal = 0;

    public List<LeaderboardEntry> buildLeaderboard(List<PollResponse> responses) {
        // step 1: flatten all events across all poll responses
        List<QuizEvent> allEvents = new ArrayList<>();
        for (PollResponse response : responses) {
            if (response.getEvents() != null) {
                allEvents.addAll(response.getEvents());
            }
        }
        System.out.println("Total raw events: " + allEvents.size());

        // step 2: deduplicate — keep first occurrence of each (roundId + participant) combo
        List<QuizEvent> dedupedEvents = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (QuizEvent event : allEvents) {
            String key = event.dedupeKey();
            if (seen.add(key)) {
                dedupedEvents.add(event);
            }
        }
        System.out.println("After dedup: " + dedupedEvents.size());

        // step 3: aggregate scores per participant
        Map<String, Integer> scoreMap = new HashMap<>();
        for (QuizEvent event : dedupedEvents) {
            scoreMap.merge(event.getParticipant(), event.getScore(), Integer::sum);
        }
        System.out.println("Participants: " + scoreMap.size());

        // step 4: build leaderboard entries
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            leaderboard.add(new LeaderboardEntry(entry.getKey(), entry.getValue()));
        }

        // step 5: sort descending by totalScore (natural order via Comparable)
        Collections.sort(leaderboard);

        // step 6: compute grand total
        grandTotal = 0;
        for (LeaderboardEntry entry : leaderboard) {
            grandTotal += entry.getTotalScore();
        }
        System.out.println("Grand total: " + grandTotal);

        return leaderboard;
    }

    public int getGrandTotal() {
        return grandTotal;
    }
}
