import java.time.LocalDate;

// Inheritance: DailyHabit extends Habit
// Polymorphism: Overrides markComplete() and getSuggestion() with daily-specific logic
public class DailyHabit extends Habit {
    private int targetDays;

    public DailyHabit(String name, String category, int targetDays) {
        super(name, category);
        this.targetDays = targetDays;
    }

    public int getTargetDays() { return targetDays; }

    @Override
    public void markComplete() {
        // If a day was skipped, the streak resets before counting today
        if (isMissedYesterday()) {
            resetStreak();
        }
        setLastCompletedDate(LocalDate.now());
        incrementStreak();
        incrementCompletions();
        System.out.println("✔ Daily habit '" + getName() + "' marked complete! Streak: " + getStreak() + " day(s).");
    }

    @Override
    public String getFrequency() {
        return "Daily";
    }

    @Override
    public String getSuggestion() {
        // --- MISSED / BROKEN STREAK: show motivational recovery message ---
        if (isMissedYesterday()) {
            int lost = getStreak(); // streak not yet reset here (shown before next log)
            if (lost >= 7) {
                return "💔 You missed a day and broke a " + lost + "-day streak — that stings! "
                     + "But every champion falls. What matters is standing back up. Start fresh today! 🚀";
            } else if (lost >= 3) {
                return "😔 Your " + lost + "-day streak slipped away yesterday. Don't let one bad day "
                     + "become two — come back stronger today! You've done it before. 💪";
            } else {
                return "⚠️ You missed yesterday's session. No worries — slip-ups are part of the journey! "
                     + "Log today and get that streak climbing again. 🌱";
            }
        }

        // --- NEVER STARTED ---
        if (isNeverStarted()) {
            return "⚡ You haven't started yet. Begin today — even 5 minutes counts!";
        }

        // --- ON TRACK: progress-based encouragement ---
        int streak = getStreak();
        if (streak < 3) {
            return "🌱 Good start! Try to build momentum — aim for 3 days in a row.";
        } else if (streak < 7) {
            return "🔥 Great streak! You're building a habit. Push to 7 days!";
        } else if (streak < 21) {
            return "💪 Excellent! Over a week strong. Keep it up — 21 days forms a habit!";
        } else {
            return "🏆 Outstanding! You've mastered this habit. Consider increasing intensity or adding a new goal.";
        }
    }

    @Override
    public void displayProgress() {
        System.out.println("──────────────────────────────────────");
        System.out.println("  Habit     : " + getName());
        System.out.println("  Category  : " + getCategory());
        System.out.println("  Frequency : " + getFrequency());
        System.out.println("  Target    : " + targetDays + " days");
        System.out.println("  Streak    : " + getStreak() + " day(s)");
        System.out.println("  Completed : " + getTotalCompletions() + " time(s)");
        System.out.println("  Progress  : " + getProgressBar());
        System.out.println("  Tip       : " + getSuggestion());
        System.out.println("──────────────────────────────────────");
    }

    private String getProgressBar() {
        int percent = Math.min((getTotalCompletions() * 100) / targetDays, 100);
        int filled = percent / 10;
        String bar = "█".repeat(filled) + "░".repeat(10 - filled);
        return "[" + bar + "] " + percent + "%";
    }
}
