package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;

public class TaskManagerApp extends JFrame
{
    private final TaskManager taskManager;
    private final TaskRepository repository;

    private final JLabel titleValue = new JLabel("-");
    private final JLabel priorityValue = new JLabel("-");
    private final JLabel dueDateValue = new JLabel("-");
    private final JTextArea notesValue = new JTextArea();

    private final DefaultListModel<Task> listModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(listModel);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public TaskManagerApp(TaskManager taskManager, TaskRepository repository)
    {
        super("Personal Task Manager");
        this.taskManager = taskManager;
        this.repository = repository;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);

        buildUi();
        refreshList();

        taskList.addListSelectionListener(e ->
        {
            if (!e.getValueIsAdjusting())
            {
                updateDetailsFromSelection();
            }
        });

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                exitSafely();
            }
        });
    }

    private void buildUi()
    {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(245, 246, 248));
        setContentPane(root);

        JLabel header = new JLabel("Personal Task Manager");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        root.add(header, BorderLayout.NORTH);

        // Task list
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFixedCellHeight(56);
        taskList.setCellRenderer(new TaskListRenderer());

        JScrollPane listScroll = new JScrollPane(taskList);
        listScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        listScroll.getViewport().setBackground(new Color(248, 249, 251));

        // Details
        JPanel details = buildDetailsPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, details);
        split.setResizeWeight(0.4);
        split.setBorder(null);

        root.add(split, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton doneBtn = new JButton("Mark Done");
        JButton deleteBtn = new JButton("Delete");
        JButton exitBtn = new JButton("Exit");

        addBtn.addActionListener(e -> addTask());
        editBtn.addActionListener(e -> editTask());
        doneBtn.addActionListener(e -> markDone());
        deleteBtn.addActionListener(e -> deleteSelected());
        exitBtn.addActionListener(e -> exitSafely());

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(doneBtn);
        buttons.add(deleteBtn);
        buttons.add(exitBtn);

        root.add(buttons, BorderLayout.SOUTH);
    }

    private JPanel buildDetailsPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        panel.setBackground(new Color(250, 250, 252));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 12, 8, 12);
        c.anchor = GridBagConstraints.NORTHWEST;

        JLabel titleKey = key("Title");
        JLabel priorityKey = key("Priority");
        JLabel dueKey = key("Due date");
        JLabel notesKey = key("Notes");

        styleValue(titleValue, 16f, true);
        styleValue(priorityValue, 14f, false);
        styleValue(dueDateValue, 14f, false);

        c.gridx = 0; c.gridy = 0;
        panel.add(titleKey, c);
        c.gridy++;
        panel.add(priorityKey, c);
        c.gridy++;
        panel.add(dueKey, c);
        c.gridy++;
        panel.add(notesKey, c);

        c.gridx = 1; c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        panel.add(titleValue, c);
        c.gridy++;
        panel.add(priorityValue, c);
        c.gridy++;
        panel.add(dueDateValue, c);

        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        notesValue.setEditable(false);
        notesValue.setLineWrap(true);
        notesValue.setWrapStyleWord(true);
        notesValue.setFont(notesValue.getFont().deriveFont(14f));
        notesValue.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane notesScroll = new JScrollPane(notesValue);
        notesScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        panel.add(notesScroll, c);

        return panel;
    }

    private JLabel key(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 12f));
        l.setForeground(new Color(120, 120, 120));
        return l;
    }

    private void styleValue(JLabel label, float size, boolean bold)
    {
        label.setFont(label.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, size));
    }

    private void refreshList()
    {
        listModel.clear();
        taskManager.getTasks().forEach(listModel::addElement);

        setTitle("Personal Task Manager (" + taskManager.size() + " tasks)");

        if (!listModel.isEmpty() && taskList.getSelectedIndex() == -1)
        {
            taskList.setSelectedIndex(0);
        }
        updateDetailsFromSelection();
    }

    private void updateDetailsFromSelection()
    {
        Task t = taskList.getSelectedValue();
        if (t == null)
        {
            titleValue.setText("-");
            priorityValue.setText("-");
            dueDateValue.setText("-");
            notesValue.setText("");
            return;
        }

        titleValue.setText(t.getTitle());
        priorityValue.setText(t.getPriority().name());
        dueDateValue.setText(t.getDueDate() == null ? "-" : DATE_FMT.format(t.getDueDate()));
        notesValue.setText(t.getNotes() == null ? "" : t.getNotes());
    }

    private void addTask()
    {
        Task t = TaskDialogs.showTaskForm(this, "Add Task", null);
        if (t == null) return;

        taskManager.addTask(t);
        saveSafely();
        refreshList();
        taskList.setSelectedIndex(listModel.size() - 1);
    }

    private void editTask()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0) return;

        Task updated = TaskDialogs.showTaskForm(this, "Edit Task", listModel.get(idx));
        if (updated == null) return;

        taskManager.updateTask(idx, updated);
        saveSafely();
        refreshList();
        taskList.setSelectedIndex(idx);
    }

    private void markDone()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0) return;

        taskManager.markTaskDone(idx + 1);
        saveSafely();
        refreshList();
        taskList.setSelectedIndex(idx);
    }

    private void deleteSelected()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0) return;

        if (JOptionPane.showConfirmDialog(this, "Delete selected task?",
                "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            taskManager.deleteTask(idx);
            saveSafely();
            refreshList();
        }
    }

    private void saveSafely()
    {
        try { repository.save(taskManager.getTasks()); }
        catch (Exception ignored) {}
    }

    private void exitSafely()
    {
        saveSafely();
        dispose();
        System.exit(0);
    }
}
