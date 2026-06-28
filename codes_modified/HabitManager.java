import java.util.ArrayList;
import java.util.List;

// Manages the collection of habits (Single Responsibility Principle)
// Persistence: saves to disk automatically after every change via HabitStore
public class HabitManager {
    private List<Habit> habits;

    public HabitManager() {
        // Load saved habits from disk on startup
        habits = HabitStore.load();
        System.out.println("📂 Loaded " + habits.size() + " habit(s) from saved data.");
    }

    public void addHabit(Habit habit) {
        habits.add(habit);
        save();
        System.out.println("✅ Habit '" + habit.getName() + "' added and saved!");
    }

    public void removeHabit(String name) {
        Habit toRemove = findHabit(name);
        if (toRemove != null) {
            habits.remove(toRemove);
            save();
            System.out.println("🗑 Habit '" + name + "' removed.");
        } else {
            System.out.println("❌ Habit not found: " + name);
        }
    }

    public Habit findHabit(String name) {
        for (Habit h : habits) {
            if (h.getName().equalsIgnoreCase(name)) return h;
        }
        return null;
    }

    public void markHabitComplete(String name) {
        Habit h = findHabit(name);
        if (h != null) {
            h.markComplete();
            save();
        } else {
            System.out.println("❌ Habit not found: " + name);
        }
    }

    /** Call after any direct mutation (e.g. from UI Log Done button). */
    public void saveChanges() {
        save();
    }

    private void save() {
        HabitStore.save(habits);
    }

    public void viewAllHabits() {
        if (habits.isEmpty()) {
            System.out.println("📭 No habits tracked yet. Start by adding one!");
            return;
        }
        System.out.println("\n====== YOUR HABITS ======");
        for (int i = 0; i < habits.size(); i++) {
            System.out.println((i + 1) + ". " + habits.get(i).getName()
                    + " [" + habits.get(i).getFrequency() + "]"
                    + " | Streak: " + habits.get(i).getStreak());
        }
        System.out.println("=========================\n");
    }

    public void viewHabitDetails(String name) {
        Habit h = findHabit(name);
        if (h != null) {
            h.displayProgress();
        } else {
            System.out.println("❌ Habit not found: " + name);
        }
    }

    public void viewAllSuggestions() {
        if (habits.isEmpty()) {
            System.out.println("📭 No habits to suggest on. Add some habits first!");
            return;
        }
        System.out.println("\n====== SUGGESTIONS ======");
        for (Habit h : habits) {
            System.out.println("📌 " + h.getName() + ": " + h.getSuggestion());
        }
        System.out.println("=========================\n");
    }

    public int getHabitCount() {
        return habits.size();
    }

    public List<Habit> getHabits() {
        return habits;
    }
}
