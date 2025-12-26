package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Priority;
import com.faziee.taskmanager.core.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskDialogs
{
    public static Task showTaskForm(Component parent, String title, Task existing)
    {
        JTextField titleField = new JTextField(existing == null ? "" : existing.getTitle(), 20);

        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(existing == null ? Priority.MEDIUM : existing.getPriority());

        JTextField dueDateField = new JTextField(
                existing == null || existing.getDueDate() == null ? "" : existing.getDueDate().toString(),
                10
        );

        JTextArea notesArea = new JTextArea(existing == null || existing.getNotes() == null ? "" : existing.getNotes(), 5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);
        panel.add(new JLabel("Due date (yyyy-mm-dd, optional):"));
        panel.add(dueDateField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        String t = titleField.getText().trim();
        if (t.isEmpty())
        {
            JOptionPane.showMessageDialog(parent, "Title is required.");
            return null;
        }

        Priority p = (Priority) priorityBox.getSelectedItem();

        LocalDate due = null;
        String dueText = dueDateField.getText().trim();
        if (!dueText.isEmpty())
        {
            try
            {
                due = LocalDate.parse(dueText);
            }
            catch (DateTimeParseException ex)
            {
                JOptionPane.showMessageDialog(parent, "Invalid date format. Use yyyy-mm-dd.");
                return null;
            }
        }

        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) notes = null;

        Task newTask = new Task(t, p, due, notes);

        if (existing != null && existing.isCompleted())
        {
            newTask.markCompleted();
        }

        return newTask;
    }
}
