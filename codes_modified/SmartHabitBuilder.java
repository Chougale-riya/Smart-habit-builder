import java.util.Scanner;

public class SmartHabitBuilder {

    static HabitManager manager = new HabitManager();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": addHabit(); break;
                case "2": markComplete(); break;
                case "3": manager.viewAllHabits(); break;
                case "4": viewDetails(); break;
                case "5": manager.viewAllSuggestions(); break;
                case "6": removeHabit(); break;
                case "7":
                    System.out.println("\nThank you for using Smart Habit Builder. Stay consistent!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1 to 7.");
            }
        }
        scanner.close();
    }

    static void printBanner() {
        System.out.println("==========================================");
        System.out.println("        SMART HABIT BUILDER v1.0         ");
        System.out.println("    Track. Improve. Grow. Every Day.     ");
        System.out.println("==========================================");
        System.out.println();
    }

    static void printMenu() {
        System.out.println("------------- MAIN MENU --------------");
        System.out.println("  1. Add New Habit");
        System.out.println("  2. Mark Habit as Complete");
        System.out.println("  3. View All Habits");
        System.out.println("  4. View Habit Details");
        System.out.println("  5. Get Suggestions");
        System.out.println("  6. Remove a Habit");
        System.out.println("  7. Exit");
        System.out.println("--------------------------------------");
    }

    static void addHabit() {
        System.out.println("\n-- Add New Habit --");
        System.out.print("Enter habit name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter category (Health / Study / Fitness / Other): ");
        String category = scanner.nextLine().trim();

        System.out.print("Is this a Daily or Weekly habit? (D/W): ");
        String type = scanner.nextLine().trim().toUpperCase();

        if (type.equals("D")) {
            System.out.print("Enter target number of days: ");
            int targetDays = readInt();
            manager.addHabit(new DailyHabit(name, category, targetDays));

        } else if (type.equals("W")) {
            System.out.print("Enter target number of weeks: ");
            int targetWeeks = readInt();
            System.out.print("How many sessions per week? ");
            int sessionsPerWeek = readInt();
            manager.addHabit(new WeeklyHabit(name, category, targetWeeks, sessionsPerWeek));

        } else {
            System.out.println("Invalid type entered. Habit not added.");
        }
    }

    static void markComplete() {
        if (manager.getHabitCount() == 0) {
            System.out.println("No habits to mark. Add one first!");
            return;
        }
        manager.viewAllHabits();
        System.out.print("Enter habit name to mark complete: ");
        String name = scanner.nextLine().trim();
        manager.markHabitComplete(name);
    }

    static void viewDetails() {
        if (manager.getHabitCount() == 0) {
            System.out.println("No habits tracked yet.");
            return;
        }
        manager.viewAllHabits();
        System.out.print("Enter habit name to view details: ");
        String name = scanner.nextLine().trim();
        manager.viewHabitDetails(name);
    }

    static void removeHabit() {
        if (manager.getHabitCount() == 0) {
            System.out.println("No habits to remove.");
            return;
        }
        manager.viewAllHabits();
        System.out.print("Enter habit name to remove: ");
        String name = scanner.nextLine().trim();
        manager.removeHabit(name);
    }

    static int readInt() {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine().trim());
                if (val > 0) return val;
                System.out.print("Please enter a positive number: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }
    }
}