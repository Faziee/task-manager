package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskListRenderer extends JPanel implements ListCellRenderer<Task>
{
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JLabel titleLabel = new JLabel();
    private final JLabel metaLabel = new JLabel();

    private static final Color BG_NORMAL   = new Color(252, 253, 255);   // white
    private static final Color BG_SELECTED = new Color(210, 220, 235);   // blue-gray (selection)
    private static final Color BG_DONE     = new Color(232, 246, 238);   // mint (completed)
    private static final Color BG_OVERDUE  = new Color(255, 238, 238);   // soft red

    private static final Color TEXT_PRIMARY = new Color(28, 28, 32);
    private static final Color TEXT_MUTED   = new Color(115, 115, 130);
    private static final Color TEXT_DONE    = new Color(140, 140, 150);
    private static final Color DANGER       = new Color(220, 85, 85);

    public TaskListRenderer()
    {
        setLayout(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(14, 18, 14, 18));
        setOpaque(true);

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        metaLabel.setFont(metaLabel.getFont().deriveFont(12.8f));

        add(titleLabel, BorderLayout.NORTH);
        add(metaLabel, BorderLayout.SOUTH);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Task> list,
            Task task,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        boolean overdue = !task.isCompleted()
                && task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDate.now());

        titleLabel.setText(task.isCompleted()
                ? "✓ " + task.getTitle()
                : task.getTitle());

        String meta = task.getPriority().name();
        if (task.getDueDate() != null)
        {
            meta += "  •  due " + DATE_FMT.format(task.getDueDate());
        }
        metaLabel.setText(meta);

        if (task.isCompleted())
        {
            setBackground(BG_DONE);
            titleLabel.setForeground(TEXT_DONE);
            metaLabel.setForeground(TEXT_DONE);
        }
        else if (isSelected)
        {
            setBackground(BG_SELECTED);
            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(TEXT_MUTED);
        }
        else if (overdue)
        {
            setBackground(BG_OVERDUE);
            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(DANGER);
        }
        else
        {
            setBackground(BG_NORMAL);
            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(TEXT_MUTED);
        }

        return this;
    }
}