# Quiz Leaderboard System

A plain Java console app that polls a quiz API, deduplicates overlapping event data across poll windows, aggregates scores per participant, builds a ranked leaderboard, and submits the final result to a validator endpoint. No frameworks, just Java 17, Maven, and Gson.

---

## How It Works

1. **Poll** - Makes 10 sequential GET requests to `/quiz/messages` (poll=0 through poll=9), with a mandatory 5 sec delay between each call. Each response contains a list of quiz events.

2. **Collect** - All events from all 10 poll responses are flattened into a single list.

3. **Deduplicate** - Events are deduplicated using a composite key of `roundId + "|" + participant`. In distributed systems, the same event can appear in multiple poll windows - keeping duplicates would inflate scores. Only the first occurrence of each key is kept.

4. **Aggregate** - Scores are summed per participant across all their unique round events.

5. **Rank** - Participants are sorted by total score in descending order.

6. **Submit** - The final leaderboard is POSTed to `/quiz/submit` as JSON.

---

## Project Structure

```
src/main/java/com/quizleaderboard/
├── Main.java               Entry point — orchestrates the full flow
├── QuizPoller.java         Handles polling the API 10 times with delays
├── LeaderboardBuilder.java Flattens, deduplicates, aggregates, and ranks
├── QuizSubmitter.java      POSTs the final leaderboard to the submit endpoint
└── model/
    ├── QuizEvent.java      Represents a single quiz event (roundId, participant, score)
    ├── PollResponse.java   Wraps a single API poll response (regNo, setId, pollIndex, events)
    └── LeaderboardEntry.java  One ranked entry (participant, totalScore) — sorts descending
```

---

## Tech Stack

- **Java 17**
- **Maven** for build management
- **Gson 2.10.1** for JSON parsing and serialization
- **java.net.HttpURLConnection** for HTTP — no Spring, no OkHttp, no external HTTP libraries

---

## How to Run

**Prerequisites:** Java 17+, Maven 3.6+

```bash
git clone https://github.com/deepanjan1011/quiz-leaderboard-system.git
cd quiz-leaderboard-system
```

**Run directly:**
```bash
mvn clean compile exec:java -Dexec.mainClass="com.quizleaderboard.Main"
```

**Or build and run the jar:**
```bash
mvn clean package
java -jar target/quiz-leaderboard-1.0-SNAPSHOT.jar
```

Note: the run takes about 45 sec - 10 polls with a 5 sec mandatory delay between each one.

---

## Sample Output

```
Starting quiz leaderboard system...
Polling [1/10]... got 2 events
Polling [2/10]... got 1 events
Polling [3/10]... got 2 events
Polling [4/10]... got 1 events
Polling [5/10]... got 2 events
Polling [6/10]... got 1 events
Polling [7/10]... got 2 events
Polling [8/10]... got 1 events
Polling [9/10]... got 2 events
Polling [10/10]... got 1 events
Total raw events: 15
After dedup: 9
Participants: 3
Grand total: 1365

--- Leaderboard ---
Rank 1: Diana — 470 points
Rank 2: Ethan — 455 points
Rank 3: Fiona — 440 points
Grand total: 1365 points

Submitting leaderboard...
Submit response (HTTP 201): {"regNo":"RA2311026020010","totalPollsMade":10,"submittedTotal":1365,"attemptCount":1}
Done.
```

---

## Deduplication Strategy

The quiz API is designed to return overlapping data, the same event can show up in multiple poll windows. If you naively sum all scores across all polls without deduplicating, you end up inflating totals significantly. The fix is straightforward: before aggregating anything, assign each event a composite key of `roundId + "|" + participant`. This uniquely identifies a participant's score for a specific round. A `HashSet` tracks which keys have already been seen, and any event whose key has already appeared is discarded. Only the first occurrence is kept. This reduced the raw event count from 15 down to 9 in the actual run above.
