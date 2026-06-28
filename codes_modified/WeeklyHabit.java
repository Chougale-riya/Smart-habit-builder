import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

// Inheritance: WeeklyHabit extends Habit
// Polymorphism: Overrides markComplete() and getSuggestion() with weekly-specific logic
public class WeeklyHabit extends Habit {
    private int targetWeeks;
    private int completionsThisWeek;
    private int requiredPerWeek;
    private int weekNumber; // ISO week number when completionsThisWeek was last updated

    public WeeklyHabit(String name, String category, int targetWeeks, int requiredPerWeek) {
        super(name, category);
        this.targetWeeks = targetWeeks;
        this.requiredPerWeek = requiredPerWeek;
        this.completionsThisWeek = 0;
        this.weekNumber = currentISOWeek();
    }

    public int getCompletionsThisWeek() {
        rolloverWeekIfNeeded();
        return completionsThisWeek;
    }

    public int getRequiredPerWeek() { return requiredPerWeek; }
    public int getTargetWeeks() { return targetWeeks; }
    public int getRawCompletionsThisWeek() { return completionsThisWeek; }
    public int getWeekNumber() { return weekNumber; }

    /** Called by HabitStore to restore saved state without side effects. */
    public void restoreWeeklyState(int savedCompletions, int savedWeekNumber) {
        this.completionsThisWeek = savedCompletions;
        this.weekNumber = savedWeekNumber;
    }

    public void resetWeeklyCount() {
        completionsThisWeek = 0;
        weekNumber = currentISOWeek();
    }

    /**
     * Detects if the calendar has rolled into a new week and the previous week's
     * goal was NOT met — used to show missed-week motivation.
     */
    public boolean isMissedLastWeek() {
        int current = currentISOWeek();
        // If we're in a new week AND we didn't meet the goal last week
        return current != weekNumber && completionsThisWeek < requiredPerWeek && getTotalCompletions() > 0;
    }

    /** Auto-roll the weekly counter when a new ISO week starts. */
    private void rolloverWeekIfNeeded() {
        int current = currentISOWeek();
        if (current != weekNumber) {
            completionsThisWeek = 0;
            weekNumber = current;
        }
    }

    private int currentISOWeek() {
        return LocalDate.now().get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
    }

    @Override
    public void markComplete() {
        rolloverWeekIfNeeded();
        setLastCompletedDate(java.time.LocalDate.now()); // 24hr timer: block same-day duplicate logs
        completionsThisWeek++;
        incrementCompletions();
        System.out.println("✔ Weekly habit '" + getName() + "' logged! This week: "
                + completionsThisWeek + "/" + requiredPerWeek + " sessions done.");
        if (completionsThisWeek >= requiredPerWeek) {
            incrementStreak();
            System.out.println("🎉 Weekly goal met! Streak: " + getStreak() + " week(s).");
        }
    }

    @Override
    public String getFrequency() {
        return "Weekly (" + requiredPerWeek + "x per week)";
    }

    @Override
    public String getSuggestion() {
        rolloverWeekIfNeeded();

        // --- MISSED LAST WEEK: motivational recovery message ---
        if (isMissedLastWeek()) {
            int shortfall = requiredPerWeek - completionsThisWeek;
            return "😔 You missed your weekly goal last week — " + shortfall + " session(s) short. "
                 + "That's okay! Every new week is a fresh start. Schedule your first session right now "
                 + "and let's get that streak back! 💪";
        }

        // --- NEVER STARTED ---
        if (isNeverStarted()) {
            return "⚡ No sessions this week yet. Schedule your first one now!";
        }

        // --- IN PROGRESS THIS WEEK ---
        if (completionsThisWeek < requiredPerWeek) {
            int remaining = requiredPerWeek - completionsThisWeek;
            int daysLeft = (int) LocalDate.now()
                .until(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))).getDays() + 1;
            if (daysLeft <= 2 && remaining > 1) {
                return "⚠️ Only " + daysLeft + " day(s) left this week and " + remaining
                     + " session(s) to go! Push hard — don't let this week slip! 🔥";
            }
            return "📅 " + remaining + " more session(s) needed to meet your weekly goal. You've got this!";
        }

        // --- WEEKLY GOAL MET ---
        int streak = getStreak();
        if (streak < 2) {
            return "🌱 Weekly goal achieved! Try maintaining it for 2 weeks in a row.";
        } else if (streak < 4) {
            return "🔥 Strong consistency! Keep going for a full month streak.";
        } else {
            return "🏆 You're crushing it! Consider increasing sessions per week.";
        }
    }

    @Override
    public void displayProgress() {
        System.out.println("──────────────────────────────────────");
        System.out.println("  Habit         : " + getName());
        System.out.println("  Category      : " + getCategory());
        System.out.println("  Frequency     : " + getFrequency());
        System.out.println("  Target        : " + targetWeeks + " week(s)");
        System.out.println("  Week Streak   : " + getStreak() + " week(s)");
        System.out.println("  This Week     : " + completionsThisWeek + "/" + requiredPerWeek);
        System.out.println("  Total Done    : " + getTotalCompletions() + " session(s)");
        System.out.println("  Tip           : " + getSuggestion());
        System.out.println("──────────────────────────────────────");
    }
}
