package com.faziee.taskmanager.core;

import java.util.ArrayList;
import java.util.List;

public class TaskManager
{
    private final List<Task> tasks = new ArrayList<>();

    public List<Task> getTasks()
    {
        return this.tasks;
    }

    public void setTasks(List<Task> loaded)
    {
        this.tasks.clear();
        this.tasks.addAll(loaded);
    }

    public void addTask(Task task)
    {
        this.tasks.add(task);
    }

    public void updateTask(int indexZeroBased, Task updated)
    {
        this.tasks.set(indexZeroBased, updated);
    }

    public void deleteTask(int indexZeroBased)
    {
        this.tasks.remove(indexZeroBased);
    }

    public boolean markTaskDone(int oneBasedIndex)
    {
        int idx = oneBasedIndex - 1;

        if (idx < 0 || idx >= this.tasks.size())
        {
            return false;
        }

        this.tasks.get(idx).markCompleted();

        return true;
    }

    public boolean isEmpty()
    {
        return this.tasks.isEmpty();
    }

    public int size()
    {
        return this.tasks.size();
    }
}
