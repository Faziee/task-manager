package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Priority;
import com.faziee.taskmanager.core.Task;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TaskDialogs
{
    public static Task showTaskForm(Component parent, String title, Task existing)
    {
        JTextField titleField = new JTextField(existing == null ? "" : existing.getTitle(), 20);

        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(existing == null ? Priority.MEDIUM : existing.getPriority());

        // --- Due date picker (optional) ---
        JCheckBox hasDueDate = new JCheckBox("Set due date");
        hasDueDate.setSelected(existing != null && existing.getDueDate() != null);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dueDateSpinner = new JSpinner(dateModel);
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd"));

        // Set initial value if editing
        if (existing != null && existing.getDueDate() != null)
        {
            Date d = Date.from(existing.getDueDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());
            dueDateSpinner.setValue(d);
        }

        dueDateSpinner.setEnabled(hasDueDate.isSelected());
        hasDueDate.addActionListener(e -> dueDateSpinner.setEnabled(hasDueDate.isSelected()));

        JTextArea notesArea = new JTextArea(
                existing == null || existing.getNotes() == null ? "" : existing.getNotes(),
                5,
                20
        );
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Priority:"));
        panel.add(priorityBox);

        panel.add(hasDueDate);
        panel.add(dueDateSpinner);

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
        if (hasDueDate.isSelected())
        {
            Date selected = (Date) dueDateSpinner.getValue();
            due = selected.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) notes = null;

        Task newTask = new Task(t, p, due, notes);

        // preserve completion status when editing
        if (existing != null && existing.isCompleted())
        {
            newTask.markCompleted();
        }

        return newTask;
    }
}
