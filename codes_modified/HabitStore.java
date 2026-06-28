import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HabitStore — handles saving and loading all habits to/from a JSON file.
 * Uses plain Java (no external libraries). Data is stored at:
 *   [user home directory]/SmartHabitBuilder/habits_data.json
 *
 * JSON format per habit:
 * {
 *   "type": "daily" | "weekly",
 *   "name": "...",
 *   "category": "...",
 *   "streak": 5,
 *   "totalCompletions": 12,
 *   "lastCompletedDate": "2024-06-01",   // or "null"
 *   "targetDays": 30,                    // daily only
 *   "targetWeeks": 12,                   // weekly only
 *   "requiredPerWeek": 3,                // weekly only
 *   "completionsThisWeek": 2,            // weekly only
 *   "weekNumber": 22                     // weekly only
 * }
 */
public class HabitStore {

    private static final String SAVE_DIR  = System.getProperty("user.home") + File.separator + "SmartHabitBuilder";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "habits_data.json";

    // ------------------------------------------------------------------ save

    public static void save(List<Habit> habits) {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < habits.size(); i++) {
                sb.append(toJson(habits.get(i)));
                if (i < habits.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
            Files.writeString(Paths.get(SAVE_FILE), sb.toString());
        } catch (IOException e) {
            System.err.println("⚠️  Could not save habits: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ load

    public static List<Habit> load() {
        List<Habit> habits = new ArrayList<>();
        Path path = Paths.get(SAVE_FILE);
        if (!Files.exists(path)) return habits;          // first run — no file yet

        try {
            String json = Files.readString(path).trim();
            if (json.isEmpty() || json.equals("[]")) return habits;

            // Strip outer [ ]
            json = json.substring(1, json.lastIndexOf(']')).trim();

            // Split into individual objects by "},{" boundaries
            List<String> objects = splitObjects(json);
            for (String obj : objects) {
                Habit h = fromJson(obj.trim());
                if (h != null) habits.add(h);
            }
        } catch (IOException e) {
            System.err.println("⚠️  Could not load habits: " + e.getMessage());
        }
        return habits;
    }

    // --------------------------------------------------------- serialisation

    private static String toJson(Habit h) {
        StringBuilder sb = new StringBuilder();
        sb.append("  {\n");
        if (h instanceof DailyHabit) {
            DailyHabit d = (DailyHabit) h;
            sb.append(field("type",              "daily"));
            sb.append(field("name",              h.getName()));
            sb.append(field("category",          h.getCategory()));
            sb.append(fieldInt("streak",         h.getStreak()));
            sb.append(fieldInt("totalCompletions", h.getTotalCompletions()));
            sb.append(field("lastCompletedDate", dateStr(h.getLastCompletedDate())));
            sb.append(fieldInt("targetDays",     d.getTargetDays()));
        } else if (h instanceof WeeklyHabit) {
            WeeklyHabit w = (WeeklyHabit) h;
            sb.append(field("type",              "weekly"));
            sb.append(field("name",              h.getName()));
            sb.append(field("category",          h.getCategory()));
            sb.append(fieldInt("streak",         h.getStreak()));
            sb.append(fieldInt("totalCompletions", h.getTotalCompletions()));
            sb.append(field("lastCompletedDate", dateStr(h.getLastCompletedDate())));
            sb.append(fieldInt("targetWeeks",    w.getTargetWeeks()));
            sb.append(fieldInt("requiredPerWeek",w.getRequiredPerWeek()));
            sb.append(fieldInt("completionsThisWeek", w.getRawCompletionsThisWeek()));
            sb.append(fieldInt("weekNumber",     w.getWeekNumber(), true)); // last field — no trailing comma
        }
        // close: remove trailing comma from last daily field too
        String result = sb.toString().replaceAll(",\n  \\}$", "\n  }");
        // safer: just trim trailing comma before closing brace
        return trimLastComma(sb.toString()) + "  }";
    }

    /** Parse one JSON object string back into a Habit. */
    private static Habit fromJson(String obj) {
        try {
            String type              = strVal(obj, "type");
            String name              = strVal(obj, "name");
            String category          = strVal(obj, "category");
            int streak               = intVal(obj, "streak");
            int totalCompletions     = intVal(obj, "totalCompletions");
            String dateRaw           = strVal(obj, "lastCompletedDate");
            LocalDate lastDate       = dateRaw.equals("null") ? null : LocalDate.parse(dateRaw);

            if ("daily".equals(type)) {
                int targetDays = intVal(obj, "targetDays");
                DailyHabit h = new DailyHabit(name, category, targetDays);
                restoreBase(h, streak, totalCompletions, lastDate);
                return h;

            } else if ("weekly".equals(type)) {
                int targetWeeks          = intVal(obj, "targetWeeks");
                int requiredPerWeek      = intVal(obj, "requiredPerWeek");
                int completionsThisWeek  = intVal(obj, "completionsThisWeek");
                int weekNumber           = intVal(obj, "weekNumber");
                WeeklyHabit h = new WeeklyHabit(name, category, targetWeeks, requiredPerWeek);
                restoreBase(h, streak, totalCompletions, lastDate);
                h.restoreWeeklyState(completionsThisWeek, weekNumber);
                return h;
            }
        } catch (Exception e) {
            System.err.println("⚠️  Skipping corrupt habit entry: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------- base field restore

    private static void restoreBase(Habit h, int streak, int totalCompletions, LocalDate lastDate) {
        for (int i = 0; i < streak; i++) h.incrementStreak();
        for (int i = 0; i < totalCompletions; i++) h.incrementCompletions();
        if (lastDate != null) h.setLastCompletedDate(lastDate);
    }

    // --------------------------------------------------------- JSON helpers

    private static String field(String key, String value) {
        return "    \"" + key + "\": \"" + value + "\",\n";
    }
    private static String fieldInt(String key, int value) {
        return "    \"" + key + "\": " + value + ",\n";
    }
    private static String fieldInt(String key, int value, boolean last) {
        return "    \"" + key + "\": " + value + (last ? "\n" : ",\n");
    }
    private static String dateStr(LocalDate d) {
        return d == null ? "null" : d.toString();
    }

    /** Remove the last trailing comma inside the object body before closing brace. */
    private static String trimLastComma(String s) {
        int last = s.lastIndexOf(",\n");
        if (last >= 0) return s.substring(0, last) + "\n";
        return s;
    }

    private static String strVal(String json, String key) {
        // Handles: "key": "value"  or  "key": "null"
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private static int intVal(String json, String key) {
        String search = "\"" + key + "\": ";
        int start = json.indexOf(search);
        if (start == -1) return 0;
        start += search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
        return Integer.parseInt(json.substring(start, end).trim());
    }

    /**
     * Splits a JSON array body (without outer [ ]) into individual object strings.
     * Handles nested braces correctly.
     */
    private static List<String> splitObjects(String body) {
        List<String> result = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    result.add(body.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    public static String getSaveFilePath() {
        return SAVE_FILE;
    }
}
