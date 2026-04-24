package com.quizleaderboard.model;

import java.util.List;

public class PollResponse {

    private String regNo;
    private String setId;
    private int pollIndex;
    private List<QuizEvent> events;

    public PollResponse(String regNo, String setId, int pollIndex, List<QuizEvent> events) {
        this.regNo = regNo;
        this.setId = setId;
        this.pollIndex = pollIndex;
        this.events = events;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getSetId() {
        return setId;
    }

    public int getPollIndex() {
        return pollIndex;
    }

    public List<QuizEvent> getEvents() {
        return events;
    }
}
