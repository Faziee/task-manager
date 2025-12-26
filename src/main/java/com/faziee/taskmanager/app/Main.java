package com.faziee.taskmanager.app;
import com.faziee.taskmanager.core.TaskManager;
import com.faziee.taskmanager.storage.TaskRepository;
import com.faziee.taskmanager.ui.TaskManagerApp;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        TaskManager taskManager = new TaskManager();
        TaskRepository repository = new TaskRepository("tasks.json");

        try
        {
            taskManager.setTasks(repository.load());
        }
        catch (Exception e)
        {
            System.out.println("Could not load tasks file (starting fresh).");
        }

        SwingUtilities.invokeLater(() ->
        {
            TaskManagerApp app = new TaskManagerApp(taskManager, repository);
            app.setVisible(true);
        });
    }
}
