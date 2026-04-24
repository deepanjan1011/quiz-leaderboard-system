package com.quizleaderboard.model;

public class QuizEvent {

    private String roundId;
    private String participant;
    private int score;

    public QuizEvent(String roundId, String participant, int score) {
        this.roundId = roundId;
        this.participant = participant;
        this.score = score;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getParticipant() {
        return participant;
    }

    public int getScore() {
        return score;
    }

    // used to detect duplicate submissions for the same round + participant
    public String dedupeKey() {
        return roundId + "|" + participant;
    }

    @Override
    public String toString() {
        return "QuizEvent{roundId='" + roundId + "', participant='" + participant + "', score=" + score + "}";
    }
}
