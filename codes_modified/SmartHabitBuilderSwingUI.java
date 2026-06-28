import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SmartHabitBuilderSwingUI {

    private HabitManager manager = new HabitManager();
    private JPanel habitListPanel;
    private JFrame frame;

    // Form Fields
    private JTextField nameInput;
    private JComboBox<String> categoryInput;
    private JRadioButton dailyRadio;
    private JRadioButton weeklyRadio;
    private JPanel dynamicFieldsPanel;
    
    // Dynamic Inputs
    private JLabel primaryTargetLabel;
    private JTextField primaryTargetInput;
    private JLabel secondaryLabel;
    private JTextField secondaryInput;

    public static void main(String[] args) {
        // Set a clean system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default if error occurs
        }

        SwingUtilities.invokeLater(() -> new SmartHabitBuilderSwingUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Smart Habit Builder v1.0  |  Data: " + HabitStore.getSaveFilePath());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLayout(new BorderLayout());

        // --- LEFT SIDEBAR: Add New Habit Form ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(43, 48, 58)); // Dark elegant theme
        sidebar.setPreferredSize(new Dimension(350, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 25, 30, 25));

        // App Title
        JLabel appTitle = new JLabel("<html><body style='width: 250px;'><b>Smart Habit</b><br>Builder</body></html>");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(appTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        // Form Section Title
        JLabel formTitle = new JLabel("CREATE NEW HABIT");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(new Color(160, 170, 178));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(formTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // Habit Name Input (Larger Layout Sizing)
        nameInput = new JTextField();
        styleTextField(nameInput);
        nameInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nameInput.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(63, 71, 86), 2), 
            "Habit Name", 0, 0, new Font("Segoe UI", Font.PLAIN, 12), Color.LIGHT_GRAY
        ));
        sidebar.add(nameInput);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        // Category Selection Dropdown (Larger Text)
        String[] categories = {"Health", "Study", "Fitness", "Other"};
        categoryInput = new JComboBox<>(categories);
        categoryInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        categoryInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        categoryInput.setBackground(new Color(63, 71, 86));
        categoryInput.setForeground(Color.BLACK);
        sidebar.add(categoryInput);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Type Option Fields (Larger Fonts)
        dailyRadio = new JRadioButton("Daily", true);
        weeklyRadio = new JRadioButton("Weekly");
        dailyRadio.setFont(new Font("Segoe UI", Font.BOLD, 15));
        weeklyRadio.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dailyRadio.setForeground(Color.WHITE);
        dailyRadio.setBackground(new Color(43, 48, 58));
        weeklyRadio.setForeground(Color.WHITE);
        weeklyRadio.setBackground(new Color(43, 48, 58));
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(dailyRadio);
        typeGroup.add(weeklyRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setBackground(new Color(43, 48, 58));
        radioPanel.add(dailyRadio);
        radioPanel.add(Box.createRigidArea(new Dimension(25, 0)));
        radioPanel.add(weeklyRadio);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(radioPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Dynamic Form Fields Setup
        dynamicFieldsPanel = new JPanel();
        dynamicFieldsPanel.setBackground(new Color(43, 48, 58));
        dynamicFieldsPanel.setLayout(new BoxLayout(dynamicFieldsPanel, BoxLayout.Y_AXIS));
        dynamicFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        primaryTargetLabel = new JLabel("Target Days:");
        primaryTargetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        primaryTargetLabel.setForeground(Color.WHITE);
        primaryTargetInput = new JTextField("30");
        styleTextField(primaryTargetInput);
        primaryTargetInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        secondaryLabel = new JLabel("Sessions per week:");
        secondaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        secondaryLabel.setForeground(Color.WHITE);
        secondaryInput = new JTextField("3");
        styleTextField(secondaryInput);
        secondaryInput.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        dynamicFieldsPanel.add(primaryTargetLabel);
        dynamicFieldsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        dynamicFieldsPanel.add(primaryTargetInput);
        sidebar.add(dynamicFieldsPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        // Listeners for changes between Daily and Weekly choices
        dailyRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                primaryTargetLabel.setText("Target Days:");
                primaryTargetInput.setText("30");
                dynamicFieldsPanel.remove(secondaryLabel);
                dynamicFieldsPanel.remove(secondaryInput);
                frame.revalidate();
                frame.repaint();
            }
        });

        weeklyRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                primaryTargetLabel.setText("Target Weeks:");
                primaryTargetInput.setText("12");
                dynamicFieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                dynamicFieldsPanel.add(secondaryLabel);
                dynamicFieldsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                dynamicFieldsPanel.add(secondaryInput);
                frame.revalidate();
                frame.repaint();
            }
        });

        // Add Habit Submission Button (Text changed to Visible BLACK color)
        JButton addBtn = new JButton("Add Habit");
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.BLACK); // Clearly visible contrast text color
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> handleAddHabit());
        sidebar.add(addBtn);

        // --- RIGHT AREA: Dashboard List Display ---
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(248, 249, 250));
        dashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel dashboardTitle = new JLabel("Your Habits Dashboard");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        dashboard.add(dashboardTitle, BorderLayout.NORTH);

        habitListPanel = new JPanel();
        habitListPanel.setLayout(new BoxLayout(habitListPanel, BoxLayout.Y_AXIS));
        habitListPanel.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(habitListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(248, 249, 250));
        dashboard.add(scrollPane, BorderLayout.CENTER);

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(dashboard, BorderLayout.CENTER);

        // Habits are loaded from disk automatically by HabitManager
        
        refreshHabitList();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private void handleAddHabit() {
        String name = nameInput.getText().trim();
        String category = (String) categoryInput.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a habit name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (dailyRadio.isSelected()) {
                int targetDays = Integer.parseInt(primaryTargetInput.getText().trim());
                manager.addHabit(new DailyHabit(name, category, targetDays));
            } else {
                int targetWeeks = Integer.parseInt(primaryTargetInput.getText().trim());
                int sessionsPerWeek = Integer.parseInt(secondaryInput.getText().trim());
                manager.addHabit(new WeeklyHabit(name, category, targetWeeks, sessionsPerWeek));
            }
            nameInput.setText("");
            // addHabit() inside HabitManager auto-saves to disk
            refreshHabitList();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numbers for targets.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshHabitList() {
        habitListPanel.removeAll();

        if (manager.getHabitCount() == 0) {
            JLabel emptyLabel = new JLabel("📭 No habits tracked yet. Use the sidebar menu to start!");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(25, 10, 0, 0));
            habitListPanel.add(emptyLabel);
        } else {
            for (Habit h : manager.getHabits()) {
                habitListPanel.add(createHabitCard(h));
                habitListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        habitListPanel.revalidate();
        habitListPanel.repaint();
    }

    private JPanel createHabitCard(Habit habit) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Left Information Area
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(habit.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel metaLabel = new JLabel(habit.getCategory() + "  •  " + habit.getFrequency());
        metaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        metaLabel.setForeground(Color.GRAY);

        String stats = "🔥 Streak: " + habit.getStreak() + (habit instanceof WeeklyHabit ? " week(s)" : " day(s)") + "  |  📊 Total Done: " + habit.getTotalCompletions();
        if (habit instanceof WeeklyHabit) {
            WeeklyHabit wh = (WeeklyHabit) habit;
            stats += "  |  📅 This Week: " + wh.getCompletionsThisWeek() + "/" + wh.getRequiredPerWeek();
        }
        // Show 24hr timer status for both daily and weekly habits
        if (habit.isAlreadyLoggedToday()) {
            stats += "  |  ✅ Done today — next session available tomorrow";
        } else {
            if (habit instanceof DailyHabit) {
                stats += "  |  ⏰ Ready to log today!";
            } else {
                // Weekly: show how many sessions still needed this week
                WeeklyHabit wh2 = (WeeklyHabit) habit;
                int remaining = wh2.getRequiredPerWeek() - wh2.getCompletionsThisWeek();
                if (remaining > 0) {
                    stats += "  |  ⏰ " + remaining + " session(s) left this week";
                } else {
                    stats += "  |  🏁 Weekly goal met!";
                }
            }
        }
        JLabel statsLabel = new JLabel(stats);
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        infoPanel.add(metaLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(statsLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Right Action Controls: Log Done on top, written Delete button underneath
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        actionPanel.setBackground(Color.WHITE);

        boolean alreadyLogged = habit.isAlreadyLoggedToday();
        JButton doneBtn = new JButton(alreadyLogged ? "✅ Done Today" : "✔ Log Done");
        doneBtn.setBackground(alreadyLogged ? new Color(200, 230, 201) : new Color(232, 245, 233));
        doneBtn.setForeground(alreadyLogged ? new Color(120, 150, 120) : new Color(46, 125, 50));
        doneBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        doneBtn.setFocusPainted(false);
        doneBtn.setPreferredSize(new Dimension(110, 35));
        doneBtn.addActionListener(e -> {
            if (!habit.canLogToday()) {
                // Already logged today — show a friendly popup and block the streak
                String popupMsg;
                if (habit instanceof WeeklyHabit) {
                    WeeklyHabit wh = (WeeklyHabit) habit;
                    popupMsg = "<html><b>⏳ Session already logged today!</b><br><br>"
                        + "You've already recorded a session for <i>" + habit.getName() + "</i> today.<br>"
                        + "You have <b>" + wh.getCompletionsThisWeek() + "/" + wh.getRequiredPerWeek()
                        + "</b> sessions done this week.<br>"
                        + "Come back tomorrow to log your next session! 📅</html>";
                } else {
                    popupMsg = "<html><b>⏳ Streak already logged today!</b><br><br>"
                        + "You've already marked <i>" + habit.getName() + "</i> complete for today.<br>"
                        + "Come back tomorrow to keep your streak going! 🔥</html>";
                }
                JOptionPane.showMessageDialog(
                    frame,
                    popupMsg,
                    "Already Done Today",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            habit.markComplete();
            manager.saveChanges();   // persist streak + date to disk
            refreshHabitList();
        });

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBackground(new Color(255, 235, 238));
        deleteBtn.setForeground(new Color(198, 40, 40));
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            manager.removeHabit(habit.getName()); // saveChanges called inside removeHabit
            refreshHabitList();
        });

        actionPanel.add(doneBtn);
        actionPanel.add(deleteBtn);
        card.add(actionPanel, BorderLayout.EAST);

        // Bottom Tip Suggestion Area
        JLabel tipLabel = new JLabel("💡 Tip: " + habit.getSuggestion());
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tipLabel.setForeground(new Color(85, 85, 85));
        card.add(tipLabel, BorderLayout.SOUTH);

        return card;
    }

    private void styleTextField(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        tf.setBackground(new Color(63, 71, 86));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
    }
}