package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
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

    private static final Color APP_BG   = new Color(244, 246, 250);
    private static final Color CARD_BG  = new Color(252, 253, 255);

    private static final Color PRIMARY      = new Color(108, 160, 150);
    private static final Color PRIMARY_DARK = new Color(88, 138, 130);

    private static final Color DANGER       = new Color(232, 95, 95);
    private static final Color DANGER_TEXT  = new Color(190, 60, 60);

    private static final Color TEXT_MUTED   = new Color(115, 115, 130);
    private static final Color TEXT_STRONG  = new Color(35, 35, 42);

    public TaskManagerApp(TaskManager taskManager, TaskRepository repository)
    {
        super("Personal Task Manager");
        this.taskManager = taskManager;
        this.repository = repository;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1040, 650);
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
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBorder(new EmptyBorder(26, 26, 26, 26)); // ✅ outer padding
        root.setBackground(APP_BG);
        setContentPane(root);

        JLabel header = new JLabel("Personal Task Manager");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 30f));
        header.setBorder(new EmptyBorder(0, 2, 6, 2));
        root.add(header, BorderLayout.NORTH);

        // Left list
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFixedCellHeight(78);
        taskList.setCellRenderer(new TaskListRenderer());
        taskList.setBackground(CARD_BG);

        JScrollPane listScroll = new JScrollPane(taskList);
        listScroll.setBorder(BorderFactory.createEmptyBorder());
        listScroll.getViewport().setBackground(CARD_BG);

        JPanel listCard = wrapCard(listScroll);

        // Right details
        JPanel detailsCard = wrapCard(buildDetailsPanel());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listCard, detailsCard);
        split.setResizeWeight(0.34);
        split.setBorder(null);
        split.setOpaque(false);
        split.setBackground(APP_BG);
        split.setDividerSize(10);

        root.add(split, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(4, 0, 0, 0));

        JButton addBtn = modernButton("Add", ButtonStyle.PRIMARY);
        JButton editBtn = modernButton("Edit", ButtonStyle.SECONDARY);
        JButton doneBtn = modernButton("Mark Done", ButtonStyle.SECONDARY);
        JButton deleteBtn = modernButton("Delete", ButtonStyle.DANGER);
        JButton exitBtn = modernButton("Exit", ButtonStyle.GHOST);

        addBtn.addActionListener(e -> addTask());
        editBtn.addActionListener(e -> editTask());
        doneBtn.addActionListener(e -> markDone());
        deleteBtn.addActionListener(e -> deleteSelected());
        exitBtn.addActionListener(e -> exitSafely());

        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(doneBtn);
        bottom.add(deleteBtn);
        bottom.add(exitBtn);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildDetailsPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;

        // Left column (keys)
        c.insets = new Insets(10, 0, 10, 18);
        panel.add(key("Title"), c);
        c.gridy++;
        panel.add(key("Priority"), c);
        c.gridy++;
        panel.add(key("Due date"), c);
        c.gridy++;
        panel.add(key("Notes"), c);

        // Right column (values)
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 8, 0);

        styleValue(titleValue, 22f, true);
        styleValue(priorityValue, 15f, false);
        styleValue(dueDateValue, 15f, false);

        panel.add(titleValue, c);
        c.gridy++;
        panel.add(priorityValue, c);
        c.gridy++;
        panel.add(dueDateValue, c);

        // Notes (scroll area)
        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 0, 0, 0);

        notesValue.setEditable(false);
        notesValue.setLineWrap(true);
        notesValue.setWrapStyleWord(true);
        notesValue.setFont(notesValue.getFont().deriveFont(15f));
        notesValue.setBackground(new Color(245, 247, 250));
        notesValue.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane notesScroll = new JScrollPane(notesValue);
        notesScroll.setBorder(BorderFactory.createEmptyBorder());
        notesScroll.getViewport().setBackground(Color.WHITE);
        notesScroll.setBackground(CARD_BG);

        JPanel notesHolder = new JPanel(new BorderLayout());
        notesHolder.setBackground(CARD_BG);
        notesHolder.setBorder(new EmptyBorder(2, 0, 0, 0));
        notesHolder.add(notesScroll, BorderLayout.CENTER);

        panel.add(notesHolder, c);

        return panel;
    }

    private JLabel key(String text)
    {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 13f));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void styleValue(JLabel label, float size, boolean bold)
    {
        label.setFont(label.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(TEXT_STRONG);
    }

    private JPanel wrapCard(JComponent inner)
    {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(16, 16, 16, 16)); // padding “card”
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    private enum ButtonStyle { PRIMARY, SECONDARY, DANGER, GHOST }

    private JButton modernButton(String text, ButtonStyle style)
    {
        JButton b = new JButton(text);

        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 13.5f));
        b.setOpaque(true);
        b.setContentAreaFilled(true);

        Insets padding = new Insets(10, 18, 10, 18);

        switch (style)
        {
            case PRIMARY ->
            {
                b.setBackground(PRIMARY);
                b.setForeground(Color.WHITE);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_DARK, 1, true),
                        new EmptyBorder(padding)
                ));
            }
            case SECONDARY ->
            {
                b.setBackground(new Color(245, 246, 250));
                b.setForeground(new Color(35, 35, 40));
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(224, 228, 236), 1, true),
                        new EmptyBorder(padding)
                ));
            }
            case DANGER ->
            {
                b.setBackground(new Color(255, 236, 236));
                b.setForeground(DANGER_TEXT);
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(242, 180, 180), 1, true),
                        new EmptyBorder(padding)
                ));
            }
            case GHOST ->
            {
                b.setBackground(APP_BG);
                b.setForeground(new Color(60, 60, 70));
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(224, 228, 236), 1, true),
                        new EmptyBorder(padding)
                ));
            }
        }

        return b;
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
            dueDateValue.setForeground(TEXT_STRONG);
            notesValue.setText("");
            return;
        }

        titleValue.setText(t.getTitle());
        priorityValue.setText(t.getPriority().name());

        if (t.getDueDate() == null)
        {
            dueDateValue.setText("-");
            dueDateValue.setForeground(TEXT_STRONG);
        }
        else
        {
            dueDateValue.setText(DATE_FMT.format(t.getDueDate()));
            if (!t.isCompleted() && t.getDueDate().isBefore(LocalDate.now()))
            {
                dueDateValue.setForeground(DANGER);
            }
            else
            {
                dueDateValue.setForeground(TEXT_STRONG);
            }
        }

        notesValue.setText(t.getNotes() == null ? "" : t.getNotes());
        notesValue.setCaretPosition(0);
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
