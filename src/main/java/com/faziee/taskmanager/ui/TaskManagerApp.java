package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    public TaskManagerApp(TaskManager taskManager, TaskRepository repository)
    {
        super("Personal Task Manager");
        this.taskManager = taskManager;
        this.repository = repository;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(650, 450);
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

        taskList.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e)
            {
                int index = taskList.locationToIndex(e.getPoint());

                if (index >= 0)
                {
                    taskList.setSelectedIndex(index);
                    updateDetailsFromSelection();
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    editSelectedTask();
                }
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
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Personal Task Manager");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(header, BorderLayout.NORTH);

        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel detailsPanel = buildDetailsPanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(taskList),
                detailsPanel
        );
        splitPane.setResizeWeight(0.45);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton doneBtn = new JButton("Mark Done");
        JButton deleteBtn = new JButton("Delete");
        JButton exitBtn = new JButton("Exit");

        addBtn.addActionListener(e -> addTaskPopup());
        editBtn.addActionListener(e -> editSelectedTask());
        doneBtn.addActionListener(e -> markDone());
        deleteBtn.addActionListener(e -> deleteSelected());
        exitBtn.addActionListener(e -> exitSafely());

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(doneBtn);
        buttons.add(deleteBtn);
        buttons.add(exitBtn);

        add(buttons, BorderLayout.SOUTH);
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

    private void addTaskPopup()
    {
        Task created = TaskDialogs.showTaskForm(this, "Add Task", null);
        if (created == null) return;

        taskManager.getTasks().add(created);
        saveSafely();
        refreshList();

        taskList.setSelectedIndex(listModel.size() - 1);
        updateDetailsFromSelection();
    }

    private void editSelectedTask()
    {
        int idx = taskList.getSelectedIndex();
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        Task existing = taskManager.getTasks().get(idx);
        Task edited = TaskDialogs.showTaskForm(this, "Edit Task", existing);
        if (edited == null) return;

        taskManager.getTasks().set(idx, edited);
        saveSafely();
        refreshList();

        taskList.setSelectedIndex(idx);
        updateDetailsFromSelection();
    }

    private JPanel buildDetailsPanel()
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel grid = new JPanel(new GridLayout(0, 1, 6, 6));

        grid.add(new JLabel("Title:"));
        grid.add(titleValue);

        grid.add(new JLabel("Priority:"));
        grid.add(priorityValue);

        grid.add(new JLabel("Due date:"));
        grid.add(dueDateValue);

        grid.add(new JLabel("Notes:"));
        notesValue.setLineWrap(true);
        notesValue.setWrapStyleWord(true);
        notesValue.setEditable(false);

        panel.add(grid, BorderLayout.NORTH);
        panel.add(new JScrollPane(notesValue), BorderLayout.CENTER);

        JLabel hint = new JLabel("Tip: double-click a task to edit");
        panel.add(hint, BorderLayout.SOUTH);

        return panel;
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
        dueDateValue.setText(selected.getDueDate() == null ? "-" : selected.getDueDate().toString());
        notesValue.setText(selected.getNotes() == null ? "" : selected.getNotes());
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

        taskManager.getTasks().remove(idx); // make this cleaner later
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
}