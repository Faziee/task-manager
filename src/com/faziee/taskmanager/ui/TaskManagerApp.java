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
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton addBtn = new JButton("Add");
        JButton doneBtn = new JButton("Mark Done");
        JButton deleteBtn = new JButton("Delete");
        JButton exitBtn = new JButton("Exit");

        addBtn.addActionListener(e -> addTaskPopup());
        doneBtn.addActionListener(e -> markDone());
        deleteBtn.addActionListener(e -> deleteSelected());
        exitBtn.addActionListener(e -> exitSafely());

        buttons.add(addBtn);
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
    }

    private void addTaskPopup()
    {
        String title = JOptionPane.showInputDialog(
                this,
                "Enter task title:",
                "Add Task",
                JOptionPane.PLAIN_MESSAGE
        );

        if (title == null) return; // cancelled
        title = title.trim();

        if (title.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Task title can't be empty.");
            return;
        }

        taskManager.addTask(title);
        saveSafely();
        refreshList();
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