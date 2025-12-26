package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
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
        // Root padding
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        // Header
        JLabel header = new JLabel("Personal Task Manager");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        root.add(header, BorderLayout.NORTH);

        // Task list styling
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setVisibleRowCount(-1);
        taskList.setFixedCellHeight(44);
        taskList.setCellRenderer(new TaskListRenderer());
        JScrollPane listScroll = new JScrollPane(taskList);
        listScroll.setBorder(cardBorder());
        listScroll.getViewport().setBackground(taskList.getBackground());

        // Details card
        JPanel detailsPanel = buildDetailsPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailsPanel);
        split.setResizeWeight(0.42);
        split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBorder(new EmptyBorder(8, 0, 0, 0));

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
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(cardBorder());

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 10, 12);

        JLabel titleLabel = new JLabel("Title");
        JLabel priorityLabel = new JLabel("Priority");
        JLabel dueLabel = new JLabel("Due date");
        JLabel notesLabel = new JLabel("Notes");

        styleKey(titleLabel);
        styleKey(priorityLabel);
        styleKey(dueLabel);
        styleKey(notesLabel);

        styleValue(titleValue, 16f, true);
        styleValue(priorityValue, 14f, false);
        styleValue(dueDateValue, 14f, false);

        content.add(titleLabel, c);
        c.gridy++;
        content.add(priorityLabel, c);
        c.gridy++;
        content.add(dueLabel, c);
        c.gridy++;
        content.add(notesLabel, c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 10, 0);

        content.add(titleValue, c);
        c.gridy++;
        content.add(priorityValue, c);
        c.gridy++;
        content.add(dueDateValue, c);

        c.gridy++;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;

        notesValue.setLineWrap(true);
        notesValue.setWrapStyleWord(true);
        notesValue.setEditable(false);
        notesValue.setBorder(new EmptyBorder(10, 10, 10, 10));
        notesValue.setFont(notesValue.getFont().deriveFont(14f));

        JScrollPane notesScroll = new JScrollPane(notesValue);
        notesScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        content.add(notesScroll, c);

        notesScroll.setPreferredSize(new Dimension(480, 220));
        notesValue.setBackground(new Color(250, 250, 250));


        card.add(content, BorderLayout.CENTER);

        JLabel hint = new JLabel("Tip: select a task to view details. Use Edit to change it.");
        hint.setBorder(new EmptyBorder(0, 12, 12, 12));
        hint.setForeground(new Color(120, 120, 120));
        card.add(hint, BorderLayout.SOUTH);

        return card;
    }

    private void refreshList()
    {
        listModel.clear();
        for (Task t : taskManager.getTasks())
        {
            listModel.addElement(t);
        }

        setTitle("Personal Task Manager (" + taskManager.size() + " tasks)");

        if (!listModel.isEmpty() && taskList.getSelectedIndex() == -1)
        {
            taskList.setSelectedIndex(0);
        }
        updateDetailsFromSelection();
    }

    private void updateDetailsFromSelection()
    {
        Task selected = taskList.getSelectedValue();

        if (selected == null)
        {
            titleValue.setText("-");
            priorityValue.setText("-");
            dueDateValue.setText("-");
            notesValue.setText("");
            return;
        }

        titleValue.setText(selected.getTitle());
        priorityValue.setText(String.valueOf(selected.getPriority()));
        dueDateValue.setText(selected.getDueDate() == null ? "-" : DATE_FMT.format(selected.getDueDate()));
        notesValue.setText(selected.getNotes() == null ? "" : selected.getNotes());
    }

    private void addTask()
    {
        Task created = TaskDialogs.showTaskForm(this, "Add Task", null);
        if (created == null) return;

        taskManager.addTask(created);
        saveSafely();
        refreshList();
        taskList.setSelectedIndex(listModel.size() - 1);
    }

    private void editTask()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        Task existing = listModel.getElementAt(idx);
        Task updated = TaskDialogs.showTaskForm(this, "Edit Task", existing);
        if (updated == null) return;

        taskManager.updateTask(idx, updated);
        saveSafely();
        refreshList();
        taskList.setSelectedIndex(idx);
    }

    private void markDone()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        boolean ok = taskManager.markTaskDone(idx + 1);
        if (!ok)
        {
            JOptionPane.showMessageDialog(this, "Could not mark task as done.");
            return;
        }

        saveSafely();
        refreshList();
        taskList.setSelectedIndex(idx);
    }

    private void deleteSelected()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete selected task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        taskManager.deleteTask(idx);
        saveSafely();
        refreshList();
    }

    private void saveSafely()
    {
        try
        {
            repository.save(taskManager.getTasks());
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Warning: could not save tasks.");
        }
    }

    private void exitSafely()
    {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Exit the app?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION)
        {
            saveSafely();
            dispose();
            System.exit(0);
        }
    }

    // ---------- Helpers / styling ----------

    private static void styleKey(JLabel label)
    {
        label.setForeground(new Color(120, 120, 120));
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
    }

    private static void styleValue(JLabel label, float size, boolean bold)
    {
        label.setFont(label.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, size));
    }

    private static CompoundBorder cardBorder()
    {
        return BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        );
    }

    private class TaskListRenderer extends JPanel implements ListCellRenderer<Task>
    {
        private final JLabel title = new JLabel();
        private final JLabel meta = new JLabel();

        TaskListRenderer()
        {
            setLayout(new BorderLayout(6, 2));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
            meta.setFont(meta.getFont().deriveFont(Font.PLAIN, 12f));
            meta.setForeground(new Color(120, 120, 120));
            add(title, BorderLayout.NORTH);
            add(meta, BorderLayout.SOUTH);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task value, int index, boolean isSelected, boolean cellHasFocus)
        {
            String t = value.getTitle();
            title.setText(value.isCompleted() ? "✓ " + t : t);

            String due = value.getDueDate() == null ? "" : (" • due " + DATE_FMT.format(value.getDueDate()));
            meta.setText(value.getPriority() + due);

            if (value.isCompleted())
            {
                title.setForeground(new Color(140, 140, 140));
            }
            else
            {
                title.setForeground(list.getForeground());
            }

            if (isSelected)
            {
                setBackground(list.getSelectionBackground());
                title.setForeground(list.getSelectionForeground());
                meta.setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                meta.setForeground(new Color(120, 120, 120));
            }

            return this;
        }
    }
}