import java.time.LocalDate;

// Abstract base class demonstrating Abstraction + Encapsulation
public abstract class Habit {
    private String name;
    private String category;
    private int streak;
    private int totalCompletions;
    private LocalDate lastCompletedDate; // tracks the last day this habit was logged

    public Habit(String name, String category) {
        this.name = name;
        this.category = category;
        this.streak = 0;
        this.totalCompletions = 0;
        this.lastCompletedDate = null;
    }

    // Getters and Setters (Encapsulation)
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getStreak() { return streak; }
    public int getTotalCompletions() { return totalCompletions; }
    public LocalDate getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(LocalDate date) { this.lastCompletedDate = date; }

    public void incrementStreak() { streak++; }
    public void resetStreak() { streak = 0; }
    public void incrementCompletions() { totalCompletions++; }

    /**
     * Returns true if the habit has already been logged today.
     */
    public boolean isAlreadyLoggedToday() {
        return lastCompletedDate != null && lastCompletedDate.equals(LocalDate.now());
    }

    /**
     * Returns true if logging is allowed:
     * - Never logged before, OR
     * - Last log was on a previous day (at least 1 calendar day ago)
     */
    public boolean canLogToday() {
        return lastCompletedDate == null || lastCompletedDate.isBefore(LocalDate.now());
    }

    /**
     * Returns true if the user MISSED yesterday — meaning they had a streak going
     * but did NOT log the habit yesterday, breaking the chain.
     * (Covers daily habits; for weekly habits use isMissedThisWeek instead.)
     */
    public boolean isMissedYesterday() {
        if (lastCompletedDate == null) return false;
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // Missed if last log was BEFORE yesterday (gap of 2+ days)
        return lastCompletedDate.isBefore(yesterday);
    }

    /**
     * Returns true if the habit was never logged at all (brand new, untouched).
     */
    public boolean isNeverStarted() {
        return lastCompletedDate == null && streak == 0 && totalCompletions == 0;
    }

    // Abstract methods (Abstraction) - subclasses must implement
    public abstract void markComplete();
    public abstract String getFrequency();
    public abstract String getSuggestion();
    public abstract void displayProgress();
}
