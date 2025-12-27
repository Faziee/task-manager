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

    private static final Color BG_NORMAL   = new Color(252, 253, 255);
    private static final Color BG_SELECTED = new Color(210, 220, 235);
    private static final Color BG_DONE     = new Color(232, 246, 238);
    private static final Color BG_OVERDUE  = new Color(255, 238, 238);

    private static final Color TEXT_PRIMARY = new Color(28, 28, 32);
    private static final Color TEXT_MUTED   = new Color(115, 115, 130);
    private static final Color TEXT_DONE    = new Color(140, 140, 150);
    private static final Color DANGER       = new Color(220, 85, 85);

    private static final Color ACCENT_SELECTED = new Color(120, 140, 175);
    private static final Color ACCENT_DONE     = new Color(120, 185, 150);
    private static final Color ACCENT_OVERDUE  = DANGER;                  // red accent
    private static final Color SEPARATOR       = new Color(232, 236, 244);

    private final JPanel accentBar = new JPanel();
    private final JLabel titleLabel = new JLabel();
    private final JLabel metaLabel = new JLabel();

    private final JPanel content = new JPanel(new BorderLayout(0, 6));
    private final JPanel center = new JPanel(new BorderLayout());

    public TaskListRenderer()
    {
        setLayout(new BorderLayout());
        setOpaque(true);

        accentBar.setPreferredSize(new Dimension(6, 1));
        accentBar.setOpaque(true);

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        metaLabel.setFont(metaLabel.getFont().deriveFont(12.8f));

        content.setBorder(new EmptyBorder(14, 16, 14, 18));
        content.setOpaque(false);

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(metaLabel, BorderLayout.SOUTH);

        center.setOpaque(false);
        center.add(content, BorderLayout.CENTER);

        add(accentBar, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
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

        titleLabel.setText(task.isCompleted() ? "✓ " + task.getTitle() : task.getTitle());

        String meta = task.getPriority().name();
        if (task.getDueDate() != null)
        {
            meta += "  •  due " + DATE_FMT.format(task.getDueDate());
        }
        metaLabel.setText(meta);

        if (task.isCompleted())
        {
            setBackground(BG_DONE);
            accentBar.setBackground(ACCENT_DONE);

            titleLabel.setForeground(TEXT_DONE);
            metaLabel.setForeground(TEXT_DONE);
        }
        else if (overdue)
        {
            setBackground(BG_OVERDUE);
            accentBar.setBackground(ACCENT_OVERDUE);

            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(DANGER);
        }
        else if (isSelected)
        {
            setBackground(BG_SELECTED);
            accentBar.setBackground(ACCENT_SELECTED);

            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(TEXT_MUTED);
        }
        else
        {
            setBackground(BG_NORMAL);
            accentBar.setBackground(BG_NORMAL);

            titleLabel.setForeground(TEXT_PRIMARY);
            metaLabel.setForeground(TEXT_MUTED);
        }

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SEPARATOR));

        return this;
    }
}
