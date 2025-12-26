package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Priority;
import com.faziee.taskmanager.core.Task;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Locale;

public class TaskDialogs
{
    public static Task showTaskForm(Component parent, String dialogTitle, Task existing)
    {
        JTextField titleField = new JTextField(
                existing == null ? "" : existing.getTitle(),
                22
        );

        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(
                existing == null ? Priority.MEDIUM : existing.getPriority()
        );

        JTextArea notesArea = new JTextArea(
                existing == null || existing.getNotes() == null ? "" : existing.getNotes(),
                6,
                24
        );
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JCheckBox hasDueDate = new JCheckBox("Set due date");
        hasDueDate.setSelected(existing != null && existing.getDueDate() != null);

        DatePickerSettings settings = new DatePickerSettings(Locale.getDefault());
        settings.setAllowKeyboardEditing(true);
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setFormatForDatesBeforeCommonEra("yyyy-MM-dd");
        settings.setVisibleDateTextField(true);

        DatePicker datePicker = new DatePicker(settings);
        datePicker.getComponentDateTextField().setColumns(10);
        datePicker.getComponentDateTextField()
                .setToolTipText("Type date as yyyy-MM-dd or use the calendar");

        if (existing != null && existing.getDueDate() != null)
        {
            datePicker.setDate(existing.getDueDate());
        }

        JLabel dateHint = new JLabel("(yyyy-MM-dd)");
        dateHint.setFont(dateHint.getFont().deriveFont(Font.PLAIN, 11f));
        dateHint.setForeground(Color.GRAY);

        datePicker.setEnabled(hasDueDate.isSelected());
        hasDueDate.addActionListener(e ->
                datePicker.setEnabled(hasDueDate.isSelected())
        );

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dateRow.add(datePicker);
        dateRow.add(dateHint);
        dateRow.setOpaque(false);

        // ---------- Layout ----------
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        panel.add(new JLabel("Title:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        panel.add(titleField, c);

        c.gridy++;
        c.gridx = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Priority:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(priorityBox, c);

        c.gridy++;
        c.gridx = 0;
        panel.add(hasDueDate, c);

        c.gridx = 1;
        panel.add(dateRow, c);

        c.gridy++;
        c.gridx = 0;
        panel.add(new JLabel("Notes:"), c);

        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(320, 140));
        panel.add(notesScroll, c);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                dialogTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        String titleText = titleField.getText().trim();
        if (titleText.isEmpty())
        {
            JOptionPane.showMessageDialog(parent, "Title is required.");
            return null;
        }

        Priority priority = (Priority) priorityBox.getSelectedItem();

        LocalDate dueDate = null;
        if (hasDueDate.isSelected())
        {
            dueDate = datePicker.getDate();
        }

        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) notes = null;

        Task newTask = new Task(titleText, priority, dueDate, notes);

        if (existing != null && existing.isCompleted())
        {
            newTask.markCompleted();
        }

        return newTask;
    }
}