package com.faziee.taskmanager.core;

import java.time.LocalDate;
import java.util.Objects;

public class Task
{
    private String title;
    private Priority priority;
    private LocalDate dueDate;
    private String notes;
    private boolean completed;

    public Task()
    {

    }

    public Task(String title, Priority priority, LocalDate dueDate, String notes)
    {
        this.title = Objects.requireNonNull(title);
        this.priority = Objects.requireNonNull(priority);
        this.dueDate = dueDate;
        this.notes = notes;
        this.completed = false;
    }

    // Backwards-compat constructor (for old code for storage), remove later
//    public Task(String title)
//    {
//        this(title, Priority.MEDIUM, null, null);
//    }

    public String getTitle()
    {
        return this.title;
    }

    public Priority getPriority()
    {
        return this.priority;
    }

    public LocalDate getDueDate()
    {
        return this.dueDate;
    }

    public String getNotes()
    {
        return this.notes;
    }

    public boolean isCompleted()
    {
        return this.completed;
    }

    public void markCompleted()
    {
        this.completed = true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(completed ? "[x] " : "[ ] ");
        sb.append(title);
        sb.append(" (").append(priority).append(")");

        if (dueDate != null)
        {
            sb.append(" - due ").append(dueDate);
        }

        return sb.toString();
    }
}
