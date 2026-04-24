package com.quizleaderboard.model;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private String participant;
    private int totalScore;

    public LeaderboardEntry(String participant, int totalScore) {
        this.participant = participant;
        this.totalScore = totalScore;
    }

    public String getParticipant() {
        return participant;
    }

    public int getTotalScore() {
        return totalScore;
    }

    // descending by totalScore — higher scores rank first
    @Override
    public int compareTo(LeaderboardEntry other) {
        return Integer.compare(other.totalScore, this.totalScore);
    }
}
