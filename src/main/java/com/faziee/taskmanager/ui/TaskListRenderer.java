package com.faziee.taskmanager.ui;

import com.faziee.taskmanager.core.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TaskListRenderer extends JPanel implements ListCellRenderer<Task>
{
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final JLabel titleLabel = new JLabel();
    private final JLabel metaLabel = new JLabel();

    // 🎨 Pastel palette
    private static final Color BG_NORMAL = new Color(248, 249, 251);
    private static final Color BG_SELECTED = new Color(180, 210, 245);
    private static final Color BG_DONE = new Color(230, 240, 232);

    private static final Color TEXT_PRIMARY = new Color(30, 30, 30);
    private static final Color TEXT_MUTED = new Color(130, 130, 130);
    private static final Color TEXT_DONE = new Color(150, 150, 150);

    public TaskListRenderer()
    {
        setLayout(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(10, 12, 10, 12));
        setOpaque(true);

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
        metaLabel.setFont(metaLabel.getFont().deriveFont(Font.PLAIN, 12.5f));
        metaLabel.setForeground(TEXT_MUTED);

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
        // ----- Text -----
        titleLabel.setText(task.isCompleted() ? "✓ " + task.getTitle() : task.getTitle());

        String meta = task.getPriority().name();
        if (task.getDueDate() != null)
        {
            meta += "  •  due " + DATE_FMT.format(task.getDueDate());
        }
        metaLabel.setText(meta);

        // ----- Colors -----
        if (task.isCompleted())
        {
            setBackground(BG_DONE);
            titleLabel.setForeground(TEXT_DONE);
            metaLabel.setForeground(TEXT_DONE);
        }
        else if (isSelected)
        {
            setBackground(BG_SELECTED);
            titleLabel.setForeground(Color.WHITE);
            metaLabel.setForeground(Color.WHITE);
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